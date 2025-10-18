
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id= "sailingScheduleForm" action="sailingScheduleSave.fin">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Sailing Schedule</title>
            <script>
                $(document).ready(function () {
                    var table = $('#example').DataTable({
                        ajax: "sailingScheduleGson.fin",
                        "serverSide": true,
                        "bAutoWidth": false,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [20, 25, 50],
                        "dom": 'l<"top">pt',
                        "aoColumns": [
                            {
                                "mData": "param1",
                                "render": function (mData, full, row) {
                                    var link1 = row['param10'];
                                    var link2 = row['param11'];
                                    return '<a href="sailingScheduleRetrieve.fin?scheduleId=' + link2 + '&scheduleSeqId=' + link1 + '">' + mData + '</a>';
                                }
                            },
                            {"mData": "param2"},
                            {"mData": "param3"},
                            {"mData": "param4"},
                            {"mData": "param5"},
                            {"mData": "param6"},
                            {"mData": "param7"},
                            {"mData": "param8"},
                            {"mData": "param9"},
                            {"mData": "param12"},
                            {"mData": "param13"},
                            {"mData": "param14"},
                            {"mData": "param15"},
                            {"mData": "param16"},
                            {"mData": "param17"}

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
                        $('#sailingScheduleForm').attr("action", "sailingScheduleCreate.fin");
                        $("#sailingScheduleForm").submit();
                    });
                    
                });
            </script>
        </head>
        <body>
            <header>
                Sailing Schedule Search
            </header>
            <table id="example" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th>VSL</th>
                        <th>VOY</th>
                        <th>CARRIER</th>
                        <th>CFS DATE</th>
                        <th>CY DATE</th>
                        <th>POL</th>
                        <th>ETD</th>
                        <th>POD</th>
                        <th>ETA</th>
                        <th>VSL</th>
                        <th>VOY</th>
                        <th>POL</th>
                        <th>POD</th>
                        <th>ETD</th>
                        <th>ETA</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='3'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='4'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='5'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='6'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='7'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='8'></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                </thead>
            </table>
        </html:form>
    </body>
</html>
