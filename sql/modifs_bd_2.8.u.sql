INSERT INTO menu2 VALUES (144,'Payment.multiple.modification.auth');
INSERT INTO menuprofil VALUES (144,0,FALSE);
INSERT INTO menuprofil VALUES (144,1,FALSE);
INSERT INTO menuprofil VALUES (144,2,FALSE);
INSERT INTO menuprofil VALUES (144,3,FALSE);
INSERT INTO menuprofil VALUES (144,4,TRUE);

INSERT INTO menuaccess SELECT idper,144, FALSE FROM login WHERE profil IN(0,1,2,3);
INSERT INTO menuaccess SELECT idper,144, TRUE FROM login WHERE profil = 4;
