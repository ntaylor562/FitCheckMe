import { createContext, useContext, useEffect, useState } from "react";
import { getTags } from "../backend/Application";


const TagsContext = createContext({
	tags: [],
	isTagsLoading: true
});

export const TagsProvider = ({ children }) => {
	const [tags, setTags] = useState([]);
	const [isTagsLoading, setTagsLoading] = useState(true);

	useEffect(() => {
		const initializeTags = async () => {
			const tags = await getTags().catch((error) => {
				console.error(error);
				return [];
			});
			setTags(tags);
			setTagsLoading(false);
		}
		initializeTags();
	}, []);


	return (
		<TagsContext.Provider value={{ tags, isTagsLoading }}>
			{children}
		</TagsContext.Provider>
	);
}

//Since this requires authentication, do not use this above login page as it will cause infinite redirects
export function useTags() {
	const context = useContext(TagsContext);
	if (context === undefined) {
		throw new Error('useTags must be used within a TagsProvider');
	}
	return context;
}
