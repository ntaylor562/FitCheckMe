import { handleFetchException } from "./ExceptionHandling";
import FetchWithRefreshRetry from "./FetchWithRefreshRetry";


export async function getGarments() {
	return await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/garment/all`, {
		method: 'GET',
		credentials: 'include'
	})
	.then((response) => handleFetchException(response))
	.then((response) => response.json())
	.then((data) => console.log(data))
}