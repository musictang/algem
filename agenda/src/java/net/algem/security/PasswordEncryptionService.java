/*
 * @(#)PasswordEncryptionService.java	1.0.6 18/11/15
 *
 * Copyright (c) 2015 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of AlgemWebApp.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Utility class for password encryption.
 * This class stems from a sample of Jerry Orr:
 * @see <a href="http://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html">secure-password-storage-donts-dos-and</a>.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.6
 * @since 1.0.6 18/11/15
 */
class PasswordEncryptionService
  implements EncryptionService
{

  @Override
  public boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt)
          throws UserException {
    // Encrypt the clear-text password using the same salt that was used to
    // encrypt the original password
    byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);
    //System.out.println("encoded :"+Arrays.toString(encryptedAttemptedPassword));
    // Authentication succeeds if encrypted password that the user entered
    // is equal to the stored hash
    return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
  }


  public byte[] getEncryptedPassword(String password, byte[] salt) throws UserException
           {
    try {
      // PBKDF2 with SHA-1 as the hashing algorithm. Note that the NIST
      // specifically names SHA-1 as an acceptable hashing algorithm for PBKDF2
      String algorithm = "PBKDF2WithHmacSHA1";
      // SHA-1 generates 160 bit hashes, so that's what makes sense here
      int derivedKeyLength = 160;
      // Pick an iteration count that works for you. The NIST recommends at
      // least 1,000 iterations:
      // http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf
      // iOS 4.x reportedly uses 10,000:
      // http://blog.crackpassword.com/2010/09/smartphone-forensics-cracking-blackberry-backup-passwords/
      int iterations = 20000;

      KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);

      SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

      return f.generateSecret(spec).getEncoded();
    } catch (NoSuchAlgorithmException ex) {
      throw new UserException(ex.getMessage());
    } catch (InvalidKeySpecException ex) {
      throw new UserException(ex.getMessage());
    }
  }


 private byte[] generateSalt() throws UserException {
    try {
      // VERY important to use SecureRandom instead of just Random
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

      // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
      byte[] salt = new byte[8];
      random.nextBytes(salt);

      return salt;
    } catch (NoSuchAlgorithmException ex) {
      throw new UserException(ex.getMessage());
    }

  }

  @Override
  public UserPass createPassword(String pass) throws UserException {
      byte[] salt;
      salt = generateSalt();
      return new UserPass(getEncryptedPassword(pass, salt), salt);
  }

}