<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>

<html>
    <html:form autocomplete="off" method="post" id="userForm" action="user.fin">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <title>User Search</title>
        <script>
                $(document).ready(function () {
                    var table = $('#userview').DataTable({
                        ajax: "adminGson.fin?param=User",
                        "bJQueryUI": true,
                        deferRender: true,
                        "pagingType": "full_numbers",
                        "bRetrieve": true,
                        "bFilter": true,
                        "bProcessing": true,
                        "dom": 'l<"top">pt',
                        "lengthMenu": [20,50],
                        "aoColumns": [
                            {"mData": "userId",
                                "render": function (mData, full, row) {
                                     var link = row['userId'];
                                    return '<a href="retrieveUser.fin?code=' + link + '">' + mData + '</a>';
                                }

                            },
                            {"mData": "description"},
                            {"mData": "cityName"},
                            {"mData": "role"},
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
                        $('#userForm').attr("action", "user.fin");
                        $("#userForm").submit();
                    });
                });

            </script>
    </head>
    <body>
        <header>
                USER SEARCH
            </header>
            <table id="userview" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>USER ID</th>
                        <th>USER NAME</th>
                        <th>CITY</th>
                        <th>ROLE</th>                        
                        <th>STATUS</th>
                    </tr>
                </thead>
                 <thead>
                    <tr>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='3'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='4'></td>
                    </tr>
                </thead>
            </table>
    </body>
    </html:form>
</html>
