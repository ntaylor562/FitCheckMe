import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
	plugins: [react()],
	base: "/",
	server: {
		host: true,
		origin: "http://localhost:3000",
		port: 3000
	}
})
