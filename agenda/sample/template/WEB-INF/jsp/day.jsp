<%--
/*
 * @(#)day.jsp	1.0.3 07/03/14
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
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <title id="pageTitle"></title>
    <link rel="shortcut icon" href="images/favicon.ico">
    <link rel="stylesheet" href="css/algem.css" />
    <link rel="stylesheet" href="css/smoothness/jquery-ui-1.10.0.custom.css" />
    <script type="text/javascript" src="js/jquery-1.9.0.min.js"></script>
    <script type="text/javascript" src="js/algem.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.10.0.custom.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui.datepicker-fr.js"></script>
    <script type="text/javascript">
      var estabId = ${estab};
      var currentDate = '${now}';

      $(function() {
        init();
        var picker = $("#datepicker");
        //picker.datepicker({ appendText: "(jj-mm-yyyy)", changeMonth: true, changeYear: true })
        picker.datepicker({changeMonth: true, changeYear: true, autoSize: true});
        picker.datepicker('setDate',currentDate);
        picker.datepicker("refresh");
        $('#estabSelection').val(estabId);
        document.title = 'Agenda ' + $('#estabSelection option:selected').text();
        picker.change(function(){
          window.location='today.html?d='+ this.value + '&e='+ estabId;
        });

        // Next Day Link
        $('a#next').click(function () {
          var date = new Date(picker.datepicker('getDate'));
          date.setDate(date.getDate()+1);
          picker.datepicker('setDate', date).change();
          return false;
        });

        // Previous Day Link
        $('a#previous').click(function () {
          var date = new Date(picker.datepicker('getDate'));
          date.setDate(date.getDate()-1);
          picker.datepicker('setDate', date).change();
          return false;
        });

        $('#estabSelection').change(function(){
          var eId = $('#estabSelection option:selected').val();
          window.location='today.html?d='+ $("#datepicker").val() + '&e='+ eId;
        })

        $('#colorHelp').hover(function() {
          $(this).css('cursor','pointer');
          $('#help').toggle()
        })

        $('#contact').hover(function() {
          $(this).css('cursor','pointer');
          $('#timetable').toggle()
        })
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
      <nav style="float: left">
<!--        <a href="index.html" title="${msg_home}"><img id="logo" src="images/Algem0_trans_48x48.png" style="vertical-align: middle" /></a>-->
        <select id="estabSelection" name="estabId" tabindex="4">
          <c:forEach var="estab" items="${estabList}">
            <option value="${estab.id}">${estab.name}</option>
          </c:forEach>
        </select>
        <label id="dow" for="datepicker">${dayName}</label><input type="text" id="datepicker" style="font-size:small" tabindex="3"/>
        &nbsp;&nbsp;&nbsp;&nbsp;<a href="" title="${msg_prev_day}" id="previous" tabindex="2">&lt;&lt;&nbsp;</a>&nbsp;|&nbsp;<a href="" title="${msg_next_day}" id="next" tabindex="1">&nbsp;&gt;&gt;</a>
      </nav>

      <%--<p id="colorCode" style="cursor:pointer; text-decoration: underline;font-size: x-small">Code couleurs</p> --%>

      <!--<span class="colorSquare" style="background-color:#FCD300">FCD300</span>	<span class="colorSquare" style="background-color:#BDA62F">BDA62F</span>	<span class="colorSquare" style="background-color:#A48900">A48900</span>	<span class="colorSquare" style="background-color:#FEDF3F">FEDF3F</span>	<span class="colorSquare" style="background-color:#FEE772">FEE772</span>-->
      <!--<span class="colorSquare" style="background-color:#3C13AD">3C13AD</span>	<span class="colorSquare" style="background-color:#422B82">422B82</span>	<span class="colorSquare" style="background-color:#220670">220670</span>	<span class="colorSquare" style="background-color:#6D47D6">6D47D6</span>	<span class="colorSquare" style="background-color:#896DD6">896DD6</span>-->

    </header>
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
      <span class="colorSquare" title="${msg_course_co}" style="background-color:#FF3333"></span>&nbsp;${msg_course_co}
      <div id="colorHelp" style="position: relative; display: inline-block;">
        <span class="colorSquare colorHelp" style="background-color:#00D059;">?</span>&nbsp;<u><spring:message code="course.available.label" /></u>
        <div id="help" class="tip" style="width: 200px"><spring:message code="course.available.tip" /></div>
      </div>
      <span class="colorSquare" title="${msg_course_used}" style="background-color:#80e82c"></span>&nbsp;${msg_course_used}
      <span class="colorSquare" title="${msg_group_rehearsal}" style="background-color:#2158FF"></span>&nbsp;${msg_group_rehearsal}
      <span class="colorSquare" title="${msg_member_rehearsal}" style="background-color:#3399FF"></span>&nbsp;${msg_member_rehearsal}
    </div>

    <section id="calendar">
      <!-- Grille horaire -->
      <table id="grid">
        <%--<tr><th>08:00</th></tr>
        <tr><th>08:30</th></tr>--%>
        <tr><th>09:00</th></tr>
        <tr><th>09:30</th></tr>
        <tr><th>10:00</th></tr>
        <tr><th>10:30</th></tr>
        <tr><th>11:00</th></tr>
        <tr><th>11:30</th></tr>
        <tr><th>12:00</th></tr>
        <tr><th>12:30</th></tr>
        <tr><th>13:00</th></tr>
        <tr><th>13:30</th></tr>
        <tr><th>14:00</th></tr>
        <tr><th>14:30</th></tr>
        <tr><th>15:00</th></tr>
        <tr><th>15:30</th></tr>
        <tr><th>16:00</th></tr>
        <tr><th>16:30</th></tr>
        <tr><th>17:00</th></tr>
        <tr><th>17:30</th></tr>
        <tr><th>18:00</th></tr>
        <tr><th>18:30</th></tr>
        <tr><th>19:00</th></tr>
        <tr><th>19:30</th></tr>
        <tr><th>20:00</th></tr>
        <tr><th>20:30</th></tr>
        <tr><th>21:00</th></tr>
        <tr><th>21:30</th></tr>
        <tr><th>22:00</th></tr>
        <tr><th>22:30</th></tr>
        <tr><th>23:00</th></tr>
        <tr><th>23:30</th></tr>
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
    </section>
    <footer>
      <address>
        <img src="images/agplv3-88x31.png" style="vertical-align: middle"/>© 2014 Musiques Tangentes&nbsp;&nbsp;|&nbsp;&nbsp;
        <a href="http://www.algem.net" target="_blank">http://www.algem.net</a>
      </address>
    </footer>
  </body>
</html>
