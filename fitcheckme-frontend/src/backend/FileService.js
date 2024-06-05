import { getEnvVariable } from "./Env";
import { handleFetchException } from "./ExceptionHandling"
import FetchWithRefreshRetry from "./FetchWithRefreshRetry";


export async function uploadImage(file) {
	const backendFilePostRes = await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/file/image`, {
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

	const fileUploadResponseDTO = await backendFilePostRes.clone().json();

	const s3UploadResponse = await FetchWithRefreshRetry(fileUploadResponseDTO.presignedURL, {
		method: 'PUT',
		body: file
	})
		.then((response) => handleFetchException(response));
	if(!s3UploadResponse.ok) {
		return s3UploadResponse;
	}
	return backendFilePostRes;
}

export async function uploadImages(files) {
	const backendFilePostRes = await FetchWithRefreshRetry(`${getEnvVariable("BACKEND_URL")}/api/file/images`, {
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

	const filesToFileUploadResponseDTO = Array.from(await backendFilePostRes.clone().json()).map((fileUploadResponseDTO) => {
		return {
			...fileUploadResponseDTO,
			file: files.find((file) => fileUploadResponseDTO.fileName.includes(file.name))
		}
	});

	for (const fileUploadResponse of filesToFileUploadResponseDTO) {
		const currentFileUploadResponse = await FetchWithRefreshRetry(fileUploadResponse.presignedURL, {
			method: 'PUT',
			body: fileUploadResponse.file
		})
			.then((response) => handleFetchException(response));

		if (!currentFileUploadResponse.ok) {
			return currentFileUploadResponse;
		}
	}

	return backendFilePostRes;

}