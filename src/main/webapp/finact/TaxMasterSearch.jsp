<%-- 
    Document   : TaxMasterSearch
    Created on : Nov 13, 2017, 4:46:12 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id="TaxForm" action="taxMaster.fin">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>TaxMasterSearch</title>
            <script>
                $(document).ready(function () {
                    var table = $('#TaxMasterview').DataTable({
                        ajax: "taxMasterGson.fin",
                        "bJQueryUI": true,
                        deferRender: true,
                        "pagingType": "full_numbers",
                        "bRetrieve": true,
                        "bFilter": true,
                        "bProcessing": true,
                        "dom": 'l<"top">pt',
                        "lengthMenu": [20, 30, 50],
                        "aoColumns": [
                            {"mData": "description",
                                "render": function (mData, full, row) {
                                    var link = row['taxid'];
                                    return '<a href="retrieveTaxMaster.fin?id=' + link + '">' + mData + '</a>';
                                }

                            },
                            {"mData": "percentage"},
                            {"mData": "isPartition"},
                            {"mData": "sub1Desc"},
                            {"mData": "sub1Per"},
                            {"mData": "sub2Desc"},
                            {"mData": "sub2Per"}
                        ]
                    });

                    $("div.top").append('' +
                            '<input type="button" id="btnnew" class="finbutton" value="New"/>');

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
                        $('#TaxForm').attr("action", "taxMaster.fin");
                        $("#TaxForm").submit();
                    });
                });

            </script>
        </head>
        <body>
            <header>
                TAX MASTER
            </header>
            <table id="TaxMasterview" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>DESCRIPTION</th>
                        <th>PERCENTAGE</th>
                        <th>SPLIT?</th>
                        <th>SUB HEAD1</th>
                        <th>SUB HEAD1 PER(%)</th>
                        <th>SUB HEAD2</th>
                        <th>SUB HEAD2 PER(%)</th>
                    </tr>
                </thead>
            </table>
        </body>
    </html:form>
</html>
