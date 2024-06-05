import { Button, Container, Flex, FormControl, FormLabel, Input, InputGroup, InputRightAddon, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, Select, VStack, useDisclosure, useTheme, useToast } from "@chakra-ui/react";
import { useTags } from "../contexts/TagsContext"
import { useState } from "react";
import { MultiSelect } from "chakra-multiselect";
import { toTitleCase } from "../utils/StringUtil";
import { AddIcon, CloseIcon } from "@chakra-ui/icons";
import { createGarment, editOutfit } from "../backend/Application";
import OutfitCard from "./OutfitCard";
import GarmentSelector from "./GarmentSelector";
import FileUploadInput from "./FileUploadInput";


export default function EditOutfit({ outfit, handleOutfitUpdate }) {
	const { tags } = useTags();

	const defaultFormValues = {
		outfitName: outfit.outfitName,
		outfitDesc: outfit.outfitDesc,
		tags: outfit.outfitTags.map((tag) => { return { value: `${tag.tagId}`, label: toTitleCase(tag.tagName) } }),
		garments: new Set(outfit.garments.map((garment) => garment.garmentId)),
		files: []
	}

	const { isOpen, onOpen, onClose } = useDisclosure()
	const [formValues, setFormValues] = useState({ ...defaultFormValues })

	const toast = useToast();

	const handleOpen = () => {
		setFormValues({ ...defaultFormValues });
		onOpen();
	}

	const handleClose = () => {
		setFormValues({ ...defaultFormValues });
		onClose();
	}

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

	const handleFileChange = (e) => {
		setFormValues({
			...formValues,
			files: [...e.target.files]
		})
	}

	const handleSubmit = async (e) => {
		e.preventDefault();

		if(JSON.stringify(formValues) === JSON.stringify(defaultFormValues)) {
			toast({
				title: 'No changes made',
				status: 'info',
				duration: 5000,
				isClosable: true,
			})
			handleClose();
			return;
		}

		const existingGarmentIds = new Set(outfit.garments.map((garment) => garment.garmentId));
		const existingTagIds = new Set(outfit.outfitTags.map((tag) => tag.tagId));
		const formTagIds = new Set(formValues.tags.map((tag) => parseInt(tag.value)));

		const addGarmentIds = Array.from(formValues.garments).filter((garmentId) => !existingGarmentIds.has(garmentId));
		const removeGarmentIds = Array.from(existingGarmentIds).filter((garmentId) => !formValues.garments.has(garmentId));
		const addTagIds = Array.from(formTagIds).filter((tagId) => !existingTagIds.has(tagId));
		const removeTagIds = Array.from(existingTagIds).filter((tagId) => !formTagIds.has(tagId));

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
				}
				else {
					handleClose()
					toast({
						title: 'Outfit updated.',
						status: 'success',
						duration: 5000,
						isClosable: true,
					})
					handleOutfitUpdate();
				}
			});
	}

	return (
		<>
			<Container _hover={{cursor: "pointer"}} onClick={handleOpen} >
				<OutfitCard outfit={outfit} size={"lg"}/>
			</Container>

			<Modal isOpen={isOpen} onClose={handleClose} size="xl" scrollBehavior="inside">
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>Edit Outfit</ModalHeader>
					<ModalCloseButton />
					<ModalBody>
						<VStack>
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
							<FileUploadInput name="images" multiple accept=".png, .jpg, .jpeg" handleFileChange={handleFileChange} />
							</FormControl>
						</VStack>
					</ModalBody>
					<ModalFooter>
						<Button onClick={handleSubmit} colorScheme='green' mr={3}>Edit</Button>
						<Button variant='ghost' onClick={handleClose}>Cancel</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</>
	)
}

