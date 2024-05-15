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
import { useAuth } from "./contexts/AuthContext";


function App() {
	const { currentUser } = useAuth();

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

	//TODO set up react router to require auth on the required pages (like profile)
	return (
		<>
			<Routes>
				<Route path="/" element={<NavBar routes={routes} />}>
					<Route index element={<Home />} />
					<Route path="login" element={<Login />} />
					<Route path="register" element={<Register />} />
					<Route path="profile" element={<Profile user={currentUser} isCurrentUser={true} />} />
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
