import { Button, Container, Flex, FormControl, FormLabel, Input, InputGroup, InputRightAddon, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, Select, VStack, useDisclosure, useTheme, useToast } from "@chakra-ui/react";
import { useTags } from "../contexts/TagsContext"
import { useState } from "react";
import { MultiSelect } from "chakra-multiselect";
import { toTitleCase } from "../utils/StringUtil";
import { AddIcon, CloseIcon } from "@chakra-ui/icons";
import { createGarment, editOutfit } from "../backend/Application";
import OutfitCard from "./OutfitCard";


export default function EditOutfit({ outfit }) {
	const { tags } = useTags();

	const defaultFormValues = {
		outfitName: outfit.outfitName,
		outfitDesc: outfit.outfitDesc,
		tags: outfit.outfitTags.map((tag) => { return { value: `${tag.tagId}`, label: toTitleCase(tag.tagName) } }),
		garments: new Set(outfit.garments.map((garment) => garment.garmentId))
	}

	const { isOpen, onOpen, onClose } = useDisclosure()
	const [formValues, setFormValues] = useState({ ...defaultFormValues })
	const [enteredURLs, setEnteredURLs] = useState(new Set())

	const toast = useToast();

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

	const handleSubmit = async (e) => {
		e.preventDefault();
		console.log(outfit.outfitId, formValues.outfitName, formValues.outfitDesc, formValues.tags.map((tag) => parseInt(tag.value)), Array.from(formValues.garments));

		return;
		await editOutfit(outfit.outfitId, formValues.outfitName, formValues.outfitDesc, formValues.tags.map((tag) => parseInt(tag.value)), Array.from(formValues.garments))
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
				}
			});
	}

	return (
		<>
			<Container _hover={{cursor: "pointer"}} onClick={onOpen} >
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

