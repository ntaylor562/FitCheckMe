import { Modal, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, ModalOverlay, useDisclosure } from "@chakra-ui/react";


export default function EditGarment({ garment, handleGarmentUpdate, isOpen, handleClose }) {
	<Modal isOpen={isOpen} onClose={handleClose}>
		<ModalOverlay />
		<ModalContent>
			<ModalHeader>Edit Garment</ModalHeader>
			<ModalCloseButton />
			<ModalBody>

			</ModalBody>
			<ModalFooter>

			</ModalFooter>
		</ModalContent>
	</Modal>
}