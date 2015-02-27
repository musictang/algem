CREATE TABLE situation_familiale(
  id serial PRIMARY KEY,
  code char(1) UNIQUE,
  libelle varchar(64)
);
ALTER TABLE situation_familiale OWNER TO nobody;

INSERT INTO situation_familiale VALUES(DEFAULT, 'C', 'Célibataire');
INSERT INTO situation_familiale VALUES(DEFAULT, 'M', 'Marié');
INSERT INTO situation_familiale VALUES(DEFAULT, 'D', 'Divorcé');
INSERT INTO situation_familiale VALUES(DEFAULT, 'S', 'Séparé');
INSERT INTO situation_familiale VALUES(DEFAULT, 'V', 'Voeuf');
INSERT INTO situation_familiale VALUES(DEFAULT, 'W', 'Vie maritale');
INSERT INTO situation_familiale VALUES(DEFAULT, 'P', 'Pacs');


ALTER TABLE salarie ADD sitfamiliale integer;
ALTER TABLE salarie ADD enfants date[];
