
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" command="soBean" method="post" id= "soForm" action="sailingScheduleSave.fin">
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
                        ajax: "soGson.fin?param=${soBean.expImp}",
                        "serverSide": true,
                        "bRetrieve": true,
                        "bJQueryUI": true,
                        "bProcessing": true,
                        "lengthMenu": [20, 25, 50],
                        "dom": 'i<"top">ptr',
                        "aoColumns": [
                            {
                                "mData": "bkgRefNo",
                                "render": function (mData, full, row) {
                                    var blNumber = row['blNumber'];
                                    if(blNumber == ''){
                                        return '';
                                    }
                                    else{
                                        return '<input type="checkbox" name="uuids" value="' + mData + '"/>';
                                    }
                                }
                            },
                            {
                                "mData": "loadingAgentCode",
                                "render": function (mData, full, row) {
                                    var bkgRefNo = row['bkgRefNo'];
                                    return '<a href="soPDF.fin?bookingRefNumber=' + bkgRefNo + '" target="_blank"><img src="finactImages/icon_pdf.png" class="logopdf"></a>';
                                }
                            },
                            {
                                "mData": "bkgRefNo",
                                "render": function (mData, full, row) {
                                    return '<a href="soRetrieve.fin?param=${soBean.expImp}&bookingRefNumber=' + mData + '">'+mData+'</a>';
                                }
                            },
                            {
                                "mData": "bkgType",
                                "render": function (mData, full, row) {
                                    var bkgRefNo = row['bkgRefNo'];
                                    var soNumber = row['soNumber'];
                                    var mDataWQ = "'"+mData+"'";
                                    var pod = row['pod'];
                                    var la = row['loadingAgentCode'];
                                    var soNo = "'"+la + '/' + pod + '/' + soNumber+"'";
                                    if(mData === 'F'){
                                        return '<a href="javascript:openLF('+bkgRefNo+','+mDataWQ+','+soNo+')"><img src="finactImages/fcl.gif" class="logopdf"></a>';
                                    }
                                    else{
                                        return '<a href="javascript:openLF('+bkgRefNo+','+mDataWQ+','+soNo+')"><img src="finactImages/lcl.gif" class="logopdf"></a>';
                                    }
                                }
                            },
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
                                        return '<a target="new" href="blRetrieve.fin?param=${soBean.expImp}&blNumber=' + mData + '">'+mData+'</a>';
                                    else
                                        return '';
                                }
                            },
                            {"mData": "salesBy"}

                        ]
                    });
                    $("div.top").append('&nbsp;&nbsp;&nbsp;&nbsp;' +
                            '&nbsp;&nbsp;<input type="button" id="btnnew" value="New" class="finbutton"/>&nbsp;&nbsp;<rbac:rbac pattern="(.)\\|(Y)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)" formId="1045"><input type="button" id="deleteBtn" value="Delete" class="finbutton"/></rbac:rbac>&nbsp;');

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
                        $('#soForm').attr("action", "soCreate.fin?param=${soBean.expImp}");
                        $("#soForm").submit();
                    });

                });

                $(function () {
                    $("#searchBtn").on('click', function () {
                        $('#soForm').attr("action", "so.fin?param=${soBean.expImp}");
                        $("#soForm").submit();
                    });
                    $("#deleteBtn").on('click', function () {
                        $('#soForm').attr("action", "deleteSO.fin?param=${soBean.expImp}");
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
                        $('#soForm').attr("action", "so.fin?param=${soBean.expImp}");
                        $("#soForm").submit();
                    }
                });

                function openLF(bkgRefNo, bkgType, soNo){
                    window.open("soFLList.fin?param=${soBean.expImp}&bookingRefNumber="+bkgRefNo+"&soNo="+soNo+"&bkgType="+bkgType, "_blank", "toolbar=no,scrollbars=yes,resizable=yes,top=300,left=500,width=800,height=200");
                }
            </script>
        </head>
        <body>
            <header>
                Shipping Order Search - ${soBean.expImp}
            </header>

            <div id="accordion">
            <h3>SEARCH</h3>
            <div class="rows">
                <table class="table" id="tbl" height="50">
                    <tr>
                        <td>
                            <label style="TEXT-TRANSFORM: uppercase;">Bkg Ref No</label>
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
                            A. SHIPPER
                        </td>
                        <td>
                            <html:input path="actShipperName"/>
                        </td>
                        <td>
                            A. CONSIGNEE
                        </td>
                        <td>
                            <html:input path="actConsigneeName"/>
                        </td>
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
                            CARRIER
                        </td>
                        <td>
                            <html:input path="carrierName"/>
                        </td>
                        <td>
                            B. DATE
                        </td>
                        <td>
                            <html:input path="bookingRefDate"/><html:input path="bookingRefDateTo"/>
                        </td>

                    </tr>
                    <tr>
                        <td>
                            BOOKING TYPE
                        </td>
                        <td>
                            <html:select path="bookingType" cssClass="smallplus">
                                <html:option value=""/>
                                <html:option value="L">LCL</html:option>
                                <html:option value="F">FCL</html:option>
                            </html:select>
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
                        <th width="5"></th>
                        <th width="5">PRN</th>
                        <th width="10">B/R NO</th>
                        <th width="5">TYPE</th>
                        <th>S/O NO</th>
                        <th>SHPR</th>
                        <th>CNEE</th>
                        <th>VSL</th>
                        <th>VOY</th>
                        <th>C</th>
                        <th>POL</th>
                        <th>ETD</th>
                        <th>POD</th>
                        <th>ETA</th>
                        <th>JOB NO</th>
                        <th>B/L NO</th>
                        <th>SALESMAN</th>
                    </tr>
                </thead>
                <thead>

                </thead>
            </table>

        </html:form>
    </body>
</html>
