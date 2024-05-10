import { Box, Button, Heading, HStack, Text } from "@chakra-ui/react"
import { getGarments } from "../backend/Application"
import { useState } from "react";
import CreateOutfit from "./CreateOutfit";
import { auth_refresh } from "../backend/Auth";
import { useAuth } from "../contexts/AuthContext";
import { TagsProvider } from "../contexts/TagsContext";


export default function Testing() {
	const { isAuthenticated, isLoading, currentUser, login, logout } = useAuth();

	return (
		<Box>
			<TagsProvider>
				<Heading>Testing</Heading>
				<Text>This is a test page.</Text>
				{isAuthenticated && currentUser && <Text>Logged in as {currentUser.username}</Text>}
				{
					isLoading ? <Text>Loading...</Text> :
						<HStack spacing={4}>
							{!isAuthenticated && <Button colorScheme="green" onClick={async () => { login('test', 'test') }}>Login</Button>}
							<Button colorScheme="blue" onClick={async () => { getGarments() }}>Test get garments</Button>
							{isAuthenticated && <Button colorScheme="red" onClick={async () => { logout() }}>Logout</Button>}
							{isAuthenticated && <CreateOutfit />}
						</HStack>
				}
				<Button colorScheme="gray" onClick={() => auth_refresh()}>Refresh token</Button>
			</TagsProvider>
		</Box>
	)
}