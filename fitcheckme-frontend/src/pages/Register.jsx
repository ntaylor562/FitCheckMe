import {
	Flex,
	Box,
	FormControl,
	FormLabel,
	Input,
	InputGroup,
	HStack,
	InputRightElement,
	Stack,
	Button,
	Heading,
	Text,
	useColorModeValue,
	Link,
	FormErrorMessage,
} from '@chakra-ui/react'
import { ViewIcon, ViewOffIcon } from '@chakra-ui/icons'
import { useEffect, useState } from 'react'
import { useAuth } from '../backend/AuthContext';
import { useNavigate } from 'react-router-dom';
import { auth_register } from '../backend/Auth';

export default function Register() {
	const { isAuthenticated, login } = useAuth();
	const [showPassword, setShowPassword] = useState(false);
	const [formValues, setFormValues] = useState({
		username: '',
		email: '',
		password: ''
	});
	const [formError, setFormError] = useState(null);
	const navigate = useNavigate();

	useEffect(() => {
		if (isAuthenticated) navigate('/');
	})

	const handleInputChange = (event) => {
		setFormError(null);
		setFormValues({
			...formValues,
			[event.target.name]: event.target.value
		});
	}

	const handleSubmit = async (event) => {
		event.preventDefault();
		await auth_register(formValues.username, formValues.email, formValues.password)
			.then(async (response) => {
				if (!response.ok) {
					throw new Error(`Failed to register: ${await response.text()}`);
				}
				return response;
			})
			.then(async () => {
				await login(formValues.username, formValues.password)
					.then(() => {
						navigate('/');
					})
					.catch((error) => {
						//TODO popup error message
						console.error(error);
						setFormError(error.message);
					})
			})
			.catch((error) => {
				//TODO popup error message
				console.error(error);
				setFormError(error.message);
			});
	}

	return (
		<Flex
			minH={'100%'}
			align={'center'}
			justify={'center'}>
			<Stack spacing={8} mx={'auto'} w={'md'} py={12} px={6}>
				<Stack align={'center'}>
					<Heading fontSize={'4xl'} textAlign={'center'}>
						Register
					</Heading>
				</Stack>
				<Box
					rounded={'lg'}
					bg={useColorModeValue('white', 'gray.700')}
					boxShadow={'lg'}
					p={8}>
					<form onSubmit={handleSubmit} onChange={handleInputChange}>
						<FormControl isInvalid={formError}>
							<Stack spacing={4}>
								<FormControl isRequired>
									<FormLabel>Username</FormLabel>
									<Input name="username" type="username" />
								</FormControl>
								<FormControl isRequired>
									<FormLabel>Email address</FormLabel>
									<Input name="email" type="email" />
								</FormControl>
								<FormControl isRequired>
									<FormLabel>Password</FormLabel>
									<InputGroup>
										<Input name="password" type={showPassword ? 'text' : 'password'} />
										<InputRightElement h={'full'}>
											<Button
												variant={'ghost'}
												onClick={() => setShowPassword((showPassword) => !showPassword)}>
												{showPassword ? <ViewIcon /> : <ViewOffIcon />
												}
											</Button>
										</InputRightElement>
									</InputGroup>
								</FormControl>
								<Stack spacing={10} pt={2}>
									<Button
										type="submit"
										loadingText="Submitting"
										size="lg"
										bg={'blue.400'}
										color={'white'}
										_hover={{
											bg: 'blue.500',
										}}>
										Sign up
									</Button>
								</Stack>
								<FormErrorMessage>{formError}</FormErrorMessage>
								<Stack pt={6}>
									<Text align={'center'}>
										Already a user? <Link color={'blue.400'} href='/login'>Login</Link>
									</Text>
								</Stack>
							</Stack>
						</FormControl>
					</form>
				</Box>
			</Stack>
		</Flex>
	)
}