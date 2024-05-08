import { Box, Button, Heading, HStack, Text } from "@chakra-ui/react"
import { login } from "../backend/Auth"
import { getGarments } from "../backend/Application"


export default function Testing() {
	return (
		<Box>
			<Heading>Testing</Heading>
			<Text>This is a test page.</Text>
			<HStack spacing={4}>
				<Button colorScheme="green" onClick={async () => { login('test', 'test') }}>Test login</Button>
				<Button colorScheme="blue" onClick={async () => { getGarments() }}>Test get garments</Button>
			</HStack>
		</Box>
	)
}