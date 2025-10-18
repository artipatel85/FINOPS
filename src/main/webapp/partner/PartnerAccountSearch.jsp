<%-- 
    Document   : PartnerSearch
    Created on : Sep 9, 2017, 7:09:19 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="../DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id= "partnerForm" action="partnerAccount.fin">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>PartnerSearch</title>
            <script>
                $(document).ready(function () {
                    var table = $('#example').DataTable({
                        ajax: "partnerGson.fin?param=PartnerAccount",
                        "serverSide": true,
                        "bAutoWidth": false,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [20,50],
                        "dom": 'lpt',
                        "aoColumns": [
                            {"mData": null, "sDefaultContent": '<input type="checkbox">'},
                            {
                                "mData": "partnerAccountCode",
                                "render": function (mData, full, row) {
                                    var link1 = row['partnerCode'];
                                    return '<a href="partnerAccount.fin?partnerCode=' + link1 + '">' + mData + '</a>';
                                }
                            },
                            {"mData": "partnerCode"},
                            {"mData": "description1"},
                            {"mData": "address1"},
                            {"mData": "panNo"},
                            {"mData": "stateCode"},
                            {"mData": "gstinNo"},
                            {"mData": "country"},
                            {"mData": "acctName"}

                        ]
                    });                    
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
               
                 
            </script>
        </head>
        <body>
            <header>
                Partner Account Search
            </header>
            <table id="example" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th></th>
                        <th>Code</th>
                        <th>Partner Code</th>
                        <th>Full Name</th>
                        <th>Address</th>
                        <th>Pan No</th>
                        <th>State</th>
                        <th>GSTIN</th>
                        <th>Country</th>
                        <th>Linked Ledger</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='0'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='1'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='2'></td>
                        <td><input type='text' value='' class='filter largeXL1' data-column-index='3'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='4'></td>
                        <td><input type='text' value='' class='filter smalltext2' data-column-index='5'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='6'></td>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='7'></td>
                        <td><input type='text' value='' class='filter midtext' data-column-index='8'></td>
                    </tr>
                </thead>
            </table>
        </html:form>
    </body>
</html>
