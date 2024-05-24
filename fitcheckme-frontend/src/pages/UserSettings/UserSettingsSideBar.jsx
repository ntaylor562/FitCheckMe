import { Box, Button, Divider, StackDivider, Text, VStack, useColorModeValue } from "@chakra-ui/react";
import { Link, Outlet } from "react-router-dom";

export default function UserSettingsSideBar({ routes }) {
	return (
		<Box className="page">
			<Box w="20%" alignItems="center" p="10px">
				<Text fontSize="20px" textAlign="center" paddingBlock="10px">Settings</Text>
				<Divider />
				<VStack alignItems="baseline" p="20px" divider={<StackDivider />}>
					{routes.map((route) => (
						<Link key={route.path} to={route.path} style={{width: "100%", height: "100%"}}>
							<Button display="inline" p="10px" w="100%" variant="ghost" colorScheme="gray" textAlign="left" _hover={{ color: "blue.500", bg: useColorModeValue("gray.200", "whiteAlpha.200") }}>
								<Text fontWeight="normal" fontSize="16px">{route.name}</Text>
							</Button>
						</Link>
					))}
				</VStack>
			</Box>
			<Divider orientation="vertical" />
			<Outlet />
		</Box>
	)
}