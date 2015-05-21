<%-- 
/*
 * @(#)index.jsp	1.0.3 25/03/13
 *
 * Copyright (c) 2014 Musiques Tangentes. All Rights Reserved.
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
    @version 1.0.3
    @since 1.0.0 11/02/13
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Agenda Algem</title>
    <link rel="shortcut icon" href="images/favicon.ico">
    <link rel="stylesheet" href="css/algem.css" />
    <script type="text/javascript" src="js/jquery-1.9.0.min.js"></script>
    <script type="text/javascript" src="js/algem.js"></script>
    <script type="text/javascript">
      $(function() {
        init();
      });
    </script>
  </head>

  <body>
    <jsp:useBean id="now" scope="request" class="java.util.Date" />
    <fmt:formatDate scope="request" pattern="dd-MM-yyyy" var="today" value="${now}" />
    <div id="global">
      <header id="headerIndex">
        <h1 style="margin-bottom: 2em"><span style="font-size:smaller;font-style:italic">agenda</span>&nbsp;ALGEM</h1>
        <nav>
          <table style="width:100%">
            <tr>
              <c:forEach var="estab" items="${estabList}">
                <td>
                  <c:url value="today.html" var="todayUrl">
                    <c:param name="d" value="${today}" />
                    <c:param name="e" value="${estab.id}" />
                  </c:url>

                  <a href="${todayUrl}">
                    <img src="images/Algem0_trans.png"/>
                  </a><br />
                  <a href="${todayUrl}">${estab.name}</a>
                </td>
              </c:forEach>
            </tr>
          </table>
        </nav>
        
      </header>
      <footer id="footerIndex">
        <address style="color:#9D964E">
          <img src="images/agplv3-88x31.png" style="vertical-align: middle"/>© 2014 Musiques Tangentes&nbsp;&nbsp;|&nbsp;&nbsp;
        <a href="http://www.algem.net" target="_blank">http://www.algem.net</a>
        </address>
      </footer>

    </div>
  </body>
</html>
