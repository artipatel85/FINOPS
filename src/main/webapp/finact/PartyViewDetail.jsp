<%-- 
    Document   : PartyViewDetail
    Created on : Sep 15, 2017, 3:19:39 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <html:form id="partyViewDetailForm">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Pragma" content="no-cache">
        <meta http-equiv="Expires" content="-1">
        <title>PartyViewDetail</title>
        <script>
            $(document).ready(function () {
                var i = 1;
                var table = $('#example').DataTable({
                    'fnDrawCallback': function () {
                        $('.dataTables_filter').each(function () {
                            if(i == 1){
                                i++;
                                $(this).append('<button class="finbutton pull-right" id="backButton" type="button">Back</button>');
                            }
                        });
                    },
                    ajax: "partyViewDetailGson.fin",
                    "ordering":false,
                    "bJQueryUI": true,
                    deferRender: true,
                    "pagingType": "full_numbers",
                    "bRetrieve": true,
                    "bFilter": true,
                    "bProcessing": true,
                    "dom": 'l<"top">pt',
                    "lengthMenu": [15, 30, 50],
                    "aoColumns": [
                        {
                            "mData": "param1",
                            "render": function(mData, full, row){
                                var cond = row['param24'];
                                var href = "billingPDFReport.fin?PARAM=REVENUE&irn="+cond+"&trxid="+row['param18'];
                                if(cond === null){
                                    href = "billingPDFReport.fin?PARAM=REVENUE&trxid="+row['param18'];
                                }
                                return '<a href="' + href + '" target="_blance">' + mData + '</a>';
                            }
                        },
                        {"mData": "param19"},
                        {"mData": "param2"},
                        {"mData": "param8"},
                        {"mData": "param9"},
                        {"mData": "param10"},
                        {"mData": "param11"},
                        {"mData": "param12"},
                        {"mData": "param13"},
                        {"mData": "param14"},
                        {"mData": "param17"},
                        {"mData": "param3"},
                        {"mData": "param20"},
                        {"mData": "param21"},
                        {"mData": "param22"},
                        {"mData": "param23"}
                    ]
                });
                $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
                            '<input type="button" id="btnPartyViewDetailXLS" class="finbutton" value="Excel"/>'+
                            '&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" id="backButton" class="finbutton" value="Back"/>');

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

                $("#backButton").on("click", function () {
                    var params = 'back=Back';
                    $("#partyview").load('outstandingPartyView.fin?' + params).dialog("open");
                });
            });
            $(function () {
                    $("#btnPartyViewDetailXLS").on('click',function(){
                        $('#partyViewDetailForm').attr("action", "partywiseDetail.fin");
                        $("#partyViewDetailForm").submit();
                    });
                });
        </script>
        <style>
             .ui-dialog .ui-dialog-titlebar {
                padding: .4em 1em;
                position: relative;
                background-color: #23527c;
                color: white;
            }
        </style>
    </head>
    <body>
        <table id="example" class="display" cellspacing="0" width="100%">

            <thead>
                <tr>
                    <th>Invoice No</th>
                    <th>BL No</th>
                    <th>Date</th>
                    <th>0-15</th>
                    <th>16-30</th>
                    <th>31-60</th>
                    <th>61-90</th>
                    <th>91-180</th>
                    <th>181-999</th>
                    <th>Total</th>
                    <th>Currency</th>
                    <th>Rate</th>
                    <th>Age</th>
                    <th>Inv No</th>
                    <th>Export/Import</th>
                    <th>Sea/Air</th>
                </tr>
            </thead>
            
        </table>
    </body>
    </html:form>
</html>
