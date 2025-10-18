<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>GST - JSON</title>
        <link type="text/css" href="<c:url value='/assets/css/bootstrap.min.css' />" rel="stylesheet" />
        <script lang="javascript">
            $(function () {
                $("#excelBtn").on("click", function () {
                    $('#gstinJSONForm').attr("action", "gstJsonUpload.fin?type=excel");
                    $("#gstinJSONForm").submit();
                });

                $("#analyzeBtn").on("click", function () {
                    $('#gstinJSONForm').attr("action", "gstJsonUpload.fin?type=analyze");
                    $("#gstinJSONForm").submit();
                });
            });
        </script>
    </head>
    <body>
        <header>
            GST - Creditor
        </header>
        <html:form method="post" id="gstinJSONForm" commandName="reportBean" enctype="multipart/form-data">
            <table id="table" class="tablec">
                <tr>
                    <td><label>Start Date</label></td>
                    <td><html:input type="text" path="param2" id="date" class="medium"/></td>

                </tr>
                <tr>
                    <td><label>End Date</label></td>
                    <td><html:input type="text" path="param3" id="date2" class="medium" /></td>
                </tr>
                <tr>
                    <td><label>Branch</label></td>
                    <td><html:select path="param4">
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
                    <td>
                        <label for="file1">Upload Json file</label>
                    </td>
                    <td>
                        <input type="file" name="param100" id="param100">
                    </td>
                </tr>
            </table>
            <table class="tb2">
                <tr>
                    <td><button type="button" id="excelBtn" class="finbutton">EXCEL</button></td>
                    <td><button type="button" id="analyzeBtn" class="finbutton">ANALYZE</button></td>

                </tr>
            </table>
        </html:form>
    </div>
</div>
</div>
</body>
</html>