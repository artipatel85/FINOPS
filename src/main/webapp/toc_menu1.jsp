
<%@ page language="java" import="java.util.*"%>
<%@ page import="com.finops.admin.model.LoginBean"%>
<%@include file="taglibs.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
    <head>
        <title>Shikhar FWD Pvt LTD</title>
        <LINK href="scripts/tab.css" type="text/css" rel=stylesheet>
        <link rel="stylesheet" href="scripts/winTabs_style.css" type="text/css">
        <script language="javascript" type="text/javascript" src="scripts/winTabs.js"></script>
        <script language="javascript" type="text/javascript" src="scripts/mouseevt.js"></script>
        <SCRIPT src="scripts/tabpane.js" type="text/javascript"></SCRIPT>
        <link rel="StyleSheet" href="dtree.css" type="text/css"/>
        <script type="text/javascript" src="dtree.js"></script>
        <script type="text/javascript" src="scripts/shortcuts.js"></script>
    </head>

    <body onload=mouseDown();>
        <div class="dtree">
            <div class="dtree">
                <%
                    LoginBean loginVoObj = (LoginBean) session.getAttribute("loginLst");
                    String name = loginVoObj.getUserBean().getUserId();
                    String userRole = loginVoObj.getUserBean().getRole();
                    String agent = loginVoObj.getUserBean().getBranch();
                    String first = name.substring(0, 1).toUpperCase();

                    String rest = name.substring(1);
                    String username = first + rest;
                    int acctYear = loginVoObj.getPeriodBean().getAcctYear();
                %>  

                <script type="text/javascript">	
                    var i = 1;
                    var temp = 0;
                    var current = 0;
                    var userID = '<%= name%>';
                    d = new dTree('d');     
                    d.add(0,-1,'<%=username%> / <%=acctYear%>','homePage.do?method=showHome','Home Page','main');
                    d.add(1,0,'<%=agent%>');
                    <c:forEach items="${sessionScope.privList}" var="privObject">

                        <c:if test="${privObject.url == '#'}">
                            d.add('${privObject.formId % 1000}','${privObject.parentId % 1000}','${privObject.name}');
                        </c:if>
                        <c:if test="${privObject.url != '#'}">
                            d.add('${privObject.formId % 1000}','${privObject.parentId % 1000}','${privObject.name}','${privObject.url}','','main');
                        </c:if>
                    </c:forEach>
                        d.add(9997,0,'Help','logout.fin','Logout','_parent');
                        d.add(9998,0,'Update User Profile','userDetails.do?invoke=retrieveUser&userId='+userID,'Update User Profile','main');
                        d.add(9999,0,'Logout','logout.fin','Logout','_parent');
                        document.write(d);
                </script>

            </div>

            
            
    </body>
</html>