
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id= "templateForm" action="billTemplate.fin">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>BillTemplateSearch</title>
            <style>

            </style>
            <script>
                $(document).ready(function () {
                    var table = $('#billTemp').DataTable({
                        ajax: "billTemplateGson.fin",
                        "bJQueryUI": true,
                        deferRender: true,
                        "pagingType": "full_numbers",
                        "bRetrieve": true,
                        "bFilter": true,
                        "bProcessing": true,
                        "dom": 'l<"top">pt',
                        "lengthMenu": [10, 20, 50],
                        "aoColumns": [
                            {  "mData": "templateName",
                                 "render": function (mData, full, row) {
                                  var link = row['templateName'];
                                  return '<a href="retrieveBillTemplate.fin?templateName=' + link + '">' + mData + '</a>';
                            }
                            
                            },
                            {"mData": "seaAir"},
                            {"mData": "expImp"},
                            {"mData": "localForeign"},
                            {"mData": "revExp"},
                            {"mData": "billType"},
                            {"mData": "templateType"}
                        ]
                    });

                    $("div.top").append('' +
                            '<input type="button" id="btnnew" class="finbutton" value="New"/>');

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

                    $('#dropdown1').on('change', function () {
                        table.columns(1).search(this.value).draw();
                    });
                    $('#dropdown2').on('change', function () {
                        table.columns(2).search(this.value).draw();
                    });
                    $('#dropdown3').on('change', function () {
                        table.columns(3).search(this.value).draw();
                    });
                    $('#dropdown4').on('change', function () {
                        table.columns(4).search(this.value).draw();
                    });
                    $('#dropdown5').on('change', function () {
                        table.columns(5).search(this.value).draw();
                    });
                    $('#dropdown6').on('change', function () {
                        table.columns(6).search(this.value).draw();
                    });
                });
                $(function () {
                    $("#btnnew").on('click', function () {
                        $('#templateForm').attr("action", "billTemplate.fin");
                        $("#templateForm").submit();
                    });
                });
            </script>
        </head>
        <body>
            <header>
                BILLING TEMPLATE
            </header>
            <table id="billTemp" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th>TEMPLATE</th>
                        <th>SEA/AIR</th>
                        <th>EXPORT/IMPORT</th>
                        <th>LOCAL/FOREIGN</th>
                        <th>REVENUE/EXPENSE</th>
                        <th>BILL TYPE</th>
                        <th>TEMPLATE TYPE</th>
                    </tr>
                </thead>
                <thead>
                    <tr>
                        <td><input type='text' value='' class='filter smalltext' data-column-index='0'></td>
                        <td><select id="dropdown1">
                                <option value="">SELECT</option>
                                <option value="SEA">SEA</option>
                                <option value="AIR">AIR</option>
                            </select></td>
                        <td><select id="dropdown2">
                                <option value="">SELECT</option>
                                <option value="EXPORT">EXPORT</option>
                                <option value="IMPORT">IMPORT</option>
                            </select></td>
                        <td><select id="dropdown3">
                                <option value="">SELECT</option>
                                <option value="LOCAL">LOCAL</option>
                                <option value="FOREIGN">FOREIGN</option>
                            </select></td>
                        <td><select id="dropdown4">
                                <option value="">SELECT</option>
                                <option value="REVENUE">REVENUE</option>
                                <option value="EXPENSE">EXPENSE</option>
                            </select></td>
                        <td><select id="dropdown5">
                                <option value="">SELECT</option>
                                <option value="INVOICE">INVOICE</option>
                                <option value="CREDITNOTE">CREDITNOTE</option>
                                <option value="DEBITNOTE">DEBITNOTE</option>
                                <option value="MISC">MISC</option>
                                <option value="MISCEXP">MISCEXP</option>
                                <option value="EXPENSE">EXPENSE</option>
                            </select></td>
                        <td><select id="dropdown6">
                                <option value="">SELECT</option>
                                <option value="IGST">IGST</option>
                                <option value="SGST">SGST</option>
                                <option value="UTGST">UTGST</option>
                                <option value="EXPORT-SEZ">EXPORT-SEZ</option>
                                <option value="EXEMPTED">EXEMPTED</option>
                            </select></td>
                    </tr>
                </thead>
            </table>
        
    </body>
    </html:form>
</html>
