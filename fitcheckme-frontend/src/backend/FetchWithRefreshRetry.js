import { auth_refresh } from "./Auth";

export default async function FetchWithRefreshRetry(url, options) {
	let response = await fetch(url, options);
	if (response.ok) return response;

	if (response.status === 401) {
		const originalResponse = response.clone();
		return await auth_refresh()
			.then((response) => {
				if (response.ok) return response;
				console.error(`Could not refresh token: ${response.status}`);
				return originalResponse;
			})
			.then(async () => await fetch(url, options))
			.catch((error) => { console.error(`Could not refresh token: ${error}`); return originalResponse; });
	}

	return response;
}