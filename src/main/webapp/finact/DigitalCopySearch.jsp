<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form method="post" commandName="voucherBean" id="journalVoucherForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Digital Documents</title>
            <script type="text/javascript">
                $(document).ready(function () {
                    var table = $('#example').DataTable({
                        ajax: "digitalCopyGson.fin",
                        "serverSide": true,
                        "bJQueryUI": true,
                        "bRetrieve": true,
                        "bProcessing": true,
                        "dom": 'l<"top">pt',
                        "lengthMenu": [20, 30, 50],
                        "aoColumns": [
                            {
                                "mData": "param1",
                                "render": function (mData, full, row) {
                                    var link = row['param1'];
                                    return '<input type="checkbox" name="id" value="'+link+'"/>';
                                }
                            },
                            {"mData": "param13"},
                            {"mData": "param2"},
                            {"mData": "param3"},
                            {"mData": "param4"},
                            {"mData": "param7"},
                            {"mData": "param8"},
                            {"mData": "param14"},
                            {
                               "mData": "param6",
                               "render": function (mData, full, row) {
                                       var text = row['param6'];
                                       return '<a href="' + mData + '" >'+text+'</a>';
                                   }
                           }

                        ]
                    });
                    $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" id="new" class="finbutton" value="New" onclick="create()">' +
                            '&nbsp;&nbsp;<rbac:rbac pattern="(.)\\|(Y)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)"><input type="submit" class="finbutton" id="delete" value="Delete" onclick="deleted()"></rbac:rbac>' +
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
                function create() {
                    $('#journalVoucherForm').attr("action", "digitalCopyNew.fin");
                    $("#journalVoucherForm").submit();
                }
                function deleted() {
                    $('#journalVoucherForm').attr("action", "deleteDigitalCopy.fin");
                    $("#journalVoucherForm").submit();
                }
            </script> 
        </head>
        <body>
            <header>
                Digital Documents
            </header>
            <table id="example" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th></th>
                        <th>Document Number</th>
                        <th>SO Number</th>
                        <th>BL Number</th>
                        <th>File Type</th>
                        <th>Shipper</th>
                        <th>Consignee</th>
                        <th>Job Number</th>
                        <th>Cloud URL</th>

                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='2'></td>
                        <td><input type='text' value='' id="date" class='filter smalltext' data-column-index='3'></td>
                        <td><input type='text' value='' class='filter largetext' data-column-index='4'></td>
                        <td><input type='text' value='' class='filter largetext' data-column-index='5'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='6'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='7'></td>

                    </tr>
                </thead>
            </table>
        </body>
    </html:form>
</html>
