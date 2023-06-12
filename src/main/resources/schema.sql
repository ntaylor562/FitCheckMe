CREATE SCHEMA app;
CREATE SCHEMA community;

CREATE TABLE IF NOT EXISTS community.user(
	user_id BIGSERIAL PRIMARY KEY,
	username VARCHAR(25) NOT NULL,
	bio TEXT
);

CREATE TABLE IF NOT EXISTS app.tag(
	tag_id SERIAL PRIMARY KEY,
	tag_name VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS app.outfit(
	outfit_id BIGSERIAL PRIMARY KEY,
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
	garment_id BIGSERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS app.garment_tag(
	garment_id BIGINT REFERENCES app.garment(garment_id),
	tag_id INTEGER REFERENCES app.tag(tag_id),
	PRIMARY KEY(garment_id, tag_id)
);

CREATE TABLE IF NOT EXISTS app.color(
	color_id SERIAL PRIMARY KEY,
	color_name VARCHAR(20) NOT NULL,
	color_r SMALLINT NOT NULL,
	color_g SMALLINT NOT NULL,
	color_b SMALLINT NOT NULL
);


