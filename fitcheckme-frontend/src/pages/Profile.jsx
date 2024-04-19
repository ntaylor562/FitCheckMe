export default function Profile({ user }) {
	return (
		<div>
			<h1>{user.username}'s Profile</h1>
			<p>Bio: {user.bio}</p>
		</div>
	)
}