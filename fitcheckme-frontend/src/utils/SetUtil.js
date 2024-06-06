

export function areSetsEqual(set1, set2) {
	if (set1.size !== set2.size) return false;
	for (let elem of set1) {
		if (!set2.has(elem)) return false;
	}
	return true;
}