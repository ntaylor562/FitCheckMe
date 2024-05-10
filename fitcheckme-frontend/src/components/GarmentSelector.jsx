import { Box, Card, Checkbox, FormControl, FormLabel, HStack, Input, VStack } from "@chakra-ui/react";
import { MultiSelect } from "chakra-multiselect";
import { useState } from "react";
import { toTitleCase } from "../utils/StringUtil";
import { useTags } from "../contexts/TagsContext";
import CreateGarment from "./CreateGarment";


export default function GarmentSelector({ selectedGarments, handleGarmentSelect }) {
	const defaultFormValues = {
		search: "",
		tags: [],
		garments: new Set()
	}

	const tempGarments = [
		{
			garmentId: 1,
			garmentName: "Blue Shirt",
			userId: 1,
			urls: ["https://www.google.com", "https://www.youtube.com"],
			garmentTags: [
				{
					tagId: 1,
					tagName: "blue"
				},
				{
					tagId: 2,
					tagName: "shirt"
				}
			]
		},
		{
			garmentId: 2,
			garmentName: "Green Shirt",
			userId: 1,
			urls: ["https://www.google.com", "https://www.youtube.com"],
			garmentTags: [
				{
					tagId: 1,
					tagName: "green"
				},
				{
					tagId: 2,
					tagName: "shirt"
				}
			]
		},
		{
			garmentId: 3,
			garmentName: "Green Pants",
			userId: 1,
			urls: ["https://www.google.com", "https://www.youtube.com"],
			garmentTags: [
				{
					tagId: 1,
					tagName: "green"
				},
				{
					tagId: 2,
					tagName: "pants"
				}
			]
		}
	]

	const [userGarments, setUserGarments] = useState([...tempGarments]);
	const [formValues, setFormValues] = useState({ ...defaultFormValues });
	const { tags } = useTags();

	const handleMultiSelectChange = (e) => {
		setFormValues({
			...formValues,
			tags: e
		})
	}

	const handleSearchChange = (e) => {
		setFormValues({
			...formValues,
			search: e.target.value
		})
	}

	return <FormControl>
		<FormLabel>Garments</FormLabel>
		<VStack w="100%" spacing={4}>
			<Input name="search" value={formValues.search} onChange={handleSearchChange} placeholder="Search for garments" />
			<FormControl>
				<MultiSelect
					name="tags"
					options={tags.map((tag) => { return { value: tag.tagName, label: toTitleCase(tag.tagName) } })}
					value={formValues.tags}
					onChange={handleMultiSelectChange}
					placeholder='Select tags'
				/>
			</FormControl>
			<HStack w="100%" wrap="wrap">
				{userGarments.map((garment) => {
					return <GarmentCard key={garment.garmentId} garment={garment} selected={selectedGarments.has(garment.garmentId)} handleGarmentSelect={handleGarmentSelect} />
				})}
			</HStack>
			<CreateGarment />
		</VStack>
	</FormControl>

}

function GarmentCard({ garment, selected, handleGarmentSelect }) {
	return <Card _hover={{ cursor: "pointer" }} p="10px" position="relative">
		<Box boxSize="100px" onClick={() => handleGarmentSelect(garment.garmentId)}>{garment.garmentName}</Box>
		<Checkbox pointerEvents="none" position="absolute" bottom="10px" right="10px" onChange={() => handleGarmentSelect(garment.garmentId)} isChecked={selected} />
	</Card>
}
