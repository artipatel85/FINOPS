<%-- 
    Document   : CountrySearch
    Created on : Jan 11, 2018, 8:49:39 AM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id="RoleForm" action="role.fin">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Roles</title>
         <script>
                $(document).ready(function () {
                    var table = $('#roleview').DataTable({
                        ajax: "adminGson.fin?param=Role",
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
                            {"mData": "role",
                                "render": function (mData, full, row) {
                                    return '<a href="retrieveRole.fin?roleName=' + mData + '">' + mData + '</a>';
                                }

                            },
                            {"mData": "description"}
                        ]
                    });

                    $("div.top").append('' +
                            '&nbsp;&nbsp;<input type="button" id="btnnew" class="finbutton" value="New"/>' +
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
                        $('#RoleForm').attr("action", "role.fin");
                        $("#RoleForm").submit();
                    });
                });

            </script>
    </head>
    <body>
        <header>
                ROLE
            </header>
            <table id="roleview" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th></th>
                        <th>Role Name</th>
                        <th>Description</th>
                        
                    </tr>
                </thead>
                 <thead>
                    <tr>
                        <td></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='2'></td>
                        
                    </tr>
                </thead>
            </table>
    </body>
    </html:form>
</html>
