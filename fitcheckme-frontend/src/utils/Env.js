

// Get environment variable (automatically omits VITE_ prefix)
export function getEnvVariable(name) {
	// @ts-ignore
	return import.meta.env["VITE_" + name];
}