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
		return await auth_login(username, password).then((response) => {
			if (response.ok) setAuthenticated(true);
			return response;
		});
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