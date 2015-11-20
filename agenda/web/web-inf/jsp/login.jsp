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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>JSP Page</title>
<!--		les resources statiques (images, css) doivent être placées en dehors de WEB-INF-->
		<link rel="stylesheet" href="css/springtest.css" />
	</head>
	<body>

		<sf:form modelAttribute="user" action="login.html" method="post">
			<fieldset><legend>Formulaire de login</legend>
				<label for="login"><spring:message code="login.label" text="login" /></label>
				<sf:input  path="login" id="login"/><br />
				<sf:errors cssClass="error" path="login" />
				<%--<label for="profile"><spring:message code="profile.label" /></label>
				<sf:select path="profile" id="profile" >
					<c:forEach items="${profiles}" var="name">
						<option value="${name}">${name}</option>
					</c:forEach>

				</sf:select><br />--%>
				<spring:message code="submit.label" var="submitText"/>
				<input type="submit" value="${submitText}" />
				</fieldset>
			</sf:form>
		<span class="error"><spring:message text="${unknown}"  /></span>
		<table>
		<c:forEach var="u" items="${userlist}" >
			<tr>
				<th>${u.id}</th>
				<td><c:out value="<a href='user.html?id=${u.id}'>${u.login}</a>" /></td>
				<td><c:out value="${u.profile}" /></td>
			</tr>
		</c:forEach>
		</table>
	</body>
</html>
