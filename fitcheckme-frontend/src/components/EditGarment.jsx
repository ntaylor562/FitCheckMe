import { Button, FormControl, FormLabel, Input, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, VStack, useDisclosure, useToast } from "@chakra-ui/react";
import { useState } from "react";
import { toTitleCase } from "../utils/StringUtil";
import { MultiSelect } from "chakra-multiselect";
import { useTags } from "../contexts/TagsContext";
import EditImages from "./EditImages";
import { editGarment, editGarmentImages } from "../backend/Application";
import { uploadImages } from "../backend/FileService";
import { areSetsEqual } from "../utils/SetUtil";
import TagInput from "./TagInput";


export default function EditGarment({ garment, handleGarmentUpdate, isOpen, handleClose }) {
	const { tags } = useTags();

	const defaultFormValues = {
		garmentName: garment.garmentName,
		urls: new Set(garment.urls),
		tags: garment.garmentTags.map((tag) => { return { value: `${tag.tagId}`, label: toTitleCase(tag.tagName) } })
	}

	const [formValues, setFormValues] = useState({ ...defaultFormValues })
	const [filesToUpload, setFilesToUpload] = useState([]);
	const [filesToDelete, setFilesToDelete] = useState([]);
	const [shownImages, setShownImages] = useState(new Set(garment.images));

	const toast = useToast();

	const onClose = () => {
		setFormValues({ ...defaultFormValues });
		setFilesToUpload([]);
		setFilesToDelete([]);
		setShownImages(new Set(garment.images));
		handleClose();
	}

	const handleFormChange = (e) => {
		setFormValues({
			...formValues,
			[e.target.name]: e.target.value
		})
	}

	const handleAddURL = (url) => {
		let newEnteredURLs = new Set(formValues.urls);
		newEnteredURLs.add(url);
		setFormValues({
			...formValues,
			urls: newEnteredURLs
		});
	}

	const handleRemoveURL = (url) => {
		let newEnteredURLs = new Set(formValues.urls);
		newEnteredURLs.delete(url);
		setFormValues({
			...formValues,
			urls: newEnteredURLs
		});
	}

	const handleMultiSelectChange = (e) => {
		setFormValues({
			...formValues,
			tags: e
		})
	}

	const handleUploadFileChange = (e) => {
		setFilesToUpload([...e.target.files]);
	}

	const handleDeleteImages = (imagesToDelete) => {
		setFilesToDelete([...filesToDelete, ...imagesToDelete]);
		const newShownImages = new Set(shownImages);
		for (let image of imagesToDelete) {
			newShownImages.delete(image);
		}
		setShownImages(newShownImages);
	}

	const handleSubmit = async (e) => {
		e.preventDefault();

		let editedGarment = false;
		const existingURLs = new Set(garment.urls);
		const existingTagIds = new Set(garment.garmentTags.map((tag) => tag.tagId));
		const formTagIds = new Set(formValues.tags.map((tag) => parseInt(tag.value)));

		const addURLs = Array.from(formValues.urls).filter((url) => !existingURLs.has(url));
		const removeURLs = Array.from(existingURLs).filter((url) => !formValues.urls.has(url));
		const addTagIds = Array.from(formTagIds).filter((tagId) => !existingTagIds.has(tagId));
		const removeTagIds = Array.from(existingTagIds).filter((tagId) => !formTagIds.has(tagId));

		try {
			if (JSON.stringify(formValues) !== JSON.stringify(defaultFormValues) || !areSetsEqual(formValues.urls, defaultFormValues.urls)) {
				await editGarment(
					garment.garmentId,
					garment.garmentName === formValues.garmentName ? null : formValues.garmentName,
					addURLs.length === 0 ? null : addURLs,
					removeURLs.length === 0 ? null : removeURLs,
					addTagIds.length === 0 ? null : addTagIds,
					removeTagIds.length === 0 ? null : removeTagIds
				)
					.then(async (response) => {
						if (!response.ok) {
							const contentType = response.headers.get("content-type");
							const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
							toast({
								title: 'Error editing garment.',
								description: message,
								status: 'error',
								duration: 5000,
								isClosable: true,
							})
							throw new Error(message);
						}
						else {
							editedGarment = true;
						}
					})
			}

			if (filesToUpload.length !== 0 || filesToDelete.length !== 0) {
				let addImageIds = [];
				if (filesToUpload.length !== 0) {
					addImageIds = (await uploadImages(filesToUpload)
						.then(async (response) => {
							if (!response.ok) {
								const contentType = response.headers.get("content-type");
								const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
								toast({
									title: editedGarment ? 'Successfully edited garment details but failed uploading images.' : 'Error adding images',
									description: message,
									status: 'error',
									duration: 5000,
									isClosable: true,
								})
								throw new Error(message);
							} else {
								return response;
							}
						})
						.then((res) => res.json())).map((image) => image.fileId);
				}

				await editGarmentImages(garment.garmentId, addImageIds, filesToDelete.map((image) => image.fileId))
					.then(async (response) => {
						if (!response.ok) {
							const contentType = response.headers.get("content-type");
							const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
							toast({
								title: editedGarment ? 'Successfully edited garment details but failed editing images.' : 'Error editing images',
								description: message,
								status: 'error',
								duration: 5000,
								isClosable: true,
							})
							throw new Error(message);
						}
					})
			}

			toast({
				title: 'Outfit updated.',
				status: 'success',
				duration: 5000,
				isClosable: true,
			})
			handleGarmentUpdate();
			onClose();
		} catch (error) {
			console.error(error);
		}
	}

	return (
		<Modal isOpen={isOpen} onClose={onClose} size="xl" scrollBehavior="inside">
			<ModalOverlay />
			<ModalContent>
				<ModalHeader>Edit Garment</ModalHeader>
				<ModalCloseButton />
				<ModalBody>
					<VStack spacing={4}>
						<FormControl>
							<FormLabel>Garment Name</FormLabel>
							<Input required type='text' name='garmentName' value={formValues.garmentName} onChange={handleFormChange} />
						</FormControl>
						<FormControl>
							<FormLabel>URLs</FormLabel>
							<TagInput enteredValues={formValues.urls} handleAdd={handleAddURL} handleRemove={handleRemoveURL} placeholder="Enter a store URL for the garment" />
						</FormControl>
						<FormControl>
							<FormLabel>Tags</FormLabel>
							<MultiSelect
								name='tags'
								value={formValues.tags}
								onChange={handleMultiSelectChange}
								options={tags.map((tag) => { return { value: `${tag.tagId}`, label: toTitleCase(tag.tagName) } })}
								placeholder='Select tags'
							/>
						</FormControl>
						<FormControl>
							<FormLabel>Images</FormLabel>
							<EditImages images={shownImages} handleUploadFileChange={handleUploadFileChange} handleDeleteImages={handleDeleteImages} />
						</FormControl>
					</VStack>
				</ModalBody>
				<ModalFooter>
					<Button onClick={handleSubmit} colorScheme='green' mr={3}>Edit</Button>
					<Button variant='ghost' onClick={onClose}>Cancel</Button>
				</ModalFooter>
			</ModalContent>
		</Modal>
	)
}