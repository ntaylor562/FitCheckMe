import { Box, List, ListItem, useColorModeValue } from "@chakra-ui/react"
import { Link, Outlet } from "react-router-dom"
import ThemeToggle from "./ThemeToggle"
import { useIsAuthenticated } from "../backend/AuthContext"


export default function TempNavigation({ routes }) {
	const isAuthenticated = useIsAuthenticated();
	const authBoxColor = isAuthenticated ? useColorModeValue("green.400", "green") : useColorModeValue("red.400", "red");
	return (
		<>
			<ThemeToggle />
			<Box borderRadius="full" bg={authBoxColor} paddingInline="10px" w="fit-content">{isAuthenticated ? "Logged in" : "Not logged in"}</Box>
			<List>
				{routes.map((route) => {return <ListItem key={route.path}><Link to={route.path}>{route.name}</Link></ListItem>})}
			</List>
			<Outlet />
		</>
	)
}