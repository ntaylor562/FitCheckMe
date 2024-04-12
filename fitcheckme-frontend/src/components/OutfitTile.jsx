import React from "react"
import { getAllOutfits } from "../services/OutfitService"

export default function OutfitTile() {
	getAllOutfits()
	return <React.Fragment>Hello World</React.Fragment>
}
