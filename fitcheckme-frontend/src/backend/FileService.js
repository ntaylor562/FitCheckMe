import { handleFetchException } from "./ExceptionHandling"
import FetchWithRefreshRetry from "./FetchWithRefreshRetry";


export async function uploadImage(file) {
	// @ts-ignore
	const backendFilePostRes = await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/file/image`, {
		method: 'POST',
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({fileName: file.name})
	})
		.then((response) => handleFetchException(response))

	if (!backendFilePostRes.ok) {
		return backendFilePostRes;
	}

	const fileUploadResponseDTO = await backendFilePostRes.json();

	await FetchWithRefreshRetry(fileUploadResponseDTO.presignedURL, {
		method: 'PUT',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({ ...file, name: fileUploadResponseDTO.fileName })
	})
		.then((response) => handleFetchException(response))
		.then((response) => {
			if (!response.ok) {
				throw new Error(`Failed to upload file ${fileUploadResponseDTO.fileName}`);
			}
		})

}

export async function uploadImages(files) {
	// @ts-ignore
	const backendFilePostRes = await FetchWithRefreshRetry(`${import.meta.env.VITE_BACKEND_URL}/api/file/images`, {
		method: 'POST',
		credentials: 'include',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify(files.map((file) => {
			return {
				fileName: file.name
			}
		}))
	})
		.then((response) => handleFetchException(response))

	if (!backendFilePostRes.ok) {
		return backendFilePostRes;
	}

	const fileUploadResponseDTO = await backendFilePostRes.clone().json();

	for (const fileUploadResponse of fileUploadResponseDTO) {
		await FetchWithRefreshRetry(fileUploadResponse.presignedURL, {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ ...fileUploadResponse, name: fileUploadResponse.fileName })
		})
			.then((response) => handleFetchException(response))
			.then((response) => {
				if (!response.ok) {
					throw new Error(`Failed to upload file ${fileUploadResponse.fileName}`);
				}
			})
	}

	return backendFilePostRes;

}