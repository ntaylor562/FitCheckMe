import { Card, CardBody, CardHeader, Heading, Image, Text, VStack } from "@chakra-ui/react";
import { getImageSource } from "../utils/SourceGetter";


export default function GarmentCard({ garment, size="sm" }) {
	if (!garment) return null;

	return (
		<Card size={size} variant='outline' borderRadius='3xl'>
			<CardHeader>
				<Heading size='md'>{garment.garmentName}</Heading>
			</CardHeader>
			<CardBody>
				<VStack spacing={4}>
					{garment.images.length > 0 && <Image borderRadius='lg' boxSize='300px' src={getImageSource(garment.images[0].fileName)} alt='garment-image' />}
				</VStack>
			</CardBody>
		</Card>
	)
}