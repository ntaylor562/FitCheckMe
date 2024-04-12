CREATE SCHEMA IF NOT EXISTS app;
CREATE SCHEMA IF NOT EXISTS community;

CREATE TABLE IF NOT EXISTS community.user(
	user_id SERIAL PRIMARY KEY,
	username VARCHAR(25) UNIQUE NOT NULL,
	bio TEXT
);

CREATE INDEX IF NOT EXISTS user_username ON community.user(username);

CREATE TABLE IF NOT EXISTS community.following(
	follower_id INTEGER,
	followee_id INTEGER,
	follow_date TIMESTAMP WITH TIME ZONE NOT NULL,
	PRIMARY KEY(follower_id, followee_id)
);

CREATE TABLE IF NOT EXISTS app.tag(
	tag_id SERIAL PRIMARY KEY,
	tag_name VARCHAR(30) UNIQUE NOT NULL
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

