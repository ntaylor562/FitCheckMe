import { Box, Button, Heading, HStack, Text } from "@chakra-ui/react"
import { login, logout } from "../backend/Auth"
import { getGarments } from "../backend/Application"


export default function Testing() {
	return (
		<Box>
			<Heading>Testing</Heading>
			<Text>This is a test page.</Text>
			<HStack spacing={4}>
				<Button colorScheme="green" onClick={async () => { login('test', 'test') }}>Login</Button>
				<Button colorScheme="blue" onClick={async () => { getGarments() }}>Test get garments</Button>
				<Button colorScheme="red" onClick={async () => { logout() }}>Logout</Button>
			</HStack>
		</Box>
	)
}