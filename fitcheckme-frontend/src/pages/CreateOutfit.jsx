import { Button, Drawer, DrawerBody, DrawerCloseButton, DrawerContent, DrawerHeader, DrawerOverlay, Flex, FormControl, FormLabel, Input, VStack, background, useColorModeValue, useDisclosure, useToast } from "@chakra-ui/react"
import { MultiSelect } from "chakra-multiselect"
import { useState } from "react"
import { toTitleCase } from "../utils/StringUtil";

export default function CreateOutfit() {
	const tempNumOutfits = 0;

	const tempTags = ["casual", "formal", "workout", "beach", "winter", "summer"]

	const defaultFormValues = {
		outfitName: `Outfit ${tempNumOutfits + 1}`,
		outfitDesc: ""
	}

	const { isOpen, onOpen, onClose } = useDisclosure()
	const [formValues, setFormValues] = useState({ ...defaultFormValues })

	const toast = useToast();

	const handleClose = () => {
		setFormValues({ ...defaultFormValues })
		onClose();
	}

	const handleChange = (e) => {
		console.log(e);
		setFormValues({
			...formValues,
			[e.target.name]: e.target.value
		})
	}
	const handleMultiSelectChange = (e) => {
		console.log(e)
		setFormValues({
			...formValues,
			tags: e
		})
	
	}

	const handleSubmit = async (e) => {
		e.preventDefault()
		formValues.tags = formValues.tags.map((tag) => tag.value);

		//TODO handle submit
		console.log(formValues)

		handleClose()
		toast({
			title: 'Outfit created.',
			description: "Your outfit has been created and can now be viewed for inspiration.",
			status: 'success',
			duration: 5000,
			isClosable: true,
		})
	}

	return (
		<>
			<Button colorScheme="teal" onClick={onOpen}>Create outfit</Button>
			<Drawer placement="bottom" onClose={handleClose} isOpen={isOpen} size="full">
				<DrawerOverlay />
				<DrawerContent>
					<DrawerCloseButton />
					<DrawerHeader>Create outfit</DrawerHeader>
					<DrawerBody>
						<form onSubmit={handleSubmit} onChange={handleChange}>
							<VStack align={"baseline"} spacing={4}>
								<Flex>
									<FormControl>
										<FormLabel>Outfit name</FormLabel>
										<Input placeholder={`Outfit ${tempNumOutfits + 1}`} name="outfitName" type="text" />
									</FormControl>
								</Flex>
								<FormControl>
									<FormLabel>Description</FormLabel>
									<Input name="outfitDesc" type="text" />
								</FormControl>
								<FormControl>
									<FormLabel>Tags</FormLabel>
									<MultiSelect
										name="tags"
										options={tempTags.map((tag) => { return { value: tag, label: toTitleCase(tag) } })}
										value={formValues.tags}
										onChange={handleMultiSelectChange}
										label='Choose tags'
										placeholder='Select tags'
									/>
								</FormControl>
								<Button w="100%" type="submit" colorScheme="green" >Create</Button>
							</VStack>
						</form>
					</DrawerBody>
				</DrawerContent>
			</Drawer>
		</>
	)
}