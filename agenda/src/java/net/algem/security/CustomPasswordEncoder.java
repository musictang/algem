/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.security;

import org.apache.commons.codec.binary.Base64;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.encoding.PasswordEncoder;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.6.a 25/11/2015
 */
public class CustomPasswordEncoder
  implements PasswordEncoder
{

  private final PasswordEncryptionService service = new PasswordEncryptionService();

  @Override
  public String encodePassword(String rawPass, Object salt) throws DataAccessException {
    byte[] crypted = null;
    try {
      byte[] b64salt = Base64.decodeBase64((String) salt);
      crypted = service.getEncryptedPassword(rawPass, b64salt);
    } catch (UserException ex) {
      return "";
    }
    return Base64.encodeBase64String(crypted);
  }

  @Override
  public boolean isPasswordValid(String encodedPass, String rawPass, Object salt) throws DataAccessException {
    String rawEncoded = encodePassword(rawPass, salt);
    return rawEncoded.equals(encodedPass);
  }
  
}
