import { auth_refresh } from "./Auth";

export async function handleFetchException(response) {
	if (response.ok) return response;

	console.error(response);
	return response;
}