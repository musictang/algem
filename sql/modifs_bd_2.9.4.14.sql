-- 2.9.4.14
ALTER TABLE login ALTER login TYPE VARCHAR(16);

ALTER TABLE login ADD CONSTRAINT login_unique UNIQUE (login);

-- TODO vérifier doublons au préalable
ALTER TABLE menuaccess ADD CONSTRAINT menuaccess_pk PRIMARY KEY (idper,idmenu);

CREATE TABLE idper_photo (
    idper integer primary key,
    photo bytea
);

ALTER TABLE idper_photo OWNER TO nobody;
