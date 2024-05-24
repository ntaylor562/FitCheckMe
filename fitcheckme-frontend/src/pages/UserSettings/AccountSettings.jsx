import { Box, FormControl, FormLabel, Heading, Input } from "@chakra-ui/react";


export default function AccountSettings() {
	return (
		<Box className="subpage">
			<Heading as="h1" size="lg">Account Settings</Heading>
			<form>
				<FormControl>
					<FormLabel>Username</FormLabel>
					<Input type="text" />
				</FormControl>
			</form>
		</Box>
	)
}