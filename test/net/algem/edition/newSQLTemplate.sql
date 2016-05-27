-- 21651
-- Recherche des emails des adhérents
-- solution 1 7,706 ms
SELECT DISTINCT m.email FROM email m
WHERE m.idper = 21651
AND m.archive = false
UNION
SELECT DISTINCT m.email FROM email m JOIN eleve e ON (m.idper = e.payeur) JOIN personne p ON (e.payeur = p.id)
WHERE e.idper = 21651
AND e.idper NOT IN (SELECT idper FROM  email) -- on ne cherche pas le payeur si l'adhérent a déjà une adresse
AND p.organisation IS NULL -- on ne cherche le payeur que s'il n'appartient pas à une organisation
AND m.archive = false;

-- solution 2  3,402 ms ne fonctionne pas correctement
-- SELECT DISTINCT e.email FROM email e JOIN eleve m
-- ON (e.idper = m.idper OR (e.idper = m.payeur AND m.payeur IN (
-- SELECT id FROM personne WHERE organisation IS NULL)
-- )))
-- WHERE m.idper = 21651 -- 18584(gambier)
-- AND e.archive = false;


SELECT p.* FROM planning p
			WHERE jour = '30-05-2016' AND idper = 19657
			AND ((debut >= '10:00' AND debut < '12:00') OR (fin > '10:00' AND fin <= '12:00')
			OR (debut <= '10:00' AND fin >= '12:00'));