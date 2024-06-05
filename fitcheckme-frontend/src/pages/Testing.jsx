import { Box, Button, Heading, HStack, Text, VStack } from "@chakra-ui/react"
import CreateOutfit from "./CreateOutfit";
import { auth_refresh } from "../backend/Auth";
import { useAuth } from "../contexts/AuthContext";
import { TagsProvider } from "../contexts/TagsContext";
import EditOutfit from "../components/EditOutfit";
import { useEffect, useState } from "react";
import { getUserOutfits } from "../backend/Application";


export default function Testing() {
	const { isAuthenticated, isLoading, currentUser, login, logout } = useAuth();
	const [userOutfits, setUserOutfits] = useState(null);

	useEffect(() => {
		if (isAuthenticated && userOutfits === null) {
			fetchUserOutfits();
		}
	}, [isAuthenticated])

	const fetchUserOutfits = async () => {
		setUserOutfits(await getUserOutfits());
	}

	const handleCreateOutfit = async () => {
		await fetchUserOutfits();
	}

	return (
		<Box>
			<TagsProvider>
				<Heading>Testing</Heading>
				<Text>This is a test page.</Text>
				{isAuthenticated && currentUser && <Text>Logged in as {currentUser.username}</Text>}
				<VStack alignItems="baseline" spacing={4}>
					{
						isLoading ? <Text>Loading...</Text> :
							<>
								<HStack spacing={4}>
									{!isAuthenticated && <Button colorScheme="green" onClick={async () => { login('test', 'test') }}>Login</Button>}
									{isAuthenticated && <Button colorScheme="red" onClick={async () => { logout() }}>Logout</Button>}
									{isAuthenticated && <CreateOutfit handleCreateOutfit={handleCreateOutfit} numExistingOutfits={userOutfits !== null ? userOutfits.length : 0} />}
								</HStack>
								{isAuthenticated && userOutfits !== null && userOutfits.length > 0 && userOutfits.map((outfit) => <EditOutfit key={outfit.outfitId} outfit={outfit} handleOutfitUpdate={fetchUserOutfits} />)}
							</>
					}
					<Button colorScheme="gray" onClick={() => auth_refresh()}>Refresh token</Button>
				</VStack>
			</TagsProvider>
		</Box>
	)
}