
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <html:form id="outPartyViewForm">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>OutstandingPartyView</title>
        <link href="<c:url value="https://cdn.datatables.net/buttons/1.4.2/css/buttons.jqueryui.min.css"/>" rel="stylesheet">
        <script src="https://cdn.datatables.net/buttons/1.4.2/js/buttons.jqueryui.min.js"></script>
        <style>
            .ui-dialog .ui-dialog-titlebar {
                padding: .4em 1em;
                position: relative;
                background-color: #23527c;
                color: white;
            }
        </style>
        <script>
            $(document).ready(function () {
                var table = $('#example').DataTable({
                    ajax: "outstandingPartyGson.fin",
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
                            "render": function (mData, full, row) {
                                var link = row['param3'];
                                var href = "javascript:viewFunction1('"+link+"')";
                                return '<a href="' + href + '">' + mData + '</a>';
                            }
                        },
                        {"mData": "param14"},
                        {"mData": "param5"},
                        {"mData": "param6"},
                        {"mData": "param7"},
                        {"mData": "param8"},
                        {"mData": "param9"},
                        {"mData": "param10"},
                        {"mData": "param11"},
                        {"mData": "param12"}
                    ]
                });
                
                $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;' +
                            '<input type="button" id="btnOutPartyViewXLS" class="finbutton" value="Excel"/>');
                    
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
                $("#partyview1").dialog({
                    autoOpen: false,
                    resizable: false,
                    height: "500",
                    width: "1000",
                    modal: true,
                    show: {
                        effect: "fold",
                        duration: 400
                    },
                    hide: {
                        effect: "fold",
                        duration: 400
                    }
                });

            });
            $(function () {
                    $("#btnOutPartyViewXLS").on('click',function(){
                        $('#outPartyViewForm').attr("action", "partywiseSummary.fin");
                        $("#outPartyViewForm").submit();
                    });
                });
            function viewFunction1(link) {
                $("#partyview").load(link).dialog("open");
            }

        </script>

    </head>
    <body>
        <table id="example" class="display" cellspacing="0" width="100%">

            <thead>
                <tr>
                    <th>Account</th>
                    <th>Credit Period</th>
                    <th>0-15</th>
                    <th>16-30</th>
                    <th>31-60</th>
                    <th>61-90</th>
                    <th>91-180</th>
                    <th>181-999</th>
                    <th>Total</th>
                    <th>Current</th>
                </tr>
            </thead>
            <thead>
                <tr>
                    <td><input type='text' value='' class='filter largeXL1' data-column-index='0'></td>
                    <td></td>
                    <td><input type='text' value='' class='filter smalltext' data-column-index='1'></td>
                    <td><input type='text' value='' class='filter smalltext1' data-column-index='2'></td>
                    <td><input type='text' value='' class='filter smalltext1' data-column-index='3'></td>
                    <td><input type='text' value='' class='filter smalltext1' data-column-index='4'></td>
                    <td><input type='text' value='' class='filter smalltext1' data-column-index='5'></td>
                    <td><input type='text' value='' class='filter smalltext1' data-column-index='6'></td>
                    <td><input type='text' value='' class='filter smalltext' data-column-index='7'></td>
                    <td></td>
                </tr>
            </thead>
        </table>

    </body>
    </html:form>
</html>
