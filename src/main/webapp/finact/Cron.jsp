
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <title>Cron</title>
        <script>

            function cron() {
                $('#cronForm').attr("action", "processCron.fin");
                $("#cronForm").submit();
            }
        </script>
    </head>
    <body>
        <div class="main">
            <header>
                Profitability
            </header>
            <html:form method="post" id="cronForm" commandName="reportBean">
                <table class="tablec">
                    <col width="10%">
                    <col width="80%">

                    <tr>
                        <td><label>Accounting Year</label></td>
                        <td><html:input type="text" path="acctYear" id="date" class="medium"/></td>

                    </tr>

                </table>
                <table class="tb2">
                    <tr>
                        <td><button type="button" class="finbutton"  onClick="cron()">PROCESS</button></td>
                    </tr>
                </table>

            </html:form>
        </div>
    </body>
</html>
