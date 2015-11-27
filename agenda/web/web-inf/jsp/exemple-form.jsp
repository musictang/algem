<%-- 
    Document   : exemple-form
    Created on : 27 nov. 2015, 16:03:55
    Author     : <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JSP Page</title>
  </head>
  <body>
    <h1>Hello World!</h1>
     <sf:form modelAttribute="user" action="login.html" method="post" cssClass="hpadded pure-form pure-form-stacked">
        <fieldset><legend>Saisissez vos identifiants</legend>
          <label for="login"><spring:message code="login.label" text="Login" /></label>
          <%-- Les messages d'erreur doivent être affichés à l'intérieur du formulaire --%>
          <sf:input path="login" id="login" required="required"/>
          <sf:errors path="login" cssClass="error"/>
          <label for="password"><spring:message code="password.label" text="Password" /></label>
          <sf:password path="password" id="password" required="required"/>
          <sf:errors path="password" cssClass="error"/>
          <spring:message code="login.submit.label" var="submitText"/>
          <input type="submit" value="${submitText}" class="button-xlarge pure-button pure-button-primary"/>
        </fieldset>
      </sf:form>
  </body>
</html>
