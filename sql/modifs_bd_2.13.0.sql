ALTER TABLE eleve ALTER archiv SET DEFAULT false;

UPDATE eleve SET profession = 'Aucun(e)' where profession = 'aucun';
