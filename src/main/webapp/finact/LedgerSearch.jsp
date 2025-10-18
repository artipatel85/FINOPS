<%-- 
    Document   : LedgerSearch
    Created on : Oct 28, 2017, 7:30:53 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id= "ledgerForm" action="ledger.fin">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>LedgerSearch</title>
            <script>
                $(document).ready(function () {
                    var table = $('#ledgerview').DataTable({
                        ajax: "ledgerGson.fin",
                        "serverSide": true,
                        "bAutoWidth": false,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [12, 10, 20],
                        "dom": 'l<"top">pt',
                        "aoColumns": [
                            {
                                "mData": "codeCombinationId",
                                "render": function (mData, full, row) {
                                        return '<a href="ledgerDelete.fin?id=' + mData + '"><img src="finactImages/delete.gif" class="logopdf"></a>';
                                    }
                            },
                            {
                                "mData": "acctName",
                                "render": function (mData, full, row) {
                                    var link = row['codeCombinationId'];
                                    return '<a href="retrieveLedger.fin?id=' + link + '">' + mData + '</a>';
                                }
                            },
                            {"mData": "parentName"},
                            {"mData": "acctTypeName"},
                            {"mData": "prtAddress"},
                            {"mData": "gstinNo"},
                            {"mData": "country"}
                        ]
                    });
                    $("div.top").append('' +
                            '&nbsp;&nbsp;<input type="button" id="btnnew" class="finbutton" value="New"/>&nbsp;&nbsp;'+
                            '&nbsp;&nbsp;<html:select id="parentType" path="parentName">'+
                            '<html:option value="ALL">ALL</html:option>'+
                            '<html:option value="DEBTORS">DEBTORS</html:option>'+
                            '<html:option value="CREDITORS">CREDITORS</html:option>'+                            
                            '</html:select>&nbsp;&nbsp;'+
                            '&nbsp;&nbsp;<input type="checkbox" id="btnnew" class="finbutton" value="New"/>&nbsp;&nbsp;'+
                            '&nbsp;&nbsp;<input type="button" id="btnnew" class="finbutton" value="Excel" onClick="excel()"/>&nbsp;&nbsp;');
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
                    $('#dropdown1').on('change', function () {
                        table.columns(2).search(this.value).draw();
                    });
                });
                $(function () {
                    $("#btnnew").on('click', function () {
                        $('#ledgerForm').attr("action", "ledger.fin");
                        $("#ledgerForm").submit();
                    });
                });
                $(window).keypress(function (event) {
                    if (!(event.which === 115 && event.shiftKey))
                        return true;
                    $("input[name=save]").click();
                    event.preventDefault();
                    return false;
                });
                function excel() {
                    $('#ledgerForm').attr("action", "ledgerXLSReport.fin");
                    $("#ledgerForm").submit();
                }
            </script>
        </head>
        <body>
            <header>
                Ledger Search
            </header>
            <div class="finErrMsg">
            <c:out value="${ledgerBean.errMsg}"/>
            </div>
            <table id="ledgerview" class="display" cellspacing="0" width="100%">
                
                <thead>
                    <tr>
                        <th></th>
                        <th>ACCOUNT NAME</th>
                        <th>UNDER</th>
                        <th>ACCOUNT TYPE</th>
                        <th>ADDRESS</th>
                        <th>GSTIN NO</th>
                        <th>COUNTRY</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='3'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='4'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='5'></td>
                    </tr>
                </thead>
            </table>
        </body>
    </html:form>
</html>
