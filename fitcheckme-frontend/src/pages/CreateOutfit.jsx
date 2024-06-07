import { Button, Drawer, DrawerBody, DrawerCloseButton, DrawerContent, DrawerHeader, DrawerOverlay, Flex, FormControl, FormLabel, Input, VStack, background, useColorModeValue, useDisclosure, useToast } from "@chakra-ui/react"
import { MultiSelect } from "chakra-multiselect"
import { useState } from "react"
import { toTitleCase } from "../utils/StringUtil";
import { createOutfit, editOutfitImages } from "../backend/Application";
import GarmentSelector from "../components/GarmentSelector";
import { useTags } from "../contexts/TagsContext";
import FileUploadInput from "../components/FileUploadInput";
import { uploadImages } from "../backend/FileService";

export default function CreateOutfit({ handleCreateOutfit, defaultName = "" }) {
	const { tags } = useTags();

	const defaultFormValues = {
		outfitName: defaultName,
		outfitDesc: "",
		tags: [],
		garments: new Set()
	}

	const { isOpen, onOpen, onClose } = useDisclosure()
	const [formValues, setFormValues] = useState({ ...defaultFormValues })
	const [filesToUpload, setFilesToUpload] = useState([]);

	const toast = useToast();

	const handleOpen = () => {
		setFormValues({ ...defaultFormValues })
		setFilesToUpload([]);
		onOpen();
	}

	const handleChange = (e) => {
		if (e.target.name === "outfitName" && e.target.value === "") {
			setFormValues({
				...formValues,
				[e.target.name]: defaultFormValues.outfitName
			})
		}
		else {
			setFormValues({
				...formValues,
				[e.target.name]: e.target.value
			})
		}
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

	const handleSubmit = async (e) => {
		e.preventDefault()

		try {
			const createdOutfit = await createOutfit(formValues.outfitName, formValues.outfitDesc, formValues.tags.map((tag) => parseInt(tag.value)), Array.from(formValues.garments))
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
						throw new Error(message);
					}
					return await response.json();
				});

			if (filesToUpload.length > 0) {
				await uploadImages(filesToUpload)
					.then(async (response) => {
						if (!response.ok) {
							const contentType = response.headers.get("content-type");
							const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
							toast({
								title: 'Successfully created outfit details but failed uploading images.',
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
					})
					.then(async (res) => {
						await editOutfitImages(createdOutfit.outfitId, res.map((image) => image.fileId), [])
							.then(async (response) => {
								if (!response.ok) {
									const contentType = response.headers.get("content-type");
									const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
									toast({
										title: 'Successfully created outfit details but failed uploading images.',
										description: message,
										status: 'error',
										duration: 5000,
										isClosable: true,
									})
									throw new Error(message);
								}
							})
					})
			}

			toast({
				title: 'Outfit created.',
				status: 'success',
				duration: 5000,
				isClosable: true,
			})
			handleCreateOutfit();
			onClose();
		} catch (error) {
			console.error(error);
		}
	}

	return (
		<>
			<Button colorScheme="teal" onClick={handleOpen}>Create outfit</Button>
			<Drawer placement="bottom" onClose={onClose} isOpen={isOpen} size="full">
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
										<Input placeholder={defaultName} name="outfitName" type="text" />
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
								<GarmentSelector handleGarmentSelect={handleGarmentSelect} selectedGarments={formValues.garments} />
								<FormControl>
									<FormLabel>Images</FormLabel>
									<FileUploadInput name="images" multiple accept=".png, .jpg, .jpeg" handleFileChange={handleUploadFileChange} />
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