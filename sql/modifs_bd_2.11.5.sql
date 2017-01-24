-- -- 2.11.5
COMMENT ON COLUMN postit.ptype IS '0 = interne, 1 = interne urgent, 2 = externe';
COMMENT ON COLUMN postit.emet IS 'Numéro d''émetteur';
COMMENT ON COLUMN postit.dest IS 'Numéro du destinataire (0 si public)';
COMMENT ON COLUMN postit.jour IS 'Date de création';
COMMENT ON COLUMN postit.echeance IS 'Date d''échéance';
