import { Avatar, Box, Flex, HStack, Image, Menu, MenuButton, MenuItem, MenuList, Spacer, Text, useColorModeValue, useToast } from "@chakra-ui/react";
import { Link, Outlet, useNavigate } from "react-router-dom";
import ThemeToggle from "./ThemeToggle";
import { useAuth } from "../contexts/AuthContext";
import FCMLogo from '../assets/fcm_logo.png'
import { getImageSource } from "../utils/SourceGetter";


export default function NavBar({ routes }) {
	const { currentUser, isAuthenticated, logout } = useAuth();
	const backgroundColor = useColorModeValue('gray.200', 'gray.700')
	const navigate = useNavigate();
	const toast = useToast();

	const handleLogout = () => {
		logout().then(() => {
			toast({
				title: 'You have been logged out',
				status: 'success',
				duration: 5000,
				isClosable: true,
			})
			navigate('/');
		})
		.catch((error) => {
			toast({
				title: 'Failed to log out',
				description: error.message,
				status: 'error',
				duration: 5000,
				isClosable: true,
			})
		})
	}

	const getAvatarMenuButton = () => {
		if (isAuthenticated) {
			return <Menu>
				<MenuButton>
					<Avatar src={currentUser !== null && currentUser.profilePicture !== null && getImageSource(currentUser.profilePicture.fileName)} />
				</MenuButton>
				<MenuList>
					<MenuItem onClick={() => navigate("/profile")}>Profile</MenuItem>
					<MenuItem onClick={handleLogout}>Log out</MenuItem>
				</MenuList>
			</Menu>
		} else {
			return <Menu>
				<MenuButton>
					<Avatar />
				</MenuButton>
				<MenuList>
					<MenuItem onClick={() => navigate("/login")}>Log in</MenuItem>
					<MenuItem onClick={() => navigate("/register")}>Sign up</MenuItem>
				</MenuList>
			</Menu>
		}
	}

	return (
		<Box h="full">
			<Flex h="10vh" minWidth="max-content" alignItems="center" paddingBlock="10px" paddingInline="20px" bg={backgroundColor}>
				<HStack spacing={4}>
					<Link to="/"><Image boxSize="50px" objectFit="fill" src={FCMLogo} alt="Logo" borderRadius="full" /></Link>
					{routes.map((route) => <Link key={route.path} to={route.path}><Text _hover={{textDecor: "underline"}}>{route.name}</Text></Link> )}
				</HStack>
				<Spacer />
				<HStack spacing={4}>
					<ThemeToggle />
					{getAvatarMenuButton()}
				</HStack>
			</Flex>
			<Outlet />
		</Box>
	)
}