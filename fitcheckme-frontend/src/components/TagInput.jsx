import { AddIcon, CloseIcon } from "@chakra-ui/icons";
import { Button, Input, InputGroup, InputRightAddon, VStack } from "@chakra-ui/react"
import { useState } from "react";


export default function TagInput({ enteredValues, handleAdd, handleRemove }) {
	const [currentInput, setCurrentInput] = useState("");

	const handleInputChange = (e) => {
		setCurrentInput(e.target.value);
	}

	const handleSubmit = (e) => {
		e.preventDefault();

		if (currentInput === "") return;
		handleAdd(currentInput);
		setCurrentInput("");
	}

	return (
		<VStack spacing={2}>
			{Array.from(enteredValues).map((url, index) => {
				return (
					<InputGroup key={index}>
						<Input isDisabled type='text' value={url} />
						<InputRightAddon p="0px"><Button colorScheme="red" borderLeftRadius="0px" onClick={() => handleRemove(url)}><CloseIcon /></Button></InputRightAddon>
					</InputGroup>
				)
			})}
			<InputGroup>
				<Input type='text' onChange={handleInputChange} onKeyDown={(e) => e.key === "Enter" && handleSubmit(e)} value={currentInput} />
				<InputRightAddon bg="transparent" borderColor="inherit" borderLeft="0px" p="0px">
					<Button variant="ghost" borderLeftRadius="0px" onClick={handleSubmit}><AddIcon /></Button>
				</InputRightAddon>
			</InputGroup>
		</VStack>
	)
}