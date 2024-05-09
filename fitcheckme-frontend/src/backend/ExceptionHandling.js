import { auth_refresh } from "./Auth";

export async function handleFetchException(response) {
	if (response.ok) return response;

	if (response.status === 401 || response.status === 403) {
		window.location.href = `${import.meta.env.VITE_FRONTEND_URL}/login`;
		return response;
	}

	console.error(response);
	return response;
}