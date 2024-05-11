import { Button, Flex, FormControl, FormLabel, Input, InputGroup, InputRightAddon, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, Select, VStack, useDisclosure, useTheme, useToast } from "@chakra-ui/react";
import { useTags } from "../contexts/TagsContext"
import { useState } from "react";
import { MultiSelect } from "chakra-multiselect";
import { toTitleCase } from "../utils/StringUtil";
import { AddIcon, CloseIcon } from "@chakra-ui/icons";
import { createGarment } from "../backend/Application";


export default function CreateGarment({addGarment}) {
	const defaultFormValues = {
		garmentName: "",
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

	const handleURLSubmit = (e) => {
		e.preventDefault();
		if (formValues.url === "") return;
		let newEnteredURLs = new Set(enteredURLs);
		newEnteredURLs.add(formValues.url);
		setEnteredURLs(newEnteredURLs);
		setFormValues({
			...formValues,
			url: ""
		})
	}

	const handleRemoveUrl = (url) => {
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
		console.log(formValues);
		
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
					})
				}
				else {
					handleClose()
					toast({
						title: 'Garment created.',
						description: "Your outfit has been created and can now be added to your outfit.",
						status: 'success',
						duration: 5000,
						isClosable: true,
					})
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
						<VStack saturate={4}>
							<FormControl>
								<FormLabel>Garment Name</FormLabel>
								<Input required type='text' name='garmentName' value={formValues.garmentName} onChange={handleFormChange} />
							</FormControl>

							<FormControl>
								<FormLabel>URLs</FormLabel>
								<VStack spacing={2}>
									{Array.from(enteredURLs).map((url, index) => {
										return (
											<InputGroup key={index}>
												<Input isDisabled type='text' value={url} />
												<InputRightAddon p="0px"><Button colorScheme="red" borderLeftRadius="0px" onClick={() => handleRemoveUrl(url)}><CloseIcon /></Button></InputRightAddon>
											</InputGroup>
										)
									})
									}
									<InputGroup>
										<Input type='text' name='url' onChange={handleFormChange} onKeyDown={(e) => e.key === "Enter" && handleURLSubmit(e)} value={formValues.url} />
										<InputRightAddon bg="transparent" borderColor="inherit" borderLeft="0px" p="0px">
											<Button variant="ghost" borderLeftRadius="0px" onClick={handleURLSubmit}><AddIcon /></Button>
										</InputRightAddon>
									</InputGroup>
								</VStack>
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

