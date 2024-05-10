import { createContext, useContext, useEffect, useState } from "react";
import { auth_login, auth_logout } from "../backend/Auth";
import FetchWithRefreshRetry from "../backend/FetchWithRefreshRetry";


const AuthContext = createContext({
	isAuthenticated: false,
	isLoading: true,
	currentUser: null,
	login: () => { },
	logout: () => { }
});

export const AuthProvider = ({ children }) => {
	const [isAuthenticated, setAuthenticated] = useState(false);
	const [currentUser, setCurrentUser] = useState(null);
	const [isLoading, setLoading] = useState(true);
	useEffect(() => {
		const initializeAuth = async () => {
			const response = await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/auth/isAuthenticated`, {method: 'GET', credentials: 'include'});
			setAuthenticated(response.status === 200);
			setLoading(false);
		};
		initializeAuth();
	}, []);

	const login = async (username, password) => {
		return await auth_login(username, password).then((response) => {
			if (response.ok) setAuthenticated(true);
			return response;
		})
		.then(async (response) => {
			const res = response.clone();
			if (response.ok) 
				setCurrentUser(await response.json());
			return res;
		})
	}

	const logout = async () => {
		return await auth_logout().then(async (response) => {
			if (response.ok) setAuthenticated(false);
			return response;
		});
	}

	return (
		<AuthContext.Provider
			value={{
				isAuthenticated,
				isLoading,
				currentUser,
				login,
				logout
			}}
		>
			{children}
		</AuthContext.Provider>
	);
};

export function useAuth() {
	const context = useContext(AuthContext);
	if (context === undefined) {
		throw new Error('useAuth must be used within an AuthProvider');
	}
	return context;
}

export function useIsAuthenticated() {
	const context = useAuth();
	return context.isAuthenticated;
}