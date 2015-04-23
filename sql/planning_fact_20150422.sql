CREATE TYPE planning_fact_type as ENUM (
  'absence', 'remplacement', 'rattrapage', 'activite_baisse', 'activite_sup'
);

CREATE TABLE planning_fact (
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

CREATE INDEX planning_fact_planning_idx ON planning_fact (planning);