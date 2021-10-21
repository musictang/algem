/*
 * @(#)User.java	2.11.5 11/01/17
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
package net.algem.security;

import net.algem.contact.Person;

/**
 * Algem user. An user is defined by a default profile.
 *
 * @see net.algem.security.UserIO
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.11.5
 */
public class User
        extends Person {

    private static final long serialVersionUID = -3336222510237928108L;
    private String login;
    private transient String password;
    private int profile;
    private transient UserPass pass;
    private int desktop;
    private String emailAgent;
    private String webAgent;
    private String textAgent;
    private String tableAgent;

    public User() {
    }

    public User(Person p) {
        id = p.getId();
        name = p.getName();
        firstName = p.getFirstName();
        gender = p.getGender();
    }

    public int getDesktop() {
        return desktop;
    }

    public void setDesktop(int desktop) {
        this.desktop = desktop;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Person p = (Person) obj;
        if (!super.equals(p)) {
            return false;
        }
        final User other = (User) obj;
        if ((this.login == null) ? (other.login != null) : !this.login.equals(other.login)) {
            return false;
        }
        if (this.profile != other.profile) {
            return false;
        }
        /*
        if (this.pass != other.pass && (this.pass == null || !this.pass.equals(other.pass))) {
            return false;
        }
        */
        if (this.desktop != other.desktop) {
            return false;
        }
        if (this.emailAgent != other.emailAgent && (this.emailAgent == null || !this.emailAgent.equals(other.emailAgent))) {
            return false;
        }
        if (this.webAgent != other.webAgent && (this.webAgent == null || !this.webAgent.equals(other.webAgent))) {
            return false;
        }
        if (this.textAgent != other.textAgent && (this.textAgent == null || !this.textAgent.equals(other.textAgent))) {
            return false;
        }
        if (this.tableAgent != other.tableAgent && (this.tableAgent == null || !this.tableAgent.equals(other.tableAgent))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.login != null ? this.login.hashCode() : 0);
        hash = 17 * hash + this.profile;
        hash = 17 * hash + (this.pass != null ? this.pass.hashCode() : 0);
        hash = 17 * hash + this.desktop;
        hash = 17 * hash + (this.emailAgent != null ? this.emailAgent.hashCode() : 0);
        hash = 17 * hash + (this.webAgent != null ? this.webAgent.hashCode() : 0);
        hash = 17 * hash + (this.textAgent != null ? this.textAgent.hashCode() : 0);
        hash = 17 * hash + (this.tableAgent != null ? this.tableAgent.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + login + ")";
    }

    void setLogin(String s) {
        login = s;
    }

    public String getLogin() {
        return login;
    }

    void setPassword(String s) {
        password = s;
    }

    public String getPassword() {
        return password;
    }

    void setProfile(int i) {
        profile = i;
    }

    public int getProfile() {
        return profile;
    }

    UserPass getPassInfo() {
        return pass;
    }

    void setPassInfo(UserPass pass) {
        this.pass = pass;
    }

    public String getEmailAgent() {
        return emailAgent;
    }

    public void setEmailAgent(String emailAgent) {
        this.emailAgent = emailAgent;
    }

    public String getWebAgent() {
        return webAgent;
    }

    public void setWebAgent(String webAgent) {
        this.webAgent = webAgent;
    }

    public String getTextAgent() {
        return textAgent;
    }

    public void setTextAgent(String textAgent) {
        this.textAgent = textAgent;
    }

    public String getTableAgent() {
        return tableAgent;
    }

    public void setTableAgent(String tableAgent) {
        this.tableAgent = tableAgent;
    }

}
