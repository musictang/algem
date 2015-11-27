<%--
/*
 * @(#)index.jsp	1.0.4 26/05/15
 *
 * Copyright (c) 2015 Musiques Tangentes. All Rights Reserved.
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
    @version 1.0.4
    @since 1.0.0 11/02/13
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<spring:eval var="org" expression="@organisation" />
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>planning ${org['nom']}</title>
    <meta name="viewport" content="width=device-width"> <!-- important -->
    <!-- Les resources statiques (images, css) doivent être placées en dehors de WEB-INF -->
    <spring:url value="/resources/themes/default/img" var="img_dir" />
    <spring:url value="/resources/themes/default/css" var="css_dir" />
    <link rel="stylesheet" href="${css_dir}/common.css" />
    <link rel="stylesheet" href="${css_dir}/planning.css" />
    <link rel="stylesheet" href="http://yui.yahooapis.com/pure/0.6.0/pure-min.css">
    <link rel="shortcut icon" href="${img_dir}/favicon.ico" />
    <style>
      /*body {background-position: left 690px;}*/
      h1 {
        font-family: Verdana,Arial,sans-serif;
        font-size: 2.5em;
        padding: 0.4em;
        /*color: #b66000;*/
        color: ivory;
        text-shadow: 1px 1px 1px rgba(0, 0, 0, 1);
      }

      h1 > span {
        font-family: "Times new roman",serif;
        font-size:smaller;
        font-style:italic
      }

      div.page {
        margin: 0 auto;
        width:100%;
        text-align: center;
        height:640px;
        background: #fff8dc; /* Old browsers */
        background: -moz-linear-gradient(top, #fff8dc 0%, #ffc65d 100%); /* FF3.6+ */
        background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#fff8dc), color-stop(100%,#ffc65d)); /* Chrome,Safari4+ */
        background: -webkit-linear-gradient(top, #fff8dc 0%,#ffc65d 100%); /* Chrome10+,Safari5.1+ */
        background: -o-linear-gradient(top, #fff8dc 0%,#ffc65d 100%); /* Opera 11.10+ */
        background: -ms-linear-gradient(top, #fff8dc 0%,#ffc65d 100%); /* IE10+ */
        background: linear-gradient(to bottom, #fff8dc 0%,#ffc65d 100%); /* W3C */
        filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#fff8dc', endColorstr='#ffc65d',GradientType=0 ); /* IE6-9 */
      }

      div.estab {
        display: inline-block;
        margin:2em;
        text-align: center;
      }

      .center {
        text-align: center;
      }
    </style>
  </head>

  <body>
    <jsp:useBean id="now" scope="request" class="java.util.Date" />
    <fmt:formatDate scope="request" pattern="dd-MM-yyyy" var="today" value="${now}" />

    <header class="center"><h1><span>planning</span>&nbsp;${org['nom']}</h1></header>
    <div class="page">
      <c:forEach var="estab" items="${estabList}">
        <c:url value="daily.html" var="todayUrl">
          <c:param name="d" value="${today}" />
          <c:param name="e" value="${estab.id}" />
        </c:url>
        <div class="estab">
          <a href="${todayUrl}">
            <img src="${img_dir}/Algem0_trans.png" alt="agenda"/>
          </a><br />
          <a href="${todayUrl}">${estab.name}</a>
        </div>
      </c:forEach>

    </div>
    <footer style="height:3em">
      <address>
        ©&nbsp;2015&nbsp;Musiques&nbsp;Tangentes&nbsp;AGPL&nbsp;v.3&nbsp;| <a href="http://www.algem.net" target="_blank">http://www.algem.net</a>
      </address>
    </footer>
  </body>
</html>
