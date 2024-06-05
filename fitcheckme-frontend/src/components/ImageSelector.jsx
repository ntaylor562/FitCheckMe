import { Box, Card, Checkbox, FormControl, HStack, Image, VStack } from "@chakra-ui/react";
import { getImageSource } from "../utils/SourceGetter";


export default function ImageSelector({ images, selectedImages, handleImageSelect }) {
	return (
		<FormControl>
			<VStack w="100%" spacing={4}>
				{images !== null && images.size > 0 ?
					<HStack w="100%" wrap="wrap">
						{Array.from(images).map((image) => {
							return <ImageCard key={image.fileId} image={image} selected={selectedImages.has(image)} handleImageSelect={handleImageSelect} />
						})}
					</HStack>
					: <></>
				}
			</VStack>
		</FormControl>
	)

}

function ImageCard({ image, selected, handleImageSelect }) {
	return (
		<Card _hover={{ cursor: "pointer" }} position="relative">
			<Box boxSize="100px" onClick={() => handleImageSelect(image)}>
				<Image borderRadius="lg" src={getImageSource(image.fileName)} alt='image' />
			</Box>
			<Checkbox pointerEvents="none" position="absolute" bottom="10px" right="10px" onChange={() => handleImageSelect(image)} isChecked={selected} />
		</Card>
	)
}
