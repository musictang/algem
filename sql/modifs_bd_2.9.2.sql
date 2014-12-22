ALTER TABLE carteabopersonne ADD constraint carteabopersonne_px primary key(id);
ALTER TABLE carteabopersonne RENAME idcarte TO idpass;

CREATE TABLE carteabopersessions (
	id serial PRIMARY KEY,
	idcarte integer REFERENCES carteabopersonne(id) ON DELETE CASCADE,
	idplanning integer
);