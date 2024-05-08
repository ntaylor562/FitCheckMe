

export async function getGarments() {
	return await fetch(`${import.meta.env.VITE_BACKEND_URL}/api/garment/all`, {
		method: 'GET',
		credentials: 'include'
	})
	.then((response) => response.json())
	.then((data) => console.log(data))
	.catch((error) => console.error(error.message))
}