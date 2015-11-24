<%--
/*
 * @(#)login.jsp	1.0.0 11/02/13
 *
 * Copyright (c) 2013 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
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
--%>
<%--
    @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
    @version 1.0.0
    @since 1.0.0 11/02/13
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <title id="Connexion"></title>
    <meta name="viewport" content="width=device-width"> <!-- important -->
    <link rel="shortcut icon" href="img/favicon.ico">
    <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.6.0/pure-min.css">
    <link rel="stylesheet" href="css/common.css" />
    <!--    <link rel="stylesheet" href="css/planning-algem.css" />-->
    <link rel="stylesheet" href="css/smoothness/jquery-ui-1.10.0.custom.css" />
    <!--		les resources statiques (images, css) doivent être placées en dehors de WEB-INF-->
  </head>
  <body>
    <div class="centered">
      <sf:form modelAttribute="user" action="login.html" method="post" cssClass="hpadded pure-form pure-form-stacked">
        <fieldset><legend>Saisissez vos identifiants</legend>
          <label for="login"><spring:message code="login.label" text="login" /></label>
          <%-- Les messages d'erreur doivent être affichés à l'intérieur du formulaire --%>
          <sf:input path="login" id="login" required="required" />
          <sf:errors path="login" cssClass="error"/>
          <label for="password"><spring:message code="password.label" text="password" /></label>
          <sf:password path="password" id="password" required="required" />
          <sf:errors path="password" cssClass="error"/>
          <spring:message code="submit.label" var="submitText"/>
          <input type="submit" value="${submitText}" class="button-xlarge pure-button pure-button-primary"/>
        </fieldset>
      </sf:form>
      <span class="error"><spring:message code="${unknown}" text="${unknown}" /></span>
    </div>

    <%--<table>
      <c:forEach var="u" items="${userlist}" >
        <tr>
          <th>${u.id}</th>
          <td><c:out value="<a href='user.html?id=${u.id}'>${u.login}</a>" /></td>
          <td><c:out value="${u.profile}" /></td>
        </tr>
      </c:forEach>
  
    </table>
    --%>
  </body>
</html>
