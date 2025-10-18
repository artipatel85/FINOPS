
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<script src="finactjs/so.js"></script>
<script src="finactjs/jquery.validate.min.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <html:form autocomplete="off" name="soForm" method="post" id="shippingOrderForm" action="siSave.fin">
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
                        "mData": "qty",
                        "render": function (mData, full, row) {
                            var bkgRefNo = row['bookingRefNumber'];
                            var href = "javascript:openContainerWindow("+bkgRefNo+")";
                            return '<a href="'+href+'">' + mData + '</a>';
                        }
                    },
                    {
                        "mData": "unit",
                        "render": function (mData, full, row) {
                            var bkgRefNo = row['bookingRefNumber'];
                            var href = "javascript:openContainerWindow("+bkgRefNo+")";
                            return '<a href="'+href+'">' + mData + '</a>';
                        }
                    },
                    {
                        "mData": "weight",
                        "render": function (mData, full, row) {
                            var bkgRefNo = row['bookingRefNumber'];
                            var href = "javascript:openContainerWindow("+bkgRefNo+")";
                            return '<a href="'+href+'">' + mData + '</a>';
                        }
                    },
                    {"mData": "measurement"},
                    {"mData": "actualQty"},
                    {"mData": "actualUnit"},
                    {"mData": "actualWeight"},
                    {"mData": "actualMeasurement"}
                ];
                $(document).ready(function () {
                    var aoColumns = aoColumnsLCL;
                    var table = $('#example').DataTable({
                        ajax: "siContainerGson.fin?bkgRefNo=${siBean.bkgRefNo}",
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

                    $("#vslBtn").on("click", function () {
                        dialogWindow("#dialog-confirm",
                                "SOSSPopup.fin?param=CONNECTBYSS&POL=" + $("#pol").val() + "&POD=" + $("#pod").val(),
                                1000, "['middle', 20]", "auto");
                    });

                    $("#addContainerBtn").on("click", function () {
                        dialogWindow("#dialog-container-so",
                                "retrieveSIContainer.fin?bkgRefNo="+ $("#bkgRefNo").val()+"&soNumber="+$("#soNumber").val(),
                                1200, "['middle', 200]", "auto");
                    });

                    $("#attachmentsBtn").on("click", function () {
                        dialogWindow("#dialog-container-attachments",
                                "soFilePopup.fin?bkgRefNo="+ $("#bkgRefNo").val() +"&pod="+ $("#pod").val() +"&soNum="+$("#soNumber").val(),
                                1200, "['middle', 200]", "auto");
                    });


                });
                function openContainerWindow(lineNo) {
                    dialogWindow("#dialog-container-so",
                            "retrieveSIContainer.fin?bkgRefNo="+lineNo+"&soNumber="+$("#soNumber").val(),
                            1200, "['middle', 200]", "auto");
                }

                function togglePayableAt(afValue) {
                    if(afValue == 'P'){
                        $("#freightPayableAt").val("ORIGIN");
                    }
                    else{
                        $("#freightPayableAt").val("DESTINATION");
                    }
                }

                function saveContainer() {
                    var rowCount = 1;
                    if($("#siContainerForm").valid()){
                        $.ajax({
                            method:"post",
                            url: "saveSIContainer.fin",
                            data: $('form[name=siContainerForm]').serialize(),
                            success: function (data) {
                                var s = location.href;
                                s = s.replace('siSave.fin','siRetrieve.fin?bookingRefNumber='+$("#bkgRefNo").val());
                                window.location.assign(s);
                            }
                        });
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
                
                function drawContainerTable(event) {
                    if (${soBean.soNumber != ''}) {
                        $('#example').DataTable().draw();
                    }
                }

                function doPrint()
                {
                    if (confirm("DO U Wish To Print The Record??"))
                    {
                        window.open('siPDF.fin?bookingRefNumber=${siBean.bkgRefNo}');
                    }
                }

                function saveCopy()
                {
                    if (confirm("DO U Wish To Copy SO??"));
                    {
                        $('#shippingOrderForm').attr("action", "siCopy.fin");
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
                    Shipping Instruction - ${siBean.expImp}
                </h3>
                <table class="table-noborder" width="100%">
                    <c:if test="${siBean.errMsg != ''}">
                        <tr>
                            <td colspan="6" class="finErrMsg"><c:out value="${siBean.errMsg}"/></td>
                        </tr>
                    </c:if>
                    <tr>
                        <td width="8%"><label>BOOKING REF</label></td>
                        <td width="29%">
                            <label><c:out value="${siBean.bkgRefNo}"/></label>
                            <html:hidden path="bkgRefNo" />
                            <html:hidden path="expImp"/>
                        </td>
                        <td width="10%"><label class="required">BKG DATE</label></td>
                        <td width="29%"><html:input path="bookingRefDate" class="smallplus"/></td>
                        <td width="10%"><label>BL NO</label></td>
                        <td>
                            <html:input path="blNumber" class="medium" readonly="true"/>
                        </td>
                    </tr>

                    <tr>
                        <td><label>S/O NO</label></td>
                        <td>
                            <label><c:out value="${siBean.loadingAgentCode}/${siBean.pod}/${siBean.soNumber}"/></label>
                            <html:hidden path="soNumber" />
                        </td>
                        <td><label class="required">DELIVERY DATE</label></td>
                        <td>
                            <html:input path="deliveryDate" class="smallplus"/>
                            <label class="required"> AT </label>
                            <html:input path="deliveryAt" class="smallplus" id="deliveryAt"/>
                        </td>
                        <td><label>JOB NO</label></td>
                        <td>
                            <html:input path="jobNumber" class="medium" readonly="true"/>
                        </td>
                    </tr>

                    <tr>
                        <td><label>CARGO TYPE</label></td>
                        <td>
                            <html:select path="cargoType" class="medium">
                                <html:option value="G">General cargo</html:option>
                                <html:option value="D">Dangerous goods</html:option>
                                <html:option value="H">Garment on hangers</html:option>
                            </html:select>
                        </td>
                        <td></td>
                        <td>

                        </td>
                        <td><label>ORIGIN WAREHOUSE</label></td>
                        <td>
                            <html:input path="originWareHouseName" class="large" id="originWareHouseName"/>
                            <html:input path="originWareHouse" class="small" id="originWareHouse"/>
                        </td>
                    </tr>

                    <tr>
                        <td><label class="required">A.SHPR</label></td>
                        <td>
                            <html:input path="actShipperName" class="large" id="actShipperName"/>
                            <html:input path="actShipper" class="small" id="actShipper"/>
                        </td>
                        <td><label class="required">A.CNEE</label></td>
                        <td>
                            <html:input path="actConsigneeName" class="large" id="actConsigneeName"/>
                            <html:input path="actConsignee" class="small" id="actConsignee"/>
                        </td>
                        <td></td>
                        <td>
                            <html:textarea path="originWareHouseContactDetails" id="originWareHouseContactDetails" rows="4" class="textareaL"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="required">SHPR</label></td>
                        <td>
                            <html:input path="shipperName" class="large" id="shipperName"/>
                            <html:input path="shipper" class="small" id="shipper"/>
                            <html:textarea path="shipperContactDetails" rows="4" class="textareaL" id="shipperContactDetails"/>
                        </td>
                        <td><label class="required">CNEE</label></td>
                        <td>
                            <html:input path="consigneeName" class="large"/>
                            <html:input path="consignee" class="small"/>
                            <html:textarea path="consigneeContactDetails" rows="4" class="textareaL"/>
                        </td>
                        <td><label class="required">LOADING AGENT</label></td>
                        <td>
                            <html:input path="loadingAgentName" class="large" id="loadingAgentName"/>
                            <html:input path="loadingAgentCode" class="small" id="loadingAgentCode"/>
                            <html:textarea path="loadingAgentContactDetails" rows="4" class="textareaL" id="loadingAgentContactDetails"/>
                        </td>
                    </tr>

                    <tr>
                        <td><label>NOTIFY</label></td>
                        <td>
                            <html:input path="soNotifyName" class="large"/>
                            <html:input path="soNotify" class="small"/>
                            <html:textarea path="soNotifyContactDetails" rows="4" class="textareaL"/>
                        </td>

                        <td><label>DEST. AGENT</label></td>
                        <td>
                            <html:input path="destinationAgentName" class="large" id="destinationAgentName"/>
                            <html:input path="destinationAgentCode" class="small" id="destinationAgentCode"/>
                            <html:textarea path="destinationAgentContactDetails" rows="4" class="textareaL" id="destinationAgentContactDetails"/>
                        </td>
                        <td><label>Currency</label></td>
                        <td>
                            <html:select path="currencyId">
                                <html:option value="">Select</html:option>
                                <html:option value="1001">AUD</html:option>
                                <html:option value="1002">EUR</html:option>
                                <html:option value="1003">FRF</html:option>
                                <html:option value="1004">GBP</html:option>
                                <html:option value="1005">HKD</html:option>
                                <html:option value="1006" selected="selected">INR</html:option>
                                <html:option value="1007">USD</html:option>
                                <html:option value="1008">YEN</html:option>
                            </html:select>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="required">AIRPORT OF DEPARTURE</label></td>
                        <td>
                            <html:input path="polName" class="large" id="polName"/>
                            <html:input path="pol" class="small" id="pol"/>
                        </td>
                        <td><label class="required">AIRPORT OF DESTINATION</label></td>
                        <td>
                            <html:input path="podName" class="large" id="podName"/>
                            <html:input path="pod" class="small" id="pod"/>
                        </td>
                        <td><label>C.INV NO</label></td>
                        <td>
                            <html:input path="comInvNumber" class="medium"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="required">DEST</label></td>
                        <td>
                            <html:input path="destName" class="large" id="destName"/>
                            <html:input path="dest" class="small" id="dest"/>
                        </td>
                        <td><label>TYPE</label></td>
                        <td>
                            <html:select path="flightType" class="medium">
                                <html:option value="CF">Cargo Flight</html:option>
                                <html:option value="PF">Passenger Flight</html:option>
                            </html:select>
                        </td>
                        <td><label>C.INV Date</label></td>
                        <td>
                            <html:input path="comInvDate" id="cinvdate" class="smallplus"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" valign="top">
                            <table width="80%">
                                <tr>
                                    <th width="50%">PAYMENT TERM</th>
                                    <th>PREPAID</th>
                                    <th>COLLECT</th>
                                </tr>
                                <tr>
                                    <td><label>AIRFREIGHT</label></td>
                                    <td><html:radiobutton onclick="togglePayableAt('P')" path="airFreight" value="P"/></td>
                                    <td><html:radiobutton onclick="togglePayableAt('C')" path="airFreight" value="C"/></td>
                                </tr>
                                <tr>
                                    <td><label>TERMINAL/TERMINAL HANDLING</label></td>
                                    <td><html:radiobutton path="terminalHandling" value="P"/></td>
                                    <td><html:radiobutton path="terminalHandling" value="C"/></td>
                                </tr>
                                <tr>
                                    <td><label>CARTAGE</label></td>
                                    <td><html:radiobutton path="cartage" value="P"/></td>
                                    <td><html:radiobutton path="cartage" value="C"/></td>
                                </tr>
                                <tr>
                                    <td><label>HANDLING/DOCUMENTATION</label></td>
                                    <td><html:radiobutton path="handlingDoc" value="P"/></td>
                                    <td><html:radiobutton path="handlingDoc" value="C"/></td>
                                </tr>
                                <tr>
                                    <td><label>PACKING</label></td>
                                    <td><html:radiobutton path="packing" value="P"/></td>
                                    <td><html:radiobutton path="packing" value="C"/></td>
                                </tr>
                                <tr>
                                    <td><label>OTHERS</label></td>
                                    <td><html:radiobutton path="otherPaymentTerms" value="P"/></td>
                                    <td><html:radiobutton path="otherPaymentTerms" value="C"/></td>
                                </tr>
                            </table>
                        </td>
                        <td colspan="4">
                            <table width="50%">
                                <tr>
                                    <th colspan="2">DOCUMENT ACCOMPANYING AIR WAYBILL</th>
                                </tr>
                                <tr>
                                    <td><label>COMMERCIAL INVOICE</label></td>
                                    <td><html:input path="commercialInvoice" class="small"/></td>
                                </tr>
                                <tr>
                                    <td><label>PACKING LIST</label></td>
                                    <td><html:input path="packingList" class="small"/></td>
                                </tr>
                                <tr>
                                    <td><label>EXPORT LICENSE</label></td>
                                    <td><html:input path="exportLicense" class="small"/></td>
                                </tr>
                                <tr>
                                    <td><label>CERTIFICATE OF ORIGIN</label></td>
                                    <td><html:input path="certOfOrigin" class="small"/></td>
                                </tr>
                                <tr>
                                    <td><label>FORM A</label></td>
                                    <td><html:input path="formA" class="small"/></td>
                                </tr>
                                <tr>
                                    <td><label>OTHERS</label></td>
                                    <td><html:input path="others" class="small"/></td>
                                </tr>
                            </table>
                        </td>
                    </tr>

                    <tr>
                        <td><label>PAYABLE AT</label></td>
                        <td>
                            <html:input path="freightPayableAt" class="medium" readonly="true" id="freightPayableAt"/>
                        </td>
                        <td><label>RECEIVED DATE</label></td>
                        <td>
                            <html:input path="cargoReceivedDate" class="medium"/>
                        </td>
                        <td><label>CARRIER</label></td>
                        <td>
                            <html:input path="carrierName" class="large"/>
                            <html:input path="carrierCode" class="small"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>VALUE FOR CUSTOM</label></td>
                        <td>
                            <html:input path="valueForCustom" class="medium"/>
                        </td>
                        <td><label>VALUE FOR CARRIAGE</label></td>
                        <td>
                            <html:input path="valueForCarriage" class="medium"/>
                        </td>
                        <td><label>AMOUNT OF INSURANCE</label></td>
                        <td>
                            <html:input path="amountOfInsurance" class="medium"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6">
                            <hr>
                        </td>
                    </tr>
                    <tr>
                        <td><label>REMARK</label></td>
                        <td>
                            <html:textarea path="remarks" class="textareaL"/>
                            <html:hidden path="status"/>
                        </td>
                        <td><label>PREPARED BY</label></td>
                        <td>
                            <label><c:out value="${siBean.createdBy}"/>:<c:out value="${siBean.creationDate}"/></label>
                        </td>
                        <td><label>AMENDED BY</label></td>
                        <td>
                            <label><c:out value="${siBean.amendedBy}"/>:<c:out value="${siBean.amendedDate}"/></label>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="4">
                            <table class="tablefooter">
                                <tr>
                                    <td><button type="submit" class="finbutton">SAVE</button></td>

                                    <c:if test="${siBean.soNumber != null}">
                                        <td><input type="button" class="finbutton" id="addContainerBtn" value="DIMENSION TABLE"/>
                                        <td><input type="button" class="finbutton" onclick="saveCopy()" value="COPY"></td>

                                        <td><input class="finbutton" onclick="doPrint()" type="button" value="PRINT" name=pBtn"></td>
                                        <td><input type="button" class="finbutton" id="attachmentsBtn" value="ATTACHMENTS"></td>
                                    </c:if>
                                    <td><button type="reset" class="finbutton">RESET</button></td>
                                    <td><button type="submit" class="finbutton">EXIT</button></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </div>
            <c:if test="${siBean.soNumber != ''}">
                <table id="example" class="display" cellspacing="0" width="100%">
                    <thead>
                        <tr>
                            <th>B.QTY</th>
                            <th>B.UNIT</th>
                            <th>B.GR.WT</th>
                            <th>B.CBM</th>
                            <th>A.QTY</th>
                            <th>A.UNIT</th>
                            <th>A.GR.WT</th>
                            <th>A.CBM</th>
                        </tr>
                    </thead>                
                </table>

                </div>
            </c:if>

        </html:form> 
        <div id="dialog-confirm" title="Sailing Schedule Popup"/>
        <div id="dialog-container-so" title="SI Container"/>
        <div id="dialog-container-attachments" title="Attachments"/>
    </body>
</html>
