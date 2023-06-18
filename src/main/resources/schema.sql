CREATE SCHEMA IF NOT EXISTS app;
CREATE SCHEMA IF NOT EXISTS community;

CREATE TABLE IF NOT EXISTS community.user(
	user_id BIGSERIAL PRIMARY KEY,
	username VARCHAR(25) UNIQUE NOT NULL,
	bio TEXT
);

CREATE TABLE IF NOT EXISTS community.following(
	follower_id BIGSERIAL,
	followee_id BIGSERIAL,
	follow_date TIMESTAMP WITH TIME ZONE NOT NULL,
	PRIMARY KEY(follower_id, followee_id)
);

CREATE TABLE IF NOT EXISTS app.tag(
	tag_id SERIAL PRIMARY KEY,
	tag_name VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS app.outfit(
	outfit_id BIGSERIAL PRIMARY KEY,
	user_id BIGINT REFERENCES community.user(user_id),
	outfit_name VARCHAR(50) NOT NULL,
	outfit_desc TEXT,
	outfit_creation_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS app.outfit_tag(
	outfit_id BIGINT REFERENCES app.outfit(outfit_id),
	tag_id INTEGER REFERENCES app.tag(tag_id),
	PRIMARY KEY(outfit_id, tag_id)
);

CREATE TABLE IF NOT EXISTS app.garment(
	garment_id BIGSERIAL PRIMARY KEY,
	garment_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS app.garment_url(
	garment_id BIGINT REFERENCES app.garment(garment_id),
	garment_url TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS app.garment_tag(
	garment_id BIGINT REFERENCES app.garment(garment_id),
	tag_id INTEGER REFERENCES app.tag(tag_id),
	PRIMARY KEY(garment_id, tag_id)
);

