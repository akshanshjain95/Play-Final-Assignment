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

CREATE TABLE IF NOT EXISTS hobbytable (
id serial NOT NULL,
hobby VARCHAR(30),
PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS usertohobbyid (
id serial NOT NULL,
userid INT NOT NULL,
hobby_id INT NOT NULL,
PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS assignment (
id serial NOT NULL,
title VARCHAR(30) NOT NULL,
description VARCHAR(100) NOT NULL,
PRIMARY KEY(id)
);

INSERT INTO hobbytable VALUES
(1, 'Programming'),
(2, 'Reading'),
(3, 'Sports'),
(4, 'Writing'),
(5, 'Swimming');

INSERT INTO assignment VALUES
(1, 'Assignemnt1', 'Description1'),
(2, 'Assignemnt2', 'Description2'),
(3, 'Assignemnt3', 'Description3'),
(4, 'Assignemnt4', 'Description4'),
(5, 'Assignemnt5', 'Description5');