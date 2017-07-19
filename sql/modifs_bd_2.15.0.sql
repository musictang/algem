-- -- 2.15.0
CREATE TABLE societe(
    id integer PRIMARY KEY DEFAULT(1),
    guid uuid,
    nom varchar(128) NOT NULL,
    responsable varchar(256),
    siret varchar(16),
    naf varchar(16),
    tva varchar(16),
    codefp varchar(16),
    adr1 varchar(64),
    adr2 varchar(64),
    cdp varchar(8),
    ville varchar(64),
    tel varchar(24),
    mail varchar(64),
    domaine varchar(64),
    logo bytea,
    tampon bytea,
    constraint societe_single_row CHECK(id = 1)
);

CREATE TABLE pagemodel(
    mtype smallint primary key,
    page bytea
);
