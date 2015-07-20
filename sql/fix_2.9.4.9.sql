DELETE FROM menuprofil WHERE oid IN(SELECT p1.oid FROM menuprofil p1, menuprofil p2 WHERE p1.profil = 0 AND p1.profil = p2.profil AND p1.idmenu = p2.idmenu AND p1.oid > p2.oid);
