CREATE TABLE userlink
(userid UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
 created TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP()
);


CREATE TABLE user
(userid UUID NOT NULL,
 username VARCHAR(50) NOT NULL UNIQUE,
 email VARCHAR(50) NOT NULL UNIQUE,
 admin BOOLEAN,
 last_login TIME,
 password VARCHAR(300) NOT NULL,
 created TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(),
 foreign key (userid) references userlink(userid)
);


CREATE TABLE planlink
(planid UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
 userid UUID NOT NULL,
 created TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(),
 foreign key (userid) references userlink(userid)
);


CREATE TABLE plan
(planid UUID NOT NULL,
 title VARCHAR(200) NOT NULL,
 status integer NOT NULL,
 description VARCHAR(2000),
 created TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(),
 foreign key (planid) references planlink(planid)
);


CREATE TABLE commentlink
(commid INTEGER PRIMARY KEY AUTO_INCREMENT,
 planid UUID NOT NULL,
 userid UUID NOT NULL,
 created TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(),
 foreign key (userid) references userlink(userid),
 foreign key (planid) references planlink(planid)
);


CREATE TABLE comment
(commid INTEGER,
 content VARCHAR(1000),
 created TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(),
 foreign key (commid) references commentlink(commid)
);


CREATE TABLE votelink
(voteid INTEGER PRIMARY KEY AUTO_INCREMENT,
 planid UUID NOT NULL,
 userid UUID NOT NULL,
 created TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(),
 foreign key (userid) references userlink(userid),
 foreign key (planid) references planlink(planid)
);


CREATE TABLE vote
(voteid INTEGER,
 type VARCHAR(10),
 created TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(),
 foreign key (voteid) references votelink(voteid)
);



