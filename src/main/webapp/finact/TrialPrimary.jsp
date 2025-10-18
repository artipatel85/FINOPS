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
        <html:form method="post" id="trialPrimaryForm" command="reportBean">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Trial Primary</title>
        <script>
            $(document).ready(function () {
                var i = 1;
                var table = $('#docfa').DataTable({
                    ajax: "TrialPrimaryGson.fin",
                    "serverSide": true,
                    "bJQueryUI": true,
                    "bProcessing": true,
                    "lengthMenu": [20, 25, 50],
                    "dom": 'l<"top">pt',
                    "aoColumns": [
                        {
                            "mData": "param1",
                            "render": function (mData, full, row) {
                                var link = row['param11'];
                                return '<a href="' + link + '">' + mData + '</a>';
                            }
                        },
                        {"mData": "param5","className":"numberTextbox"},
                        {"mData": "param6","className":"numberTextbox"}
                    ]
                });
                $("div.top").append('&nbsp;&nbsp;&nbsp;<label><b>S.Date</b></label>&nbsp;<html:input path="param4" id="startDate" class="smalltext" />' +
                            '&nbsp;<label><b>E.Date</b></label>&nbsp;<html:input path="param5" id="endDate" class="smalltext" />&nbsp;&nbsp;'+
                            '<input type="button" id="btnSearch" onClick="search()" class="finbutton" value="Search"/>'+
                        '&nbsp;&nbsp;<input type="button" id="btnExcel" onClick="excel()" class="finbutton" value="Print"/>&nbsp;&nbsp;'+
                        '<html:checkbox path="param2" value="Y"/>&nbsp;&nbsp;');
                
                
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
                $('#trialPrimaryForm').attr("action", "TrialPrimaryXLSReport.fin?id=1");
                $("#trialPrimaryForm").submit();
            }
            function search() {
                $('#trialPrimaryForm').attr("action", "TrialPrimarySearch.fin");
                $("#trialPrimaryForm").submit();
            }
             
        </script>
    </head>
    <body>
        <header>
            Trial Primary <html:input path="param12"/> <html:input path="param13"/> <html:input path="intparam1"/>
        </header>
        
        <table id="docfa" class="display" cellspacing="0" width="100%">

            <thead>
                <tr>
                    <th>Particulars</th>
                    <th>Debit</th>
                    <th>Credit</th>                    
                </tr>
            </thead>
            <thead>
                <tr>
                    <td><input type='text' value='' class='filter midtext' data-column-index='0'></td>
                    <td><input type='text' value='' class='filter smalltext2' data-column-index='1'></td>
                    <td><input type='text' value='' class='filter smalltext2' data-column-index='2'></td>
                    
                </tr>
            </thead>
        </table>
        </html:form>
    </body>
</html>
