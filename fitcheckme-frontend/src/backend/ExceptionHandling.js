import { auth_refresh } from "./Auth";

export async function handleFetchException(response) {
	if (response.ok) return response;

	if (response.status === 401 || response.status === 403) {
		window.location.href = `${import.meta.env.VITE_FRONTEND_URL}/login`;
		return response;
	}

	const contentType = response.headers.get("content-type");
	const message = contentType && contentType.includes("application/json") ? (await response.json()).message : await response.text();

	console.error(message);
	return response;
}