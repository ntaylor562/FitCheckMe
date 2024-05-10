import { Button, FormControl, FormLabel, Input, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, Select, VStack, useDisclosure } from "@chakra-ui/react";
import { useTags } from "../contexts/TagsContext"
import { useState } from "react";
import { MultiSelect } from "chakra-multiselect";
import { toTitleCase } from "../utils/StringUtil";


export default function CreateGarment() {
	const defaultFormValues = {
		garmentName: "",
		urls: [],
		tags: []
	}

	const { isOpen, onOpen, onClose } = useDisclosure()
	const [formValues, setFormValues] = useState({ ...defaultFormValues })
	const { tags } = useTags();

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
		console.log(formValues);
		//TODO create garment

		onClose();
	}

	return (
		<>
			<Button onClick={onOpen}>Create Garment</Button>

			<Modal isOpen={isOpen} onClose={onClose} size="xl">
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>Create Garment</ModalHeader>
					<ModalCloseButton />
					<ModalBody>
						<form onSubmit={handleSubmit}>
							<VStack saturate={4}>
								<FormControl>
									<FormLabel>Garment Name</FormLabel>
									<Input type='text' name='garmentName' value={formValues.garmentName} onChange={handleFormChange} />
								</FormControl>

								<FormControl>
									<FormLabel>URLs</FormLabel>
									<Input type='text' name='urls' value={formValues.urls} onChange={handleFormChange} />
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
						</form>
					</ModalBody>

					<ModalFooter>
						<Button type="submit" colorScheme='green' mr={3}>Create</Button>
						<Button variant='ghost' onClick={handleClose}>Cancel</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</>
	)
}

