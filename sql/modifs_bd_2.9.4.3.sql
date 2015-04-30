CREATE TABLE action_color(
  idaction integer PRIMARY KEY REFERENCES action(id) ON DELETE CASCADE,
  color integer DEFAULT -1
);

ALTER TABLE action_color OWNER TO nobody;

ALTER TABLE vacance ADD CONSTRAINT vacance_pk PRIMARY KEY(jour,vid);
DROP INDEX vacance_id_idx;
DROP INDEX vax1;

INSERT INTO config VALUES('Planification.administrative', 't');
