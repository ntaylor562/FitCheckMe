

export default function CreateGarment() {
	const defaultFormValues = {
		garmentName: "",
		urls: [],
		tags: []
	}

	const { isOpen, onOpen, onClose } = useDisclosure()
	const [formValues, setFormValues] = useState({ ...defaultFormValues })

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

	return (
		<>
			<Button onClick={onOpen}>Create Garment</Button>

			<Modal isOpen={isOpen} onClose={onClose}>
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>Create Garment</ModalHeader>
					<ModalCloseButton />
					<ModalBody>
						<form>
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
								<Select
									isMulti
									name='tags'
									value={formValues.tags}
									onChange={handleMultiSelectChange}
									options={tagOptions}
								/>
							</FormControl>
						</form>
					</ModalBody>

					<ModalFooter>
						<Button colorScheme='blue' mr={3} onClick={onClose}>
							Close
						</Button>
						<Button variant='ghost' onClick={handleClose}>Cancel</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</>
	)
}

