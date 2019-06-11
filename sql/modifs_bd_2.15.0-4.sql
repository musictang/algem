ALTER TABLE eleve ADD COLUMN archive boolean DEFAULT FALSE;
UPDATE eleve SET archive = archiv;
ALTER TABLE eleve DROP COLUMN archiv;
