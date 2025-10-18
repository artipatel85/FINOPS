
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" command="blBean" method="post" id= "blForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Shipping Order</title>
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
                        ajax: "blGson.fin?param=${blBean.expImp}",
                        "serverSide": true,
                        "bAutoWidth": false,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [20, 25, 50],
                        "dom": 'l<"top">pt',
                        "aoColumns": [
                            {
                                "mData": "blNumber",
                                "render": function (mData, full, row) {
                                    return '<input type="checkbox" name="uuids" value="' + mData + '"/>';
                                }
                            },
                            {
                                "mData": "blNumber",
                                "render": function (mData, full, row) {
                                    var type = row['param10'];
                                    return '<a href="blPDF.fin?blNo=' + mData + '" target="_blank"><img src="finactImages/icon_pdf.png" class="logopdf"></a>';
                                }
                            },
                            {
                                "mData": "blNumber",
                                "render": function (mData, full, row) {
                                    return '<a href="blRetrieve.fin?param=${blBean.expImp}&blNumber=' + mData + '">'+mData+'</a>';
                                }
                            },
                            {"mData": "jobNumber"},
                            {"mData": "shipperName"},
                            {"mData": "consigneeName"},
                            {"mData": "vsl"},
                            {"mData": "voy"},
                            {"mData": "pol"},
                            {"mData": "pod"},
                            {"mData": "etd"},
                            {"mData": "eta"}

                        ]
                    });
                    $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;' +
                            '&nbsp;&nbsp;<input type="button" id="btnnew" value="New" class="finbutton"/>&nbsp;<rbac:rbac pattern="(.)\\|(Y)\\|(Y)\\|(Y)\\|(.)\\|(.)\\|(.)" formId="1048"><input type="button" id="deleteBtn" value="Delete" class="finbutton"/></rbac:rbac>&nbsp;');

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
                    $(function () {
                        $("#searchBtn").on('click', function () {
                            $('#blForm').attr("action", "bl.fin?param=${blBean.expImp}");
                            $("#blForm").submit();
                        });
                        $("#deleteBtn").on('click', function () {
                            $('#blForm').attr("action", "deleteBL.fin?param=${blBean.expImp}");
                            $("#blForm").submit();
                        });
                    });
                });

                $(function () {
                    $("#btnnew").on('click', function () {
                        $('#blForm').attr("action", "blCreate.fin?param=${blBean.expImp}");
                        $("#blForm").submit();
                    });
                });

                $(document).keypress(function(event){
                    var keycode = (event.keyCode ? event.keyCode : event.which);
                    if(keycode == '13'){
                        $('#blForm').attr("action", "bl.fin?param=${blBean.expImp}");
                        $("#blForm").submit();
                    }
                });

            </script>
        </head>
        <body>
            <header>
                BL Search - ${blBean.expImp}
            </header>
            <div id="accordion">
            <h3>SEARCH</h3>
            <div class="rows">
                <table class="table" id="tbl" height="50">
                    <tr>
                        <td>
                            BL NO
                        </td>
                        <td>
                            <html:input path="blNumber"/>
                        </td>
                        <td>
                            JOB NO
                        </td>
                        <td>
                            <html:input path="jobNumber"/>
                        </td>
                        <td>
                            VSL
                        </td>
                        <td>
                            <html:input path="vsl"/>
                        </td>
                        <td>
                            VOY
                        </td>
                        <td>
                            <html:input path="voy"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            SHIPPER
                        </td>
                        <td>
                            <html:input path="shipperName"/>
                        </td>
                        <td>
                            CONSIGNEE
                        </td>
                        <td>
                            <html:input path="consigneeName"/>
                        </td>
                        <td>
                            POL
                        </td>
                        <td>
                            <html:input path="pol"/>
                        </td>
                        <td>
                            POD
                        </td>
                        <td>
                            <html:input path="pod"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            SO NUMBER
                        </td>
                        <td>
                            <html:input path="soNumber"/>
                        </td>
                        <td>
                            A.SHIPPER
                        </td>
                        <td>
                            <html:input path="actShipperName"/>
                        </td>
                        <td>
                            A.CONSIGNEE
                        </td>
                        <td>
                            <html:input path="actConsigneeName"/>
                        </td>
                        <td>
                            M.BL.NUMBER
                        </td>
                        <td>
                            <html:input path="mBlNumber"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            LOADING AGENT
                        </td>
                        <td>
                            <html:input path="loadingAgentName"/>
                        </td>
                        <td>
                            DEST. AGENT
                        </td>
                        <td>
                            <html:input path="destinationAgentName"/>
                        </td>
                        <td>
                            S.BILL NO.
                        </td>
                        <td>
                            <html:input path="cb.shippingBillNumber"/>
                        </td>
                        <td>
                            CONTAINER NO
                        </td>
                        <td>
                            <html:input path="cb.number"/>
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
                        <th>PRN</th>
                        <th>BL NO</th>
                        <th>JOB NO</th>
                        <th width="15%">SHPR</th>
                        <th width="15%">CNEE</th>
                        <th width="10%">VSL</th>
                        <th>VOY</th>
                        <th>POL</th>
                        <th>POD</th>
                        <th>ETD</th>
                        <th>ETA</th>
                    </tr>
                </thead>

            </table>
        </html:form>
    </body>
</html>
