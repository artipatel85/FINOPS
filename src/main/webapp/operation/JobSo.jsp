
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form method="post" action="jobSoSave.fin" command="jobBean" id="jobSoForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta http-equiv="Pragma" content="no-cache">
            <meta http-equiv="Expires" content="-1">
            <title>BillTemplate</title>
            <style>
                th {
                    text-align: left;
                    padding: 5px;
                    font-size:11px;
                    border: 1px solid #ddd;
                    background-color: #5c5a5a;
                    font-family: Verdana, Arial;
                }

            </style>
            <script>
                    $("#jobSoSave").on("click", function () {
                        var len = $('input:checkbox').length / 2;

                        $('#jobSoForm').attr("action", "jobSoSave.fin?param=");
                        $("#jobSoForm").submit();
                    });

            </script>
        </head>
        <body>
            <div class="comdiv">

                <header>
                    <c:out value="${jobBean.jobNumber}"/>
                    <html:hidden path="jobNumber"/>
                    <html:hidden path="expImp"/>
                </header>

            </div>

            <div class="main">
            <table id="docfa" class="display" cellspacing="0" width="100%" style="height:300px; overflow-x: scroll; overflow-y: scroll;display: block; word-break: break-all;">
                <thead>
                    <tr>
                        <th></th>
                        <th>S/O Number</th>
                        <th>BKG DATE</th>
                        <th>SHPR</th>
                        <th>CNEE</th>
                        <th>POR</th>
                        <th>B/L</th>
                        <th>Job Number</th>
                    </tr>
                </thead>
                <c:forEach items="${command.soBeanList}" var="sor" varStatus="status">
                    <tr>
                        <td>
                            <html:checkbox path="soBeanList[${status.index}].uuid" id="soCheckBox" value="${jobBean.jobNumber}"/>
                        </td>

                        <td>
                            <html:input path="soBeanList[${status.index}].soNumber" class="small readonly" readonly="true" value="${sor.soNumber}"/>
                        </td>
                        <td>
                            <html:input path="soBeanList[${status.index}].bookingRefDate" class="medium" value="${sor.bookingRefDate}"/>
                        </td>
                        <td>
                            <html:input path="soBeanList[${status.index}].shipperName" class="largeXL" value="${sor.shipperName}"/>
                        </td>
                        <td>
                            <html:input path="soBeanList[${status.index}].consigneeName" class="largeXL" value="${sor.consigneeName}"/>
                        </td>
                        <td>
                            <html:input path="soBeanList[${status.index}].por" class="small" value="${sor.por}"/>
                        </td>
                        <td>
                            <html:checkbox path="soBeanList[${status.index}].blNumber" class="small" value="" disabled="true"/>
                        </td>

                        <td>
                            <html:input path="soBeanList[${status.index}].jobNumber" class="mediume" value="${sor.jobNumber}"/>
                        </td>

                    </tr>
                </c:forEach>

            </table>
            </div>
            <table class="tablefooter">
                <tr>
                    <td><button type="button" class="finbutton" id="jobSoSave">SAVE</button></td>
                    <td><button type="reset" class="finbutton">RESET</button></td>
                    <td><button type="button" class="finbutton" onclick="exitContainer()">EXIT</button></td>
                </tr>
            </table>
        </html:form>
    </body>
</html>
