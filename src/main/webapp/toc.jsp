
<%@ page language="java" import="java.util.*"%>
<%@ page import="com.finops.admin.model.LoginBean"%>
<%@include file="taglibs.jsp" %>
<html>
    <head>	

        <%
            String username = "";
            String name = "";
            String user = "";
            String first = "";
            String rest = "";
            String userRole = "";
            LoginBean loginVoObj = (LoginBean) session.getAttribute("loginLst");
            if (null != loginVoObj) {
                name = loginVoObj.getUserBean().getUserId();
                userRole = loginVoObj.getUserBean().getRole();
            }
            first = name.substring(0, 1);

            rest = name.substring(1);
            first = first.toUpperCase();
            username = first + rest;
            String lastAccess = "";
            int acctYear = loginVoObj.getPeriodBean().getAcctYear();

        %>

        <title>Shikhar Forwarders pvt Ltd</title>
        <link rel="StyleSheet" href="dtree.css" type="text/css" />
        <script type="text/javascript" src="dtree.js"></script>

    </head>

    <frameset id="myFrameSet" cols="14%, *" framespacing="0" border="0" frameborder="0" >

        <% if ("A".equals(userRole)) {


                %>
                <frame src="toc_menu.jsp" name="menu"/>


                <%} else {

                %>

                <frame noresize="noresize" src="toc_menu1.jsp" name="menu"/>

                <%}%>

                <% if ("A".equals(userRole)) {


                                %>
                                <frame src="admin.jsp" name="main"/>


                                <%} else {

                                %>

                                <frame src="home.jsp" name="main"/>

                                <%}%>



    </frameset>

</html>




<!--<iframe src="content.html" name="content" height=252 width=521 frameborder=0 scrolling=yes></iframe>