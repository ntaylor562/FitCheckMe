import { Heading, Text, Image, Box } from '@chakra-ui/react'

export default function Profile({ user }) {
	return (
		<div className="page profile-page">
			<Image src='https://placehold.co/500x500' borderRadius='full' boxSize='150px' alt='profile-picture' />
			<Heading paddingBottom='10px'>{user.username}'s Profile</Heading>
			<Text paddingBottom='10px'>Bio: {user.bio}</Text>
			<Box w='100%' border='solid 1px'>placeholder content</Box>
		</div>
	)
}