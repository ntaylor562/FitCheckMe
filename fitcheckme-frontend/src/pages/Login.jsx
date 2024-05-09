import {
	Flex,
	Box,
	FormControl,
	FormLabel,
	Input,
	Checkbox,
	Stack,
	Button,
	Heading,
	Text,
	useColorModeValue,
} from '@chakra-ui/react'
import { useAuth } from '../backend/AuthContext';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Login() {
	const { isAuthenticated, login } = useAuth();
	const [username, setUsername] = useState('');
	const [password, setPassword] = useState('');
	const navigate = useNavigate();

	useEffect(() => {
		if(isAuthenticated) navigate('/');
	})

	const handleInputChange = (event) => {
		if (event.target.name === 'username') setUsername(event.target.value);
		else if (event.target.name === 'password') setPassword(event.target.value);
	}

	const handleSubmit = async (event) => {
		event.preventDefault();
		await login(username, password);
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
						<Stack spacing={4}>
							<FormControl>
								<FormLabel>Username/Email Address</FormLabel>
								<Input name="username" />
							</FormControl>
							<FormControl>
								<FormLabel>Password</FormLabel>
								<Input name="password" type="password" />
							</FormControl>
							<Stack spacing={10}>
								<Stack
									direction={{ base: 'column', sm: 'row' }}
									align={'start'}
									justify={'space-between'}>
									<Checkbox>Remember me</Checkbox>
									{/* <Text color={'blue.400'}>Forgot password?</Text> */}
								</Stack>
								<Button
									type='submit'
									bg={'blue.400'}
									color={'white'}
									_hover={{
										bg: 'blue.500',
									}}>
									Sign in
								</Button>
							</Stack>
						</Stack>
					</form>
				</Box>
			</Stack>
		</Flex>
	)
}