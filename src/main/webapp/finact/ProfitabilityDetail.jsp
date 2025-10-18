<%-- 
    Document   : ReconciliationDocfa
    Created on : Sep 17, 2017, 12:57:09 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>

<!DOCTYPE html>
<html>
    <head>
        <html:form method="post" commandName="reportBean" id="trialForm">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Profitability</title>
        <script>
            $(document).ready(function () {
                var table = $('#example').DataTable({
                    ajax: "profitabilityDetailGson.fin",
                    "bJQueryUI": true,
                    deferRender: true,
                    "bRetrieve": false,
                    "bFilter": false,
                    "bProcessing": false,
                    "bSort": false,
                    "dom": '<"top">lt',
                    "lengthMenu": [500],
                    "aoColumns": [
                        {"mData": "param1"},
                        {"mData": "param9"},
                        {
                            "mData": "param2",
                            "render": function (mData, full, row) {
                                var link = row['param6'];
                                //alert(mData);
                                if(mData == null){
                                    return '';
                                }
                                return '<a href="' + link + '">' + mData + '</a>';
                            }
                        },
                        {"mData": "param3"},
                        {"mData": "param4"},
                        {"mData": "param5","className":"numberTextbox"},
                        {"mData": "param7"},
                        {"mData": "param8"}
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
            function excel() {
                $('#trialForm').attr("action", "trialXLS.fin");
                $("#trialForm").submit();
            }
                
        </script>
    </head>
    <body>
        <header>
            Profitability - (BL No. :: <c:out value='${reportBean.param1}' />)
        </header>
        
        <table id="example" class="display" cellspacing="0" width="100%">

            <thead>
                <tr>
                    <th>Date</th>
                    <th>BL No</th>
                    <th>Voucher No</th>
                    <th>Account</th>
                    <th>Type</th>
                    <th>Amount</th>
                    <th>Sea/Air</th>
                    <th>Exp/Imp</th>
                </tr>
            </thead>
            
        </table>
        </html:form>
    </body>
</html>
