import { Box } from "@chakra-ui/react";

export default function HoverOverlay({ subject, overlay }) {

	return (
		<Box className="hover-overlay-container">
			<Box className="hover-overlay-subject">{subject}</Box>
			<Box className="hover-overlay-overlay">{overlay}</Box>
		</Box>
	)
}