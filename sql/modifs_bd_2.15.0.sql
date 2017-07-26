-- -- 2.15.0
CREATE TABLE societe(
    id integer PRIMARY KEY DEFAULT(1),
    idper integer,
    guid uuid,
    domaine varchar(64),
    logo bytea,
    stamp bytea,
    -- smscode varchar(32),
    -- smtp_host varchar(64),
    -- smtp_port integer,
    -- smtp_user varchar(32), -- encode
    -- smtp_pass varchar(32), -- encode
    constraint societe_single_row CHECK(id = 1)
);

CREATE TABLE pagemodel(
    mtype smallint PRIMARY KEY,
    page bytea
);

INSERT INTO personne(ptype,nom,prenom,civilite,droit_img,organisation) SELECT 0,'','','',false,valeur FROM config WHERE clef = 'Organisation.Nom';
-- COMMENT ON COLUMN personne.ptype IS '0 = societe, 1 = personne physique, 4 = salle, 5 = etablissement, 6 = agence bancaire';

CREATE TABLE organisation(
    id serial,
    nom varchar(64) UNIQUE,
    raison varchar(64),
    siret varchar(16) UNIQUE,
    naf varchar(16),
    codefp varchar(16),
    tva varchar(16)
);

DROP FUNCTION IF EXISTS setup_organisation();
CREATE FUNCTION setup_organisation() RETURNS void AS $$
    --#variable_conflict use_variable
    DECLARE
        company_idper integer;
        company_name varchar(64);
        company_adr1 varchar(50);
        company_adr2 varchar(50);
        company_cdp varchar(5);
        company_city varchar(50);
        company_domain varchar(64);
        company_siret varchar(32);
        company_naf varchar(32);
        company_tva varchar(32);
        company_codefp varchar(32);
        company_raison varchar(32);
    BEGIN
        SELECT id INTO company_idper FROM personne WHERE ptype = 0 and organisation = (SELECT valeur FROM config WHERE clef = 'Organisation.Nom') LIMIT 1;
        SELECT valeur INTO company_name FROM config WHERE clef = 'Organisation.Nom';
        SELECT valeur INTO company_adr1 FROM config WHERE clef = 'Organisation.Adresse1';
        SELECT valeur INTO company_adr2 FROM config WHERE clef = 'Organisation.Adresse2';
        SELECT valeur INTO company_cdp FROM config WHERE clef = 'Organisation.Cdp';
        SELECT valeur INTO company_city FROM config WHERE clef = 'Organisation.Ville';
        SELECT valeur INTO company_domain FROM config WHERE clef = 'Organisation.domaine';
        
        SELECT valeur INTO company_siret FROM config WHERE clef = 'Numero.SIRET';
        SELECT valeur INTO company_naf FROM config WHERE clef = 'Code.NAF';
        SELECT valeur INTO company_tva FROM config WHERE clef = 'Code.TVA';
        SELECT valeur INTO company_codefp FROM config WHERE clef = 'Code.FP';
        SELECT valeur INTO company_raison FROM config WHERE clef = 'Compta.prelevement.raison';
        RAISE NOTICE 'idper is %', company_idper;  -- Prints
        INSERT INTO societe(id,idper,domaine) VALUES(1,company_idper,company_domain);
        INSERT INTO organisation VALUES(company_name, company_raison, company_siret, company_naf, company_codefp, company_tva);
        
        INSERT INTO organisation(nom) SELECT DISTINCT organisation FROM personne WHERE organisation IS NOT NULL AND organisation != 'null' AND id != company_idper ORDER BY organisation;
        ALTER TABLE personne ADD orgid integer;
        UPDATE personne p SET orgid = SELECT id FROM organisation WHERE nom = p.organisation;
        --ALTER TABLE personne DROP COLUMN organisation;
    END;
$$ LANGUAGE plpgsql;
select setup_organisation();
-- DROP FUNCTION IF EXISTS setup_organisation();
