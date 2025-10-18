<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id="formForm" action="periodSave.fin">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <title>Account Year</title>
        <script>


            </script>
    </head>
    <body>
        <header>
                Account Year
            </header>
            <table id="countryview" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <td>
                            <html:select path="startMonth" styleClass="txt2">
                                <html:option value="999">Month</html:option>
                                <html:option value="1">Jan</html:option>
                                <html:option value="2">Feb</html:option>
                                <html:option value="3">Mar</html:option>
                                <html:option value="4">Apr</html:option>
                                <html:option value="5">May</html:option>
                                <html:option value="6">Jun</html:option>
                                <html:option value="7">Jul</html:option>
                                <html:option value="8">Aug</html:option>
                                <html:option value="9">Sep</html:option>
                                <html:option value="10">Oct</html:option>
                                <html:option value="11">Nov</html:option>
                                <html:option value="12">Dec</html:option>
                            </html:select>
                            <html:input path="startYear"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <html:select path="endMonth" styleClass="txt2">
                                <html:option value="999">Month</html:option>
                                <html:option value="1">Jan</html:option>
                                <html:option value="2">Feb</html:option>
                                <html:option value="3">Mar</html:option>
                                <html:option value="4">Apr</html:option>
                                <html:option value="5">May</html:option>
                                <html:option value="6">Jun</html:option>
                                <html:option value="7">Jul</html:option>
                                <html:option value="8">Aug</html:option>
                                <html:option value="9">Sep</html:option>
                                <html:option value="10">Oct</html:option>
                                <html:option value="11">Nov</html:option>
                                <html:option value="12">Dec</html:option>
                            </html:select>
                            <html:input path="endYear"/>
                        </td>
                    </tr>
                </thead>
                 <thead>
                    <tr>
                        <td>
                            <button type="submit" class="finbutton">Save</>
                        </td>

                    </tr>
                </thead>
            </table>
    </body>
    </html:form>
</html>
