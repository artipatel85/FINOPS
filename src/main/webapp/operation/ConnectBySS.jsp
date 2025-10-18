<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="Popup.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id= "popup3ViewForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Connect By SS</title>
            <script>
                $(document).ready(function () {
                    var table = $('#SSchedule').DataTable({
                        ajax: "popup3ViewGson.fin",
                        "serverSide": true,
                        "bAutoWidth": false,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [20, 25, 50],
                        "dom": '<"top">t',
                        "aoColumns": [
                            {
                                "mData": "param3",
                                "render": function (mData, full, row) {
                                    var key = row['param2'];                                    
                                    var href = "javascript:populateSS('"+key+"','"+row['param3']+"','"+row['param4']+"','"+row['param5']+"','"+row['param6']+"','"+row['param7']+"','"+row['param9']+"','"+row['param11']+"','"+row['param8']+"','"+row['param10']+"')";
                                    return '<a href="'+href+'" class="my_close_link">' + mData + '</a>';
                                }
                            },
                            {"mData": "param4"},
                            {"mData": "param5"},
                            {"mData": "param8"},
                            {"mData": "param9"},
                            {"mData": "param10"},
                            {"mData": "param11"}
                        ]
                    });                    
                    
                    $('.filter').on('keyup change', function () {
                        table.search('');
                        table.column($(this).data('columnIndex')).search(this.value).draw();
                    });

                    $(".dataTables_filter input").on('keyup change', function () {
                        table.columns().search('');
                        $('.filter').val('');
                    });
                });      
                
                
            </script>
        </head>
        <body>
            <header>
                Popup 3 View
            </header>
            <table id="SSchedule" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th>VSL</th>
                        <th>VOY</th>
                        <th>CARRIER</th>
                        <th>POL</th>
                        <th>ETD</th>
                        <th>POD</th>
                        <th>ETA</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='1'></td>
                        <td></td>
                        <td></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='2'></td>
                        <td></td>
                        <td></td>
                        
                    </tr>
                </thead>
            </table>
        </html:form>
    </body>
</html>
