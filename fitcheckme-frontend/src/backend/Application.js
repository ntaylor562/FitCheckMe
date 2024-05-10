import { handleFetchException } from "./ExceptionHandling";
import FetchWithRefreshRetry from "./FetchWithRefreshRetry";


export async function getGarments() {
	return await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/garment/all`, {
		method: 'GET',
		credentials: 'include'
	})
	.then((response) => handleFetchException(response))
	.then((response) => response.json())
}

export async function getTags() {
	return await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/tag/all`, {
		method: 'GET',
		credentials: 'include'
	})
	.then((response) => handleFetchException(response))
	.then((response) => response.json())
}

export async function createOutfit(outfitName, outfitDesc="", tags=[], garments=[]) {
	console.log(JSON.stringify({
		outfitName: outfitName,
		outfitDesc: outfitDesc,
		outfitTags: tags,
		garments: garments
	}));
	return await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/outfit/create`, {
		method: 'POST',
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			outfitName: outfitName,
			outfitDesc: outfitDesc,
			outfitTags: tags,
			garments: garments
		})
	})
	.then((response) => handleFetchException(response))
}
