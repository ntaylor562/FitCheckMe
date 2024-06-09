import { Button, FormControl, Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, useToast } from "@chakra-ui/react";
import FileUploadInput from "./FileUploadInput";
import { useState } from "react";
import { uploadImage } from "../backend/FileService";
import { editUser } from "../backend/Application";
import { useAuth } from "../contexts/AuthContext";


export default function UploadProfilePicture({ handlePictureUploaded, isOpen, handleClose }) {
	const { currentUser } = useAuth();

	const [fileToUpload, setFileToUpload] = useState(null);

	const toast = useToast();

	const onClose = () => {
		setFileToUpload(null);
		handleClose();
	}

	const handleFileChange = (event) => {
		setFileToUpload(event.target.files[0]);
	}

	const handleSubmit = async () => {
		try {
			await uploadImage(fileToUpload)
				.then(async (response) => {
					if (!response.ok) {
						const contentType = response.headers.get("content-type");
						const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
						toast({
							title: 'Error uploading image',
							description: message,
							status: 'error',
							duration: 5000,
							isClosable: true,
						})
						throw new Error(message);
					} else {
						return response;
					}
				})
				.then((res) => {
					if (!res.ok) {
						throw new Error("Failed to upload image");
					}
					return res.json();
				})
				.then((image) => image.fileId)
				.then(async (imageId) => {
					const userDTO = await editUser(currentUser.userId, null, null, imageId)
						.then(async (response) => {
							if (!response.ok) {
								toast({
									title: "Error updating profile picture",
									description: (await response.json()).message,
									status: "error",
									duration: 5000,
									isClosable: true
								});
								throw new Error(`Failed to update profile picture: ${(await response.json()).message}`);
							}
							else {
								toast({
									title: "Profile picture updated",
									status: "success",
									duration: 5000,
									isClosable: true
								});
								onClose();
							}
							return await response.json();
						});

					handlePictureUploaded(userDTO);
				});
		}
		catch (error) {
			console.error(error);
		}
	}

	return (
		<Modal isOpen={isOpen} onClose={onClose}>
			<ModalOverlay />
			<ModalContent>
				<ModalHeader>Upload Profile Picture</ModalHeader>
				<ModalBody>
					<ModalCloseButton />
					<FormControl>
						<FileUploadInput accept=".png, .jpg, .jpeg" multiple={false} handleFileChange={handleFileChange} />
					</FormControl>
				</ModalBody>
				<ModalFooter>
					<Button onClick={handleSubmit} colorScheme='green' mr={3}>Upload</Button>
					<Button variant='ghost' onClick={onClose}>Cancel</Button>
				</ModalFooter>
			</ModalContent>
		</Modal>
	)
}