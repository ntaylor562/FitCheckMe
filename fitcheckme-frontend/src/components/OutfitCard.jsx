import { Card, CardBody, CardFooter, CardHeader, Heading, Image, Stack, Text, VStack } from "@chakra-ui/react";


export default function OutfitCard({ outfit, size }) {
	if (!outfit) return null;
	return (
		<Card size={size !== null ? size : "sm"} variant='outline' borderRadius='3xl'>
			<CardHeader>
				<Heading size='md'>{outfit.outfitName}</Heading>
			</CardHeader>
			<CardBody>
				<VStack spacing={4}>
					<Image borderRadius='lg' boxSize='300px' src='https://placehold.co/500x500' alt='outfit-image' />
					<Text>{outfit.outfitDesc}</Text>
				</VStack>
			</CardBody>
		</Card>
	)
}