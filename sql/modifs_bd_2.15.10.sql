--2.15.10

ALTER TABLE echeancier2 ALTER COLUMN piece TYPE varchar(12);
COMMENT ON COLUMN person_instrument.ptype IS 'MEMBER = 1; TEACHER = 2; MUSICIAN = 3';
