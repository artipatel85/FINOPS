
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <html:form method="post" id="profitabilityForm">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Profitability</title>
            <script>
                $(document).ready(function () {
                    var i = 1;
                    var table = $('#docfa').DataTable({
                        ajax: "profitabilityGson.fin",
                        "serverSide": true,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [20, 25, 50],
                        "dom": 'l<"top">pt',
                        "aoColumns": [
                            {
                                "mData": "param1",
                                "render": function (mData, full, row) {
                                    var link = 'profitabilityDetail.fin?jobNo=' + mData + '&blNo=';
                                    return '<a href="' + link + '">' + mData + '</a>';
                                }
                            },
                            {
                                "mData": "param2",
                                "render": function (mData, full, row) {
                                    var link = 'profitabilityDetail.fin?blNo=' + mData + '&jobNo=';
                                    return '<a href="' + link + '">' + mData + '</a>';
                                }
                            },
                            {"mData": "param3", "className": "numberTextbox"},
                            {"mData": "param4", "className": "numberTextbox"},
                            {"mData": "param9", "className": "numberTextbox"},
                            {"mData": "param6"},
                            {"mData": "param7"},
                            {"mData": "param8"},
                            {"mData": "param11"},
                            {"mData": "param10"}
                        ]
                    });
                    
                    $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;' +
                            '&nbsp;&nbsp;<label><b>S.Date</b></label>&nbsp;<html:input path="param4" id="startDate" class="smalltext" />' +
                        '&nbsp;<label><b>E.Date</b></label>&nbsp;<html:input path="param5" id="endDate" class="smalltext" />&nbsp;&nbsp;' +
                            '<input type="button" id="btnSearch" onClick="search()" class="finbutton" value="FILTER"/>&nbsp;&nbsp;<input type="button" id="btnExcel" onClick="excel()" class="finbutton" value="EXCEL"/>&nbsp;&nbsp;');


                    $('.filter').on('keyup change', function () {
                        if (this.value.length > 0 && this.value.length < 4) {
                            return false;
                        }
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
                    $('#profitabilityForm').attr("action", "profitabilityXLSReport.fin");
                    $("#profitabilityForm").submit();
                }
                function search() {
                    $('#profitabilityForm').attr("action", "profitability.fin");
                    $("#profitabilityForm").submit();
                }
            </script>
        </head>
        <body>
            <header>
                Profitability
            </header>

            <table id="docfa" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th>Job No</th>
                        <th>BL No</th>
                        <th>Income</th>
                        <th>Expense</th>
                        <th>Profit/Loss</th>
                        <th>Sea/Air</th>
                        <th>Export/Import</th>                   
                        <th>Branch</th>
                        <th>Salesman</th>
                        <th>Party</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='3'></td>
                        <td></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='4'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='5'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='6'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='7'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='8'></td>
                    </tr>
                </thead>
            </table>
        </html:form>
    </body>
</html>
