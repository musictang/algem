-- 2.9.4.14
ALTER TABLE login ALTER login TYPE VARCHAR(16);

ALTER TABLE login ADD CONSTRAINT login_unique UNIQUE (login);

-- TODO vérifier doublons au préalable
DELETE FROM menuaccess where oid IN (SELECT m2.oid from menuaccess m1, menuaccess m2 where m1.idper = m2.idper and m1.idmenu = m2.idmenu and m2.oid > m1.oid);
ALTER TABLE menuaccess ADD CONSTRAINT menuaccess_pk PRIMARY KEY (idper,idmenu);

CREATE TABLE personne_photo (
    idper integer primary key,
    photo bytea
);

ALTER TABLE personne_photo OWNER TO nobody;
