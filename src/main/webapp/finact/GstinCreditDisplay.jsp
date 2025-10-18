<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<html>
<html:form method="post" commandName="reportBean" id="gstinCreditForm">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>GST - JSON</title>
        <link type="text/css" href="<c:url value='/assets/css/bootstrap.min.css' />" rel="stylesheet" />

        <script lang="javascript">
            $(function () {
                $("#uploadButton").on("click", function () {
                    $('#gstinCreditForm').attr("action", "gstincreditupload.fin");
                    $("#gstinCreditForm").submit();
                });

                $("#analyzeBtn").on("click", function () {
                    $('#gstinCreditForm').attr("action", "gstincreditmatch.fin");
                    $("#gstinCreditForm").submit();
                });

            });

            $(document).ready(function () {
                var table = $('#example').DataTable({
                    ajax: "gstincreditdisplaygson.fin?startDate=${reportBean.param4}&endDate=${reportBean.param5}&gstin=${reportBean.param2}",
                   "serverSide": true,
                   "bJQueryUI": true,
                   "bRetrieve": true,
                   "bProcessing": true,
                   "dom": 'l<"top">pt',
                   "lengthMenu": [20, 30, 50],
                   "aoColumns": [
                        {
                            "mData": "param1",
                            "render": function (mData, full, row) {
                                var link = row['param11']
                                return '<a href="retrieveBill.fin?param=EXPENSE&billId=' + link + '" target="_blank">' + mData + '</a>';
                            }
                        },
                        {"mData": "param2"},
                        {"mData": "param3"},
                        {"mData": "param4"},
                        {"mData": "param5"},
                        {"mData": "param6"},
                        {"mData": "param7"},
                        {"mData": "param8"},
                        {"mData": "param9"}

                    ]
                });

                $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;' +
                '&nbsp;&nbsp;<label><b>S.Date</b></label>&nbsp;<html:input path="param4" id="startDate" class="smalltext" />' +
                '&nbsp;<label><b>E.Date</b></label>&nbsp;<html:input path="param5" id="endDate" class="smalltext" />&nbsp;&nbsp;' +
                '&nbsp;<html:hidden path="param1" id="period" class="smalltext" />&nbsp;&nbsp;' +
                '&nbsp;<html:hidden path="param2" id="gstin" class="smalltext" />&nbsp;&nbsp;' +
                '<html:select path="param6">'+
                 '<html:option value="">ALL</html:option>'+
                 '<html:option value="1">MATCHED</html:option>'+
                 '<html:option value="2">GSTIN</html:option>'+
                 '<html:option value="3">SHIKHAR</html:option>'+
                 '</html:select>&nbsp;&nbsp;'+
                 '<input type="button" id="btnSearch" onClick="search()" class="finbutton" value="FILTER"/>&nbsp;&nbsp;'+
                 '<input type="button" id="btnExcel" onClick="excel()" class="finbutton" value="EXCEL"/>');


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
                $('#gstinCreditForm').attr("action", "gstinCreditXLSReport.fin");
                $("#gstinCreditForm").submit();
            }
            function search() {
                $('#gstinCreditForm').attr("action", "gstincreditsearch.fin");
                $("#gstinCreditForm").submit();
            }

            function match(period, gstin){
                $('#gstinCreditForm').attr("action", "gstincreditmatch.fin?period="+period+"&gstin="+gstin);
                $("#gstinCreditForm").submit();
            }
        </script>
    </head>
    <body>
        <header>
            GST - Creditor
        </header>
            <table id="example" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th>Voucher</th>
                        <th>Voucher Date</th>
                        <th>Inv No</th>
                        <th>Inv Date</th>
                        <th>Taxable</th>
                        <th>Tax</th>
                        <th>Category</th>
                        <th>Party</th>
                        <th>GSTIN</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='3'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='4'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='5'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='6'></td>
                        <td><input type='text' value='' class='filter largetext' data-column-index='7'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='8'></td>
                    </tr>
                </thead>
            </table>
</html:form>
</body>
</html>