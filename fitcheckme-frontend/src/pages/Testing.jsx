import { Box, Button, Flex, Heading, HStack, Text, VStack } from "@chakra-ui/react"
import CreateOutfit from "./CreateOutfit";
import { auth_refresh } from "../backend/Auth";
import { useAuth } from "../contexts/AuthContext";
import { TagsProvider } from "../contexts/TagsContext";
import { useEffect, useState } from "react";
import { getUserGarments, getUserOutfits } from "../backend/Application";
import GarmentCard from "../components/GarmentCard";
import OutfitCard from "../components/OutfitCard";
import CreateGarment from "../components/CreateGarment";


export default function Testing() {
	const { isAuthenticated, isLoading, currentUser, login, logout } = useAuth();
	const [userOutfits, setUserOutfits] = useState(null);
	const [userGarments, setUserGarments] = useState(null);

	useEffect(() => {
		if (isAuthenticated) {
			if (userOutfits === null) {
				fetchUserOutfits();
			}
			if (userGarments === null) {
				fetchUserGarments();
			}
		}

	}, [isAuthenticated])

	const fetchUserOutfits = async () => {
		setUserOutfits(await getUserOutfits());
	}

	const fetchUserGarments = async () => {
		setUserGarments(await getUserGarments());
	}

	return (
		<Box className="subpage">
			<TagsProvider>
				<Heading>Testing</Heading>
				<Text>This is a test page.</Text>
				{isAuthenticated && currentUser && <Text>Logged in as {currentUser.username}</Text>}
				<VStack alignItems="baseline" spacing={4}>
					{
						isLoading ? <Text>Loading...</Text> :
							<>
								<HStack spacing={4}>
									{isAuthenticated ?
										<>
											<Button colorScheme="red" onClick={async () => { logout() }}>Logout</Button>
											{userOutfits !== null && <CreateOutfit handleCreateOutfit={fetchUserOutfits} handleCreateGarment={fetchUserGarments} defaultName={`Outfit ${userOutfits.length + 1}`} />}
											{userGarments !== null && <CreateGarment handleCreateGarment={fetchUserGarments} defaultName={`Garment ${userGarments.length + 1}`} />}
										</> :
										<>
											<Button colorScheme="green" onClick={async () => { login('test', 'test') }}>Login</Button>
										</>
									}

								</HStack>
								{isAuthenticated && <>
									<Heading size="lg">Garments:</Heading>
									<Flex flexWrap="wrap">
										{userGarments !== null && userGarments.map((garment) => <Box key={garment.garmentId} m="10px"><GarmentCard garment={garment} handleGarmentUpdate={fetchUserGarments} isOwner /></Box>)}
									</Flex>

									<Heading size="lg">Outfits:</Heading>
									<Flex flexWrap="wrap">
										{userOutfits !== null && userOutfits.map((outfit) => <Box key={outfit.outfitId} m="10px"><OutfitCard outfit={outfit} handleOutfitUpdate={fetchUserOutfits} handleCreateGarment={fetchUserGarments} isOwner /></Box>)}
									</Flex>
								</>}
							</>
					}
					<Button colorScheme="gray" onClick={() => auth_refresh()}>Refresh token</Button>
				</VStack>
			</TagsProvider>
		</Box>
	)
}