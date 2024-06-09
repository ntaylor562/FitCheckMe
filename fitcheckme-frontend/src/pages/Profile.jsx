import { CheckIcon, CloseIcon, EditIcon } from '@chakra-ui/icons'
import { Heading, Text, Image, Box, HStack, Button, Flex, Input, InputGroup, InputRightAddon, useToast, Avatar, useDisclosure } from '@chakra-ui/react'
import { useState } from 'react';
import { editUser } from '../backend/Application';
import { useAuth } from '../contexts/AuthContext';
import { getImageSource } from '../utils/SourceGetter';
import HoverOverlay from '../components/HoverOverlay';
import UploadProfilePicture from '../components/UploadProfilePicture';

export default function Profile({ user, isCurrentUser }) {
	const [editingBio, setEditingBio] = useState(false);
	const [newBio, setNewBio] = useState((user !== null && user.bio !== null) ? user.bio : "");
	const { setCurrentUser } = isCurrentUser ? useAuth() : { setCurrentUser: () => { } };
	const { isOpen: isUploadPfpOpen, onOpen: onOpenUploadPfp, onClose: onCloseUploadPfp } = useDisclosure();
	const toast = useToast();

	if (user === null) return isCurrentUser ? <div /> : <Text>Loading...</Text>

	const handleBioChange = (event) => {
		setNewBio(event.target.value);
	}

	const handleCancelUpdateBio = () => {
		setNewBio(user.bio);
		setEditingBio(false);
	}

	const handleUpdateBio = async (e) => {
		e.preventDefault();
		if (!isCurrentUser) return;

		return await editUser(user.userId, null, newBio)
			.then(async (response) => {
				if (!response.ok) {
					throw new Error(`Failed to update bio: ${(await response.json()).message}`);
				}
				else {
					setEditingBio(false);
					setCurrentUser({ ...user, bio: newBio });
					toast({
						title: "Bio updated",
						status: "success",
						duration: 3000,
						isClosable: true
					});
				}
				return response;
			})
			.catch((error) => {
				console.error(error);
				toast({
					title: "Error updating bio",
					description: error.message,
					status: "error",
					duration: 5000,
					isClosable: true
				});
			});
	}

	const handleUpdateProfilePicture = async (userReturnDTO) => {
		setCurrentUser(userReturnDTO);
	}

	return (
		<div className="subpage profile-page">
			{isCurrentUser && <>
				<HoverOverlay
					subject={<Avatar size="2xl" boxSize="150px" src={user.profilePicture !== null && getImageSource(user.profilePicture.fileName)} />}
					overlay={
						<Box boxSize="full" alignContent="center">
							<Button onClick={onOpenUploadPfp} variant="ghost" position="relative" boxSize="full" borderRadius="full">
								<EditIcon position="absolute" boxSize="15%" />
							</Button>
						</Box>
					}
				/>
				<UploadProfilePicture isOpen={isUploadPfpOpen} handleClose={onCloseUploadPfp} handlePictureUploaded={handleUpdateProfilePicture} />
			</>}

			<Heading paddingBottom='10px'>{user.username}'s Profile</Heading>
			<HStack spacing={2} alignItems="center" paddingBottom="10px">
				{isCurrentUser && <>
					{!editingBio && <Button pos="relative" onClick={() => setEditingBio(!editingBio)}><EditIcon pos="absolute" /></Button>}
					{editingBio ?
						<form style={{ width: "100%" }} onSubmit={handleUpdateBio}>
							<InputGroup>
								<Input autoFocus value={newBio} onChange={handleBioChange} onKeyDown={(e) => e.key === "Escape" && handleCancelUpdateBio()} />
								<InputRightAddon p="0px" borderRadius="0px">
									<Button onClick={handleCancelUpdateBio} colorScheme="blackAlpha" borderRadius="0px">
										<CloseIcon />
									</Button>
								</InputRightAddon>
								<InputRightAddon p="0px">
									<Button type="submit" colorScheme="green" borderLeftRadius="0px">
										<CheckIcon />
									</Button>
								</InputRightAddon>
							</InputGroup>
						</form> :
						(user.bio !== null ? <Text>Bio: {user.bio}</Text> : <Text>User has not set their bio</Text>)
					}
				</>
				}
			</HStack>
			<Box w='100%' border='solid 1px'>placeholder content</Box>
		</div>
	)
}