
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id="CompanyForm" action="company.fin">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>CompanySearch</title>
            <script>
                $(document).ready(function () {
                    var table = $('#companyview').DataTable({
                        ajax: "companyGson.fin",
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
                            {"mData": "companyId",
                                "render": function (mData, full, row) {
                                    var link = row['companyId'];
                                    return '<a href="retrieveCompany.fin?id=' + link + '">' + mData + '</a>';
                                }

                            },
                            {"mData": "companyName1"},
                            {"mData": "address"},
                            {"mData": "cityName"},
                            {"mData": "countryName"},
                            {"mData": "tel"},
                            {"mData": "selectedStatus"}
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
                        $('#CompanyForm').attr("action", "company.fin");
                        $("#CompanyForm").submit();
                    });
                });

            </script>
        </head>
        <body>
            <header>
                COMPANY PROFILE
            </header>
            <table id="companyview" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th></th>
                        <th>COMPANY ID</th>
                        <th>COMPANY NAME</th>
                        <th>ADDRESS</th>
                        <th>CITY</th>
                        <th>COUNTRY</th>
                        <th>PHONE</th>
                        <th>STATUS</th>
                    </tr>
                </thead>
                 <thead>
                    <tr>
                       
                    </tr>
                </thead>
            </table>
        </body>
    </html:form>
</html>
