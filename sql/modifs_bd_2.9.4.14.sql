-- 2.9.4.14
-- Algem web app prerequisites
ALTER TABLE login ALTER login TYPE VARCHAR(16);
ALTER TABLE login ADD CONSTRAINT login_unique UNIQUE (login);

-- TODO vérifier doublons au préalable (verifier menuaccess_pk)
-- DELETE FROM menuaccess where oid IN (SELECT m2.oid from menuaccess m1, menuaccess m2 where m1.idper = m2.idper and m1.idmenu = m2.idmenu and m2.oid > m1.oid);
--ALTER TABLE menuaccess ADD CONSTRAINT menuaccess_pk PRIMARY KEY (idper,idmenu);

-- photo management
CREATE TABLE personne_photo (
    idper integer primary key,
    photo bytea
);

ALTER TABLE personne_photo OWNER TO nobody;

INSERT INTO menu2 VALUES(146,'Photos.export.auth');
INSERT INTO menu2 VALUES(147,'Photos.import.auth');

INSERT INTO menuprofil VALUES(146,0,false);
INSERT INTO menuprofil VALUES(146,1,false);
INSERT INTO menuprofil VALUES(146,2,false);
INSERT INTO menuprofil VALUES(146,3,false);
INSERT INTO menuprofil VALUES(146,4,true);

INSERT INTO menuprofil VALUES(147,0,false);
INSERT INTO menuprofil VALUES(147,1,false);
INSERT INTO menuprofil VALUES(147,2,false);
INSERT INTO menuprofil VALUES(147,3,false);
INSERT INTO menuprofil VALUES(147,4,true);

INSERT INTO menuaccess (select idper, 146, false from login where profil = 0);
INSERT INTO menuaccess (select idper, 146, false from login where profil = 1);
INSERT INTO menuaccess (select idper, 146, false from login where profil = 2);
INSERT INTO menuaccess (select idper, 146, false from login where profil = 3);
INSERT INTO menuaccess (select idper, 146, true from login where profil = 4);

INSERT INTO menuaccess (select idper, 147, false from login where profil = 0);
INSERT INTO menuaccess (select idper, 147, false from login where profil = 1);
INSERT INTO menuaccess (select idper, 147, false from login where profil = 2);
INSERT INTO menuaccess (select idper, 147, false from login where profil = 3);
INSERT INTO menuaccess (select idper, 147, true from login where profil = 4);
