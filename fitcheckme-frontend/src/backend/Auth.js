

export async function login(username, password) {
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
	})
	.then((response) => console.log(response));
}

export async function logout() {
	return await fetch(`${import.meta.env.VITE_BACKEND_URL}/api/auth/logout`, {
		method: 'POST',
		credentials: 'include'
	})
	.then((response) => console.log(response));
}