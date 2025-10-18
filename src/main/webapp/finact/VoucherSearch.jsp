<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
    <html:form method="post" commandName="voucherBean" id="receiptVoucherForm">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ReceiptSearch</title>

        <script type="text/javascript">
        $(document).ready(function () {
            var table = $('#example').DataTable({
                ajax: "receiptGson.fin?param=${voucherBean.voucherType}",
                "serverSide": true,
                "bJQueryUI": true,
                "bRetrieve": true,
                "bProcessing": true,
                "dom": 'l<"top">pt',
                "lengthMenu": [20, 30, 50],
                "aoColumns": [
                    {
                        "mData": "voucherNo",
                        "render": function (mData, full, row) {
                            var link = row['jeHdrId'];
                            return '<input type="checkbox" name="hdrIds" value="'+link+'"/>';
                        }
                    },
                    {
                        "mData": "jeHdrId",
                        "render": function (mData, full, row) {
                                return '<a href="voucherPDFReport.fin?voucherType=${voucherBean.voucherType}&trxId=' + mData + '" target="_blank"><img src="finactImages/icon_pdf.png" class="logopdf"></a>';
                            }
                    },
                    {
                        "mData": "voucherNo",
                        "render": function (mData, full, row) {
                                var link = row['jeHdrId'];
                                return '<a href="retrieveVoucherRow.fin?voucherType=${voucherBean.voucherType}&hdrId=' + link + '">' + mData + '</a>';
                            }
                    },
                    {"mData": "jeDate"},
                    {"mData": "hdrAcctName"},
                    {"mData": "lineAcctName"},
                    {"mData": "hdrTotalAmount","className":"numberTextbox"},
                    {"mData": "invNo"}

                ]
            });
            $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" id="new" class="finbutton" value="New" onclick="create()">'+
                        '&nbsp;&nbsp;'+
                        '<rbac:rbac pattern="(.)\\|(Y)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)"><input type="submit" class="finbutton" id="delete" value="Delete" onclick="deleted()"></rbac:rbac>'+
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
        });
        function create() {
            $('#receiptVoucherForm').attr("action", "createVoucher.fin?param=${voucherBean.voucherType}");
            $("#receiptVoucherForm").submit();
        }
        
        function deleted() {
            $('#receiptVoucherForm').attr("action", "deleteVoucher.fin?param=${voucherBean.voucherType}");
            $("#receiptVoucherForm").submit();
        }
    </script> 
</head>
<body>
    <header>
        Voucher Search - ${voucherBean.voucherType}
    </header>
    
    <table id="example" class="display" cellspacing="0" width="100%">

        <thead>
            <tr>
                <th></th>
                <th>PDF</th>
                <th>VOUCHER NO</th>
                <th>DATE</th>
                <th>DEBIT A/C</th>
                <th>CREDIT A/C</th>
                <th>AMOUNT</th>
                <th>INV NO</th>
            </tr>
        </thead>
        <thead>
            <tr>
                <td></td>
                <td></td>
                <td><input type='text' value='' class='filter smalltext' data-column-index='0'></td>
                <td><input type='text' value='' id="date" class='filter smalltext' data-column-index='1'></td>
                <td><input type='text' value='' class='filter midtext' data-column-index='2'></td>
                <td><input type='text' value='' class='filter midtext' data-column-index='3'></td>
                <td><input type='text' value='' class='filter smalltext' data-column-index='4'></td>
                <td><input type='text' value='' class='filter smalltext' data-column-index='5'></td>
            </tr>
        </thead>
    </table>
</body>
</html:form>
</html>
