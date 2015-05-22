<%--
/*
 * @(#)day.jsp	1.0.4 11/05/15
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
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <title id="pageTitle"></title>
    <meta name="viewport" content="width=device-width"> <!-- important -->
    <link rel="shortcut icon" href="img/favicon.ico">
    <link rel="stylesheet" href="css/planning-algem.css" />
    <link rel="stylesheet" href="css/smoothness/jquery-ui-1.10.0.custom.css" />
    <script type="text/javascript" src="js/jquery-1.9.0.min.js"></script>
    <script type="text/javascript" src="js/planning-algem.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.10.0.custom.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui.datepicker-fr.js"></script>
    <script type="text/javascript">
      var estabId = ${estab};
      var currentDate = '${now}';
      $(function() {
        init();
      });
    </script>
  </head>
  <body>
    <spring:message code="home.label" var="msg_home"/>
    <spring:message code="course.collective.label" var="msg_course_co"/>
    <spring:message code="course.used.label" var="msg_course_used"/>
    <spring:message code="group.rehearsal.label" var="msg_group_rehearsal"/>
    <spring:message code="member.rehearsal.label" var="msg_member_rehearsal"/>
    <spring:message code="next.day.label" var="msg_next_day"/>
    <spring:message code="prev.day.label" var="msg_prev_day"/>

    <header>
      <nav>
        <select id="estabSelection" name="estabId" tabindex="4">
          <c:forEach var="estab" items="${estabList}">
            <option value="${estab.id}">${estab.name}</option>
          </c:forEach>
        </select>
        <label id="dow" for="datepicker">${dayName}</label><input type="text" id="datepicker" style="font-size:small" tabindex="3"/>
        &nbsp;&nbsp;<a href="" title="${msg_prev_day}" id="previous" tabindex="2">&lt;&lt;&nbsp;</a>&nbsp;|&nbsp;<a href="" title="${msg_next_day}" id="next" tabindex="1">&nbsp;&gt;&gt;</a>
      </nav>

    </header>
    <aside id="info-bar">
      <img src="img/help-contents.png" id="help" alt="Aide"/>
      <img src="img/tel.png" id="tel" alt="Tel."/>
    </aside>
    <aside id="help-content">
      <ul>
        <li><span class="colorSquare" title="${msg_course_co}" style="background-color:#FF3333"></span>&nbsp;${msg_course_co}</li>
        <li><span class="colorSquare colorHelp" style="background-color:#00D059;"></span>&nbsp;<spring:message code="course.available.label" />
          <span style="font-size: smaller">(<spring:message code="course.available.tip" />)</span></li>
        <li><span class="colorSquare" title="${msg_course_used}" style="background-color:#80e82c"></span>&nbsp;${msg_course_used}</li>
        <li><span class="colorSquare" title="${msg_group_rehearsal}" style="background-color:#2158FF"></span>&nbsp;${msg_group_rehearsal}</li>
        <li><span class="colorSquare" title="${msg_member_rehearsal}" style="background-color:#3399FF"></span>&nbsp;${msg_member_rehearsal}</li>
      </ul>
      <img id="help-close" class="bt-close" alt="Fermer" src="img/close.png"/>
    </aside>
    <aside id="tel-content">
      <ul>
        <spring:eval var="horaires" expression="@horaires" />
        <c:forEach  items="${horaires}" var="entry">
          <li><c:out value="${entry.key}" />
            <%--<c:forEach items="${entry.value}" var="h" >--%>
            <c:set var="n" value="${fn:substringAfter(entry.value[0], '0')}" scope="request"/>
            &nbsp;:&nbsp;<a href="tel:+33${fn:replace(n," ", "")}">${entry.value[0]}</a> ${entry.value[1]}
            <%--</c:forEach>--%>
          </li>
        </c:forEach>
      </ul>
      <img id="tel-close" class="bt-close" alt="Fermer" src="img/close.png"/>
    </aside>
    <div id="colorInfo">
      <div id="contact">
        <span class="colorSquare" title="Horaires" style="background-color:white;font-size:larger">&#9742;</span>
        &nbsp;Horaires&nbsp;|&nbsp;
        <ul id="timetable" class="tip" style="width:460px;">
          <spring:eval var="horaires" expression="@horaires" />
          <c:forEach  items="${horaires}" var="entry">
            <li><c:out value="${entry.key}" />&nbsp;:&nbsp;<c:out value="${entry.value}" /></li>
            </c:forEach>
        </ul>
      </div>
    </div>
    <table id="grid">
      <%--<tr><th>08:00</th></tr>
        <tr><th>08:30</th></tr>--%>
      <tr><th><p>09:00</p></th></tr>
      <tr><th><p>09:30</p></th></tr>
      <tr><th><p>10:00</p></th></tr>
      <tr><th><p>10:30</p></th></tr>
      <tr><th><p>11:00</p></th></tr>
      <tr><th><p>11:30</p></th></tr>
      <tr><th><p>12:00</p></th></tr>
      <tr><th><p>12:30</p></th></tr>
      <tr><th><p>13:00</p></th></tr>
      <tr><th><p>13:30</p></th></tr>
      <tr><th><p>14:00</p></th></tr>
      <tr><th><p>14:30</p></th></tr>
      <tr><th><p>15:00</p></th></tr>
      <tr><th><p>15:30</p></th></tr>
      <tr><th><p>16:00</p></th></tr>
      <tr><th><p>16:30</p></th></tr>
      <tr><th><p>17:00</p></th></tr>
      <tr><th><p>17:30</p></th></tr>
      <tr><th><p>18:00</p></th></tr>
      <tr><th><p>18:30</p></th></tr>
      <tr><th><p>19:00</p></th></tr>
      <tr><th><p>19:30</p></th></tr>
      <tr><th><p>20:00</p></th></tr>
      <tr><th><p>20:30</p></th></tr>
      <tr><th><p>21:00</p></th></tr>
      <tr><th><p>21:30</p></th></tr>
      <tr><th><p>22:00</p></th></tr>
      <tr><th><p>22:30</p></th></tr>
      <tr><th><p>23:00</p></th></tr>
      <tr><th><p>23:30</p></th></tr>
    </table>

    <section id="canvas">
      <%-- Temps total affiché dans la grille --%>
      <c:set var="totalTime" value="900" />
      <%-- Décalage à partir de 0h --%>
      <c:set var="timeOffset" value="540" />
      <c:forEach var="entry" items="${planning}" >
        <div class="schedule_col">
          <p class="title_col">${entry.value[0].roomName}</p>
          <c:forEach var="p" items="${entry.value}">
            <%-- calculer position et hauteur planning --%>
            <%-- <c:set var="position" value="${(p.minutes - 480)/30 *  3.125}%"/> --%>
            <c:set var="pos" value="${(p.minutes - timeOffset) * 100 / totalTime}%"/>
            <c:set var="h" value="${(p.length -2) * 100 / totalTime}%"/>
            <div class="schedule" style="top:${pos};height:${h};background-color:${p.htmlColor}">${p.label}</div>
          </c:forEach>
          <%-- affichage plages horaires --%>
          <c:forEach var="p" items="${entry.value}">
            <c:forEach var="slot" items="${p.ranges}">
              <c:set var="pos" value="${(slot.minutes - timeOffset) * 100 / totalTime}%"/>
              <c:set var="h" value="${(slot.length -2) * 100 / totalTime}%"/>
              <div class="schedule range" style="top:${pos};height:${h};"></div>
            </c:forEach>
          </c:forEach>
        </div>

      </c:forEach>
      <%-- salles libres --%>
      <c:forEach var="room" items="${freeroom}" >
        <div class="schedule_col">
          <p class="title_col">${room.name}</p>
        </div>
      </c:forEach>
    </section>
    <footer>
      <address>
        ©&nbsp;2014&nbsp;Musiques&nbsp;Tangentes&nbsp;AGPL&nbsp;v.3&nbsp;| <a href="http://www.algem.net" target="_blank">http://www.algem.net</a>
      </address>
    </footer>
  </body>
</html>
