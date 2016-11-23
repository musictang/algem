COMMENT ON COLUMN person_instrument.ptype IS '1 = élève, 2 = professeur, 3 = musicien';

-- Droits de suppression/modification échéancier
INSERT INTO menu2 VALUES (148,'OrderLine.modification.auth');
INSERT INTO menu2 VALUES (149,'OrderLine.transferred.modification.auth');
INSERT INTO menu2 VALUES (150,'OrderLine.rehearsal.cancelling.auth');

INSERT INTO menuprofil VALUES(148,0,false);
INSERT INTO menuprofil VALUES(148,1,true);
INSERT INTO menuprofil VALUES(148,2,false);
INSERT INTO menuprofil VALUES(148,3,false);
INSERT INTO menuprofil VALUES(148,4,true);

INSERT INTO menuprofil VALUES(149,0,false);
INSERT INTO menuprofil VALUES(149,1,false);
INSERT INTO menuprofil VALUES(149,2,false);
INSERT INTO menuprofil VALUES(149,3,false);
INSERT INTO menuprofil VALUES(149,4,true);

INSERT INTO menuprofil VALUES(150,0,false);
INSERT INTO menuprofil VALUES(150,1,false);
INSERT INTO menuprofil VALUES(150,2,false);
INSERT INTO menuprofil VALUES(150,3,false);
INSERT INTO menuprofil VALUES(150,4,true);

INSERT INTO menuaccess (select idper, 148, false from login where profil = 0);
INSERT INTO menuaccess (select idper, 148, true from login where profil = 1);
INSERT INTO menuaccess (select idper, 148, false from login where profil = 2);
INSERT INTO menuaccess (select idper, 148, false from login where profil = 3);
INSERT INTO menuaccess (select idper, 148, true from login where profil = 4);

INSERT INTO menuaccess (select idper, 149, false from login where profil = 0);
INSERT INTO menuaccess (select idper, 149, false from login where profil = 1);
INSERT INTO menuaccess (select idper, 149, false from login where profil = 2);
INSERT INTO menuaccess (select idper, 149, false from login where profil = 3);
INSERT INTO menuaccess (select idper, 149, true from login where profil = 4);

INSERT INTO menuaccess (select idper, 150, false from login where profil = 0);
INSERT INTO menuaccess (select idper, 150, false from login where profil = 1);
INSERT INTO menuaccess (select idper, 150, false from login where profil = 2);
INSERT INTO menuaccess (select idper, 150, false from login where profil = 3);
INSERT INTO menuaccess (select idper, 150, true from login where profil = 4);
