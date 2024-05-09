import { Box, Button, Heading, HStack, Text } from "@chakra-ui/react"
import { getGarments } from "../backend/Application"
import { useState } from "react";
import { useAuth } from "../backend/AuthContext";
import CreateOutfit from "./CreateOutfit";


export default function Testing() {
	const { isAuthenticated, isLoading, login, logout } = useAuth();

	return (
		<Box>
			<Heading>Testing</Heading>
			<Text>This is a test page.</Text>
			{
				isLoading ? <Text>Loading...</Text> :
					<HStack spacing={4}>
						{isAuthenticated}
						{!isAuthenticated && <Button colorScheme="green" onClick={async () => { login('test', 'test') }}>Login</Button>}
						<Button colorScheme="blue" onClick={async () => { getGarments() }}>Test get garments</Button>
						{isAuthenticated && <Button colorScheme="red" onClick={async () => { logout() }}>Logout</Button>}
						{isAuthenticated && <CreateOutfit />}
					</HStack>
			}
		</Box>
	)
}