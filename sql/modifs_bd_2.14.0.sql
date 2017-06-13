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
