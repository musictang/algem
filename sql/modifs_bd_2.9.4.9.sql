-- completion profil 0
INSERT INTO menuprofil VALUES (1,0,FALSE);
INSERT INTO menuprofil VALUES (2,0,FALSE);
INSERT INTO menuprofil VALUES (3,0,FALSE);
INSERT INTO menuprofil VALUES (11,0,FALSE);
INSERT INTO menuprofil VALUES (12,0,FALSE);
INSERT INTO menuprofil VALUES (13,0,FALSE);
INSERT INTO menuprofil VALUES (14,0,FALSE);
INSERT INTO menuprofil VALUES (15,0,FALSE);
INSERT INTO menuprofil VALUES (21,0,FALSE);
INSERT INTO menuprofil VALUES (22,0,FALSE);
INSERT INTO menuprofil VALUES (23,0,FALSE);
INSERT INTO menuprofil VALUES (31,0,FALSE);
INSERT INTO menuprofil VALUES (32,0,FALSE);
INSERT INTO menuprofil VALUES (33,0,FALSE);
INSERT INTO menuprofil VALUES (34,0,FALSE);
INSERT INTO menuprofil VALUES (41,0,FALSE);
INSERT INTO menuprofil VALUES (42,0,FALSE);
INSERT INTO menuprofil VALUES (43,0,FALSE);
INSERT INTO menuprofil VALUES (44,0,FALSE);
INSERT INTO menuprofil VALUES (51,0,FALSE);
INSERT INTO menuprofil VALUES (52,0,FALSE);
INSERT INTO menuprofil VALUES (53,0,FALSE);
INSERT INTO menuprofil VALUES (61,0,FALSE);
INSERT INTO menuprofil VALUES (62,0,FALSE);
INSERT INTO menuprofil VALUES (71,0,FALSE);
INSERT INTO menuprofil VALUES (72,0,FALSE);
INSERT INTO menuprofil VALUES (81,0,FALSE);
INSERT INTO menuprofil VALUES (82,0,FALSE);
INSERT INTO menuprofil VALUES (101,0,FALSE);
INSERT INTO menuprofil VALUES (102,0,FALSE);

-- modifications alexandre delattre (musichalle)
CREATE TYPE planning_fact_type as ENUM (
  'absence', 'remplacement', 'rattrapage', 'activite_baisse', 'activite_sup'
);

CREATE TABLE planning_fact (
  id SERIAL PRIMARY KEY,
  date TIMESTAMP,
  type planning_fact_type,
  planning INT, --REFERENCES planning(id) ON DELETE CASCADE,
  prof INT REFERENCES prof(idper) ON DELETE CASCADE,
  commentaire TEXT,
  duree INTERVAL,
  statut SMALLINT,
  niveau SMALLINT,
  planning_desc TEXT
);

ALTER TABLE planning_fact OWNER TO nobody;

CREATE INDEX planning_fact_planning_idx ON planning_fact (planning);

