import { Button, FormControl, FormErrorMessage, FormLabel, Grid, GridItem, Heading, Input, useToast } from "@chakra-ui/react";
import { useState } from "react";
import { updatePassword } from "../../backend/Application";
import { useAuth } from "../../contexts/AuthContext";


export default function UpdatePasswordForm() {
	const defaultFormValues = {
		oldPassword: "",
		newPassword: "",
		newPasswordConfirm: "",
	};

	const [formValues, setFormValues] = useState({ ...defaultFormValues });

	const { currentUser } = useAuth();
	const [formError, setFormError] = useState("");

	const toast = useToast();

	const handleFormChange = (e) => {
		const newFormValues = {
			...formValues,
			[e.target.name]: e.target.value,
		}

		setFormValues(newFormValues);

		if (newFormValues.newPassword !== newFormValues.newPasswordConfirm) {
			setFormError("Passwords do not match");
		}
		else {
			setFormError("");
		}
	}

	const handleSubmitPasswordChange = (e) => {
		e.preventDefault();
		if (formError !== "") {
			return;
		}

		updatePassword(currentUser.userId, formValues.oldPassword, formValues.newPassword)
			.then(async (response) => {
				if (!response.ok) {
					const contentType = response.headers.get("content-type");
					const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();
					setFormError(message);
				}
				else {
					toast({
						title: 'Password updated.',
						description: `Your password has been updated.`,
						status: 'success',
						duration: 5000,
						isClosable: true,
					});

					setFormValues({ ...defaultFormValues });
					setFormError("");
				}
			});
	}

	return (
		<form onChange={handleFormChange} onSubmit={handleSubmitPasswordChange}>
			<Heading as="h2" size="lg">Update Password</Heading>
			<br />
			<FormLabel></FormLabel>
			<Grid templateColumns="1fr 2fr" w="40em" gap={6}>
				<GridItem alignContent="center">
					<FormLabel marginBlock={0} w="fit-content">Old password:</FormLabel>
				</GridItem>
				<GridItem alignContent="center">
					<FormControl>
						<Input name="oldPassword" value={formValues.oldPassword} onChange={() => { }} type="password" />
					</FormControl>
				</GridItem>
				<GridItem alignContent="center">
					<FormLabel marginBlock={0} w="fit-content">New password:</FormLabel>
				</GridItem>
				<GridItem alignContent="center">
					<FormControl>
						<Input name="newPassword" value={formValues.newPassword} onChange={() => { }} type="password" />
					</FormControl>
				</GridItem>
				<GridItem alignContent="center">
					<FormLabel marginBlock={0} w="fit-content">Confirm password:</FormLabel>
				</GridItem>
				<GridItem alignContent="center">
					<FormControl isInvalid={formError !== ""}>
						<Input name="newPasswordConfirm" value={formValues.newPasswordConfirm} onChange={() => { }} type="password" />
						<FormErrorMessage>{formError}</FormErrorMessage>
					</FormControl>
				</GridItem>
				<GridItem colSpan={2} display="flex" flexDir={"row-reverse"}>
					<Button isDisabled={formError !== ""} type="submit" colorScheme="blue">Submit</Button>
				</GridItem>
			</Grid>
		</form>
	)
}