
-- CREATE FUNCTION is_in_year()
-- RETURNS TRIGGER AS $$
-- declare
    -- debut date := (select valeur::date from public.config where clef = 'Date.DebutAnnee');
    -- now date := (select CURRENT_DATE);
-- BEGIN
  -- IF debut >= CURRENT_DATE THEN
    -- RAISE NOTICE 'annee en cours %, %', now, debut;
    -- ELSE
    -- RAISE NOTICE 'avant annee %, %', now, debut;
  -- END IF;
  -- return null;
-- END$$ LANGUAGE 'plpgsql';

-- CREATE TRIGGER inc_numero_piece
-- AFTER UPDATE ON config
-- FOR EACH STATEMENT
-- EXECUTE PROCEDURE is_in_year();

-- create table histo_echeance 
-- id serial,
 -- date_mod timestamp
