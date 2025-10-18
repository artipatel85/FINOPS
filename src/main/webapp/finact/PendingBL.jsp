<%-- 
    Document   : SalesRegister
    Created on : Aug 28, 2017, 9:18:15 AM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <title>Pending BL</title>
        
        <script>
            $(document).ready(function () {
                $("#registration").validate({

                    rules: {
                        param1: {required: true, date: true},
                        param2: {required: true, date: true}
                    },
                    messages: {
                        param1: '<div class="tool">* field is required.</div>',
                        param2: '<div class="tool">* field is required.</div>'
                    }

                });
                $("#b2bButton").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=1");
                    $("#registration").submit();
                });
                $("#b2cButton").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=2");
                    $("#registration").submit();
                });
                $("#exemptedButton").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=3");
                    $("#registration").submit();
                });
                $("#exportSEZButton").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=4");
                    $("#registration").submit();
                });
                $("#localSEZButton").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=5");
                    $("#registration").submit();
                });
                $("#journalInput").on("click", function () {
                    $('#registration').attr("action", "gstinReport.fin?param=6");
                    $("#registration").submit();
                });
            });

        </script>
    </head>
    <body>
        <div class="main">
            <header>
                Pending BL
            </header>
            <html:form method="post" action="pendingBLXLS.fin" id="registration" commandName="reportbean">
                <table class="tablec">
                    <col width="10%">
                    <col width="80%">
                    <tr>
                        <td><label>Start Date</label></td>
                        <td><html:input type="text" path="param1" id="date" class="medium"/></td>

                    </tr>
                    <tr>
                        <td><label>End Date</label></td>
                        <td><html:input type="text" path="param2" id="date2" class="medium" /></td>
                    </tr>
                    <tr>
                        <td><label>Branch</label></td>
                        <td><html:select path="param3">
                                <html:option value="">SELECT</html:option>
                                <html:option value="SFP">SFP</html:option>
                                <html:option value="SFPM">SFPM</html:option>
                                <html:option value="SFPC">SFPC</html:option>
                                <html:option value="SFPK">SFPK</html:option>
                                <html:option value="SFPG">SFPG</html:option>
                            </html:select>
                        </td>
                    </tr>
                    <tr>
                        <td><label>Branch</label></td>
                        <td><html:select path="param4">
                                <html:option value="NONE">NONE</html:option>
                                <html:option value="REVENUE">REVENUE</html:option>
                                <html:option value="EXPENSE">EXPENSE</html:option>
                            </html:select>
                        </td>
                    </tr>
                </table>
                <table class="tb2">
                    <tr>
                        <td><button type="submit" class="finbutton">Pending BL</button></td>
                        <td><button type="submit" class="finbutton">Expense - SO</button></td>
                    </tr>
                </table>

            </html:form>
        </div>
    </body>
</html>
