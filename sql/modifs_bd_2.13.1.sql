-- -- 2.13.1
-- SELECT * from siteweb where url like 'http://https://%';
UPDATE siteweb SET url = right(url, -7) WHERE url LIKE 'http://https://%';

-- Droits
ALTER TABLE menu2 ADD constraint menu2_pk PRIMARY KEY(id);
ALTER TABLE menu2 ADD constraint menu2_label_unique UNIQUE(label);
INSERT INTO menu2 VALUES(73,'Room.modification.auth');
INSERT INTO menu2 VALUES(74,'Course.reading.auth');
INSERT INTO menu2 VALUES(75,'Schedule.modification.auth');

INSERT INTO menuprofil VALUES(73,0,false);
INSERT INTO menuprofil VALUES(73,1,true);
INSERT INTO menuprofil VALUES(73,2,false);
INSERT INTO menuprofil VALUES(73,3,false);
INSERT INTO menuprofil VALUES(73,4,true);
INSERT INTO menuprofil VALUES(73,10,false);
INSERT INTO menuprofil VALUES(73,11,false);

INSERT INTO menuprofil VALUES(74,0,false);
INSERT INTO menuprofil VALUES(74,1,true);
INSERT INTO menuprofil VALUES(74,2,false);
INSERT INTO menuprofil VALUES(74,3,false);
INSERT INTO menuprofil VALUES(74,4,true);
INSERT INTO menuprofil VALUES(74,10,false);
INSERT INTO menuprofil VALUES(74,11,false);

INSERT INTO menuprofil VALUES(75,0,false);
INSERT INTO menuprofil VALUES(75,1,true);
INSERT INTO menuprofil VALUES(75,2,false);
INSERT INTO menuprofil VALUES(75,3,false);
INSERT INTO menuprofil VALUES(75,4,true);
INSERT INTO menuprofil VALUES(75,10,false);
INSERT INTO menuprofil VALUES(75,11,false);

INSERT INTO menuaccess (select idper, 73, false from login where profil = 0);
INSERT INTO menuaccess (select idper, 73, true from login where profil = 1);
INSERT INTO menuaccess (select idper, 73, false from login where profil = 2);
INSERT INTO menuaccess (select idper, 73, false from login where profil = 3);
INSERT INTO menuaccess (select idper, 73, true from login where profil = 4);
INSERT INTO menuaccess (select idper, 73, false from login where profil = 10);
INSERT INTO menuaccess (select idper, 73, false from login where profil = 11);

INSERT INTO menuaccess (select idper, 74, false from login where profil = 0);
INSERT INTO menuaccess (select idper, 74, true from login where profil = 1);
INSERT INTO menuaccess (select idper, 74, false from login where profil = 2);
INSERT INTO menuaccess (select idper, 74, false from login where profil = 3);
INSERT INTO menuaccess (select idper, 74, true from login where profil = 4);
INSERT INTO menuaccess (select idper, 74, false from login where profil = 10);
INSERT INTO menuaccess (select idper, 74, false from login where profil = 11);

INSERT INTO menuaccess (select idper, 75, false from login where profil = 0);
INSERT INTO menuaccess (select idper, 75, true from login where profil = 1);
INSERT INTO menuaccess (select idper, 75, false from login where profil = 2);
INSERT INTO menuaccess (select idper, 75, false from login where profil = 3);
INSERT INTO menuaccess (select idper, 75, true from login where profil = 4);
INSERT INTO menuaccess (select idper, 75, false from login where profil = 10);
INSERT INTO menuaccess (select idper, 75, false from login where profil = 11);
