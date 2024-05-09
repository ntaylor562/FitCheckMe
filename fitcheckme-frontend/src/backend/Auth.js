
export async function auth_login(username, password) {
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
	return await fetch(`${import.meta.env.VITE_BACKEND_URL}/api/auth/logout`, {
		method: 'POST',
		credentials: 'include'
	});
}

export async function auth_refresh() {
	return await fetch(`${import.meta.env.VITE_BACKEND_URL}/api/auth/refresh`, {
		method: 'POST',
		credentials: 'include'
	});
}