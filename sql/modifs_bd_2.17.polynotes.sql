-- 2.17.polynotes

CREATE TABLE reinscription (
    id serial primary key,
    creation date,
    prof integer,
    jour integer,
    cours integer,
    student integer,
    preference smallint,
    hour time without time zone,
    duration time without time zone,
    note varchar(256),
    colonne int,
    selected boolean,
    mailinfo timestamp,
    mailconfirm timestamp,
    action int
);

ALTER TABLE reinscription OWNER TO nobody;
COMMENT ON COLUMN reinscription.creation IS 'Date de saisie, utilisée pour selectionner l année scolaire';
COMMENT ON COLUMN reinscription.cours IS 'id du cours';
COMMENT ON COLUMN reinscription.jour IS 'Jour de la semaine en isodow Lundi=1';
COMMENT ON COLUMN reinscription.student IS 'id adhérent';
COMMENT ON COLUMN reinscription.preference IS 'numéro du voeux 1 à 3';
COMMENT ON COLUMN reinscription.hour IS 'heure début de la plage horaire';
COMMENT ON COLUMN reinscription.duration IS 'durée de la plage horaire';
COMMENT ON COLUMN reinscription.note IS 'note associé au voeu';
COMMENT ON COLUMN reinscription.colonne IS 'colonne de saisie dans le tableau';
COMMENT ON COLUMN reinscription.selected IS 'voeu validé';
COMMENT ON COLUMN reinscription.mailinfo IS 'date envoi mail lettre1 de récap des voeux';
COMMENT ON COLUMN reinscription.mailconfirm IS 'date envoi mail lettre2 de voeu validé';
COMMENT ON COLUMN reinscription.action IS 'id action pour les cours collectifs planifiés plusieurs fois même jour/heure';

CREATE INDEX reins_student_idx ON reinscription USING btree(student);
CREATE INDEX reins_cours_idx ON reinscription USING btree(cours);

-- nouvelles clés de config pour l'envoi de mail par smtp

INSERT INTO config VALUES ('Smtp.server.name','smtp.domaine.net');
INSERT INTO config VALUES ('Smtp.server.security','TLS');
INSERT INTO config VALUES ('Smtp.server.port','587');
INSERT INTO config VALUES ('Smtp.server.authentification','f');
INSERT INTO config VALUES ('Smtp.server.user','email@domaine.net');
INSERT INTO config VALUES ('Smtp.server.password','password');

