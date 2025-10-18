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
                var i = 1;
                var table = $('#example').DataTable({
                    ajax: "TrialGson.fin",
                    "serverSide": true,
                    "bJQueryUI": true,
                    "bRetrieve": true,
                    "bProcessing": true,
                    "lengthMenu": [20, 25, 50],
                    "dom": 'l<"top">pt',
                    "aoColumns": [
                        {
                            "mData": "param1",
                            "render": function (mData, full, row) {
                                var ccId = row['param12'];
                                var closingBal = row['param7'];
                                return '<input type="button" id="btnBCL" onClick="bcl('+ccId+','+closingBal+')" class="finbutton" value="B"/>';
                             }
                        },
                        {"mData": "param1"},
                        {
                            "mData": "param2",
                            "render": function (mData, full, row) {
                                var link = row['param9'];
                                return '<a href="' + link + '">' + mData + '</a>';
                            }
                        },
                        {"mData": "param8"},
                        {"mData": "param3","className":"numberTextbox"},
                        {"mData": "param4","className":"numberTextbox"},
                        {"mData": "param5","className":"numberTextbox"},
                        {"mData": "param6","className":"numberTextbox"},
                        {"mData": "param7","className":"numberTextbox"},
                        {"mData": "param10"}
                    ]
                });
                $("div.top").append('&nbsp;&nbsp;<html:input path="param1" id="date" class="smalltext"/>&nbsp;&nbsp;<html:select path="param4">'+
                            '<html:option value="0">ALL</html:option>'+
                            '<html:option value="22">SUNDRY DEBTORS</html:option>'+
                            '<html:option value="29">SUNDRY CREDTORS</html:option>'+                            
                            '</html:select>&nbsp;&nbsp;'+
                            '&nbsp;&nbsp;<html:select path="param5">'+
                            '<html:option value="ALL">ALL</html:option>'+
                            '<html:option value="LOCAL">LOCAL</html:option>'+
                            '<html:option value="FOREIGN">FOREIGN</html:option>'+                            
                            '</html:select>&nbsp;&nbsp;'+
                            '&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" id="btnExcel" onClick="excel()" class="finbutton" value="EXCEL"/>'+
                        '&nbsp;&nbsp;<input type="button" id="btnPdf" onClick="pdf()" class="finbutton" value="PDF"/>'+
                        '&nbsp;&nbsp;<label><b>Filter 0 Balance</b></label><html:checkbox path="param2" value="Y"/>'+
                         '&nbsp;&nbsp;<label><b>Only Opn Balance</b></label><html:checkbox path="param7" value="Y"/>'+
                        '&nbsp;&nbsp;<html:select path="param6">'+
                            '<html:option value="ALL">ALL</html:option>'+
                            '<html:option value="SFP">SFP</html:option>'+
                            '<html:option value="SFPM">SFPM</html:option>'+ 
                            '<html:option value="SFPC">SFPC</html:option>'+ 
                            '<html:option value="SFPK">SFPK</html:option>'+ 
                            '<html:option value="SFPG">SFPG</html:option>'+ 
                            '</html:select>');
                
                
                $('.filter').on('keyup change', function () {
                    if(this.value.length > 0 && this.value.length < 3){
                        return false;
                    }
                    table.search('');
                    table.column($(this).data('columnIndex')).search(this.value).draw();
                });
                $(".dataTables_filter input").on('keyup change', function () {
                    table.columns().search('');
                    $('.filter').val('');
                }); 
                $("#date").datepicker({
                        changeMonth: true,
                        changeYear: true,
                        dateFormat: 'yy-mm-dd'
                    });
            });
            function excel() {
                $('#trialForm').attr("action", "trialXLS.fin");
                $("#trialForm").submit();
            }
            function pdf() {
                $('#trialForm').attr("action", "trialPDF.fin");
                $("#trialForm").submit();
            }
            
            function bcl(ccId, closingBal) {
                var link = "bclPDF.fin?id="+ccId+"&clng="+closingBal+"&endDate="+$("#date").val();
//                link = link+"&endDate="+$('#date').val();
//                alert(link);
                $('#trialForm').attr("action", link);
                $("#trialForm").submit();
            }
                
        </script>
    </head>
    <body class="commonBody">
        <header>
            Trial Balance
        </header>
        
        <table id="example" class="display" cellspacing="0" width="100%">
            <thead>
                <tr>
                    <th></th>
                    <th>ACNO</th>
                    <th>LEDGER</th>
                    <th>UNDER</th>
                    <th>OPENING</th>
                    <th>Dr/Cr</th>
                    <th>Debit</th>
                    <th>Credit</th>
                    <th>Closing</th>
                    <th>Dr/Cr</th>
                </tr>
            </thead>
            <thead>
                <tr>
                    <td></td>
                    <td><input type='text' class='filter smalltext' data-column-index='0'></td>
                    <td><input type='text' class='filter midtext' data-column-index='1'></td>
                    <td><input type='text' class='filter midtext' data-column-index='2'></td>
                    <td><input type='text' class='filter smalltext2' data-column-index='3'></td>
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
