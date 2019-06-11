DROP TABLE IF EXISTS atelier_instruments;
CREATE TABLE atelier_instruments (
	idaction integer REFERENCES action(id) ON DELETE CASCADE,
	idper integer REFERENCES personne(id) ON DELETE CASCADE,
	idinstru integer,
PRIMARY KEY (idaction,idper)
);

-- -- EN CAS de table déjà alimentée
--ALTER TABLE atelier_instruments RENAME idpers TO idper;
--DROP INDEX atelier_instruments_idx;
--ALTER TABLE atelier_instruments ADD CONSTRAINT atelier_instruments_pk PRIMARY KEY(idaction,idper);
--ALTER TABLE atelier_instruments ADD CONSTRAINT atelier_instruments_idaction_fkey FOREIGN KEY(idaction) REFERENCES action(id) ON DELETE CASCADE;
--ALTER TABLE atelier_instruments ADD CONSTRAINT atelier_instruments_idper_fkey FOREIGN KEY(idper) REFERENCES personne(id) ON DELETE CASCADE;
