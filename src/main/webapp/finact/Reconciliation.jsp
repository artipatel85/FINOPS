<%-- 
    Document   : ReconciliationDocfa
    Created on : Sep 17, 2017, 12:57:09 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<%@ taglib uri="/tlds/finacc-rbac" prefix="finacc"%>
<script src="finactjs/finance.js"></script>
<!DOCTYPE html> 
<html>
    <html:form autocomplete="off" method="post" id= "reconForm" action="reconcile.fin">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta http-equiv="Pragma" content="no-cache">
            <meta http-equiv="Expires" content="-1">
            <title>Reconciliation</title>
            <script>
                $(document).ready(function () {
                    var table = $('#docfa').DataTable({
                        ajax: "reconGson.fin",
                        "serverSide": true,
                        "bAutoWidth": false,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [300, 500, 800],
                        "dom": 'l<"top">pt',
                        "aoColumns": [
                            {
                                "mData": "param12",
                                "render": function (mData, full, row) {
                                    return '<input type="checkbox" name="uuids" value="' + mData + '">';
                                }
                            },
                            {"mData": "param1"},
                            {"mData": "param2"},
                            {"mData": "param3"},
                            {"mData": "param4"},                            
                            {"mData": "param6"},                            
                            {"mData": "param8"},
                            {"mData": "param9"},
                            {"mData": "param13","className":"numberTextbox"},
                            {"mData": "param14"},
                            {"mData": "param5", "sWidth": "10%"},
                            {"mData": "param7"}
                        ]
                    });
                    $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;<label><b>Date</b></label>&nbsp;&nbsp;' +
                            '<html:input type="text" path="param1" id="date" class="smalltext" />' +
                            '&nbsp;&nbsp;<input type="submit" id="btnReconcile" class="finbutton" value="Reconcile"/>' +
                            '&nbsp;&nbsp;<label><b>BANK</b></label>&nbsp;<html:input type="text" path="param2" id="hdrAcctName" name="party" class="largeXL"/>' +
                            '&nbsp;&nbsp;<html:input type="text" path="param3" id="codeCombinationId" class="smalltext2 readonly" readonly="true"/>' +
                            '&nbsp;&nbsp;<html:select id="reconFilter" path="param4">'+
                            '<html:option value="2">UNMATCHED</html:option>'+
                            '<html:option value="0">ALL</html:option>'+
                            '<html:option value="1">MATCHED</html:option>'+                            
                            '</html:select>&nbsp;&nbsp;<input type="button" value="Filter" id="btnFilter" class="finbutton"/>'+
                            '&nbsp;&nbsp;<input type="button" id="btnPdf" class="finbutton" value="PDF"/>&nbsp;<input type="button" id="btnPdf2" class="finbutton" value="XLS"/>');

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
                $(function () {
                    $("#date").datepicker({
                        changeMonth: true,
                        changeYear: true,
                        dateFormat: 'yy-mm-dd'
                    });

                    $("#btnPdf").on('click', function () {
                        $('#reconForm').attr("action", "reconPDFReport.fin");
                        $('#reconForm').attr("target","_form");
                        $("#reconForm").submit();
                    });
                    
                    $("#btnPdf2").on('click', function () {
                        $('#reconForm').attr("action", "reconXLSReport.fin");
                        $("#reconForm").submit();
                    });
                    
                    $("#btnFilter").on('click', function () {
                        $('#reconForm').attr("action", "reconciliation.fin");
                        $("#reconForm").submit();
                    });
                });

                $(document).ready(hdrAccount);

                $(document).ready(function () {
                    $("#cashbank").autocomplete({
                        autoFocus: true,
                        source: function (request, response) {
                            $.getJSON("cashbank.fin", {
                                term: request.term
                            }, function (result) {
                                var wordlist = ($.map(result, function (item) {
                                    return {value: item.param2, data: item.param1};
                                }));
                                var re = $.ui.autocomplete.escapeRegex(request.term);
                                var matcher = new RegExp("^" + re, "i");
                                var a = $.grep(wordlist, function (item, index) {
                                    return matcher.test(item.value);
                                });
                                response(a);
                                //response($.ui.autocomplete.filter(wordlist, request.term));
                            });
                        },
                        select: function (event, ui) {
                            $("#codeid").val(ui.item.data);
                        }
                    });
                    
                });
            </script>
        </head>
        <body>
            <header>
                Reconciliation
            </header>
            <div class="finErrMsg">
            <c:out value="${errMsg}"/>
            </div>
            <table id="docfa" class="display" cellspacing="0" width="70%" style="height:600px; overflow-x: scroll; overflow-y: scroll;display: block; word-break: break-all;">
                <thead>
                    <tr>
                        <th></th>
                        <th>No</th>
                        <th>Dated</th>
                        <th>Bank Date</th>
                        <th>Narr</th>                     
                        <th>Particulars</th>                        
                        <th>Chq No</th>
                        <th>Dated</th>
                        <th>Amount</th>
                        <th></th>
                        <th>Bank</th>
                        <th>Type</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='3'></td>                        
                        <td><input type='text' value='' class='filter midtext' data-column-index='4'></td>                        
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='5'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='6'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='7'></td>
                        <td></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='8'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='9'></td>
                    </tr>
                </thead>
            </table>
        </html:form>
    </body>
</html>
