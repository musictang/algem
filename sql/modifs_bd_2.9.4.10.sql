-- 2.9.4.10
ALTER TABLE menuprofil ADD CONSTRAINT menuprofil_pk PRIMARY KEY(idmenu,profil);
ALTER TABLE menuaccess ADD CONSTRAINT menuaccess_pk PRIMARY KEY(idper,idmenu);
