<%-- 
    Document   : ReconciliationDocfa
    Created on : Sep 17, 2017, 12:57:09 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id= "reconForm" action="reconcileDocfa.fin">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta http-equiv="Pragma" content="no-cache">
            <meta http-equiv="Expires" content="-1">
            <title>ReconciliationDocfa</title>
            <script>
                $(document).ready(function () {
                    var table = $('#docfa').DataTable({
                        ajax: "recoDocfaGson.fin",
                        "serverSide": true,
                        "bAutoWidth": false,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [12, 10, 20],
                        "dom": 'l<"top">pt',
                        "aoColumns": [
                            {
                                "mData": "param10",
                                "render": function (mData, full, row) {
                                    return '<input type="checkbox" name="id" value="' + mData + '">';
                                }
                            },
                            {"mData": "param1"},
                            {"mData": "param2"},
                            {"mData": "param4", "sWidth": "22%"},
                            {"mData": "param3"},
                            {"mData": "param8", "sWidth": "13%"},
                            {"mData": "param5"},
                            {"mData": "param9"},
                            {"mData": "param7"},
                            {"mData": "param6", "className": "numberTextbox"}


                        ]
                    });
                    $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
                            '<html:input type="text" path="param1" id="date" class="smalltext" />' +
                            '&nbsp;&nbsp;<input type="submit" id="btnReconcile" class="finbutton" value="Reconcile"/>');

                    table.buttons().container()
                            .insertBefore('#example_filter');
                    $('.filter').on('keyup change', function () {
                        if (this.value.length > 0 && this.value.length < 3) {
                            return false;
                        }
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
                });

            </script>
        </head>
        <body>
            <header>
                Reconciliation-Docfa
            </header>

            <table id="docfa" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th></th>
                        <th>No</th>
                        <th>Date</th>
                        <th>Narr</th>
                        <th>PartyCode</th>
                        <th>PartyName</th>
                        <th>BankCode</th>
                        <th>BankName</th>
                        <th>Recondt</th>
                        <th>AMOUNT</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='3'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='4'></td>
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
