import { getEnvVariable } from "../utils/Env";
import FetchWithRefreshRetry from "./FetchWithRefreshRetry";

export async function auth_login(username, password) {
	return await fetch(`${getEnvVariable("BACKEND_URL")}/api/auth/login`, {
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
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/auth/logout`, {
		method: 'POST',
		credentials: 'include'
	});
}

export async function auth_refresh() {
	return await fetch(`${getEnvVariable("BACKEND_URL")}/api/auth/refresh`, {
		method: 'POST',
		credentials: 'include'
	});
}

export async function auth_register(username, email, password) {
	return await fetch(`${getEnvVariable("BACKEND_URL")}/api/user/create`, {
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