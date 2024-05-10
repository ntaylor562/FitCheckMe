import {
	Flex,
	Box,
	FormControl,
	FormLabel,
	Input,
	Stack,
	Button,
	Heading,
	useColorModeValue,
	FormErrorMessage,
} from '@chakra-ui/react'
import { useAuth } from '../contexts/AuthContext';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Login() {
	const { isAuthenticated, login } = useAuth();
	const [username, setUsername] = useState('');
	const [password, setPassword] = useState('');
	const [formError, setFormError] = useState(null);
	const navigate = useNavigate();

	useEffect(() => {
		if (isAuthenticated) navigate('/');
	})

	const handleInputChange = (event) => {
		setFormError(null);
		if (event.target.name === 'username') setUsername(event.target.value);
		else if (event.target.name === 'password') setPassword(event.target.value);
	}

	const handleSubmit = async (event) => {
		event.preventDefault();
		await login(username, password)
			.then(async (response) => {
				if (!response.ok) {
					throw new Error(`Failed to login: ${await response.text()}`);
				}
				return response;
			})
			.then(() => {
				navigate('/');
			})
			.catch((error) => {
				console.error(error);
				setFormError(error.message);
			})
	}

	return (
		<Flex
			minH={'100%'}
			align={'center'}
			justify={'center'}>
			<Stack spacing={8} mx={'auto'} w={'md'} py={12} px={6}>
				<Stack align={'center'}>
					<Heading fontSize={'4xl'}>Sign in</Heading>
				</Stack>
				<Box
					rounded={'lg'}
					bg={useColorModeValue('gray.50', 'gray.700')}
					boxShadow={'xl'}
					p={8}>
					<form onSubmit={handleSubmit} onChange={handleInputChange}>
						<FormControl isInvalid={formError}>
							<Stack spacing={4}>
								<FormControl>
									<FormLabel>Username/Email Address</FormLabel>
									<Input name="username" />
								</FormControl>
								<FormControl>
									<FormLabel>Password</FormLabel>
									<Input name="password" type="password" />
								</FormControl>
								<Flex direction="column">
									<Button
										type='submit'
										bg={'blue.400'}
										color={'white'}
										_hover={{
											bg: 'blue.500',
										}}>
										Sign in
									</Button>
								</Flex>
								<FormErrorMessage>{formError}</FormErrorMessage>
							</Stack>
						</FormControl>
					</form>
				</Box>
			</Stack>
		</Flex>
	)
}