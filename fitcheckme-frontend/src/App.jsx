import { useState } from 'react'
import "./components/ThemeToggle"
import './App.css'
import ThemeToggle from './components/ThemeToggle'
import Profile from './pages/Profile'
import { Stack } from '@chakra-ui/react'

function App() {
	const user = {
		userId: 1,
		username: "bender",
		bio: "test bio"
	}

	return (
		<>
			<ThemeToggle />
			<Profile user={user} />
		</>
	)
}

export default App
