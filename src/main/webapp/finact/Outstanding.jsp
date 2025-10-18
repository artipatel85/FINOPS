<%-- 
    Document   : outstanding
    Created on : Aug 28, 2017, 1:48:49 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>OUTSTANDING REPORT</title>
        <script>

            $(function () {
                var icons = {
                    header: "ui-icon-circle-arrow-e",
                    activeHeader: "ui-icon-circle-arrow-s"
                };
                $("#accordion").accordion({
                    collapsible: true,
                    active: false,
                    icons: icons
                });

            });
        </script>
    </head>
    <body>

        <div id="accordion">
            <h4>Outstanding - Party</h4>
            <div>
                <%@include file="/finact/Partywise.jsp" %>
            </div>
            
<!--            <h4>Outstanding - Salesman</h4>
            <div>
                <%@include file="/finact/Salesmanwise.jsp" %>
            </div>-->

        </div>       

    </body>
</html>
