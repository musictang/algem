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
    <spring:url value="/resources/themes/default/img" var="img_dir" />
    <spring:url value="/resources/themes/default/css" var="css_dir" />
    <link rel="stylesheet" href="${css_dir}/common.css" />
    <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.6.0/pure-min.css">
    <%-- <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/themes/default/css/common.css" />--%>
  </head>
  <body>
    <div class="centered">
      <c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
        <p class="error"><c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.</p>
    </c:if>
      <form  action="j_spring_security_check" method="post" class="hpadded pure-form pure-form-stacked">
        <fieldset><legend>Saisissez vos identifiants</legend>
          <label for="login"><spring:message code="form.username.label" text="Login" /></label>
          <%-- Les messages d'erreur doivent être affichés à l'intérieur du formulaire --%>
          <input id="login" required="required" name='j_username'/>
          <label for="password"><spring:message code="form.password.label" text="Password" /></label>
          <input type="password" id="password" required="required" name='j_password'/>
          <spring:message code="login.submit.label" var="submitText"/>
          <input type="submit" value="${submitText}" class="button-xlarge pure-button pure-button-primary"/>
        </fieldset>
      </form>
      <%-- <span class="error"><spring:message code="${unknown}" text="${unknown}" /></span> --%>
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
