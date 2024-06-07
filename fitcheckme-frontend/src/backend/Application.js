import { getEnvVariable } from "../utils/Env";
import { handleFetchException } from "./ExceptionHandling";
import FetchWithRefreshRetry from "./FetchWithRefreshRetry";


export async function getUserOutfits() {
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/outfit/useroutfits`, {
		method: 'GET',
		credentials: 'include'
	})
		.then((response) => handleFetchException(response))
		.then((response) => response.json())
}

export async function getUserGarments() {
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/garment/usergarments`, {
		method: 'GET',
		credentials: 'include'
	})
		.then((response) => handleFetchException(response))
		.then((response) => response.json())
}

export async function getTags() {
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/tag/all`, {
		method: 'GET',
		credentials: 'include'
	})
		.then((response) => handleFetchException(response))
		.then((response) => response.json())
}

export async function createGarment(garmentName, urls = [], tags = []) {
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/garment/create`, {
		method: 'POST',
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			garmentName: garmentName,
			urls: urls,
			garmentTags: tags
		})
	})
		.then((response) => handleFetchException(response))
}

export async function editGarment(garmentId, garmentName, addURLs, removeURLs, addTagIds, removeTagIds) {
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/garment/edit`, {
		method: 'PUT',
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			garmentId: garmentId,
			garmentName: garmentName,
			addURLs: addURLs,
			removeURLs: removeURLs,
			addTagIds: addTagIds,
			removeTagIds: removeTagIds
		})
	})
		.then((response) => handleFetchException(response))
}

export async function editGarmentImages(garmentId, addImageIds, removeImageIds) {
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/garment/editimages`, {
		method: 'PUT',
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			garmentId: garmentId,
			addImageIds: addImageIds,
			removeImageIds: removeImageIds
		})
	})
		.then((response) => handleFetchException(response))
}

export async function createOutfit(outfitName, outfitDesc = "", tags = [], garments = []) {
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/outfit/create`, {
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

export async function editOutfit(outfitId, outfitName, outfitDesc, addGarmentIds, removeGarmentIds, addTagIds, removeTagIds) {
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/outfit/edit`, {
		method: 'PUT',
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			outfitId: outfitId,
			outfitName: outfitName,
			outfitDesc: outfitDesc,
			addGarmentIds: addGarmentIds,
			removeGarmentIds: removeGarmentIds,
			addTagIds: addTagIds,
			removeTagIds: removeTagIds
		})
	})
		.then((response) => handleFetchException(response))
}

export async function editOutfitImages(outfitId, addImageIds, removeImageIds) {
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/outfit/editimages`, {
		method: 'PUT',
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			outfitId: outfitId,
			addImageIds: addImageIds,
			removeImageIds: removeImageIds
		})
	})
		.then((response) => handleFetchException(response))
}

export async function editUser(userId, username, userBio) {
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/user/details`, {
		method: 'PUT',
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			userId: userId,
			username: username,
			bio: userBio
		})
	})
		.then((response) => handleFetchException(response))
}

export async function updatePassword(userId, oldPassword, newPassword) {
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/user/password`, {
		method: 'PUT',
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({
			userId: userId,
			oldPassword: oldPassword,
			newPassword: newPassword
		})
	})
		.then((response) => handleFetchException(response))
}

export async function deleteAccount(userId) {
	return await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/user?id=${userId}`, {
		method: 'DELETE',
		credentials: 'include'
	})
		.then((response) => handleFetchException(response))
}
