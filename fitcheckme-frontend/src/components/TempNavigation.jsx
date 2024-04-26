import { List, ListItem } from "@chakra-ui/react"
import { Link, Outlet } from "react-router-dom"
import ThemeToggle from "./ThemeToggle"


export default function TempNavigation({ routes }) {
	return (
		<>
			<ThemeToggle />
			<List>
				{routes.map((route) => {return <ListItem key={route.path}><Link to={route.path}>{route.name}</Link></ListItem>})}
			</List>
			<Outlet />
		</>
	)
}