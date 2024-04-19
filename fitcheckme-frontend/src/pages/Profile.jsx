import { Heading, Text, Image, Box } from '@chakra-ui/react'

export default function Profile({ user }) {
	return (
		<div className="page profile-page">
			<Image src='' borderRadius='full' boxSize='150px' alt='profile-picture' fallback={<div className='placeholder-image' style={{width: '150px', height: '150px', borderRadius: '50%'}}>placeholder</div>} />
			<Heading paddingBottom='10px'>{user.username}'s Profile</Heading>
			<Text paddingBottom='10px'>Bio: {user.bio}</Text>
			<Box w='100%' border='solid 1px'>placeholder content</Box>
		</div>
	)
}