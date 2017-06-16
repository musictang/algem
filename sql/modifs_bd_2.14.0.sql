-- -- 2.14.0
INSERT INTO config VALUES('Code.langue','fr-FR');

ALTER TABLE echeancier2 ADD tva numeric(4,2) DEFAULT 0.0;
--INSERT INTO comptepref VALUES ('TVA',NULL,NULL);

ALTER TABLE tva ADD compte integer;
ALTER TABLE tva ALTER pourcentage TYPE numeric(4,2);

INSERT INTO compte VALUES(DEFAULT,'445711','TVA collectée 2,1%',false);
INSERT INTO compte VALUES(DEFAULT,'445712','TVA collectée 5,5%',false);
INSERT INTO compte VALUES(DEFAULT,'445713','TVA collectée 7%',false);
INSERT INTO compte VALUES(DEFAULT,'445714','TVA collectée 19,6%',false);
INSERT INTO compte VALUES(DEFAULT,'445715','TVA collectée 10%',false);

ALTER TABLE sallequip ADD visible boolean default TRUE;
ALTER TABLE sallequip ADD immo varchar(32);
CREATE INDEX sallequip_idx ON sallequip (idsalle);

INSERT INTO menu2 VALUES (151,'OrderLine.date.before.financial.year.auth');

INSERT INTO menuprofil VALUES(151,0,false);
INSERT INTO menuprofil VALUES(151,1,false);
INSERT INTO menuprofil VALUES(151,2,false);
INSERT INTO menuprofil VALUES(151,3,false);
INSERT INTO menuprofil VALUES(151,4,true);
INSERT INTO menuprofil VALUES(151,10,false);
INSERT INTO menuprofil VALUES(151,11,false);

INSERT INTO menuaccess (select idper, 151, false from login where profil = 0);
INSERT INTO menuaccess (select idper, 151, false from login where profil = 1);
INSERT INTO menuaccess (select idper, 151, false from login where profil = 2);
INSERT INTO menuaccess (select idper, 151, false from login where profil = 3);
INSERT INTO menuaccess (select idper, 151, true from login where profil = 4);
INSERT INTO menuaccess (select idper, 151, false from login where profil = 10);
INSERT INTO menuaccess (select idper, 151, false from login where profil = 11);
