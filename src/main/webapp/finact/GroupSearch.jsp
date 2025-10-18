<%-- 
    Document   : GroupSearch
    Created on : Nov 13, 2017, 4:44:29 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id="GroupForm" action="group.fin">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>GroupSearch</title>
            <script>
                $(document).ready(function () {
                    var table = $('#groupview').DataTable({
                        ajax: "groupGson.fin",
                        "bJQueryUI": true,
                        deferRender: true,
                        "pagingType": "full_numbers",
                        "bRetrieve": true,
                        "bFilter": true,
                        "bProcessing": true,
                        "dom": 'l<"top">pt',
                        "lengthMenu": [10, 20, 50],
                        "aoColumns": [
                            {"mData": "acctName",
                                "render": function (mData, full, row) {
                                    var link = row['prtCodeCombId'];
                                    return '<a href="retrieveGroup.fin?id=' + link + '">' + mData + '</a>';
                                }

                            },
                            {"mData": "parentName"},
                            {"mData": "acctTypeName"}
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
                        $('#GroupForm').attr("action", "group.fin");
                        $("#GroupForm").submit();
                    });
                });

            </script>
        </head>
        <body>
            <header>
                Group
            </header>
            <table id="groupview" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>GROUP NAME</th>
                        <th>UNDER</th>
                        <th>ACCOUNT TYPE</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td><input type='text' value='' class='filter midtext' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='2'></td>
                    </tr>
                </thead>
            </table>
        </body>
    </html:form>
</html>
