import { createContext, useContext, useEffect, useState } from "react";
import { auth_login, auth_logout } from "./Auth";
import FetchWithRefreshRetry from "./FetchWithRefreshRetry";


const AuthContext = createContext({
	isAuthenticated: false,
	isLoading: true,
	setAuthenticated: () => { }
});

export const AuthProvider = ({ children }) => {
	const [isAuthenticated, setAuthenticated] = useState(false);
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
		await auth_login(username, password);
		setAuthenticated(true);
	}

	const logout = async () => {
		await auth_logout();
		setAuthenticated(false);
	}

	return (
		<AuthContext.Provider
			value={{
				isAuthenticated,
				isLoading,
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