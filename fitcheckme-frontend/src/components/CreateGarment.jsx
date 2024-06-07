import { Button, Flex, FormControl, FormLabel, Input, InputGroup, InputRightAddon, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, Select, VStack, useDisclosure, useTheme, useToast } from "@chakra-ui/react";
import { useTags } from "../contexts/TagsContext"
import { useState } from "react";
import { MultiSelect } from "chakra-multiselect";
import { toTitleCase } from "../utils/StringUtil";
import { AddIcon, CloseIcon } from "@chakra-ui/icons";
import { createGarment } from "../backend/Application";
import TagInput from "./TagInput";


export default function CreateGarment({ addGarment, defaultName = ""}) {
	const defaultFormValues = {
		garmentName: defaultName,
		url: "", //This is what's currently being typed into the URL box, not what is saved
		tags: []
	}

	const { isOpen, onOpen, onClose } = useDisclosure()
	const [formValues, setFormValues] = useState({ ...defaultFormValues })
	const [enteredURLs, setEnteredURLs] = useState(new Set())
	const { tags } = useTags();

	const toast = useToast();

	const handleClose = () => {
		setFormValues({ ...defaultFormValues });
		setEnteredURLs(new Set());
		onClose();
	}

	const handleFormChange = (e) => {
		setFormValues({
			...formValues,
			[e.target.name]: e.target.value
		})
	}

	const handleAddURL = (url) => {
		let newEnteredURLs = new Set(enteredURLs);
		newEnteredURLs.add(url);
		setEnteredURLs(newEnteredURLs);
	}

	const handleRemoveURL = (url) => {
		let newEnteredURLs = new Set(enteredURLs);
		newEnteredURLs.delete(url);
		setEnteredURLs(newEnteredURLs);
	}

	const handleMultiSelectChange = (e) => {
		setFormValues({
			...formValues,
			tags: e
		})
	}

	const handleSubmit = async (e) => {
		e.preventDefault();

		await createGarment(formValues.garmentName, Array.from(enteredURLs), formValues.tags.map((tag) => parseInt(tag.value)))
			.then(async (response) => {
				if (!response.ok) {
					const contentType = response.headers.get("content-type");
					const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
					toast({
						title: 'Error creating garment.',
						description: message,
						status: 'error',
						duration: 5000,
						isClosable: true,
					});
				}
				else {
					handleClose();
					toast({
						title: 'Garment created.',
						description: "Your outfit has been created and can now be added to your outfit.",
						status: 'success',
						duration: 5000,
						isClosable: true,
					});
					addGarment(await response.json());
				}
			});
	}

	return (
		<>
			<Button alignSelf="baseline" colorScheme="pink" onClick={onOpen}>Create Garment</Button>

			<Modal isOpen={isOpen} onClose={handleClose} size="xl" scrollBehavior="inside">
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>Create Garment</ModalHeader>
					<ModalCloseButton />
					<ModalBody>
						<VStack spacing={4}>
							<FormControl onChange={handleFormChange}>
								<FormLabel>Garment Name</FormLabel>
								<Input type='text' name='garmentName' placeholder={defaultName} />
							</FormControl>

							<FormControl>
								<FormLabel>URLs</FormLabel>
								<TagInput enteredValues={enteredURLs} handleAdd={handleAddURL} handleRemove={handleRemoveURL} />
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
						<Button onClick={handleSubmit} colorScheme='green' mr={3}>Create</Button>
						<Button variant='ghost' onClick={handleClose}>Cancel</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</>
	)
}

