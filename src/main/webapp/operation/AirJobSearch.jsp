
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" command="blBean" method="post" id= "blForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>JOB</title>
            <style>
                    .ui-accordion .ui-accordion-header {
                    display: block;
                    cursor: pointer;
                    position: relative;
                    text-align: left;
                    margin: 1px 0 0 0;
                    font-size: 13px;
                    background-color: #5c5a5a;
                    margin-left: 0px;
                    margin-right: 0px;
                    color: white;
                    font-family: system-ui;
                    padding: 5px 2px 5px 2px;
                    border: 1px solid #5c5a5a;
                }
                .ui-accordion .ui-accordion-content {
                    border-top: 0;
                    margin-left: 0px;
                    margin-right: 0px;
                }
                .fa{
                    float: right;
                    padding-right: 10px;
                    padding-top: 5px;
                }
            </style>
            <script>
                $(function () {
                    var icons = {
                        header: "ui-icon-circle-arrow-e",
                        activeHeader: "ui-icon-circle-arrow-s"
                    };
                    $("#accordion").accordion({
                        collapsible: true,
                        icons: icons
                    });
                });
                $(document).ready(function () {
                    var table = $('#example').DataTable({
                        ajax: "airJobGson.fin?param=${jobBean.expImp}",
                        "serverSide": true,
                        "bAutoWidth": false,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [20, 25, 50],
                        "dom": 'l<"top">pt',
                        "aoColumns": [
                            {
                                "mData": "jobNumber",
                                "render": function (mData, full, row) {
                                    return '<input type="checkbox" name="blNos" value="' + mData + '"/>';
                                }
                            },
                            {
                                "mData": "jobNumber",
                                "render": function (mData, full, row) {
                                    return '<a href="airJobRetrieve.fin?param=${jobBean.expImp}&jobNumber=' + mData + '">'+mData+'</a>';
                                }
                            },
                            {"mData": "jobDate"},
                            {"mData": "pol"},
                            {"mData": "pod"},
                            {"mData": "dest"},
                            {"mData": "vsl"},
                            {"mData": "voy"},
                            {"mData": "etd"},
                            {"mData": "eta"},
                            {"mData": "currencyCode"},
                            {"mData": "mBlNumber"}

                        ]
                    });
                    $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;' +
                            '&nbsp;&nbsp;<input type="button" id="btnnew" value="New" class="finbutton"/>&nbsp;&nbsp;&nbsp;');

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
                        $('#blForm').attr("action", "airJobCreate.fin?param=${jobBean.expImp}");
                        $("#blForm").submit();
                    });
                $("#searchBtn").on('click', function () {
                        $('#blForm').attr("action", "airJob.fin?param=${jobBean.expImp}");
                        $("#blForm").submit();
                    });
                });
            </script>
        </head>
        <body>
            <header>
                Air Job Search - ${jobBean.expImp}
            </header>
            <div id="accordion">
            <h3>SEARCH</h3>
            <div class="rows">
                <table class="table" id="tbl" height="50">
                    <tr>
                        <td>
                            Job No
                        </td>
                        <td>
                            <html:input path="jobNumber" class="medium"/>
                        </td>
                        <td>
                            Date
                        </td>
                        <td>
                            <html:input path="jobDate" id="jobDate"  class="medium"/>
                        </td>
                        <td>
                            VSL
                        </td>
                        <td>
                            <html:input path="vsl" class="medium"/>
                        </td>
                        <td>
                            VOY
                        </td>
                        <td>
                            <html:input path="voy" class="medium"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            ETD
                        </td>
                        <td>
                            <html:input path="etd" class="medium"/>
                        </td>
                        <td>
                            ETA
                        </td>
                        <td>
                            <html:input path="eta" class="medium"/>
                        </td>
                        <td>
                            POL
                        </td>
                        <td>
                            <html:input path="pol" class="medium"/>
                        </td>
                        <td>
                            POD
                        </td>
                        <td>
                            <html:input path="pod" class="medium"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <button type="button" class="finbutton" id="searchBtn">SEARCH</button>
                        </td>
                        <td>
                            <button type="reset" class="finbutton">RESET</button>
                        </td>

                    </tr>
                </table>
            </div>
            </div>
            <br/>
            <table id="example" class="display" cellspacing="0" width="100%">

                <thead>
                    <tr>
                        <th></th>
                        <th>JOB NO</th>
                        <th>DATE</th>
                        <th>POL</th>
                        <th>POD</th>
                        <th>DEST</th>
                        <th>VSL</th>
                        <th>VOY</th>
                        <th>Eth</th>
                        <th>ETA</th>
                        <th>CURR</th>
                        <th>MB/L</th>
                    </tr>
                </thead>

            </table>
        </html:form>
    </body>
</html>
