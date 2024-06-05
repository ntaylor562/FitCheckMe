import { getEnvVariable } from "./Env";
import { handleFetchException } from "./ExceptionHandling"
import FetchWithRefreshRetry from "./FetchWithRefreshRetry";


function resizeImage(file, width, height) {
	return new Promise((resolve, reject) => {
		const reader = new FileReader();

		reader.onload = (event) => {
			const img = new Image();

			img.onload = () => {
				const canvas = document.createElement('canvas');
				const ctx = canvas.getContext('2d');

				canvas.width = width;
				canvas.height = height;

				ctx.drawImage(img, 0, 0, width, height);

				canvas.toBlob((blob) => {
					resolve(new File([blob], file.name, { type: file.type }));
				}, file.type);
			};

			img.src = event.target.result.toString();
		};

		reader.onerror = (error) => {
			reject(error);
		};

		reader.readAsDataURL(file);
	});
}

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
		body: await resizeImage(file, 300, 300)
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
			body: await resizeImage(fileUploadResponse.file, 300, 300)
		})
			.then((response) => handleFetchException(response));

		if (!currentFileUploadResponse.ok) {
			return currentFileUploadResponse;
		}
	}

	return backendFilePostRes;

}