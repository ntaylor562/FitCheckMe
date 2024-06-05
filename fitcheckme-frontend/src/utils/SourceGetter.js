import { getEnvVariable } from "./Env";

export function getImageSource(filePath) {
	return `${getEnvVariable('CLOUDFRONT_URL')}/${encodeURIComponent(filePath)}`
}