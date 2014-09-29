INSERT INTO menu2 VALUES (145, 'Contact.modification.auth');

INSERT INTO menuprofil VALUES(145,0,FALSE);
INSERT INTO menuprofil VALUES(145,1,FALSE);
INSERT INTO menuprofil VALUES(145,2,FALSE);
INSERT INTO menuprofil VALUES(145,3,FALSE);
INSERT INTO menuprofil VALUES(145,4,TRUE);

INSERT INTO menuaccess SELECT idper, 145, FALSE FROM login WHERE profil = 0;
INSERT INTO menuaccess SELECT idper, 145, FALSE FROM login WHERE profil = 1;
INSERT INTO menuaccess SELECT idper, 145, FALSE FROM login WHERE profil = 2;
INSERT INTO menuaccess SELECT idper, 145, FALSE FROM login WHERE profil = 3;
INSERT INTO menuaccess SELECT idper, 145, TRUE FROM login WHERE profil = 4;
