import { Box, Divider, Heading, StackDivider, VStack } from "@chakra-ui/react"
import UpdateUsernameForm from "./UpdateUsernameForm";
import UpdatePasswordForm from "./UpdatePasswordForm";


export default function AccountSettings() {

	return (
		<Box className="subpage">
			<Heading as="h1" size="xl">Account Settings</Heading>
			<br />
			<Divider />
			<br />
			<VStack divider={<StackDivider />} alignItems="baseline" spacing={10}>
				<UpdateUsernameForm />
				<UpdatePasswordForm />
			</VStack>
		</Box>
	)
}