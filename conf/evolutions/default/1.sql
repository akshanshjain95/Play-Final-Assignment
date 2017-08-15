# --- !Ups

CREATE TABLE IF NOT EXISTS "usertable" (
 id serial NOT NULL,
 firstName VARCHAR(10) NOT NULL,
 middleName VARCHAR(10),
 lastName VARCHAR(10) NOT NULL,
 mobileNo BIGINT NOT NULL,
 email VARCHAR(30) NOT NULL,
 password VARCHAR(65) NOT NULL,
 gender VARCHAR(10) NOT NULL,
 age INT NOT NULL,
 isAdmin BOOLEAN NOT NULL,
 isEnabled BOOLEAN NOT NULL,
PRIMARY KEY(id)
);


# --- !Downs

DROP TABLE usertable;
