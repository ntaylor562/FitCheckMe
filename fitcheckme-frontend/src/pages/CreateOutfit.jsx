import { Button, Drawer, DrawerBody, DrawerCloseButton, DrawerContent, DrawerHeader, DrawerOverlay, Flex, FormControl, FormLabel, Input, VStack, background, useColorModeValue, useDisclosure, useToast } from "@chakra-ui/react"
import { MultiSelect } from "chakra-multiselect"
import { useState } from "react"
import { toTitleCase } from "../utils/StringUtil";
import { createOutfit } from "../backend/Application";
import GarmentSelector from "../components/GarmentSelector";
import { useTags } from "../contexts/TagsContext";

export default function CreateOutfit() {
	const tempNumOutfits = 0;

	const { tags } = useTags();

	const defaultFormValues = {
		outfitName: `Outfit ${tempNumOutfits + 1}`,
		outfitDesc: "",
		tags: [],
		garments: new Set()
	}

	const { isOpen, onOpen, onClose } = useDisclosure()
	const [formValues, setFormValues] = useState({ ...defaultFormValues })

	const toast = useToast();

	const handleClose = () => {
		setFormValues({ ...defaultFormValues })
		onClose();
	}

	const handleChange = (e) => {
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

	const handleSubmit = async (e) => {
		e.preventDefault()
		let res = { ...formValues, tags: formValues.tags.map((tag) => parseInt(tag.value)) }

		await createOutfit(res.outfitName, res.outfitDesc, res.tags, Array.from(res.garments))
			.then(async (response) => {
				if (!response.ok) {
					const contentType = response.headers.get("content-type");
					const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
					toast({
						title: 'Error creating outfit.',
						description: message,
						status: 'error',
						duration: 5000,
						isClosable: true,
					})
				}
				else {
					handleClose()
					toast({
						title: 'Outfit created.',
						description: "Your outfit has been created and can now be viewed for inspiration.",
						status: 'success',
						duration: 5000,
						isClosable: true,
					})
				}
			});
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
						<form onKeyDown={(e) => { e.key === "Enter" && e.preventDefault() }} onSubmit={handleSubmit}>
							<VStack align={"baseline"} spacing={4}>
								<Flex>
									<FormControl onChange={handleChange}>
										<FormLabel>Outfit name</FormLabel>
										<Input placeholder={`Outfit ${tempNumOutfits + 1}`} name="outfitName" type="text" />
									</FormControl>
								</Flex>
								<FormControl onChange={handleChange}>
									<FormLabel>Description</FormLabel>
									<Input name="outfitDesc" type="text" />
								</FormControl>
								<FormControl onChange={handleChange}>
									<FormLabel>Tags</FormLabel>
									<MultiSelect
										name="tags"
										options={tags.map((tag) => { return { value: `${tag.tagId}`, label: toTitleCase(tag.tagName) } })}
										value={formValues.tags}
										onChange={handleMultiSelectChange}
										placeholder='Select tags'
									/>
								</FormControl>
								<GarmentSelector handleGarmentSelect={handleGarmentSelect} selectedGarments={formValues.garments} tags={tags} />
								<Button w="100%" type="submit" colorScheme="green" >Create</Button>
							</VStack>
						</form>
					</DrawerBody>
				</DrawerContent>
			</Drawer>
		</>
	)
}