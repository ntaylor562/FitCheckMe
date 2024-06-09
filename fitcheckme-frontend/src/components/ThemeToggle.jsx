import { MoonIcon, SunIcon } from '@chakra-ui/icons'
import { Button, Circle, useColorMode, useColorModeValue } from '@chakra-ui/react'

export default function ThemeToggle() {
	const { colorMode, toggleColorMode } = useColorMode()
	const bgColor = useColorModeValue("gray.300", "gray.600")

	return (
		<Button pos="relative" variant="ghost" borderRadius="full" _hover={{bg: bgColor}} onClick={toggleColorMode}>
			{colorMode === "light" ? <MoonIcon pos="absolute" /> : <SunIcon pos="absolute" />}
		</Button>
	)
}