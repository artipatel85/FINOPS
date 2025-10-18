<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="Popup.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id= "popup3ViewForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Popup3View</title>
            <script>
                $(document).ready(function () {
                    var table = $('#example').DataTable({
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
                                    var value = row['param2'];
                                    var href = "javascript:populate('"+mData+"','"+value+"')";
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
                
                function populate(key, value){
                    document.getElementById("partnerCode").value = key;
                    document.getElementById("description").value = value;
                    $(".ui-dialog-titlebar-close").click();
                }
            </script>
        </head>
        <body>
            <header>
                Popup 3 View
            </header>
            <table id="example" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th>Value1</th>
                        <th>Value2</th>
                        <th>Value3</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='2'></td>
                    </tr>
                </thead>
            </table>
        </html:form>
    </body>
</html>
