
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form method="post" commandName="billingBean" id="billingVoucherForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>InvoiceSearch</title>
            <script type="text/javascript">
                $(document).ready(function () {
                    var table = $('#example').DataTable({
                        ajax: "viewBillListGson.fin?PARAM=${billingBean.revExp}",
                        "serverSide": true,
                        "bJQueryUI": true,
                        "bRetrieve": true,
                        "bProcessing": true,
                        "dom": 'l<"top">pt',
                        "lengthMenu": [20, 30, 50],
                        "aoColumns": [
                            {
                                "mData": "billNo",
                                "render": function (mData, full, row) {
                                    var link = row['billId'];
                                    var cond = row['irn'];
                                    if(cond === null){
                                        return '<input type="checkbox" name="trxIds" value="'+link+'"/>';
                                    }
                                    else{
                                        return '<input type="checkbox" name="trxIds" disabled="true" value="'+link+'"/>';
                                    }
                                    
                                }
                            },
                            {
                                "mData": "billId",
                                "render": function (mData, full, row) {
                                    var type = row['revExp'];
                                    var cond = row['irn'];
                                    if(cond === null){
                                        return '<a href="billingPDFReport.fin?PARAM='+type+'&irn=&trxid=' + mData + '" target="_blank"><img src="finactImages/icon_pdf.png" class="logopdf"></a>';
                                    }
                                    else{
                                        return '<a href="billingPDFReport.fin?PARAM='+type+'&irn='+cond+'&trxid=' + mData + '" target="_blank"><img src="finactImages/icon_pdf.png" class="logopdf"></a>';
                                    }
                                }
                            },
                            {
                                "mData": "billNo",
                                "render": function (mData, full, row) {
                                    var link = row['billId'];
                                    var type = row['revExp'];
                                    return '<a href="retrieveBill.fin?param='+type+'&billId=' + link + '">' + mData + '</a>';
                                }
                            },
                            {"mData": "billDate"},
                            {"mData": "billtoName"},
                            {"mData": "seaAir"},
                            {"mData": "expImp"},
                            {"mData": "localForeign"},
                            {"mData": "billType"},
                            {
                                "mData": "invNo",
                                "render": function (mData, full, row) {
                                    var link = row['invSbNo'];
                                    var type = row['revExp'];
                                    if(type == 'REVENUE'){
                                        return mData;
                                    }
                                    else{
                                        return link;
                                    }
                                }
                            },
                            {"mData": "trxTotal", "className": "numberTextbox"}

                        ]
                    });
                    $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" id="new" class="finbutton" value="New" onclick="create()">&nbsp;&nbsp;&nbsp;&nbsp;<rbac:rbac pattern="(.)\\|(Y)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)"><input type="button" class="finbutton" id="delete" value="Delete" onclick="deleted()"></rbac:rbac>' +
                            '&nbsp;&nbsp;<input type="button" class="finbutton" id="report" value="Excel">'+
                            '&nbsp;&nbsp;<html:hidden path="revExp"/>' +
                            '&nbsp;&nbsp;');

                    table.buttons().container()
                            .insertBefore('#example_filter');
                    $('.filter').on('keyup change', function () {
                        table.search('');
                        table.column($(this).data('columnIndex')).search(this.value).draw();
                    });
                    $(".dataTables_filter input").on('keyup change', function () {
                        table.columns().search('');
                        $('.filter').val('');
                    });
                    
                    $("#report").on("click", function () {
                        $('#billingVoucherForm').attr("action", "billingReportXLS.fin?param=${billingBean.revExp}");
                        $("#billingVoucherForm").submit();
                    });
                });
                function create() {
                    $('#billingVoucherForm').attr("action", "loadBillingDataA.fin");
                    $("#billingVoucherForm").submit();
                }
                function deleted() {
                    $('#billingVoucherForm').attr("action", "deleteInvoice.fin?PARAM=${billingBean.revExp}");
                    $("#billingVoucherForm").submit();
                }
            </script> 
        </head>
        <body>
            <header>
                <c:out value="${billingBean.revExp}"/>
            </header>
            <table class="tablec">
            <c:if test="${billingBean.errMsg != null}">
                <tr>
                    <th colspan="4"><c:out value="${billingBean.errMsg}"/></th>
                </tr>
            </c:if>
            </table>
            <table id="example" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th></th>
                        <th>PDF</th>
                        <th>BILL NO</th>
                        <th>BILL DATE</th>
                        <th>BILL TO</th>
                        <th>SEA/AIR</th>
                        <th>EXP/IMP</th>
                        <th>LOCAL/FOREIGN</th>
                        <th>TYPE</th>
                        <th>INV NO</th>
                        <th>GRAND TOTAL</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td></td>
                        <td></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='0'></td>
                        <td><input type='text' value='' id="date" class='filter smalltext' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter largeXL1' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='3'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='4'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='5'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='6'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='7'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='8'></td>
                    </tr>
                </thead>
            </table>
        </body>
    </html:form>
</html>
