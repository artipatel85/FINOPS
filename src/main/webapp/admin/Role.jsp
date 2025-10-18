<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <link href="<c:url value="/finactcss/rowForm.css"/>" rel="stylesheet">
        <title>Role</title>
        
        </style>
        <script>
            $(document).ready(function () {
                $("#roleForm").validate({

                    rules: {
                        countryCode: {required: true},
                        Description: {required: true}

                    },
                    messages: {
                        roleName: '<div class="tool">* field is required.</div>',
                        Description: '<div class="tool">* field is required.</div>'
                    }

                });
            });
        </script>
    </head>
    <body>
        <div class="comdiv">
            <header>
                <a href="roleSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
                ROLE
            </header>


            <html:form method="post" id="roleForm" action="saveRoles.fin" command="adminBean">
                <table class="tablec">
                    <col width="160PX">
                    <col width="350PX">
                    <col width="160PX">
                    <col width="350PX">
                    <tr>
                        <td><label>Role NAME</label></td>
                        <td><html:hidden path="action"/><html:input path="name" class="largeXL"/></td>
                        <td><label>DESCRIPTION</label></td>
                        <td><html:input path="description" class="largeXL"/></td>
                    </tr>

                </table>   
            </div>
            <c:if test="${adminBean.action =='UPDATE'}">
            <div>
                <table class="tablevou" id="tbl5" >
                    <thead>
                        <tr>
                            <th style="width:20%"><a href="#" onclick="addRow();"><div class="logoplus"></div></a></th>
                            <th style="width:10%">SELECT</th>
                            <th style="width:10%">CREATE</th>
                            <th style="width:10%">UPDATE</th>
                            <th style="width:10%">DELETE</th>
                            <th style="width:10%">PRINT</th>
                            <th style="width:10%">UPLOAD</th>
                            <th style="width:10%">DOWNLOAD</th>
                            <th style="width:10%">APPROVE</th>
                            <th></th>
                        </tr>
                    </thead>
                </table>    
            </div>
            <div class="rows rows3">
                <iframe frameborder="0" style="position:absolute;width:1px;"></iframe>
                <table class="tablevou" id="tbl">
                    <c:forEach items="${command.priviledgeList}" var="roleRow" varStatus="status">
                        <tr>
                            <td style="width:20%">
                                <html:hidden path="priviledgeList[${status.index}].formId" class="smallM readonly" readonly="true" value="${roleRow.formId}"/>
                                <label><c:out value="${roleRow.name}"/></label>
                            </td>                                
                            <td style="width:10%">
                                <html:checkbox path="priviledgeList[${status.index}].select" class="smallM" value="Y"/>
                            </td>
                            <td style="width:10%">
                                <html:checkbox path="priviledgeList[${status.index}].create" class="smallM" value="Y"/>
                            </td>
                            <td style="width:10%">
                                <html:checkbox path="priviledgeList[${status.index}].update" class="smallM" value="Y"/>
                            </td>
                            <td style="width:10%">
                                <html:checkbox path="priviledgeList[${status.index}].delete" class="smallM" value="Y"/>
                            </td>
                            <td style="width:10%">
                                <html:checkbox path="priviledgeList[${status.index}].print" class="smallM" value="Y"/>
                            </td>
                            <td style="width:10%">
                                <html:checkbox path="priviledgeList[${status.index}].upload" class="smallM" value="Y"/>
                            </td>
                            <td style="width:10%">
                                <html:checkbox path="priviledgeList[${status.index}].download" class="smallM" value="Y"/>
                            </td>
                            <td style="width:10%">
                                <html:checkbox path="priviledgeList[${status.index}].approve" class="smallM" value="Y"/>
                            </td>
                        </tr>
                    </c:forEach>

                </table>
            </div>
            </c:if> 
            <div class="comdivfoot">
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" id="btnsubmit" class="finbutton">Save</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                        <td><button type="submit" class="finbutton"><a href="roleSearch.fin" style="color: #FFF">Exit</a></button></td>
                    </tr> 
                </table>
            </html:form>
        </div>
    </body>
</html>
