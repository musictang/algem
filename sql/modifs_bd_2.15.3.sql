-- 2.15.3
ALTER TABLE reservation ALTER COLUMN dateres SET DEFAULT current_date;
COMMENT ON COLUMN reservation.statut IS '0 = à confirmer, 1 = confirmée, 2 = annulée';
