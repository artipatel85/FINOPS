<%-- 
    Document   : PartnerSearch
    Created on : Sep 9, 2017, 7:09:19 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id= "partnerForm" action="partnerAccount.fin">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Partner Search</title>
            <script>
                $(document).ready(function () {
                    var table = $('#example').DataTable({
                        ajax: "partnerSearchGsonKYC.fin",
                        "serverSide": true,
                        "bAutoWidth": false,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [20, 25, 50],
                        "dom": 'l<"top">pt',
                        "aoColumns": [
                           {"mData": "param1"},
                           {
                                "mData": "param2",
                                "render": function (mData, full, row) {
                                                var link1 = row['param5'];
                                                return '<a href="partnerKyc.fin?kycId=' + link1 + '">' + mData + '</a>';
                                            }
                            },
                            {"mData": "param3"},
                            {"mData": "param4"}

                        ]
                    });
                     $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
                        '&nbsp;&nbsp;<input type="button" id="btnnew" value="New" class="finbutton"/>&nbsp;<input type="button" id="btndelete" value="Delete" class="finbutton"/>');
                
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
                        $('#partnerForm').attr("action", "partner.fin?id=1");
                        $("#partnerForm").submit();
                    });
                    
                    $("#btndelete").on('click', function () {
                        $('#partnerForm').attr("action", "");
                        $("#partnerForm").submit();
                    });
                });
            </script>
        </head>
        <body>
            <header>
                Partner Search
            </header>
            <table id="example" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th>Partner Code</th>
                        <th>Full Name</th>
                        <th>Status</th>
                        <th>Country</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter largeXL1' data-column-index='3'></td>
                        
                    </tr>
                </thead>
            </table>
        </html:form>
    </body>
</html>
