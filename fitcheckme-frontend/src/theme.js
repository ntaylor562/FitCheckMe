import { extendTheme } from '@chakra-ui/react'


// 3. extend the theme
const theme = extendTheme({
	config: {
		initialColorMode: 'system',
		useSystemColorMode: true,
	}
})

export default theme