import FetchWithRefreshRetry from "./FetchWithRefreshRetry";

export async function auth_login(username, password) {
	// @ts-ignore
	return await fetch(`${import.meta.env.VITE_BACKEND_URL}/api/auth/login`, {
		method: 'POST',
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			username: username,
			password: password
		})
	});
}

export async function auth_logout() {
	// @ts-ignore
	return await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/auth/logout`, {
		method: 'POST',
		credentials: 'include'
	});
}

export async function auth_refresh() {
	// @ts-ignore
	return await fetch(`${import.meta.env.VITE_BACKEND_URL}/api/auth/refresh`, {
		method: 'POST',
		credentials: 'include'
	});
}

export async function auth_register(username, email, password) {
	// @ts-ignore
	return await fetch(`${import.meta.env.VITE_BACKEND_URL}/api/user/create`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			username: username,
			email: email,
			password: password
		})
	});
}