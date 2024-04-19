import { Card, CardBody, CardFooter, CardHeader, Heading, Image, Stack, Text } from "@chakra-ui/react";


export default function OutfitCard({ outfit }) {
	if (!outfit) return null;
	return (
		<Card max maxW='sm' variant='outline' borderRadius='3xl'>
			<CardHeader>
				<Heading size='md'>{outfit.outfitName}</Heading>
			</CardHeader>
			<CardBody>
				<Stack spacing={4}>
					<Image borderRadius='lg' boxSize='300px' src='https://placehold.co/500x500' alt='outfit-image' />
					<Text>{outfit.outfitDesc}</Text>
				</Stack>
			</CardBody>
		</Card>
	)
}