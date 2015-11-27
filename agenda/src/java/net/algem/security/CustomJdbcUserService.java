/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.algem.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.6.a 25/11/2015
 */
public class CustomJdbcUserService
        extends JdbcDaoImpl
{

  private String customUsersByUsernameQuery = "SELECT l.idper,l.login,l.pass,l.clef,coalesce(p.prenom || ' ', '') || coalesce(p.nom, '') AS nom"
  + " FROM login l INNER JOIN personne p ON (l.idper = p.id) WHERE idper = ?";
  private String customAuthoritiesByUsernameQuery = "SELECT profil FROM login WHERE idper = ?";

  public String getCustomUsersByUsernameQuery() {
    return customUsersByUsernameQuery;
  }

  public void setCustomUsersByUsernameQuery(String customUsersByUsernameQuery) {
    this.customUsersByUsernameQuery = customUsersByUsernameQuery;
  }

  public String getCustomAuthoritiesByUsernameQuery() {
    return customAuthoritiesByUsernameQuery;
  }

  public void setCustomAuthoritiesByUsernameQuery(String customAuthoritiesByUsernameQuery) {
    this.customAuthoritiesByUsernameQuery = customAuthoritiesByUsernameQuery;
  }

  /**
   * Executes the SQL <tt>usersByUsernameQuery</tt> and returns a list of UserDetails objects.
   * There should normally only be one matching user.
   *
   * @param login
   * @return
   */
  @Override
  protected List<UserDetails> loadUsersByUsername(String login) {
    int idper = getIdFromLogin(login);

    return getJdbcTemplate().query(customUsersByUsernameQuery, new Object[]{idper}, new RowMapper<UserDetails>()
    {
      public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        String idAsString = String.valueOf(rs.getString(1)); // idper
        String password = rs.getString(3);
        String salt = rs.getString(4);
        String fullName = rs.getString(5);
        boolean enabled = true;
        if (idAsString == null || idAsString.isEmpty()) {
          idAsString = " ";
        }
        if (password == null || password.isEmpty()) {
          password = " ";
        }
        CustomUser cu = new CustomUser(idAsString, password, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES);
        cu.setSalt(salt == null || salt.isEmpty() ? " " : salt);
        cu.setFullName(fullName);
        return cu;
      }

    });
  }

  protected List<GrantedAuthority> loadUserAuthorities(String login) {
    int idper = getIdFromLogin(login);

    return getJdbcTemplate().query(customAuthoritiesByUsernameQuery, new Object[]{idper}, new RowMapper<GrantedAuthority>()
    {
      public GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
        String roleName = String.valueOf(rs.getInt(1));
        GrantedAuthorityImpl authority = new GrantedAuthorityImpl(roleName);

        return authority;
      }
    });

  }

  @Override
  protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery,
          List<GrantedAuthority> combinedAuthorities) {
    String returnUsername = userFromUserQuery.getUsername();
    CustomUser cu = new CustomUser(returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(),
            true, true, true, combinedAuthorities);
    cu.setSalt(((CustomUser) userFromUserQuery).getSalt());
    cu.setFullName(((CustomUser) userFromUserQuery).getFullName());
    return cu;
  }

  private int getIdFromLogin(String login) {
    try {
      return Integer.parseInt(login);
    } catch (NumberFormatException nfe) {
      return -1;
    }
  }

}
