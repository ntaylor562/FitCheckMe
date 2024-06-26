CREATE SCHEMA IF NOT EXISTS app;
CREATE SCHEMA IF NOT EXISTS community;

CREATE TABLE IF NOT EXISTS community.user(
	user_id SERIAL PRIMARY KEY,
	username VARCHAR(50) UNIQUE NOT NULL,
	email VARCHAR(30) UNIQUE NOT NULL,
	password VARCHAR(72) NOT NULL,
	bio TEXT
);

CREATE INDEX IF NOT EXISTS user_username ON community.user(username);
CREATE INDEX IF NOT EXISTS user_email ON community.user(email);

CREATE TABLE IF NOT EXISTS app.role(
	role_id SERIAL PRIMARY KEY,
	role_name VARCHAR(20) UNIQUE NOT NULL
);

CREATE INDEX IF NOT EXISTS role_name ON app.role(role_name);

CREATE TABLE IF NOT EXISTS app.user_role(
	user_id INTEGER REFERENCES community.user(user_id),
	role_id INTEGER REFERENCES app.role(role_id),
	PRIMARY KEY(user_id, role_id)
);

CREATE TABLE IF NOT EXISTS community.following(
	follower_id INTEGER,
	followee_id INTEGER,
	follow_date TIMESTAMP WITH TIME ZONE NOT NULL,
	PRIMARY KEY(follower_id, followee_id)
);

CREATE TABLE IF NOT EXISTS app.refresh_token(
	refresh_token_id SERIAL PRIMARY KEY,
	user_id INTEGER REFERENCES community.user(user_id),
	refresh_token VARCHAR(255) NOT NULL,
	expire_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS app.tag(
	tag_id SERIAL PRIMARY KEY,
	tag_name VARCHAR(30) UNIQUE NOT NULL
);

CREATE INDEX IF NOT EXISTS tag_name ON app.tag(tag_name);

CREATE TABLE IF NOT EXISTS app.image_file(
	image_file_id SERIAL PRIMARY KEY,
	user_id INTEGER REFERENCES community.user(user_id),
	image_path TEXT NOT NULL,
	image_creation_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS app.user_profile_image(
	user_id INTEGER PRIMARY KEY REFERENCES community.user(user_id),
	image_id INTEGER REFERENCES app.image_file(image_file_id)
);

CREATE TABLE IF NOT EXISTS app.outfit(
	outfit_id SERIAL PRIMARY KEY,
	user_id INTEGER REFERENCES community.user(user_id),
	outfit_name VARCHAR(50) NOT NULL,
	outfit_desc TEXT,
	outfit_creation_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS app.outfit_garment(
	outfit_id INTEGER NOT NULL,
	garment_id INTEGER NOT NULL,
	PRIMARY KEY(outfit_id, garment_id)
);

CREATE TABLE IF NOT EXISTS app.outfit_tag(
	outfit_id INTEGER REFERENCES app.outfit(outfit_id),
	tag_id INTEGER REFERENCES app.tag(tag_id),
	PRIMARY KEY(outfit_id, tag_id)
);

CREATE TABLE IF NOT EXISTS app.outfit_image(
	image_id INTEGER REFERENCES app.image_file(image_file_id),
	outfit_id INTEGER REFERENCES app.outfit(outfit_id),
	PRIMARY KEY(image_id, outfit_id)
);

CREATE TABLE IF NOT EXISTS app.garment(
	garment_id SERIAL PRIMARY KEY,
	user_id INTEGER REFERENCES community.user(user_id),
	garment_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS app.garment_url(
	garment_id INTEGER REFERENCES app.garment(garment_id),
	garment_url TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS app.garment_tag(
	garment_id INTEGER REFERENCES app.garment(garment_id),
	tag_id INTEGER REFERENCES app.tag(tag_id),
	PRIMARY KEY(garment_id, tag_id)
);

CREATE TABLE IF NOT EXISTS app.garment_image(
	image_id INTEGER REFERENCES app.image_file(image_file_id),
	garment_id INTEGER REFERENCES app.garment(garment_id),
	PRIMARY KEY(image_id, garment_id)
);
