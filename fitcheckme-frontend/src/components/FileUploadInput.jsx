import { Input } from "@chakra-ui/react";


export default function FileUploadInput({ name = null, multiple = false, accept, handleFileChange }) {
	return (
		<Input name={name} alignContent="center" multiple={multiple} accept={accept} type="file" onChange={handleFileChange} />
	)
}