import { List, ListItem } from "@chakra-ui/react"
import { Link, Outlet } from "react-router-dom"


export default function TempNavigation({ routes }) {
	return (
		<>
			<List>
				{routes.map((route) => {return <ListItem key={route.path}><Link to={route.path}>{route.name}</Link></ListItem>})}
			</List>
			<Outlet />
		</>
	)
}