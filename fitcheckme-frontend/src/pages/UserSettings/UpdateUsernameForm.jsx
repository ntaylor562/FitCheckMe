import { Button, FormControl, Grid, GridItem, Heading, Input, InputGroup, InputRightAddon, Modal, ModalBody, ModalContent, ModalHeader, ModalOverlay, SimpleGrid, useDisclosure, useToast } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { editUser } from "../../backend/Application";


export default function UpdateUsernameForm() {
	const { currentUser, isLoading, setCurrentUser } = useAuth();
	const [formUsername, setFormUsername] = useState("");
	const { isOpen, onOpen, onClose } = useDisclosure();
	const toast = useToast();

	useEffect(() => {
		if (!isLoading && currentUser)
			setFormUsername(currentUser.username);
	}, [isLoading]);

	const onUsernameChange = (e) => {
		setFormUsername(e.target.value);
	}

	const onUsernameSubmit = (e) => {
		e.preventDefault();
		onOpen();
	}

	const handleUpdateUsername = async () => {
		editUser(currentUser.userId, formUsername, null)
			.then(async (response) => {
				if (!response.ok) {
					const contentType = response.headers.get("content-type");
					const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
					toast({
						title: 'Error updating username.',
						description: message,
						status: 'error',
						duration: 5000,
						isClosable: true,
					});
				}
				else {
					onClose();
					toast({
						title: 'Username updated.',
						description: `Your username has been updated to ${formUsername}.`,
						status: 'success',
						duration: 5000,
						isClosable: true,
					});
					const newCurrentUser = { ...currentUser, username: formUsername };
					setCurrentUser(newCurrentUser);
				}
			});
	}

	return (
		<form onSubmit={onUsernameSubmit}>
			<Heading as="h2" size="lg">Update Username</Heading>
			<br />
			<FormControl>
				<Grid templateColumns="1fr 3fr">
					<GridItem alignContent="center">
						<FormControl w="fit-content">Username:</FormControl>
					</GridItem>
					<GridItem>
						<InputGroup>
							<Input value={formUsername} name="username" w="20em" type="text" onChange={onUsernameChange} />
							<InputRightAddon p="0px">
								<Button isDisabled={currentUser !== null && formUsername === currentUser.username} borderLeftRadius="unset" type="submit" w="100%">Save</Button>
							</InputRightAddon>
						</InputGroup>
					</GridItem>
				</Grid>
				<Modal isOpen={isOpen} onClose={onClose}>
					<ModalOverlay />
					<ModalContent>
						<ModalHeader>Are you sure you want to change your username to {formUsername}?</ModalHeader>
						<ModalBody>
							<SimpleGrid columns={2} spacing={10}>
								<Button colorScheme="red" onClick={() => {
									setFormUsername(currentUser.username);
									onClose();
								}}>Cancel</Button>
								<Button colorScheme="green" onClick={handleUpdateUsername}>Confirm</Button>
							</SimpleGrid>
						</ModalBody>
					</ModalContent>
				</Modal>
			</FormControl>
		</form>
	)
}