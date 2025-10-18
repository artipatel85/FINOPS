<%-- 
    Document   : Country
    Created on : Jan 11, 2018, 8:48:57 AM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@include file="/finact/Bootstrap_jquery.jsp" %>
        <title>JSP Page</title>
         <script>
              $(document).ready(function () {
                $("#countryForm").validate({

                    rules: {
                        countryCode: {required: true},
                        Description: {required: true}

                    },
                    messages: {
                        countryCode: '<div class="tool">* field is required.</div>',
                        Description: '<div class="tool">* field is required.</div>'
                    }

                });
            });
        </script>
    </head>
    <body>
        <header>
            <a href="countrySearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
            COMPANY PROFILE
        </header>
        <div class="main">

            <html:form method="post" id="countryForm" action="countrySave.fin" command="adminBean">
                <table class="tablec">
                    <col width="160PX">
                    <col width="350PX">
                    <col width="160PX">
                    <col width="350PX">
                    <tr>
                        <td><label>COUNTRY CODE</label></td>
                        <td><html:input path="countryCode" class="medium"/><html:hidden path="action" class="small"/></td>
                        <td><label>COUNYRY NAME</label></td>
                        <td><html:input path="countryName" class="largeXL"/></td>
                    </tr>
                    <tr>
                        <td><label>DESCRIPTION2</label></td>
                        <td><html:input path="description2" class="medium"/></td>
                        <td><label>ZONE NAME</label></td>
                        <td><html:input path="zone" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>STATUS</label></td>
                        <td><html:select path="status" class="select2 medium">
                                <html:option value="A">Active</html:option>
                                <html:option value="N">Suspend</html:option>
                            </html:select> </td>
                        <td><label>SHOW ON SCREEN</label></td>
                        <td><html:checkbox path="display" value="Y"/></td>
                    </tr>
                </table>
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" id="btnsubmit" class="finbutton">Save</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                        <td><button type="submit" class="finbutton"><a href="countrySearch.fin" style="color: #FFF">Exit</a></button></td>
                    </tr> 
                </table>
            </html:form>
        </div>
    </body>
</html>
