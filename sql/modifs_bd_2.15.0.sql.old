-- 2.15.0
CREATE TABLE pagemodel(
    mtype smallint PRIMARY KEY,
    page bytea
);
ALTER TABLE pagemodel OWNER TO nobody;

CREATE TABLE societe(
    id integer PRIMARY KEY DEFAULT(1),
    idper integer, -- contact
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
ALTER TABLE societe OWNER TO nobody;
COMMENT ON COLUMN personne.ptype IS '1 = personne physique, 4 = salle, 5 = etablissement, 6 = agence bancaire';

CREATE TABLE organisation(
    id serial PRIMARY KEY,
    nom varchar(64) UNIQUE,
    idper integer REFERENCES personne(id),
    raison varchar(64),
    siret varchar(16) UNIQUE,
    naf varchar(16),
    codefp varchar(16),
    codetva varchar(16)
);
ALTER TABLE organisation OWNER TO nobody;

DROP FUNCTION IF EXISTS setup_organisation();
CREATE FUNCTION setup_organisation() RETURNS void AS $$
    --#variable_conflict use_variable
    DECLARE
        company_name varchar(64);
        company_raison varchar(64);
        company_adr1 varchar(50);
        company_adr2 varchar(50);
        company_cdp varchar(5);
        company_city varchar(50);
        company_domain varchar(64);
        company_siret varchar(16);
        company_naf varchar(16);
        company_codefp varchar(16);
        company_codetva varchar(16);
        
        org_id integer;
        org_name varchar(64);
        
        company_idper integer;
        
    BEGIN
        SELECT valeur INTO company_name FROM config WHERE clef = 'Organisation.Nom';
        SELECT valeur INTO company_adr1 FROM config WHERE clef = 'Organisation.Adresse1';
        SELECT valeur INTO company_adr2 FROM config WHERE clef = 'Organisation.Adresse2';
        SELECT valeur INTO company_cdp FROM config WHERE clef = 'Organisation.Cdp';
        SELECT valeur INTO company_city FROM config WHERE clef = 'Organisation.Ville';
        SELECT valeur INTO company_domain FROM config WHERE clef = 'Organisation.domaine';
        
        SELECT valeur INTO company_raison FROM config WHERE clef = 'Compta.prelevement.raison';
        SELECT valeur INTO company_siret FROM config WHERE clef = 'Numero.SIRET';
        SELECT valeur INTO company_naf FROM config WHERE clef = 'Code.NAF';
        SELECT valeur INTO company_codefp FROM config WHERE clef = 'Code.FP';
        SELECT valeur INTO company_codetva FROM config WHERE clef = 'Code.TVA';
        
        INSERT INTO organisation(nom,idper) SELECT DISTINCT ON (organisation) organisation,id FROM personne WHERE organisation IS NOT NULL AND organisation != 'null' ORDER BY organisation;
        
        ALTER TABLE personne ADD orgid integer;
        UPDATE personne p SET orgid = (SELECT id FROM organisation WHERE nom = p.organisation);
        RAISE NOTICE 'societe idper is null %', company_idper;  -- Prints
        -- l'organisation <UNTEL> existe-elle ?
        SELECT id INTO org_id FROM organisation WHERE lower(nom) = lower(company_name);
        if org_id IS NULL THEN
            RAISE NOTICE 'company not found  %', org_id;  -- Prints
            INSERT INTO organisation(nom,idper,raison,siret,naf,codefp,codetva) VALUES(company_name, 0, company_raison, company_siret, company_naf, company_codefp, company_codetva);
            -- INSERT INTO personne(ptype,nom,prenom,civilite,droit_img,orgid) SELECT 0,'','','',false,id FROM organisation WHERE idper =0;
            SELECT id INTO org_id FROM organisation WHERE nom = company_name;
        ELSE
            RAISE NOTICE 'company found  %', org_name;  -- Prints
            UPDATE organisation SET raison=company_raison,siret=company_siret,naf=company_naf,codefp=company_codefp,codetva=company_codetva WHERE id = org_id;
        END IF;
        
        -- Nettoyage
        ALTER TABLE personne DROP COLUMN organisation;-- RENAME organisation to orgname
        ALTER TABLE personne RENAME orgid TO organisation;
        INSERT INTO personne(ptype,nom,prenom,civilite,droit_img,organisation) VALUES(0,'','','',false,org_id);
        SELECT id INTO company_idper FROM personne WHERE ptype = 0;
        RAISE NOTICE 'societe idper  %', company_idper;  -- Prints
        INSERT INTO societe(id,idper,domaine) VALUES(1,company_idper,company_domain);
        UPDATE organisation SET idper=company_idper WHERE id=org_id;
    END;
$$ LANGUAGE plpgsql;

select setup_organisation();

CREATE VIEW personnevue AS SELECT p.*,o.nom AS onom,o.raison AS oraison FROM personne p LEFT JOIN organisation o ON p.organisation = o.id;
ALTER VIEW personnevue OWNER TO nobody;
ALTER TABLE eleve ADD assurance varchar(64);
ALTER TABLE eleve ADD assuranceref varchar(64);
-- DROP FUNCTION IF EXISTS setup_organisation();

-- voir export adhérents (à revérifier)
-- liste des mandats sepa (à revérifier) DirectDebitIO.getMandates
-- libelle echéancier payeur dans échéancier (organisme)
-- voir pied de page facture (ne pas imprimer si vide)
-- aperçu impression pdf facture (chargement trop rapide)
