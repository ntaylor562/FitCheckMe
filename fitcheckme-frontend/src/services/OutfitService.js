import config from "../config.json"

export async function getAllOutfits() {
	return await fetch(`${config.BACKEND_URL}/api/outfit`, {
		method: "GET"
	}).then((res) => res.json())
}
