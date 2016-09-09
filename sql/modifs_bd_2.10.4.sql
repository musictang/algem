-- 2.10.4
--ALTER TABLE suivi DROP exc;
--ALTER TABLE suivi DROP abs;
ALTER TABLE suivi ADD note varchar(8);
ALTER TABLE suivi ADD statut smallint DEFAULT 0;
COMMENT ON COLUMN suivi.statut IS '0 = PRE (présent), 1 = ABS (absent), 2 = EXC (excusé)';
