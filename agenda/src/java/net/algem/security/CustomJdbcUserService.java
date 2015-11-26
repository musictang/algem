/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.algem.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.6.a 25/11/2015
 */
public class CustomJdbcUserService extends JdbcDaoImpl {

  private static final String CUSTOM_USERS_BY_USERNAME_QUERY
    = "SELECT p.prenom,p.nom,l.pass,l.clef FROM login l, INNER JOIN person p ON (l.idper = p.id) WHERE l.idper = ? OR l.login = ?";
  private static final String CUSTOM_AUTHORITIES_BY_USERNAME_QUERY
    = "SELECT p.prenom,p.nom,l.profil FROM login l, INNER JOIN person p ON (l.idper = p.id) WHERE l.idper = ? OR l.login = ?";

  private String authoritiesByUsernameQuery;
  private String groupAuthoritiesByUsernameQuery;
  private String usersByUsernameQuery;

  public CustomJdbcUserService() {
    usersByUsernameQuery = CUSTOM_USERS_BY_USERNAME_QUERY;
    authoritiesByUsernameQuery = CUSTOM_AUTHORITIES_BY_USERNAME_QUERY;
    groupAuthoritiesByUsernameQuery = DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * Executes the SQL <tt>usersByUsernameQuery</tt> and returns a list of UserDetails objects.
   * There should normally only be one matching user.
   */
  protected List<UserDetails> loadUsersByUsername(String login) {
    return getJdbcTemplate().query(usersByUsernameQuery, new String[]{login}, new RowMapper<UserDetails>() {
      public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        String username = rs.getString(1) + rs.getString(2);
        String password = rs.getString(3);
        String salt = rs.getString(4);
        boolean enabled = true;
        CustomUser cu = new CustomUser(username, password, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES);
        cu.setSalt(salt);
        return cu;
      }

    });
  }


}
