--2.11.4
COMMENT ON TABLE categorie_siteweb IS 'Index des différentes catégories de liens (sites web, réseau social, fichier, partage, etc.)';
-- -- see comment with psql : \dt+ categorie_siteweb 
INSERT INTO categorie_siteweb VALUES (DEFAULT,'Fichier');
INSERT INTO categorie_siteweb VALUES (DEFAULT,'Media');
INSERT INTO categorie_siteweb VALUES (DEFAULT,'Partition');
