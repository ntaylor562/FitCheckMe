import { Button, useColorMode } from '@chakra-ui/react'

export default function ThemeToggle() {
	const { colorMode, toggleColorMode } = useColorMode()

	return (
		<Button size='sm' colorScheme='gray' onClick={toggleColorMode}>Toggle {colorMode === "light" ? "Dark" : "Light"}</Button>
	)
}