<%-- 
/*
 * @(#)welcome.jsp	1.0.0 11/02/13
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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><spring:message code="homepage.label" text="perso" /></title>
    <meta name="viewport" content="width=device-width"> <!-- important -->
    <spring:url value="/resources/themes/default/img" var="img_dir" />
    <spring:url value="/resources/themes/default/css" var="css_dir" />
    <link rel="stylesheet" href="${css_dir}/common.css" />
    <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.6.0/pure-min.css">
  </head>
  <body>
    <div class="centered">
      <h1>Mes Informations</h1>
      <table class="vertical-header">
        <tr><th><spring:message code="name.label" /></th><td><sec:authentication property="principal.username"/></td></tr>
        <tr><th><spring:message code="login.label" /></th><td><sec:authentication property="principal.username"/></td></tr>
        <tr><th><spring:message code="profile.label" /></th><td><sec:authentication property="principal.username"/></td></tr>
      </table>
    </div>
    <table>	
      <c:forEach items="${acl}" var="map">
        <c:forEach items="${map}" var="entry">
          <tr><td>${entry.key}</td><td>${entry.value}</td></tr>
        </c:forEach>
      </c:forEach>
    </table>
  </body>
</html>
