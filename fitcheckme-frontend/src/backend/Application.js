import { handleFetchException } from "./ExceptionHandling";
import FetchWithRefreshRetry from "./FetchWithRefreshRetry";


export async function getUserOutfits() {
	// @ts-ignore
	return await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/outfit/useroutfits`, {
		method: 'GET',
		credentials: 'include'
	})
	.then((response) => handleFetchException(response))
	.then((response) => response.json())
}

export async function getUserGarments() {
	// @ts-ignore
	return await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/garment/usergarments`, {
		method: 'GET',
		credentials: 'include'
	})
	.then((response) => handleFetchException(response))
	.then((response) => response.json())
}

export async function getTags() {
	// @ts-ignore
	return await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/tag/all`, {
		method: 'GET',
		credentials: 'include'
	})
	.then((response) => handleFetchException(response))
	.then((response) => response.json())
}

export async function createGarment(garmentName, urls=[], tags=[]) {
	console.log({
		garmentName: garmentName,
		garmentURLs: urls,
		garmentTags: tags
	})
	// @ts-ignore
	return await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/garment/create`, {
		method: 'POST',
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			garmentName: garmentName,
			garmentURLs: urls,
			garmentTags: tags
		})
	})
	.then((response) => handleFetchException(response))
}

export async function createOutfit(outfitName, outfitDesc="", tags=[], garments=[]) {
	// @ts-ignore
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

export async function editOutfit(outfitId, outfitName, outfitDesc="", tags=[], garments=[]) {
	// @ts-ignore
	return await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/outfit/edit/${outfitId}`, {
		method: 'PUT',
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
