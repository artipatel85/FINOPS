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
        <title>Trial-Balance</title>
        <script>
            $(document).ready(function () {
                var table = $('#example').DataTable({
                    ajax: "MonthlyGson.fin",
                    "bJQueryUI": true,
                    deferRender: true,
                    "bRetrieve": true,
                    "bFilter": false,
                    "bProcessing": false,
                    "ordering":false,
                    "dom": '<"top">t',
                    "pageLength": 50,
                    "lengthMenu": false,
                    "aoColumns": [
                        {"mData": "param1"},
                        {"mData": "param2","className":"numberTextbox"},
                        {"mData": "param3","className":"numberTextbox"},
                        {"mData": "param4","className":"numberTextbox"}
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
            Monthly Summary - <c:out value='${reportBean.param3}' />
        </header>
        
        <table id="example" class="display" cellspacing="0" width="100%">

            <thead>
                <tr>
                    <th>Month</th>
                    <th>Debit</th>
                    <th>Credit</th>
                    <th>Closing</th>
                    
                </tr>
            </thead>
            
        </table>
        </html:form>
    </body>
</html>
