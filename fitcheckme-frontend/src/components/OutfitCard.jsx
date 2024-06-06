import { Card, CardBody, CardHeader, Heading, Image, Text, VStack } from "@chakra-ui/react";
import { getImageSource } from "../utils/SourceGetter";


export default function OutfitCard({ outfit, size="sm" }) {
	if (!outfit) return null;

	return (
		<Card size={size} variant='outline' borderRadius='3xl'>
			<CardHeader>
				<Heading size='md'>{outfit.outfitName}</Heading>
			</CardHeader>
			<CardBody>
				<VStack spacing={4}>
					{outfit.images.length > 0 && <Image borderRadius='lg' boxSize='300px' src={getImageSource(outfit.images[0].fileName)} alt='outfit-image' />}
					<Text>{outfit.outfitDesc}</Text>
				</VStack>
			</CardBody>
		</Card>
	)
}