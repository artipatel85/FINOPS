<%-- 
    Document   : CurrencySearch
    Created on : Jan 11, 2018, 8:51:29 AM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id="CountryForm" action="currency.fin">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <title>JSP Page</title>
        <script>
                $(document).ready(function () {
                    var table = $('#countryview').DataTable({
                        ajax: "adminGson.fin?param=Currency",
                        "bJQueryUI": true,
                        deferRender: true,
                        "pagingType": "full_numbers",
                        "bRetrieve": true,
                        "bFilter": true,
                        "bProcessing": true,
                        "dom": 'l<"top">pt',
                        "lengthMenu": [10, 20, 50],
                        "aoColumns": [
                            {"mData": null, "sDefaultContent": '<input type="checkbox">'},
                            {"mData": "currencyCode",
                                "render": function (mData, full, row) {
                                    var link = row['currencyCode'];
                                    return '<a href="retrieveCurrency.fin?code=' + link + '">' + mData + '</a>';
                                }

                            },
                            {"mData": "description"},
                            {"mData": "status"}
                        ]
                    });

                    $("div.top").append('' +
                            '<input type="button" id="btnnew" class="finbutton" value="New"/>' +
                            '&nbsp;&nbsp;<button type="submit" class="tablebtn">DELETE</button>' +
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
                $(function () {
                    $("#btnnew").on('click', function () {
                        $('#CountryForm').attr("action", "currency.fin");
                        $("#CountryForm").submit();
                    });
                });

            </script>
    </head>
    <body>
        <header>
                CURRENCY
            </header>
            <table id="countryview" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th></th>
                        <th>CURRENCY CODE</th>
                        <th>CURRENCY NAME</th>
                        <th>STATUS</th>
                    </tr>
                </thead>
                 <thead>
                    <tr>
                        <td></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='3'></td>
                    </tr>
                </thead>
            </table>
    </body>
    </html:form>
</html>
