import { Box, Button, FormControl, FormLabel, Input, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, VStack, useDisclosure, useToast } from "@chakra-ui/react";
import { useTags } from "../contexts/TagsContext"
import { useState } from "react";
import { MultiSelect } from "chakra-multiselect";
import { toTitleCase } from "../utils/StringUtil";
import { editOutfit, editOutfitImages } from "../backend/Application";
import OutfitCard from "./OutfitCard";
import GarmentSelector from "./GarmentSelector";
import { uploadImages } from "../backend/FileService";
import EditImages from "./EditImages";
import { areSetsEqual } from "../utils/SetUtil";


export default function EditOutfit({ outfit, handleOutfitUpdate, isOpen, handleClose }) {
	if (!outfit || !isOpen) return null;

	const { tags } = useTags();

	const defaultFormValues = {
		outfitName: outfit.outfitName,
		outfitDesc: outfit.outfitDesc,
		tags: outfit.outfitTags.map((tag) => { return { value: `${tag.tagId}`, label: toTitleCase(tag.tagName) } }),
		garments: new Set(outfit.garments.map((garment) => garment.garmentId)),
	}
	const [formValues, setFormValues] = useState({ ...defaultFormValues })
	const [filesToUpload, setFilesToUpload] = useState([]);
	const [filesToDelete, setFilesToDelete] = useState([]);
	const [shownImages, setShownImages] = useState(new Set(outfit.images));

	const toast = useToast();

	const handleFormChange = (e) => {
		setFormValues({
			...formValues,
			[e.target.name]: e.target.value
		})
	}

	const handleMultiSelectChange = (e) => {
		setFormValues({
			...formValues,
			tags: e
		})
	}

	const handleGarmentSelect = (garmentId) => {
		let newGarments = new Set(formValues.garments);
		if (newGarments.has(garmentId)) {
			newGarments.delete(garmentId);
		} else {
			newGarments.add(garmentId);
		}
		setFormValues({
			...formValues,
			garments: newGarments
		});
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

		let editedOutfit = false;
		const existingGarmentIds = new Set(outfit.garments.map((garment) => garment.garmentId));
		const existingTagIds = new Set(outfit.outfitTags.map((tag) => tag.tagId));
		const formTagIds = new Set(formValues.tags.map((tag) => parseInt(tag.value)));

		const addGarmentIds = Array.from(formValues.garments).filter((garmentId) => !existingGarmentIds.has(garmentId));
		const removeGarmentIds = Array.from(existingGarmentIds).filter((garmentId) => !formValues.garments.has(garmentId));
		const addTagIds = Array.from(formTagIds).filter((tagId) => !existingTagIds.has(tagId));
		const removeTagIds = Array.from(existingTagIds).filter((tagId) => !formTagIds.has(tagId));

		try {
			if (JSON.stringify(formValues) !== JSON.stringify(defaultFormValues) || !areSetsEqual(formValues.garments, defaultFormValues.garments)){
				await editOutfit(
					outfit.outfitId,
					outfit.outfitName === formValues.outfitName ? null : formValues.outfitName,
					outfit.outfitDesc === formValues.outfitDesc ? null : formValues.outfitDesc,
					addGarmentIds.length === 0 ? null : addGarmentIds,
					removeGarmentIds.length === 0 ? null : removeGarmentIds,
					addTagIds.length === 0 ? null : addTagIds,
					removeTagIds.length === 0 ? null : removeTagIds
				)
					.then(async (response) => {
						if (!response.ok) {
							const contentType = response.headers.get("content-type");
							const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
							toast({
								title: 'Error editing outfit.',
								description: message,
								status: 'error',
								duration: 5000,
								isClosable: true,
							})
							throw new Error(message);
						}
						else {
							editedOutfit = true;
						}
					})
			}

			if (filesToUpload.length !== 0 || filesToDelete.length !== 0) {
				let addImageIds = []
				if (filesToUpload.length !== 0) {
					addImageIds = (await uploadImages(filesToUpload)
						.then(async (response) => {
							if (!response.ok) {
								const contentType = response.headers.get("content-type");
								const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
								toast({
									title: editedOutfit ? 'Successfully edited outfit details but failed uploading images.' : 'Error adding images',
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
						.then((res) => {
							if (!res.ok) {
								throw new Error("Failed to upload image");
							}
							return res.json();
						})).map((image) => image.fileId);
				}

				await editOutfitImages(outfit.outfitId, addImageIds, filesToDelete.map((image) => image.fileId))
					.then(async (response) => {
						if (!response.ok) {
							const contentType = response.headers.get("content-type");
							const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
							toast({
								title: editedOutfit ? 'Successfully edited outfit details but failed editing images.' : 'Error editing images',
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
			handleOutfitUpdate();
			handleClose();
		} catch (error) {
			console.error(error);
		}
	}

	return (
		<Modal isOpen={isOpen} onClose={handleClose} size="xl" scrollBehavior="inside">
			<ModalOverlay />
			<ModalContent>
				<ModalHeader>Edit Outfit</ModalHeader>
				<ModalCloseButton />
				<ModalBody>
					<VStack spacing={4}>
						<FormControl>
							<FormLabel>Outfit Name</FormLabel>
							<Input required type='text' name='outfitName' value={formValues.outfitName} onChange={handleFormChange} />
						</FormControl>
						<FormControl>
							<FormLabel>Outfit Description</FormLabel>
							<Input required type='text' name='outfitDesc' value={formValues.outfitDesc} onChange={handleFormChange} />
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
						<GarmentSelector selectedGarments={formValues.garments} handleGarmentSelect={handleGarmentSelect} />
						<FormControl>
							<FormLabel>Images</FormLabel>
							<EditImages images={shownImages} handleUploadFileChange={handleUploadFileChange} handleDeleteImages={handleDeleteImages} />
						</FormControl>
					</VStack>
				</ModalBody>
				<ModalFooter>
					<Button onClick={handleSubmit} colorScheme='green' mr={3}>Edit</Button>
					<Button variant='ghost' onClick={handleClose}>Cancel</Button>
				</ModalFooter>
			</ModalContent>
		</Modal>
	)
}

