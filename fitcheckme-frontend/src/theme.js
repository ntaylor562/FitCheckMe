import { extendTheme } from '@chakra-ui/react'
import { MultiSelectTheme } from 'chakra-multiselect'


// 3. extend the theme
const theme = extendTheme({
	config: {
		initialColorMode: 'system',
		useSystemColorMode: true,
	},
	components: {
		MultiSelect: MultiSelectTheme
	}
})

export default theme