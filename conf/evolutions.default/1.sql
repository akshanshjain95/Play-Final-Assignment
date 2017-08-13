# --- !Ups

CREATE TABLE IF NOT EXISTS "usertable" (
 id serial NOT NULL,
 firstName VARCHAR(10) NOT NULL,
 middleName VARCHAR(10),
 lastName VARCHAR(10) NOT NULL,
 mobileNo BIGINT NOT NULL,
 username VARCHAR(30) NOT NULL,
 password VARCHAR(30) NOT NULL,
 gender VARCHAR(10) NOT NULL,
 age INT NOT NULL,
PRIMARY KEY(id)
);

# --- !Downs

DROP TABLE "usertable"
