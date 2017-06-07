-- -- 2.14.0
INSERT INTO config VALUES('Code.langue','fr-FR');

ALTER TABLE echeancier2 ADD tva numeric(4,2) DEFAULT 0.0;
INSERT INTO comptepref VALUES ('TVA',NULL,NULL);
