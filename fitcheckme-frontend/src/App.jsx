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
import { Box } from "@chakra-ui/react";
import UserSettingsSideBar from "./pages/UserSettings/UserSettingsSideBar";
import AccountSettings from "./pages/UserSettings/AccountSettings";
import AppSettings from "./pages/UserSettings/AppSettings";


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
		},
		{
			path: "/settings",
			name: "Settings"
		}
	]

	const settingsRoutes = [
		{
			path: "/settings/account",
			name: "Account"
		},
		{
			path: "/settings/app",
			name: "App"
		}
	]

	//TODO set up react router to require auth on the required pages (like profile)
	return (
		<Box>
			<Routes>
				<Route path="/" element={<NavBar routes={routes} />}>
					<Route index element={<Home />} />
					<Route path="login" element={<Login />} />
					<Route path="register" element={<Register />} />
					<Route path="profile" element={<Profile user={currentUser} isCurrentUser={true} />} />
					<Route path="/settings" element={<UserSettingsSideBar routes={settingsRoutes} />}>
						<Route index element={<AccountSettings />} />
						<Route index path="account" element={<AccountSettings />} />
						<Route path="app" element={<AppSettings />} />
						<Route path="*" element={<NotFoundPage />} />
					</Route>
					<Route path="testing" element={<Testing />} />

					<Route path="*" element={<NotFoundPage />} />
				</Route>
			</Routes>
		</Box>
	)
}

export default App
