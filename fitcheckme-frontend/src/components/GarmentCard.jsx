import { Card, CardBody, CardHeader, Heading, Icon, IconButton, Image, Menu, MenuButton, MenuItem, MenuList, Text, VStack, useDisclosure } from "@chakra-ui/react";
import { IoEllipsisHorizontal } from "react-icons/io5";
import { getImageSource } from "../utils/SourceGetter";
import EditGarment from "./EditGarment";


export default function GarmentCard({ isOwner = false, garment, handleGarmentUpdate = () => { } }) {
	if (!garment) return null;

	const { isOpen: isEditOpen, onOpen: onEditOpen, onClose: onEditClose } = useDisclosure();

	return (
		<Card variant='outline' borderRadius='3xl'>
			<EditGarment garment={garment} handleGarmentUpdate={handleGarmentUpdate} isOpen={isEditOpen} handleClose={onEditClose} />
			<Menu isLazy>
				<MenuButton
					size="lg"
					as={IconButton}
					aria-label="Garment card options"
					icon={<Icon as={IoEllipsisHorizontal} />}
					variant="ghost"
					position="absolute"
					top="5px"
					right="5px"
					borderRadius="full"
				/>
				<MenuList>
					<MenuItem>Some other temp option</MenuItem>
					{isOwner && <MenuItem onClick={onEditOpen}>Edit Garment</MenuItem>}
				</MenuList>
			</Menu>
			<CardHeader>
				<Heading size='md'>{garment.garmentName}</Heading>
			</CardHeader>
			<CardBody>
				<VStack spacing={4}>
					{garment.images.length > 0 && <Image borderRadius='lg' boxSize='300px' src={getImageSource(garment.images[0].fileName)} alt='garment-image' />}
				</VStack>
			</CardBody>
		</Card>
	)
}