
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@include file="/finact/Bootstrap_jquery.jsp" %>
        <title>FORM</title>
        <script>
              $(document).ready(function () {
                $("#formForm").validate({

                    rules: {
                        param1: {required: true}

                    },
                    messages: {
                        param1: '<div class="tool">* field is required.</div>'
                    }

                });
            });
        </script>
    </head>
    <body>
        <header>
            <a href="formSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
            FORMS
        </header>
        <div class="main">

            <html:form method="post" id="formForm" action="formSave.fin" command="reportBean">
                <table class="tablec">
                    <col width="160PX">
                    <col width="350PX">
                    <col width="160PX">
                    <col width="350PX">
                    <tr>
                        <td><label>FORM ID</label></td>
                        <td><html:input path="formId" class="small readonly" readonly="true"/><html:hidden path="action" class="small"/></td>
                        <td><label>FORM NAME</label></td>
                        <td><html:input path="name" class="largeXL"/></td>
                    </tr>
                    <tr>
                        <td><label>URL</label></td>
                        <td><html:input path="url" class="largeXL"/></td>
                        <td><label>PARENT ID</label></td>
                        <td><html:input path="parentId" class="small"/></td>
                    </tr>
                    
                </table>
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" id="btnsubmit" class="finbutton">Save</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                        <td><button type="submit" class="finbutton"><a href="formSearch.fin" style="color: #FFF">Exit</a></button></td>
                    </tr> 
                </table>
            </html:form>
        </div>
    </body>
</html>
