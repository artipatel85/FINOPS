<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>GST - JSON</title>
        <link type="text/css" href="<c:url value='/assets/css/bootstrap.min.css' />" rel="stylesheet" />

        <script lang="javascript">
            $(function () {
                $("#uploadButton").on("click", function () {
                    $('#gstinJSONForm').attr("action", "gstincreditupload.fin");
                    $("#gstinJSONForm").submit();
                });

                $("#analyzeBtn").on("click", function () {
                    $('#gstinJSONForm').attr("action", "gstincreditmatch.fin");
                    $("#gstinJSONForm").submit();
                });
            });

            $(document).ready(function () {
                var table = $('#example').DataTable({
                    ajax: "gstincreditgson.fin",
                    "serverSide": true,
                    "bJQueryUI": true,
                    "bRetrieve": true,
                    "bProcessing": true,
                    "bFilter": false,
                    "dom": '<"top">t',
                    "lengthMenu": [20, 30, 50],
                    "aoColumns": [
                        {
                            "mData": "rtnprd",
                            "render": function (mData, full, row) {
                                var branchgstin = row['gstin'];
                                return '<a href="gstincreditdisplay.fin?period=' + mData + '&gstin=' +branchgstin+ '" target="_blank">' + mData + '</a>';
                            }
                        },
                        {"mData": "gstin"},
                        {"mData": "version"},
                        {
                            "mData": "rtnprd",
                            "render": function (mData, full, row) {
                                var branchgstin = "'"+row['gstin']+"'";
                                var period = "'"+row['rtnprd']+"'";
                                return '<input type="button" name="btnMatch" onclick="match(' + period+ ',' + branchgstin + ')" class="finbutton" value="Match"/>';
                            }
                        }

                    ]
                });

            });
            
            function match(period, gstin){
                $('#gstinJSONForm').attr("action", "gstincreditmatch.fin?period="+period+"&gstin="+gstin);
                    $("#gstinJSONForm").submit();
            }
        </script>
    </head>
    <body>
        <header>
            GST - Creditor
        </header>
        <html:form method="post" id="gstinJSONForm" commandName="reportBean" enctype="multipart/form-data">
            <table id="table" class="tablec">
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
                    <td><button type="button" id="uploadButton" class="finbutton">Upload</button></td>

                </tr>
            </table>


            <table id="example" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th>Period</th>
                        <th>GSTIN</th>
                        <th>BRANCH</th>
                        <th></th>
                    </tr>
                </thead>

            </table> 
        </html:form>
    </div>
</div>
</div>
</body>
</html>