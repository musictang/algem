ALTER TABLE carteabopersonne ADD constraint carteabopersonne_px primary key(id);
ALTER TABLE carteabopersonne RENAME idcarte TO idpass;

CREATE TABLE carteabopersessions (
	id serial PRIMARY KEY,
	idcarte integer REFERENCES carteabopersonne(id) ON DELETE CASCADE,
	idplanning integer,
	debut time,
	fin time
);

ALTER TABLE absence OWNER TO nobody;

-- Pass repetition individuelle
ALTER TABLE carteaborepet ADD totalmin integer DEFAULT 60;
UPDATE carteaborepet SET totalmin = (nbseances * dureemin);
ALTER TABLE carteaborepet DROP nbseances;

ALTER TABLE version ALTER version TYPE varchar(16);
