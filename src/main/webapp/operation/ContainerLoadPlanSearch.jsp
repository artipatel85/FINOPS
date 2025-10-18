
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id= "clpForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Container Load Plan</title>
            <script>
                $(document).ready(function () {
                    var table = $('#example').DataTable({
                        ajax: "clpGson.fin",
                        "serverSide": true,
                        "bAutoWidth": false,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [20, 25, 50],
                        "dom": 'l<"top">pt',
                        "aoColumns": [
                            {
                                "mData": "number",
                                "render": function (mData, full, row) {
                                    return '<input type="checkbox" name="blNos" value="' + mData + '"/>';
                                }
                            },
                            {
                                "mData": "number",
                                "render": function (mData, full, row) {
                                    var type = row['param10'];
                                    return '<a href="finReport.do?invoke=billingInvoice&PARAM=' + type + '&trxid=' + mData + '" target="_blank"><img src="finactImages/icon_pdf.png" class="logopdf"></a>';
                                }
                            },
                            {
                                "mData": "number",
                                "render": function (mData, full, row) {
                                    var link1 = row['param10'];
                                    var link2 = row['param11'];
                                    return '<a href="retrieveCLP.fin?loadPlanNo=' + mData+ '">' + mData + '</a>';
                                }
                            },
                            {"mData": "loadPlanDate"},
                            {"mData": "warehouse"},
                            {"mData": "carrierCode"},
                            {"mData": "vsl"},
                            {"mData": "voy"},
                            {"mData": "pol"},
                            {"mData": "pod"},
                            {"mData": "actualLoadingDate"},
                            {"mData": "containerNo"}

                        ]
                    });
                     $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;' +
                        '&nbsp;&nbsp;<input type="button" id="btnnew" value="New" class="finbutton"/>&nbsp;&nbsp;&nbsp;');
                
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
                        $('#clpForm').attr("action", "createCLP.fin");
                        $("#clpForm").submit();
                    });
                    
                });
            </script>
        </head>
        <body>
            <header>
                Container Load Plan Search
            </header>
            <table id="example" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th></th>
                        <th>PRN</th>
                        <th>LOAD PLAN NO</th>
                        <th>DATE</th>
                        <th>WAREHSE</th>
                        <th>CARRIER</th>
                        <th>VSL</th>
                        <th>VOY</th>
                        <th>POL</th>
                        <th>POD</th>
                        <th>Act STUFFING DT</th>
                        <th>CONTAINER NO/SIZE</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td></td>
                        <td></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='3'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='4'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='5'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='6'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='7'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='8'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='9'></td>

                    </tr>
                </thead>
            </table>
        </html:form>
    </body>
</html>
