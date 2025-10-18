
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" command="siBean" method="post" id= "soForm" action="sailingScheduleSave.fin">
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
                $(document).ready(function () {
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
                    var table = $('#example').DataTable({
                        ajax: "siGson.fin?param=${siBean.expImp}",
                        "serverSide": true,
                        "bAutoWidth": false,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [20, 25, 50],
                        "dom": 'i<"top">ptr',
                        "aoColumns": [
                            {
                                "mData": "bkgRefNo",
                                "render": function (mData, full, row) {
                                    return '<input type="checkbox" name="bkgRefNos" value="' + mData + '"/>';
                                }
                            },
                            {
                                "mData": "loadingAgentCode",
                                "render": function (mData, full, row) {
                                    var bkgRefNo = row['bkgRefNo'];
                                    return '<a href="siPDF.fin?bookingRefNumber=' + bkgRefNo + '" target="_blank"><img src="finactImages/icon_pdf.png" class="logopdf"></a>';
                                }
                            },
                            {
                                "mData": "bkgRefNo",
                                "render": function (mData, full, row) {
                                    return '<a href="siRetrieve.fin?param=${siBean.expImp}&bookingRefNumber=' + mData + '">'+mData+'</a>';
                                }
                            },
                            {"mData": "bookingRefDate"},
                            {
                                "mData": "soNumber",
                                "render": function (mData, full, row) {
                                    var pod = row['pod'];
                                    var la = row['loadingAgentCode'];
                                    return la + '/' + pod + '/' + mData;
                                }
                            },
                            {"mData": "shipperName"},
                            {"mData": "consigneeName"},
                            {"mData": "vsl"},
                            {"mData": "voy"},
                            {
                                "mData": "loadingAgentCode",
                                "render": function (mData, full, row) {
                                    return '<img src="finactImages/pending2.gif" class="logopdf">';
                                }
                            },
                            {"mData": "pol"},
                            {"mData": "etd"},
                            {"mData": "pod"},
                            {"mData": "eta"},
                            {"mData": "jobNumber"},
                            {
                                "mData": "blNumber",
                                "render": function (mData, full, row) {
                                    if(mData !== null)
                                        return '<a target="new" href="hawbRetrieve.fin?param=${siBean.expImp}&blNumber=' + mData + '">'+mData+'</a>';
                                    else
                                        return '';
                                }
                            },
                            {"mData": "salesBy"}

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
                        $('#soForm').attr("action", "siCreate.fin?param=${siBean.expImp}");
                        $("#soForm").submit();
                    });
                });
                $(function () {
                    $("#searchBtn").on('click', function () {
                        $('#soForm').attr("action", "si.fin?param=${soBean.expImp}");
                        $("#soForm").submit();
                    });

                    $("#bookingRefDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });
                    $("#bookingRefDateTo").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });
                });

                $(document).keypress(function(event){
                    var keycode = (event.keyCode ? event.keyCode : event.which);
                    if(keycode == '13'){
                        $('#soForm').attr("action", "si.fin?param=${soBean.expImp}");
                        $("#soForm").submit();
                    }
                });

            </script>
        </head>
        <body>
            <header>
                Shipping Instruction Search - ${siBean.expImp}
            </header>
            <div id="accordion">
            <h3>SEARCH</h3>
            <div class="rows">
                <table class="table" id="tbl" height="50">
                    <tr>
                        <td>
                            <label>Bkg Ref No</label>
                        </td>
                        <td>
                            <html:input path="bkgRefNo"/>
                        </td>
                        <td>
                            SO NUMBER
                        </td>
                        <td>
                            <html:input path="soNumber"/>
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
                            Airport of Dep.
                        </td>
                        <td>
                            <html:input path="pol"/>
                        </td>
                        <td>
                            Airport of Dest.
                        </td>
                        <td>
                            <html:input path="pod"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            A. Shipper
                        </td>
                        <td>
                            <html:input path="actShipperName"/>
                        </td>
                        <td>
                            A. Consignee
                        </td>
                        <td>
                            <html:input path="actConsigneeName"/>
                        </td>
                        <td>
                            BL No
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

                    </tr>
                    <tr>
                        <td>
                            C. INVOICE NO
                        </td>
                        <td>
                            <html:input path="comInvNumber"/>
                        </td>
                        <td>
                            S. BILL NO
                        </td>
                        <td>
                            <html:input path="cb.shippingBillNumber"/>
                        </td>
                        <td>
                            B. DATE
                        </td>
                        <td>
                            <html:input path="bookingRefDate"/>
                        </td>
                        <td>
                            <html:input path="bookingRefDateTo"/>
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
                        <th>B/R NO</th>
                        <th width="6%">BKG DATE</th>
                        <th>S/I NO</th>
                        <th width="15%">SHPR</th>
                        <th width="15%">CNEE</th>
                        <th>VSL</th>
                        <th>VOY</th>
                        <th>C</th>
                        <th>Airport of Dep.</th>
                        <th>ETD</th>
                        <th>Airport of Dest.</th>
                        <th>ETA</th>
                        <th>JOB NO</th>
                        <th>B/L NO</th>
                        <th>SALESMAN</th>
                    </tr>
                </thead>

            </table>
        </html:form>
    </body>
</html>
