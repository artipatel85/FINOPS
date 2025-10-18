<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="Popup.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id= "popup3ViewForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Partner Popup</title>
            <script>
                $(document).ready(function () {
                    var table = $('#sopartnerpopup').DataTable({
                        ajax: "popup3ViewGson.fin",
                        "serverSide": true,
                        "bAutoWidth": false,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [20, 25, 50],
                        "dom": '<"top">t',
                        "aoColumns": [
                            {
                                "mData": "param1",
                                "render": function (mData, full, row) {
                                    var name = row['param2'];
                                    var address = row['param3'];
                                    var param = row['param4'];
                                    var href = "javascript:populate('"+mData+"','"+name+"','"+param+"','"+address+"')";
                                    return '<a href="'+href+'" class="my_close_link">' + mData + '</a>';
                                }
                                
                            },
                            {"mData": "param2"},
                            {"mData": "param3"}
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
                Partner View
            </header>
            <table id="sopartnerpopup" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th>Code</th>
                        <th>Name</th>
                        <th>Address</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='1'></td>
                        <td></td>
                        
                        
                    </tr>
                </thead>
            </table>
        </html:form>
    </body>
</html>
