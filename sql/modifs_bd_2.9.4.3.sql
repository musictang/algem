CREATE TABLE action_color(
  idaction integer PRIMARY KEY REFERENCES action(id) ON DELETE CASCADE,
  color integer DEFAULT -1
);

ALTER TABLE action_color OWNER TO nobody;
-- Suppression doublons vacance
DELETE FROM vacance where oid in (SELECT v2.oid from vacance v1, vacance v2 where v1.jour = v2.jour and v1.vid = v2.vid and v2.oid != v1.oid);

ALTER TABLE vacance ADD CONSTRAINT vacance_pk PRIMARY KEY(jour,vid);
DROP INDEX vacance_id_idx;
DROP INDEX vax1;

INSERT INTO config VALUES('Planification.administrative', 't');
