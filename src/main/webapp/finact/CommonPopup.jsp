<%-- 
    Document   : GroupSearch
    Created on : Nov 13, 2017, 4:44:29 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id="GroupForm" action="group.fin">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Popup</title>
            <script>
                $(document).ready(function () {
                    var table = $('#commonPopup').DataTable({
                        ajax: "soPopupExpenseGson.fin",
                        "bJQueryUI": true,
                        deferRender: true,
                        "pagingType": "full_numbers",
                        "bRetrieve": true,
                        "bFilter": false,
                        "bProcessing": false,
                        "dom": '<"top">t',
                        "lengthMenu": [10, 20, 50],
                        "aoColumns": [
                            {
                            "mData": "param1",
                            "render": function (mData, full, row) {
                                var link = row['param6'];
                                var closingBal = row['param7'];
                                //link = link+'&endDate='+$('#date').val();
                                //return '<a href="' + link + '"><img src="./finactImages/bc.gif" class="logopdf"/></a>';
                                return '<a href="'+link+'">'+mData+'</a>';
                             }
                        },
                            {"mData": "param2"},
                            {"mData": "param3"},
                            {"mData": "param4"}
                        ]
                    });

                    
                    
                });
                

            </script>
        </head>
        <body>
            <header>
                Data
            </header>
            <table id="commonPopup" class="display" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th></th>
                        <th></th>
                        <th></th>
                        <th></th>
                    </tr>
                </thead>
                
            </table>
        </body>
    </html:form>
</html>
