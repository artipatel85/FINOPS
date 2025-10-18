<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form method="post" id="auditTrailForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Audit Trail</title>
            <style>
                .scroll_y{
                  height:30px;
                  width:30px;
                  overflow-y: scroll;
                }
            </style>
            <script type="text/javascript">
                $(document).ready(function () {
                    var table = $('#example').DataTable({
                        ajax: "auditTrailGson.fin",
                        "serverSide": true,
                        "bJQueryUI": true,
                        "bRetrieve": true,
                        "bProcessing": true,
                        "dom": 'l<"top">pt',
                        "lengthMenu": [20, 30, 50],
                        "aoColumns": [
                            {"mData":"param1"},
                            {"mData": "param7"},
                            {"mData": "param3"},
                            {"mData": "param4"},
                            {"mData": "param5"},
                            {"mData": "param6"}
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
                Audit Trail
            </header>
            <table id="example" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th width="10">USER ID</th>
                        <th width="50">DATA</th>
                        <th width="10">ACTION</th>
                        <th width="10">DATE</th>
                        <th width="10">TYPE</th>
                        <th width="10">BRANCH</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='0'></td>
                        <td><input type='text' value=''  class='filter smalltext' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='3'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='4'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='5'></td>
                    </tr>
                </thead>
            </table>
        </body>
    </html:form>
</html>
