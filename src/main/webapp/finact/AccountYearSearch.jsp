<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id="formForm" action="form.fin">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <title>Account Year</title>
        <script>
                $(document).ready(function () {
                    var table = $('#countryview').DataTable({
                        ajax: "periodGson.fin",
                        "bJQueryUI": true,
                        deferRender: true,
                        "pagingType": "full_numbers",
                        "bRetrieve": true,
                        "bFilter": true,
                        "bProcessing": true,
                        "dom": 'l<"top">pt',
                        "lengthMenu": [10, 20, 50],
                        "aoColumns": [

                            {"mData": "acctYear",
                                "render": function (mData, full, row) {
                                   return '<input type="checkbox" name="ids" value="'+mData+'"/>';
                                }

                            },
                            {"mData":"acctYear"},
                            {"mData":"startDate"},
                            {"mData":"endDate"},
                            {"mData": "periodStatus"}
                        ]
                    });

                    $("div.top").append('' +
                            '&nbsp;&nbsp;<input type="button" id="btnnew" class="finbutton" value="NEW"/>' +
                            '&nbsp;&nbsp;<input type="button" class="finbutton" value="CLOSE" id="btnClose" />' +
                            '&nbsp;&nbsp;<input type="button" class="finbutton" id="btnSwitch" value="SWITCH"/>' +
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
                    $("#btnSwitch").on('click', function () {
                        $('#formForm').attr("action", "setAccountYear.fin");
                        $("#formForm").submit();
                    });

                    $("#btnnew").on('click', function () {
                        $('#formForm').attr("action", "periodNew.fin");
                        $("#formForm").submit();
                    });

                    $("#btnClose").on('click', function () {
                        $('#formForm').attr("action", "##closeAccountYear.fin");
                        $("#formForm").submit();
                    });
                });

            </script>
    </head>
    <body>
        <header>
                Account Year
            </header>
            <table id="countryview" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th></th>
                        <th>Account Year</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Status</th>
                    </tr>
                </thead>
                 <thead>
                    <tr>
                        <td></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter small' data-column-index='3'></td>
                    </tr>
                </thead>
            </table>
    </body>
    </html:form>
</html>
