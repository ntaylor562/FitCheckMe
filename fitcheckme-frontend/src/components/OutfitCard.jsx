import { Card, CardBody, CardHeader, Heading, Icon, IconButton, Image, Menu, MenuButton, MenuItem, MenuList, Text, VStack, useDisclosure } from "@chakra-ui/react";
import { getImageSource } from "../utils/SourceGetter";
import { IoEllipsisHorizontal } from "react-icons/io5";
import EditOutfit from "./EditOutfit";


export default function OutfitCard({ isOwner = false, outfit, handleOutfitUpdate = () => { }, handleCreateGarment = () => { } }) {
	if (!outfit) return null;

	const { isOpen: isEditOpen, onOpen: onEditOpen, onClose: onEditClose } = useDisclosure();

	return (
		<Card variant='outline' borderRadius='3xl'>
			<EditOutfit outfit={outfit} handleOutfitUpdate={handleOutfitUpdate} handleCreateGarment={handleCreateGarment} isOpen={isEditOpen} handleClose={onEditClose} />
			<Menu isLazy>
				<MenuButton
					size="lg"
					as={IconButton}
					aria-label="Outfit card options"
					icon={<Icon as={IoEllipsisHorizontal} />}
					variant="ghost"
					position="absolute"
					top="5px"
					right="5px"
					borderRadius="full"
				/>
				<MenuList>
					<MenuItem>Some other temp option</MenuItem>
					{isOwner && <MenuItem onClick={onEditOpen}>Edit Outfit</MenuItem>}
				</MenuList>
			</Menu>
			<CardHeader>
				<Heading size='md'>{outfit.outfitName}</Heading>
			</CardHeader>
			<CardBody>
				<VStack spacing={4}>
					{outfit.images.length > 0 && <Image borderRadius='lg' boxSize='300px' src={getImageSource(outfit.images[0].fileName)} alt='outfit-image' />}
					<Text>{outfit.outfitDesc}</Text>
				</VStack>
			</CardBody>
		</Card>
	)
}