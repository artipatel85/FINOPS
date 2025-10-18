<%-- 
    Document   : ReconciliationDocfa
    Created on : Sep 17, 2017, 12:57:09 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <html:form method="post" id="tdsForm">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>TDS Register</title>
        <script>
            $(document).ready(function () {
                var i = 1;
                var table = $('#docfa').DataTable({
                    ajax: "TDSGson.fin",
                    "serverSide": true,
                    "bJQueryUI": true,
                    "bProcessing": true,
                    "lengthMenu": [20, 25, 50],
                    "dom": 'l<"top">pt',
                    "aoColumns": [
                        {"mData": "param1"},
                        {"mData": "param2"},
                        {"mData": "param3"},
                        {"mData": "param4"},
                        {"mData": "param5"},
                        {"mData": "param6"},
                        {"mData": "param7"},
                        {"mData": "param8","className":"numberTextbox"},
                        {"mData": "param9","className":"numberTextbox"}
                    ]
                });
                $("div.top").append('&nbsp;&nbsp;&nbsp;<label><b>S.Date</b></label>&nbsp;<html:input path="param4" id="startDate" class="smalltext" />' +
                            '&nbsp;<label><b>E.Date</b></label>&nbsp;<html:input path="param5" id="endDate" class="smalltext" />&nbsp;&nbsp;'+
                            '<input type="button" id="btnSearch" onClick="search()" class="finbutton" value="Payable"/>'+
                        '&nbsp;&nbsp;<input type="button" id="btnExcel" onClick="excel()" class="finbutton" value="Print TDS Payable"/>&nbsp;&nbsp;'+
                        '<input type="button" id="btnSearch" onClick="search2()" class="finbutton" value="Receivable"/>'+
                        '&nbsp;&nbsp;<input type="button" id="btnExcel2" onClick="excel2()" class="finbutton" value="Print TDS Receivable"/>&nbsp;&nbsp;');
                
                
                $('.filter').on('keyup change', function () {
                    table.search('');
                    table.column($(this).data('columnIndex')).search(this.value).draw();
                });
                $(".dataTables_filter input").on('keyup change', function () {
                    table.columns().search('');
                    $('.filter').val('');
                }); 
                $("#startDate").datepicker({
                    changeMonth: true,
                    changeYear: true,
                    dateFormat: 'yy-mm-dd'
                });

                $("#endDate").datepicker({
                    changeMonth: true,
                    changeYear: true,
                    dateFormat: 'yy-mm-dd'
                });
            });
            function excel() {
                $('#tdsForm').attr("action", "TDSXLSReport.fin?id=1");
                $("#tdsForm").submit();
            }
            function search() {
                $('#tdsForm').attr("action", "TDS.fin?id=1");
                $("#tdsForm").submit();
            }
            function excel2() {
                $('#tdsForm').attr("action", "TDSXLSReport.fin?id=2");
                $("#tdsForm").submit();
            }
            function search2() {
                $('#tdsForm').attr("action", "TDS.fin?id=2");
                $("#tdsForm").submit();
            }    
        </script>
    </head>
    <body>
        <header>
            TDS Register
        </header>
        
        <table id="docfa" class="display" cellspacing="0" width="100%">

            <thead>
                <tr>
                    <th>Voucher No</th>
                    <th>Date</th>
                    <th>Type</th>
                    <th>Party</th>
                    <th>TDS Account</th>
                    <th>PAN</th>
                    <th>GSTIN</th>
                    <th>Basic Amount</th>
                    <th>TDS Amount</th>                    
                </tr>
            </thead>
            <thead>
                <tr>
                    <td><input type='text' value='' class='filter smalltext' data-column-index='0'></td>
                    <td><input type='text' value='' class='filter smalltext2' data-column-index='1'></td>
                    <td><input type='text' value='' class='filter smalltext2' data-column-index='2'></td>
                    <td><input type='text' value='' class='filter midtext' data-column-index='3'></td>
                    <td><input type='text' value='' class='filter midtext' data-column-index='4'></td>
                    <td><input type='text' value='' class='filter smalltext' data-column-index='5'></td>
                    <td><input type='text' value='' class='filter smalltext' data-column-index='6'></td>
                    <td><input type='text' value='' class='filter numberTextbox' data-column-index='7'></td>
                    <td><input type='text' value='' class='filter numberTextbox' data-column-index='8'></td>
                </tr>
            </thead>
        </table>
        </html:form>
    </body>
</html>
