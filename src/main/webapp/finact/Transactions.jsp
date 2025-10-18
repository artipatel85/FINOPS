<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <html:form method="post" id="trialForm">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Transactions</title>
            <script>
                var matchedAmount = 0;
                $(document).ready(function () {
                    var i = 1;
                    var table = $('#docfa').DataTable({
                        "autoWidth": false,
                        ajax: "TransGSON.fin",
                        "serverSide": true,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [500, 100, 150],
                        "dom": 'l<"top">p<"center"><"bottom">',
                        "aoColumns": [
                            {"mData": "param1"},
                            {
                                "mData": "param2",
                                "render": function (mData, full, row) {
                                    var link = createURL(row['param4'], row['param9'], row['param11']);
                                    //alert(mData);
                                    if(mData == null){
                                        return '';
                                    }
                                    return '<a href="' + link + '">' + mData + '</a>';
                                }
                            },
                            {"mData": "param3"},
                            {"mData": "param4"},
                            {"mData": "param5"},
                            {"mData": "param6"},
                            {"mData": "param7", "className": "numberTextbox"},
                            {"mData": "param8", "className": "numberTextbox"},
                            {
                                "mData": "param9",
                                "render": function (mData, full, row) {
                                    if(mData == null){
                                        return '';
                                    }
                                    var param13 = row['param13'];
                                    var param14 = row['param14'];
                                    var value = mData+'|'+param13+'|'+param14;
                                    var sum = Number(row['param7']) - Number(row['param8']);
                                    return '<input type="checkbox" class="idArr" onclick="calculate('+sum+',this)" name="uuids" value="' + value + '">';
                                }
                            },
                            {"mData": "param10"}
                        ]
                    });
                    $("div.top").append('&nbsp;&nbsp;&nbsp;<label><b>S.Date</b></label>&nbsp;<html:input path="param4" id="startDate" class="smalltext" />' +
                            '&nbsp;<label><b>E.Date</b></label>&nbsp;<html:input path="param5" id="endDate" class="smalltext" />' +
                            '&nbsp;&nbsp;<html:select id="mathcFilter" path="param6">' +
                            '<html:option value="0">ALL</html:option>' +
                            '<html:option value="1">MATCHED</html:option>' +
                            '<html:option value="2">UNMATCHED</html:option>' +
                            '</html:select>&nbsp;&nbsp;<input type="button" id="btnSearch" onClick="search()" class="finbutton" value="Search"/>'+
                            '&nbsp;&nbsp;<input type="button" id="btnExcel" onClick="excel()" class="finbutton" value="Excel"/>&nbsp;&nbsp;' +
                            '&nbsp;&nbsp<input type="button" id="btnPdf" onClick="pdf()" class="finbutton" value="PDF"/>&nbsp;&nbsp;');

                    $("div.center").append('&nbsp;&nbsp;&nbsp;');

                    $("div.bottom").append('<label><b>Balance</b></label>&nbsp;&nbsp;'+
                            '<input type="text" class="smalltext rightAlign" id="balance"/>' +
                            '&nbsp;&nbsp;<label><b>Ref No</b>&nbsp;&nbsp;</label><html:input path="param7" cssClass="smalltext rightAlign" id="refNo"/>' +
                            '&nbsp;&nbsp;<input type="button" id="btnMatch" onClick="match()" class="finbutton" value="Match"/>'+
                            '&nbsp;&nbsp;<input type="button" id="btnAdvise" onClick="advise()" class="finbutton" value="Advise"/>' +
                            '&nbsp;&nbsp;<label><b>S.Opn.Bal.</b>&nbsp;&nbsp;</label><html:input path="param8" class="smalltext rightAlign" id="sobalance"/>' +
                            '&nbsp;&nbsp;<input type="button" id="btnSBalance" onClick="sbalance()" class="finbutton" value="Update"/>');

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
                    $('#trialForm').attr("action", "TransactionsXLSReport.fin");
                    $("#trialForm").submit();
                }
                function pdf() {
                    $('#trialForm').attr("action", "TransactionsPDFReport.fin");
                    $("#trialForm").submit();
                }

                function createURL(source, hdrId, billId){
                    var url = "#";
                    if ("PAYMENT" == source) {
                        url = "retrieveVoucherRow.fin?voucherType=PAYMENT&hdrId=" + hdrId;
                    } else if ("RECEIPT"==source) {
                        url = "retrieveVoucherRow.fin?voucherType=RECEIPT&hdrId=" + hdrId;
                    } else if ("CONTRA"==source) {
                        url = "retrieveVoucherRow.fin?voucherType=CONTRA&hdrId=" + hdrId;
                    } else if ("CREDIT"==source) {
                        url = "paymentVoucher.do?invoke=retrievePayment&param=CREDIT&jeHdrId=" + hdrId;
                    } else if ("DEBIT"==source) {
                        url = "paymentVoucher.do?invoke=retrievePayment&param=DEBIT&jeHdrId=" + hdrId;
                    } else if ("JOURNAL"==source) {
                        url = "retrieveVoucherRow.fin?voucherType=JOURNAL&hdrId=" + hdrId;
                    } else {
                        var mid = ("EXPENSE"==source) ? source : "REVENUE";
                        url = "retrieveBill.fin?param=" + mid + "&billId=" + billId;
                    }
                    //alert(url);
                    return url;
                }
                
                function calculate(sum, src){
                    if(src.checked){
                        matchedAmount = Number(matchedAmount) + Number(sum);
                    }
                    else{
                        matchedAmount = Number(matchedAmount) - Number(sum);
                    }
                    $('#balance').val(matchedAmount);
                    //alert(sum);
                }
                
                function match() {
                    $('#trialForm').attr("action", "LedgerMatch.fin");
                    $("#trialForm").submit();
                }

                function advise() {
                    $('#trialForm').attr("action", "PaymentAdvisePDFReport.fin");
                    $("#trialForm").submit();
                }
                
                function search() {
                    $('#trialForm').attr("action", "Trasactions.fin?month=0&openingBalance=0");
                    $("#trialForm").submit();
                }
                
                function sbalance() {
                    $('#trialForm').attr("action", "SettleOpeningBalance.fin");
                    $("#trialForm").submit();
                }
            </script>
        </head>
        <body>
            <header>
                Transactions - <c:out value='${reportBean.param3}' />
            </header>

                <table id="docfa" class="display" cellspacing="0" width="100%" style="height:600px; overflow-x: scroll; overflow-y: scroll;display: block; word-break: break-all;">

                <thead>
                    <tr>
                        <th>Date</th>
                        <th>No.</th>
                        <th>Particulars</th>
                        <th>Type</th>
                        <th>Chq. No.</th>
                        <th>Chq.Dt.</th>
                        <th>Debit</th>
                        <th>Credit</th>
                        <th width="10px"></th>
                        <th>RefNo</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter largeXXL' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='3'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='4'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='5'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='6'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='7'></td>
                        <td></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='8'></td>
                    </tr>
                </thead>
            </table>
        </html:form>
    </body>
</html>
