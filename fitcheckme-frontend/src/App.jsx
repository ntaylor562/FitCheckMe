import { useState } from 'react'
import { Route, Routes } from "react-router-dom";
import './App.css'
import "./components/ThemeToggle"
import ThemeToggle from './components/ThemeToggle'
import Profile from './pages/Profile'
import { Stack } from '@chakra-ui/react'
import OutfitCard from './components/OutfitCard'
import ErrorPage from './pages/ErrorPage';
import Home from './pages/Home';
import Testing from './pages/Testing';
import NotFoundPage from './pages/NotFoundPage';
import TempNavigation from './components/TempNavigation';

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

	let routes = [
		{
			path: "/",
			name: "Home"
		},
		{
			path: "/profile",
			name: "Profile"
		},
		{
			path: "/testing",
			name: "Testing"
		}
	]

	return (
		<>
			<Routes>
				<Route path="/" element={<TempNavigation routes={routes} />}>
					<Route index element={<Home />} />
					<Route path="profile" element={<Profile user={user}/>} />
					<Route path="testing" element={<Testing />} />

					<Route path="*" element={<NotFoundPage />} />
				</Route>
			</Routes>
			{/* <ThemeToggle /> */}
			{/* <Profile user={user} /> */}
			{/* <OutfitCard outfit={outfit} /> */}
		</>
	)
}

export default App
