import { Route, Routes } from "react-router-dom";
import './App.css'
import "./components/ThemeToggle"
import Profile from './pages/Profile'
import Home from './pages/Home';
import Testing from './pages/Testing';
import NotFoundPage from './pages/NotFoundPage';
import Login from './pages/Login';
import Register from './pages/Register';
import NavBar from './components/NavBar';


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
			path: "/login",
			name: "Login"
		},
		{
			path: "/register",
			name: "Register"
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
				<Route path="/" element={<NavBar routes={routes} />}>
					<Route index element={<Home />} />
					<Route path="login" element={<Login />} />
					<Route path="register" element={<Register />} />
					<Route path="profile" element={<Profile user={user} />} />
					<Route path="testing" element={<Testing />} />

					<Route path="*" element={<NotFoundPage />} />
				</Route>
			</Routes>
			{/* <Profile user={user} /> */}
			{/* <OutfitCard outfit={outfit} /> */}
		</>
	)
}

export default App
