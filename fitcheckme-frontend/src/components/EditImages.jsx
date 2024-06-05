import { useState } from "react";
import ImageSelector from "./ImageSelector";
import FileUploadInput from "./FileUploadInput";
import { Button, VStack } from "@chakra-ui/react";


export default function EditImages({ images, handleUploadFileChange, handleDeleteImages }) {
	const [selectedImagesToDelete, setSelectedImagesToDelete] = useState(new Set());

	const handleImageSelect = (image) => {
		let newSelectedImagesToDelete = new Set(selectedImagesToDelete);
		if (newSelectedImagesToDelete.has(image)) {
			newSelectedImagesToDelete.delete(image);
		} else {
			newSelectedImagesToDelete.add(image);
		}
		setSelectedImagesToDelete(newSelectedImagesToDelete);
	}

	return (<VStack spacing={4}>
		<FileUploadInput name="images" multiple accept=".png, .jpg, .jpeg" handleFileChange={handleUploadFileChange} />
		<ImageSelector images={images} selectedImages={selectedImagesToDelete} handleImageSelect={handleImageSelect} />
		{selectedImagesToDelete.size > 0 && <Button colorScheme="red" onClick={() => handleDeleteImages(selectedImagesToDelete)}>Remove Selected Images</Button>}
	</VStack>

	)
}