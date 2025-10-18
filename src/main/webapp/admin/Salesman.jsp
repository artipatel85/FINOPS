
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@include file="/finact/Bootstrap_jquery.jsp" %>
        <title>JSP Page</title>
        <script>
              $(document).ready(function () {
                $("#currencyForm").validate({

                    rules: {
                        currencyCode: {required: true},
                        Description: {required: true}

                    },
                    messages: {
                        currencyCode: '<div class="tool">* field is required.</div>',
                        Description: '<div class="tool">* field is required.</div>'
                    }

                });
            });
        </script>
    </head>
    <body>
        <header>
            <a href="currencySearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
            COMPANY PROFILE
        </header>
        <div class="main">

            <html:form method="post" id="salesmanForm" action="salesmanSave.fin" command="adminBean">
                <table class="tablec">
                    <col width="160PX">
                    <col width="350PX">
                    <col width="160PX">
                    <col width="350PX">
                    <tr>
                        <td><label>CODE</label></td>
                        <td><html:input path="code" class="medium"/><html:hidden path="action" class="small"/></td>
                        <td><label>NAME</label></td>
                        <td><html:input path="name" class="largeXL"/></td>
                    </tr>
                    <tr>
                        <td><label>DESIGNATION</label></td>
                        <td><html:input path="designation" class="largeXL"/></td>
                        <td><label>STATUS</label></td>
                        <td><html:select path="Status" class="select2 medium">
                                <html:option value="A">Active</html:option>
                                <html:option value="N">Suspend</html:option>
                            </html:select> </td>
                    </tr>
                    <tr>
                        <td><label>EMAIL ID</label></td>
                        <td><html:input path="emailId" class="medium"/></td>
                    </tr>
                </table>
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" id="btnsubmit" class="finbutton">Save</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                        <td><button type="submit" class="finbutton"><a href="currencySearch.fin" style="color: #FFF">Exit</a></button></td>
                    </tr> 
                </table>
            </html:form>
        </div>
    </body>
</html>
