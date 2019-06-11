-- 2.15.0
CREATE TABLE pagemodel(
    mtype smallint PRIMARY KEY,
    page bytea
);

ALTER TABLE pagemodel OWNER TO nobody;
COMMENT ON COLUMN pagemodel.mtype IS 'Type de modèle : 0 = modèle par défaut, 1 = devis/factures, 2 = contrat de formation, 3 = convention de stage';
COMMENT ON COLUMN pagemodel.page IS 'Document pdf';

CREATE TABLE societe(
    id integer PRIMARY KEY,
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
COMMENT ON COLUMN societe.idper IS 'Numéro de contact';
COMMENT ON COLUMN societe.guid IS 'Identification globale';
COMMENT ON COLUMN societe.domaine IS 'Nom de domaine';
COMMENT ON COLUMN societe.logo IS 'Logo de la société';
COMMENT ON COLUMN societe.stamp IS 'Image de tampon/signature';

COMMENT ON COLUMN personne.ptype IS '1 = personne physique ou organisme, 4 = salle, 5 = etablissement, 6 = agence bancaire';


CREATE TABLE organisation(
    idper integer PRIMARY KEY REFERENCES personne(id) ON DELETE CASCADE,
    referent integer DEFAULT 0 REFERENCES personne(id) ON DELETE SET DEFAULT,
    nom varchar(64) UNIQUE,
    raison varchar(64),
    siret varchar(16) UNIQUE,
    naf varchar(16),
    codefp varchar(16),
    codetva varchar(16)
);
ALTER TABLE organisation OWNER TO nobody;
COMMENT ON COLUMN organisation.idper IS 'Identifiant fiche contact';
COMMENT ON COLUMN organisation.referent IS 'Identifiant référent';
COMMENT ON COLUMN organisation.nom IS 'Nom organisme';
COMMENT ON COLUMN organisation.raison IS 'Raison sociale';
COMMENT ON COLUMN organisation.siret IS 'Numéro siret';
COMMENT ON COLUMN organisation.naf IS 'Code NAF (APE)';
COMMENT ON COLUMN organisation.codefp IS 'Code formation professionnelle';
COMMENT ON COLUMN organisation.codetva IS 'Identifiant TVA intra-communautaire';

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
        
        INSERT INTO organisation(nom,idper,referent) SELECT DISTINCT ON (organisation) organisation,id,id FROM personne WHERE organisation IS NOT NULL AND organisation != 'null' ORDER BY organisation;
        
        ALTER TABLE personne ADD orgid integer;
        --UPDATE personne p SET orgid = (SELECT id FROM organisation WHERE nom = p.organisation);
        UPDATE personne SET orgid = id WHERE id IN(SELECT idper FROM organisation);
        -- l'organisation <UNTEL> existe-elle ?
        SELECT idper INTO org_id FROM organisation WHERE lower(nom) = lower(company_name);
        if org_id IS NULL THEN
            RAISE NOTICE 'company not found  %', org_id;  -- Prints
            INSERT INTO personne(ptype,civilite,droit_img,organisation) values (1,'','f',company_name);
            SELECT id INTO org_id FROM personne WHERE organisation = company_name;
            INSERT INTO organisation(idper,referent,nom,raison,siret,naf,codefp,codetva) VALUES(org_id,org_id, company_name,company_raison, company_siret, company_naf, company_codefp, company_codetva);
            UPDATE personne SET orgid = id WHERE id = org_id;
            -- INSERT INTO personne(ptype,nom,prenom,civilite,droit_img,orgid) SELECT 0,'','','',false,id FROM organisation WHERE idper =0;
        ELSE
            RAISE NOTICE 'company found  %', org_name;  -- Prints
            UPDATE organisation SET raison=company_raison,siret=company_siret,naf=company_naf,codefp=company_codefp,codetva=company_codetva WHERE idper = org_id;
        END IF;
        
        -- Nettoyage
        ALTER TABLE personne DROP COLUMN organisation;-- RENAME organisation to orgname
        ALTER TABLE personne RENAME orgid TO organisation;
        --INSERT INTO personne(ptype,nom,prenom,civilite,droit_img,organisation) VALUES(0,'','','',false,org_id);
        --SELECT id INTO company_idper FROM personne WHERE ptype = 0;
        SELECT idper INTO company_idper FROM organisation WHERE nom = company_name;
        RAISE NOTICE 'societe idper  %', company_idper;  -- Prints
        INSERT INTO societe(id,idper,domaine) VALUES(1,company_idper,company_domain);
        --UPDATE organisation SET idper=company_idper WHERE id=org_id;
    END;
$$ LANGUAGE plpgsql;

select setup_organisation();

CREATE VIEW personnevue AS SELECT p.*,o.nom AS onom,o.raison AS oraison FROM personne p LEFT JOIN organisation o ON p.organisation = o.idper;
ALTER VIEW personnevue OWNER TO nobody;
ALTER TABLE eleve ADD assurance varchar(64);
ALTER TABLE eleve ADD assuranceref varchar(64);

-- part 1
CREATE TABLE contratformation (
    id serial,
    ctype smallint,
    idper  integer,
    idcmd integer,
    libelle varchar(256),
    saison varchar(32),
    debut date,
    fin date,
    financement varchar(128),
    total numeric(9,2),
    montant numeric(9,2),
    volumint real,
    volumext real,
    datesign date
    
);
ALTER TABLE contratformation OWNER TO nobody;
COMMENT ON COLUMN contratformation.ctype IS 'Type de contrat : 0 = non défini, 1 = bipartite, 2 = tripartite';
COMMENT ON COLUMN contratformation.libelle IS 'Nom formation';
COMMENT ON COLUMN contratformation.financement IS 'Nom de l''organisme financeur';
COMMENT ON COLUMN contratformation.total IS 'Montant total de la formation';
COMMENT ON COLUMN contratformation.montant IS 'Montant pris en charge';
COMMENT ON COLUMN contratformation.volumint IS 'Nombre d''heures de formation en interne';
COMMENT ON COLUMN contratformation.volumext IS 'Nombre d''heures de formation en entreprise';

-- part 2
INSERT INTO config VALUES('Gestion.contrats.formation','f');
INSERT INTO config VALUES('Gestion.conventions.stage','f');
-- part 3
CREATE TABLE conventionstage (
    id serial,
    ctype smallint,
    idper  integer,
    idorg integer,
    assurance varchar(64),
    assuranceref varchar(64),
    libelle varchar(256),
    saison varchar(32),
    debut date,
    fin date,
    datesign date
);
ALTER TABLE conventionstage OWNER TO nobody;
COMMENT ON COLUMN conventionstage.ctype IS 'Type de convention : 0 = non défini, 1 = bipartite, 2 = tripartite';
COMMENT ON COLUMN conventionstage.libelle IS 'Nom de la formation';
COMMENT ON COLUMN conventionstage.assurance IS 'Nom assurance responsabilité civile';
COMMENT ON COLUMN conventionstage.assuranceref IS 'Référence du contrat d''assurance';
COMMENT ON COLUMN conventionstage.debut IS 'Date de début du stage en entreprise';
COMMENT ON COLUMN conventionstage.fin IS 'Date de fin de stage en entreprise';
COMMENT ON COLUMN conventionstage.datesign IS 'Date de signature';

-- part 4
ALTER TABLE eleve ADD COLUMN archive boolean DEFAULT FALSE;
UPDATE eleve SET archive = archiv;
ALTER TABLE eleve DROP COLUMN archiv;

DROP FUNCTION IF EXISTS setup_organisation();
