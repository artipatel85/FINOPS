
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" command="hawbBean" method="post" id= "hawbForm">
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
                        ajax: "hawbGson.fin?param=${hawbBean.expImp}",
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
                                    return '<a href="hawbRetrieve.fin?param=${hawbBean.expImp}&blNumber=' + mData + '">'+mData+'</a>';
                                }
                            },
                            {"mData": "jobNumber"},
                            {"mData": "shipperName"},
                            {"mData": "consigneeName"},
                            {"mData": "flightNo"},
                            {"mData": "flightDate"},
                            {"mData": "pol"},
                            {"mData": "pod"},
                            {"mData": "creationDate"}

                        ]
                    });
                    $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;' +
                            '&nbsp;&nbsp;<input type="button" id="btnnew" value="New" class="finbutton"/>&nbsp;<rbac:rbac pattern="(.)\\|(Y)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)" formId="1053"><input type="button" id="btndelete" value="Delete" class="finbutton"/></rbac:rbac>&nbsp;');

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
                        $('#hawbForm').attr("action", "hawbCreate.fin?param=${hawbBean.expImp}");
                        $("#hawbForm").submit();
                    });
                    $("#searchBtn").on('click', function () {
                            $('#hawbForm').attr("action", "hawb.fin?param=${hawbBean.expImp}");
                            $("#hawbForm").submit();
                        });
                    $("#btndelete").on('click', function () {
                        $('#hawbForm').attr("action", "hawbDelete.fin?param=${hawbBean.expImp}");
                        $("#hawbForm").submit();
                    });
                });

                $(document).keypress(function(event){
                    var keycode = (event.keyCode ? event.keyCode : event.which);
                    if(keycode == '13'){
                        $('#hawbForm').attr("action", "hawb.fin?param=${hawbBean.expImp}");
                        $("#hawbForm").submit();
                    }
                });
            </script>
        </head>
        <body>
            <header>
                HAWB Search - ${hawbBean.expImp}
            </header>
            <div id="accordion">
            <h3>SEARCH</h3>
            <div class="rows">
                <table class="table" id="tbl" height="50">
                    <tr>
                        <td>
                            HAWB No
                        </td>
                        <td>
                            <html:input path="blNumber"/>
                        </td>
                        <td>
                            Job No
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
                            Shipper
                        </td>
                        <td>
                            <html:input path="shipperName"/>
                        </td>
                        <td>
                            Consignee
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
                            SI Number
                        </td>
                        <td>
                            <html:input path="soNumber"/>
                        </td>
                        <td>
                            A.Shipper
                        </td>
                        <td>
                            <html:input path="actShipperName"/>
                        </td>
                        <td>
                            A.Consignee
                        </td>
                        <td>
                            <html:input path="actConsigneeName"/>
                        </td>
                        <td>
                            M.BL.Number
                        </td>
                        <td>
                            <html:input path="mBlNumber"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Loading Agent
                        </td>
                        <td>
                            <html:input path="loadingAgentName"/>
                        </td>
                        <td>
                            Destination Agent
                        </td>
                        <td>
                            <html:input path="destinationAgentName"/>
                        </td>
                        <td>
                            S.Bill No.
                        </td>
                        <td>
                            <html:input path="cb.shippingBillNumber"/>
                        </td>
                        <td>
                            Invoice No
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
                        <th>HAWB NO</th>
                        <th>JOB NO</th>
                        <th width="15%">SHPR</th>
                        <th width="15%">CNEE</th>
                        <th width="10px">FLIGHT NO</th>
                        <th>FLIGHT DATE</th>
                        <th>POL</th>
                        <th>POD</th>
                        <th>CR DATE</th>
                    </tr>
                </thead>

            </table>
        </html:form>
    </body>
</html>
