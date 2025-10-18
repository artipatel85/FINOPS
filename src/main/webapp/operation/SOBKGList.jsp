
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<script src="finactjs/so.js"></script>
<script src="finactjs/jquery.validate.min.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <html:form autocomplete="off" name="soForm" method="post" id="shippingOrderForm" action="soSave.fin">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Shipping Order - Export</title>
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
                    margin-bottom: 5px;
                    font-family: cursive;
                    padding: 2px 2px 2px 2px;
                    border-top: 1px solid #5c5a5a;
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
                .hasDatepicker {
                                  z-index: 1500 !important; /* has to be larger than 1050 */
                                }
                                .ui-datepicker {
                                    z-index: 9999 !important;
                                }
                                .datepicker{ z-index:99999 !important; }

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

                var aoColumnsLCL = [
                    {
                        "mData": "number",
                        "render": function (mData, full, row) {
                            var lineNo = row['lineNumber'];
                            var bkgRefNo = row['bookingRefNumber'];
                            var lineSealNumber = row['lineSealNumber'];
                            var size = row['size'];
                            var href = "javascript:callDelete("+lineNo+","+bkgRefNo+")";
                            return '<input type="button" value="D" onclick="'+href+'"/>';
                        }
                    },
                    {
                        "mData": "number",
                        "render": function (mData, full, row) {
                            var lineNo = row['lineNumber'];
                            var size = row['size'];
                            var lineSealNumber = row['lineSealNumber'];
                            var href = "javascript:openContainerWindow("+lineNo+")";
                            return '<a href="'+href+'">' + mData + '/'+size+'/'+lineSealNumber+'</a>';
                        }
                    },
                    {"mData": "customSealNumber"},
                    {"mData": "actualQty"},
                    {"mData": "actualUnit"},
                    {"mData": "actualWeight"},
                    {"mData": "actualNetWeight"},
                    {"mData": "actualMeasurement"},
                    {"mData": "qty"},
                    {"mData": "unit"},
                    {"mData": "weight"},
                    {"mData": "netWeight"},
                    {"mData": "measurement"}

                ];

                var aoColumnsFCL = [
                    {
                        "mData": "number",
                        "render": function (mData, full, row) {
                            var lineNo = row['lineNumber'];
                            var bkgRefNo = row['bookingRefNumber'];
                            var size = row['size'];
                            var href = "javascript:callDelete("+lineNo+","+bkgRefNo+")";
                            if($("#blNumber").val() == ''){
                                return '<a href="'+href+'"><IMG height=25 alt="Delete" src="images/delete.gif" width=28 border=0></A></a>';
                            }
                            else{
                                return '';
                            }
                        }
                    },
                    {
                        "mData": "number",
                        "render": function (mData, full, row) {
                            var lineNo = row['lineNumber'];
                            var size = row['size'];
                            var lineSealNumber = row['lineSealNumber'];
                            var href = "javascript:openContainerWindow("+lineNo+")";
                            return '<a href="'+href+'">' + mData + '/'+size+'/'+lineSealNumber+'</a>';
                        }
                    },
                    {"mData": "customSealNumber"},
                    {"mData": "qty"},
                    {"mData": "unit"},
                    {"mData": "netWeight"},
                    {"mData": "measurement"}

                ];

                $(document).ready(function () {
                    var aoColumns = ('${soBean.bookingType}' == 'F') ? aoColumnsFCL : aoColumnsLCL;
                    var table = $('#example').DataTable({
                        ajax: "soContainerGson.fin?bkgRefNo=${soBean.bkgRefNo}",
                        "serverSide": true,
                        "bJQueryUI": true,
                        "bRetrieve": true,
                        "bProcessing": true,
                        "bFilter": false,
                        "dom": '<"top">t',
                        "deferLoading": 0,
                        "lengthMenu": [20, 30, 50],
                        "aoColumns": aoColumns
                    });
                    drawContainerTable();
                });

                $(document).ready(actShipper);
                $(document).ready(actConsignee);
                $(document).ready(shipper);
                $(document).ready(carrierCode);
                $(document).ready(consignee);
                $(document).ready(soNotify);
                $(document).ready(alsoNotify);
                $(document).ready(por);
                $(document).ready(pol);
                $(document).ready(polName);
                $(document).ready(pod);
                $(document).ready(from);
                $(document).ready(dest);
                $(document).ready(upTo);
                $(document).ready(deliveryAt);
                $(document).ready(loadingAgent);
                $(document).ready(actConsigneeName);
                $(document).ready(shipperName);
                $(document).ready(carrierName);
                $(document).ready(consigneeName);
                $(document).ready(soNotifyName);
                $(document).ready(destinationAgent);
                $(document).ready(originWareHouse);
                $(document).ready(actShipperName);
                $(document).ready(alsoNotifyName);
                $(document).ready(destinationAgentName);
                $(document).ready(loadingAgentName);
                $(document).ready(originWareHouseName);


                $(function () {
                    $("#bookingRefDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#deliveryDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#cinvdate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#etd").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#eta").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#cfsDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#cyDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#shippingBillDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });
                    
                    $("#cargoReceivedDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#actualStuffingDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#vslBtn").on("click", function () {
                        dialogWindow("#dialog-confirm",
                                "SOSSPopup.fin?param=CONNECTBYSS&POL=" + $("#pol").val() + "&POD=" + $("#pod").val(),
                                1000, "['middle', 20]", "auto");
                    });

                    $("#addContainerBtn").on("click", function () {
                        window.open("retrieveContainer.fin?bkgRefNo="+ $("#bkgRefNo").val() +"&lineNo=0&type="+$("#bookingType").val()+"&soNumber="+$("#soNumber").val()+"&param="+$("#expImp").val(),
                                             "SO Container", "width=1100,height=700,top=400,left=500");
                    });

                    $("#polCheck").on("click", function () {
                        $("#from").val($("#pol").val());
                        $("#fromName").val($("#polName").val());
                    });

                    $("#destCheck").on("click", function () {
                        //alert(1);
                        $("#upTo").val($("#dest").val());
                        $("#upToName").val($("#destName").val());
                    });

                    $("#attachmentsBtn").on("click", function () {
                        dialogWindow("#dialog-container-attachments",
                                "soFilePopup.fin?bkgRefNo="+ $("#bkgRefNo").val() +"&pod="+ $("#pod").val() +"&soNum="+$("#soNumber").val(),
                                1200, "['middle', 200]", "auto");
                    });

                    $("#freightType").on("change", function () {
                        if($("#freightType").val() == 'P'){
                            $("#freightPayableAt").val("ORIGIN");
                        }
                        else{
                            $("#freightPayableAt").val("DESTINATION");
                        }
                    });

                    $('input[type=radio][name=porRadio]').on('change', function() {
                      switch ($(this).val()) {
                        case 'por':
                          $("#from").val($("#por").val());
                          $("#fromName").val($("#porName").val());
                          break;
                        case 'pol':
                          $("#from").val($("#pol").val());
                          $("#fromName").val($("#polName").val());
                          break;
                      }
                    });

                    $('input[type=radio][name=podRadio]').on('change', function() {
                      switch ($(this).val()) {
                        case 'pod':
                          $("#upTo").val($("#pod").val());
                          $("#upToName").val($("#podName").val());
                          break;
                        case 'dest':
                          $("#upTo").val($("#dest").val());
                          $("#upToName").val($("#destName").val());
                          break;
                      }
                    });
                });
                function openContainerWindow(lineNo) {

                     window.open("retrieveContainer.fin?bkgRefNo="+ $("#bkgRefNo").val() +"&lineNo="+lineNo+"&type="+$("#bookingType").val()+"&soNumber="+$("#soNumber").val()+"&param="+$("#expImp").val(),
                                                                             "SO Container", "width=1100,height=700,top=400,left=500");
                }
                function callDelete(lineNo, bkgRefNo) {
                    if (confirm("DO U Wish To Delete Container?"));
                    {
                        $('#shippingOrderForm').attr("action", "soDelete.fin?bookingRefNumber="+bkgRefNo+"&lineNo="+lineNo+"&param=${soBean.expImp}");
                        $("#shippingOrderForm").submit();
                    }
                }



                function populateSS(key, value1, value2, value3, value4, value5, value6, value7, value8, value9) {
                    $("#vsl").val(value1);
                    $("#voy").val(value2);
                    $("#carrierCode").val(value3);
                    $("#cfsDate").val(value4 === 'null' ? '' : value4);
                    $("#cyDate").val(value5 === 'null' ? '' : value5);
                    $("#etd").val(value6);
                    $("#eta").val(value7);
                    $(".ui-dialog-titlebar-close").click();
                }



                function exitContainer() {
                    location.reload();
                }

                function getContainer(lineNo) {
                    var table = $('#example').DataTable();

                        $.ajax({
                            url: "retrieveSOContainer.fin?bkgRefNo="+$("#bkgRefNo").val()+"&lineNo="+lineNo,
                            //data: $('form[name=soForm]').serialize(),
                            success: function (data) {
                                $("#number").val(data.aaData.number);
                                $("#size").val(data.aaData.size);
                                $("#measurement").val(data.aaData.measurement);
                                $("#markNumber").val(data.aaData.markNumber);
                            }
                        });
                    
                }
                
                function drawContainerTable(event) {
                    if (${soBean.soNumber != ''}) {
                        $('#example').DataTable().draw();
                    }
                }

                function doPrint()
                {
                    if (confirm("DO U Wish To Print The Record??"))
                    {
                        window.open('soPDF.fin?bookingRefNumber=${soBean.bkgRefNo}');
                    }
                }

                function saveCopy()
                {
                    if (confirm("DO U Wish To Copy SO??"));
                    {
                        $('#shippingOrderForm').attr("action", "soCopy.fin");
                        $("#shippingOrderForm").submit();
                    }
                }
                $(document).ready(function () {
                    $("#shippingOrderForm").validate({
                        rules: {
                            bookingRefDate:{required: true},
                            bookingType: {required: true},
                            deliveryDate: {required: true},
                            deliveryAt: {required: true},
                            actShipper: {required: true},
                            actConsignee: {required: true},
                            shipper: {required: true},
                            consignee: {required: true},
                            por: {required: true},
                            pol: {required: true},
                            pod: {required: true},
                            dest: {required: true},
                            fromFreightCode: {required: true},
                            upToFreightCode: {required: true}
                        },
                        messages: {
                            bookingRefDate: '<label class="tool">* field is required.</label>',
                            bookingType: '<label class="tool">* field is required.</label>',
                            deliveryDate: '<label class="tool">* field is required.</label>',
                            deliveryAt: '<label class="tool">* field is required.</label>',
                            actShipper: '<label class="tool">* field is required.</label>',
                            actConsignee: '<label class="tool">* field is required.</label>',
                            shipper: '<label class="tool">* field is required.</label>',
                            consignee: '<label class="tool">* field is required.</label>',
                            por: '<label class="tool">* field is required.</label>',
                            pol: '<label class="tool">* field is required.</label>',
                            pod: '<label class="tool">* field is required.</label>',
                            dest: '<label class="tool">* field is required.</label>',
                            fromFreightCode: '<label class="tool">* field is required.</label>',
                            upToFreightCode: '<label class="tool">* field is required.</label>'
                        }
                    });
                });

            </script>
        </head>
        <body>
            <div id="accordion">

                <h3>
                    Shipping Order - ${soBean.expImp}
                </h3>
                <table class="table-noborder" width="100%">

                    <tr>
                        <td><label>BOOKING REF</label></td>
                        <td>
                            <label><b><c:out value="${soBean.bkgRefNo}"/></label>
                        </td>
                        <td><label>BKG TYPE</label></td>
                        <td>
                            <label><b><c:out value="${soBean.bkgType}"/></label>
                        </td>
                        <td><label>S/O NO</label></td>
                        <td>
                            <label><b><c:out value="${soBean.soNumber}"/></label>
                        </td>
                    </tr>
                </table>
            </div>

            <c:if test="${soBean.soNumber != '' && soBean.bookingType == 'L'}">
                <table id="example" class="display" cellspacing="0" width="100%">
                    <thead>
                        <tr>
                            <th></th>
                            <th>CONTAINER NO/SIZE/L.SEAL</th>
                            <th>C.SEAL</th>
                            <th>A.QTY</th>
                            <th>A.UNIT</th>
                            <th>A.GR.WT</th>
                            <th>A.NT.WT</th>
                            <th>A.CBM</th>
                            <th>B.QTY</th>
                            <th>B.UNIT</th>
                            <th>B.GR.WT</th>
                            <th>B.NT.WT</th>
                            <th>B.CBM</th>
                        </tr>
                    </thead>                
                </table>

                </div>
            </c:if>
            <c:if test="${soBean.soNumber != '' && soBean.bookingType == 'F'}">
                <table id="example" class="display" cellspacing="0" width="100%">
                    <thead>
                        <tr>
                            <th></th>
                            <th>CONTAINER NO/SIZE/L.SEAL</th>
                            <th>C.SEAL</th>
                            <th>QTY</th>
                            <th>UNIT</th>
                            <th>KGS</th>
                            <th>CBM</th>
                        </tr>
                    </thead>                
                </table>

            </c:if>            
        </html:form> 

    </body>
</html>
