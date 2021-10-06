/*
 * @(#)RumGenerator.java	2.8.t 07/05/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.accounting;

import java.io.*;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for generation of rum numbers.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.t
 * @since 2.8.r 29/12/13
 */
public class RumGenerator {

    private static SecureRandom random = new SecureRandom();
    private static Pattern datePattern = Pattern.compile("[0-3][0-9]-[0-1][0-9]-2[0-9]{3}");
    private static final int LEADING = 0;
    private static final int TRAILING = 1;

    public static void main(String... args) {

        if (args.length < 1) {
            System.err.println("Erreur : Nombre d'arguments incorrect !");
            System.err.println("Usage : java RumGenerator <dateecheance> < fichier_payeurs.txt");
            System.exit(1);
        }

        String signDate = args[0];
        Matcher m = datePattern.matcher(signDate);
        if (!m.matches()) {
            System.err.println("Erreur : Date de signature (ou d'échéance) incorrecte !");
            System.err.println("Date de signature (ou d'échéance) au format : jj-mm-aaaa");
            System.exit(1);
        }

//    String ics = args[1];
        try (
                FileOutputStream os = new FileOutputStream(new File("mandats.sql"));
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));) {
            String line;
            while ((line = in.readLine()) != null) {
                String sql = generateMigrationSQL(line, signDate) + System.getProperty("line.separator");
                os.write(sql.getBytes());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        //-- 56 | 1234 | 2013-01-02 | 2014-01-15 | TRUE | FRST | M1446136132 150114 1234 |
    }

    static String generateRum(String idper, String signDate) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat df2 = new SimpleDateFormat("ddMMyy");
        Date d = null;
        try {
            d = df.parse(signDate);
        } catch (ParseException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        String r = String.valueOf(Math.abs(random.nextInt()));
        return "M"
                + padWithLeadingZeros(r, 10)
                + " " + df2.format(d)
                + " " + idper;
    }

    private static String generateMigrationSQL(String idper, String signDate) {
        return idper + ";NULL;" + signDate + ";TRUE;FRST;++" + generateRum(idper, signDate);
    }

    private static String pad(String chaine, int size, char c, int where) {

        if (chaine == null) {
            chaine = "";
        }

        String resultat = chaine;
        int numSpaces = size - chaine.length();
        if (numSpaces > 0) {
            for (int i = 0; i < numSpaces; i++) {
                if (where == TRAILING) {
                    resultat += c;
                } else {
                    resultat = c + resultat;
                }
            }
        }
        return resultat;
    }

    public static String padWithLeadingZeros(String chaine, int size) {
        return pad(chaine, size, '0', LEADING);
    }
}
