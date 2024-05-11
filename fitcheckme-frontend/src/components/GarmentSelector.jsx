import { Box, Card, Checkbox, FormControl, FormLabel, HStack, Input, VStack } from "@chakra-ui/react";
import { MultiSelect } from "chakra-multiselect";
import { useEffect, useState } from "react";
import { toTitleCase } from "../utils/StringUtil";
import { useTags } from "../contexts/TagsContext";
import CreateGarment from "./CreateGarment";
import { getUserGarments } from "../backend/Application";


export default function GarmentSelector({ selectedGarments, handleGarmentSelect }) {
	const defaultFormValues = {
		search: "",
		tags: [],
		garments: new Set()
	}

	const [userGarments, setUserGarments] = useState([]);
	const [formValues, setFormValues] = useState({ ...defaultFormValues });
	const { tags } = useTags();

	useEffect(() => {
		fetchGarments();
	}, [])

	const fetchGarments = async () => {
		setUserGarments(await getUserGarments());
	}

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

	//TODO improve search
	const getSearchResults = () => {
		const tagSet = new Set(formValues.tags.map((tag) => parseInt(tag.value)));
		if(formValues.search === "" && tagSet.size === 0) {
			return userGarments;
		}

		const searchLower = formValues.search.toLowerCase();
		return userGarments.filter((garment) => {
			if(formValues.search !== "" && !garment.garmentName.toLowerCase().includes(searchLower)) {
				return false;
			}
			if(tagSet.size > 0 && !garment.garmentTags.some((tag) => tagSet.has(tag.tagId))) {
				return false;
			}
			return true;
		})
	}

	console.log(tags.map((tag) => { return { value: `${tag.tagName}`, label: toTitleCase(tag.tagName) } }));

	return <FormControl>
		<FormLabel>Garments</FormLabel>
		<VStack w="100%" spacing={4}>
			<Input name="search" value={formValues.search} onChange={handleSearchChange} placeholder="Search for garments" />
			<FormControl>
				<MultiSelect
					name="tags"
					options={tags.map((tag) => { return { value: `${tag.tagId}`, label: toTitleCase(tag.tagName) } })}
					value={formValues.tags}
					onChange={handleMultiSelectChange}
					placeholder='Select tags'
				/>
			</FormControl>
			<HStack w="100%" wrap="wrap">
				{getSearchResults().map((garment) => {
					return <GarmentCard key={garment.garmentId} garment={garment} selected={selectedGarments.has(garment.garmentId)} handleGarmentSelect={handleGarmentSelect} />
				})}
			</HStack>
			<CreateGarment addGarment={(garment) => setUserGarments([...userGarments, garment])} />
		</VStack>
	</FormControl>

}

function GarmentCard({ garment, selected, handleGarmentSelect }) {
	return <Card _hover={{ cursor: "pointer" }} p="10px" position="relative">
		<Box boxSize="100px" onClick={() => handleGarmentSelect(garment.garmentId)}>{garment.garmentName}</Box>
		<Checkbox pointerEvents="none" position="absolute" bottom="10px" right="10px" onChange={() => handleGarmentSelect(garment.garmentId)} isChecked={selected} />
	</Card>
}
