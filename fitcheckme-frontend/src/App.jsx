import { useState } from 'react'
import "./components/ThemeToggle"
import './App.css'
import ThemeToggle from './components/ThemeToggle'
import Profile from './pages/Profile'
import { Stack } from '@chakra-ui/react'
import OutfitCard from './components/OutfitCard'

function App() {
	const user = {
		userId: 1,
		username: "bender",
		bio: "test bio"
	}
	const outfit = {
		outfitId: 1,
		outfitName: "test outfit",
		outfitDesc: "test outfit description",
		creationDate: new Date().toISOString(),
		outfitTags: [],
		garments: []
	}

	return (
		<>
			<ThemeToggle />
			{/* <Profile user={user} /> */}
			<OutfitCard outfit={outfit} />
		</>
	)
}

export default App
