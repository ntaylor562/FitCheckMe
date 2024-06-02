import { Button, Flex, FormControl, Modal, ModalBody, ModalContent, ModalFooter, ModalHeader, ModalOverlay, Spacer, Text, VStack, useDisclosure, useToast } from "@chakra-ui/react";
import { useAuth } from "../../contexts/AuthContext";
import { deleteAccount } from "../../backend/Application";
import { useNavigate } from "react-router-dom";


export default function DeleteAccountModal() {
	const { isOpen, onClose, onOpen } = useDisclosure();
	const { currentUser, logout } = useAuth();
	const navigate = useNavigate();

	const toast = useToast();

	const handleDeleteAccount = async (e) => {
		e.preventDefault();

		await deleteAccount(currentUser.userId)
		.then(async (response) => {
			if(response.ok) {
				logout();
				navigate('/');
				toast({
					title: 'Account deleted.',
					status: 'success',
					duration: 5000,
					isClosable: true,
				});
			}
			else {
				const contentType = response.headers.get("content-type");
				const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
				toast({
					title: 'Error deleting account.',
					description: message,
					status: 'error',
					duration: 5000,
					isClosable: true,
				});
			}
		})
		onClose();
	}

	return (
		<>
			<Button colorScheme="red" onClick={onOpen}>Delete Account</Button>
			<Modal isOpen={isOpen} onClose={onClose}>
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>Delete Account</ModalHeader>
					<ModalBody>
						<VStack alignItems="baseline" spacing={4}>
							<Text>Are you sure you want to delete your account?</Text>
						</VStack>
					</ModalBody>
					<form onSubmit={handleDeleteAccount}>
						<ModalFooter>
							<FormControl>
								<Flex>
									<Button colorScheme="blue" onClick={onClose}>Cancel</Button>
									<Spacer />
									<Button colorScheme="red" type="submit">Delete Account</Button>
								</Flex>
							</FormControl>
						</ModalFooter>
					</form>
				</ModalContent>
			</Modal>
		</>
	)
}