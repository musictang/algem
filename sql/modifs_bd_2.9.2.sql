ALTER TABLE carteabopersonne ADD constraint carteabopersonne_px primary key(id);
ALTER TABLE carteabopersonne RENAME idcarte TO idpass;

CREATE TABLE carteabopersessions (
	id serial PRIMARY KEY,
	idcarte integer REFERENCES carteabopersonne(id) ON DELETE CASCADE,
	idplanning integer,
	debut time,
	fin time
);

ALTER TABLE carteabopersessions OWNER TO nobody;

-- ALTER TABLE absence OWNER TO nobody;
DROP TABLE IF EXISTS absence;

-- Pass repetition individuelle
ALTER TABLE carteaborepet ADD totalmin integer DEFAULT 60;
UPDATE carteaborepet SET totalmin = (nbseances * dureemin);
ALTER TABLE carteaborepet DROP nbseances;

ALTER TABLE version ALTER version TYPE varchar(16);

DROP TABLE IF EXISTS atelier_instruments;
CREATE TABLE atelier_instruments (
	idaction integer REFERENCES action(id) ON DELETE CASCADE,
	idper integer REFERENCES personne(id) ON DELETE CASCADE,
	idinstru integer,
PRIMARY KEY (idaction,idper)
);

ALTER TABLE atelier_instruments OWNER TO nobody;

