
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<script src="finactjs/so.js"></script>
<script src="finactjs/jquery.validate.min.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <html:form autocomplete="off" name="hawbForm" command="hawbBean" method="post" id="hawbForm" action="hawbSave.fin">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Bill of Lading</title>
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

                $(document).ready(function () {
                    var table = $('#example').DataTable({
                        ajax: "hawbContainerGson.fin?blNo=${hawbBean.blNumber}",
                        "serverSide": true,
                        "bJQueryUI": true,
                        "bRetrieve": true,
                        "bProcessing": true,
                        "bFilter": false,
                        "dom": '<"top">t',
                        "deferLoading": 0,
                        "lengthMenu": [20, 30, 50],
                        "aoColumns": [
                            {
                                "mData": "bookingRefNumber",
                                "render": function (mData, full, row) {
                                    var lineNo = row['lineNumber'];
                                    var lineSealNumber = row['lineSealNumber'];
                                    var la = row['loadingAgentCode'];
                                    var href = "javascript:openContainerWindow("+lineNo+","+mData+")";
                                    return '<a href="'+href+'">' + la + '/'+mData+'</a>';
                                }
                            },
                            {
                                "mData": "soNumber",
                                "render": function (mData, full, row) {
                                    var la = row['loadingAgentCode'];
                                    return la + '/'+mData;
                                }
                            },
                            {
                                "mData": "serviceTerm"
                            },
                            {"mData": "qty"},
                            {"mData": "weight"},
                            {"mData": "chargeableWeight"}

                        ]
                    });
                    drawContainerTable();
                });

                $(document).ready(actShipper);
                $(document).ready(actConsignee);
                $(document).ready(shipper);
                $(document).ready(consignee);
                $(document).ready(soNotify);
                $(document).ready(alsoNotify);
                $(document).ready(por);
                $(document).ready(poi);
                $(document).ready(pol);
                $(document).ready(pol2);
                $(document).ready(pod);
                $(document).ready(pod2);
                $(document).ready(from);
                $(document).ready(dest);
                $(document).ready(upTo);
                $(document).ready(loadingAgent);
                $(document).ready(destinationAgent);
                $(document).ready(actShipperName);
                $(document).ready(alsoNotifyName);
                $(document).ready(destinationAgentName);
                $(document).ready(loadingAgentName);
                $(document).ready(shipperName);
                $(document).ready(carrierName);
                $(document).ready(consigneeName);
                $(document).ready(soNotifyName);
                $(document).ready(actConsigneeName);


                $(function () {
                    $("#bookingRefDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#bookingRefDate").datepicker('setDate', new Date());

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

                    $("#etd2").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#eta2").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#flightDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#flightDate2").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#blIssueDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#cargoReceivedDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#freightType").on("change", function () {
                        if($("#freightType").val() == 'P'){
                            $("#freightPayableAt").val("ORIGIN");
                        }
                        else{
                            $("#freightPayableAt").val("DESTINATION");
                        }
                    });

                });

                function openContainerWindow(lineNo, bkgRefNo) {
                    dialogWindow("#dialog-container",
                            "retrieveHawbContainer.fin?blNo="+ $("#blNo").val() +"&bkgRefNo="+bkgRefNo +"&lineNo="+lineNo,
                            1200, "['middle', 500]", "auto");
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

                function podPopup(src, idx) {
                    autopopup(src, "popup3ViewAutoPort.fin", function (event, ui) {
                        $('input[id="podDescription"]').get(idx).value = ui.item.data;
                    });
                }

                function exitContainer() {
                    var s = location.href;
                    s = s.replace('hawbSave.fin','hawbRetrieve.fin?blNumber='+$("#blNumber").val()+"&param="+$("#expImp").val());
                    window.location.assign(s);
                }

                $(document).ready(function () {
                    $("#blForm").validate({
                        rules: {
                            bookingRefDate:{required: true},
                            bookingType: {required: true},
                            deliveryDate: {required: true},
                            deliveryAt: {required: true},
                            comInvNumber: {required: true},
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
                            comInvNumber: '<label class="tool">* field is required.</label>',
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



                function saveContainer() {
                    var table = $('#example').DataTable();

                        $.post({
                            url: "saveHawbContainer.fin",
                            data: $('form[name=hawbContainerForm]').serialize(),
                            success: function (data) {
                                alert("record saved successfully");
                                var s = location.href;
                                s = s.replace('hawbSave.fin','hawbRetrieve.fin?blNumber='+$("#blNumber").val()+"&param="+$("#expImp").val());
                                window.location.assign(s);
                            }
                        });

                }

                function invoicing(blNo) {
                    window.open('loadBillingData.fin?blNo=' + blNo + '&seaAir=AIR&expImp=${command.expImp}&PARAM=REVENUE&billType=INVOICE&proforma=false');
                }

                function proforma(blNo) {
                    window.open('loadBillingData.fin?blNo=' + blNo + '&seaAir=AIR&expImp=${command.expImp}&PARAM=REVENUE&billType=INVOICE&proforma=true');
                }

                function bos(blNo) {
                    window.open('loadBillingData.fin?blNo=' + blNo + '&seaAir=AIR&expImp=${command.expImp}&PARAM=REVENUE&billType=BOS&proforma=false');
                }

                function print(blNo, type) {
                    window.open('hawbPDF.fin?blNo=' + blNo + '&type='+type);
                }

                function approveBL(blNo) {
                    document.forms[0].action = "approveBL.fin";
                    document.forms[0].submit();
                }

                function sysGeneratedPrint(blNo) {
                    var nooforiginal = document.forms[0].noOfOriginal.value;
                    window.open('blPrint.fin?blNo=' + blNo + '&type=' + nooforiginal, "_blank", "toolbar=yes,scrollbars=yes,resizable=yes,top=300,left=500,width=400,height=200");
                }

                function getContainer(lineNo) {
                    var table = $('#example').DataTable();
                    var rows = table.data().count();
                    //alert(rows === 0);bkgRefNo
                    //alert($("#bookingType").val());

                        $.ajax({
                            url: "retrieveSOContainer.fin?bkgRefNo="+$("#bkgRefNo").val()+"&lineNo=8 ",
                            //data: $('form[name=soForm]').serialize(),
                            success: function (data) {
                                alert(data.aaData.size);
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

            </script>
        </head>
        <body>
            <div id="accordion">

                <h3>
                    House Airway Bill - ${command.expImp}
                </h3>
                <table class="table-noborder" width="100%">
                    <c:if test="${command.errMsg != null}">
                        <tr>
                            <th colspan="6"><c:out value="${command.errMsg}"/></th>
                        </tr>
                    </c:if>
                    <tr>
                        <td width="8%"><label>BL NO.</label></td>
                        <td width="29%">
                            <html:input path="blNumber" class="large" id="blNo" style="text-transform:uppercase"/>
                            <html:hidden path="bkgRefNo"/>
                            <html:hidden path="bookingType"/>
                            <html:hidden path="expImp"/>
                            <html:hidden path="action"/>
                        </td>
                        <td width="8%"><label>JOB NO</label></td>
                        <td width="29%"><html:input path="jobNumber" class="medium" readonly="true"/></td>
                    </tr>

                    <tr>
                        <td><label>M. BL NO</label></td>
                        <td>
                            <html:input path="mBlNumber" class="large" style="text-transform:uppercase"/>
                        </td>
                        <td><label>CARGO TYPE</label></td>
                        <td>
                            <html:select path="cargoType" class="medium">
                                <html:option value="G">General cargo</html:option>
                                <html:option value="D">Dangerous goods</html:option>
                                <html:option value="H">Garment on hangers</html:option>
                            </html:select>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6"><hr/></td>
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

                    </tr>

                    <tr>
                        <td><label class="required">SHPR</label></td>
                        <td>
                            <html:input path="shipperName" class="large" id="shipperName"/>
                            <html:input path="shipper" class="small" id="shipper"/>
                        </td>
                        <td><label class="required">CNEE</label></td>
                        <td>
                            <html:input path="consigneeName" class="large"/>
                            <html:input path="consignee" class="small"/>
                        </td>

                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <html:textarea path="shipperContactDetails" rows="4" class="textareaL" id="shipperContactDetails"/>
                        </td>
                        <td></td>
                        <td>
                            <html:textarea path="consigneeContactDetails" rows="4" class="textarea"/>
                        </td>

                    </tr>
                    <tr>
                        <td><label>NOTIFY</label></td>
                        <td>
                            <html:input path="soNotifyName" class="large"/>
                            <html:input path="soNotify" class="small"/>
                        </td>

                        <td><label class="required">CARRIER</label></td>
                        <td>
                            <html:input path="carrierName" class="large"/>
                            <html:input path="carrierCode" class="small"/>
                        </td>
                    </tr>
                     <tr>
                        <td></td>
                        <td>
                            <html:textarea path="soNotifyContactDetails" rows="4" class="textareaL"/>
                        </td>

                        <td></td>
                        <td>

                        </td>
                    </tr>
                    <tr>
                        <td colspan="6"><hr/></td>
                    </tr>
                    <tr>
                        <td><label class="required">FLIGHT NO</label></td>
                        <td>
                            <html:input path="flightNo" class="small"/>
                            <html:select class="medium" path="flightType">
                                <html:option value="" ></html:option>
                                <html:option value="CF" >Cargo Flight</html:option>
                                <html:option value="PF">Passenger Flight</html:option>
                            </html:select>
                        </td>
                        <td><label>FLIGHT DATE</label></td>
                        <td>
                            <html:input path="flightDate" class="medium"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="required">POL</label></td>
                        <td>
                            <html:input path="pol" class="small" id="pol"/>
                            <html:input path="polName" class="large" id="polName"/>
                        </td>
                        <td><label class="required">POD</label></td>
                        <td>
                            <html:input path="pod" class="small" id="pod"/>
                            <html:input path="podName" class="medium" id="podName"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>ETD</label></td>
                        <td>
                            <html:input path="etd" class="smallplus"/>
                        </td>
                        <td><label>ETA</label></td>
                        <td>
                            <html:input path="eta" class="smallplus"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>2ND FLIGHT NO</label></td>
                        <td>
                            <html:input path="flightNo2" class="small"/>
                            <html:select class="medium" path="flightType2">
                                <html:option value="" ></html:option>
                                <html:option value="CF" >Cargo Flight</html:option>
                                <html:option value="PF">Passenger Flight</html:option>
                            </html:select>
                        </td>
                        <td><label>FLIGHT DATE</label></td>
                        <td>
                            <html:input path="flightDate2" class="medium"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="required">POL</label></td>
                        <td>
                            <html:input path="pol2" class="small" id="pol2"/>
                            <html:input path="polName2" class="large" id="polName2"/>
                        </td>
                        <td><label class="required">POD</label></td>
                        <td>
                            <html:input path="pod2" class="small" id="pod2"/>
                            <html:input path="podName2" class="medium" id="podName2"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>ETD</label></td>
                        <td>
                            <html:input path="etd2" class="smallplus"/>
                        </td>
                        <td><label>ETA</label></td>
                        <td>
                            <html:input path="eta2" class="smallplus"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6"><hr/></td>
                    </tr>
                    <tr>
                        <td><label>DATE OF ISSUE</label></td>
                        <td>
                            <html:input path="blIssueDate" class="smallplus"/>
                        </td>
                        <td><label>PLACE OF ISSUE</label></td>
                        <td>
                            <html:input path="poi" class="small" id="poi"/>
                            <html:input path="poiName" class="medium" id="poiName"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>DEST</label></td>
                        <td>
                            <html:input path="dest" class="small" id="dest"/>
                            <html:input path="destName" class="large" id="destName"/>
                        </td>
                        <td><label>CARGO RECEIVED DATE</label></td>
                        <td>
                           <html:input path="cargoReceivedDate" class="medium"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6"><hr/></td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <table width="100%" height="100%">
                                <tr>
                                    <th width="50%">PAYMENT TERM</th>
                                    <th>PREPAID</th>
                                    <th>COLLECT</th>
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
                        <td colspan="2">
                            <table width="100%">
                                <tr>
                                    <th colspan="1">FLIGHT DETAILS</th>
                                    <th>PREPAID</th>
                                    <th>COLLECT</th>
                                    <th></th>
                                </tr>
                                <tr>
                                    <td><label>AIRFREIGHT</label></td>
                                    <td><html:radiobutton onclick="togglePayableAt()" path="pcAirFreight" value="P"/></td>
                                    <td><html:radiobutton onclick="togglePayableAt()" path="pcAirFreight" value="C"/></td>
                                    <td><html:input path="commercialInvoice" class="small"/></td>
                                </tr>
                                <tr>
                                    <td><label>TAX</label></td>
                                    <td><html:radiobutton onclick="togglePayableAt()" path="pcTax" value="P"/></td>
                                    <td><html:radiobutton onclick="togglePayableAt()" path="pcTax" value="C"/></td>
                                    <td><html:input path="packingList" class="small"/></td>
                                </tr>
                                <tr>
                                    <td><label>DUE AGENT</label></td>
                                    <td><html:radiobutton onclick="togglePayableAt()" path="pcDueAgent" value="P"/></td>
                                    <td><html:radiobutton onclick="togglePayableAt()" path="pcDueAgent" value="C"/></td>
                                    <td><html:input path="exportLicense" class="small"/></td>
                                </tr>
                                <tr>
                                    <td><label>DUE CARRIER</label></td>
                                    <td><html:radiobutton onclick="togglePayableAt()" path="pcDueCarrier" value="P"/></td>
                                    <td><html:radiobutton onclick="togglePayableAt()" path="pcDueCarrier" value="C"/></td>
                                    <td><html:input path="certOfOrigin" class="small"/></td>
                                </tr>
                                <tr>
                                    <td><label>TOTAL</label></td>
                                    <td><html:input path="totalPrepaid" class="medium"/></td>
                                    <td><html:input path="totalCollect" class="medium"/></td>
                                    <td><html:input path="formA" class="small"/></td>
                                </tr>
                            </table>
                        </td>
                    </tr>

                    <tr>
                        <td><label>PAYABLE AT</label></td>
                        <td>
                            <html:select path="freightType" class="smallplus" id="freightType">
                                <html:option value="P">Prepaid</html:option>
                                <html:option value="C">Collect</html:option>
                            </html:select>
                            AT
                            <html:input path="freightPayableAt" class="smallplus" id="freightPayableAt"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>CURRENCY</label></td>
                        <td>
                            <html:select path="currencyId">
                                <html:option value="">Select</html:option>
                                <html:option value="1001">AUD</html:option>
                                <html:option value="1002">EUR</html:option>
                                <html:option value="1003">FRF</html:option>
                                <html:option value="1004">GBP</html:option>
                                <html:option value="1005">HKD</html:option>
                                <html:option value="1006">INR</html:option>
                                <html:option value="1007">USD</html:option>
                                <html:option value="1008">YEN</html:option>
                            </html:select>

                        </td>
                        <td><label>VALUE FOR CARRIAGE</label></td>
                        <td>
                            <html:input path="valueForCarriage" class="medium"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>VALUE FOR CUSTOM</label></td>
                        <td>
                            <html:input path="valueForCustom" class="medium"/>
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
                        <td><label class="required">LOADING AGENT</label></td>
                        <td>
                            <html:input path="loadingAgentCode" class="small" id="loadingAgentCode"/>
                            <html:input path="loadingAgentName" class="large" id="loadingAgentName"/>
                        </td>
                        <td><label>DEST. AGENT</label></td>
                        <td>
                            <html:input path="destinationAgentCode" class="small" id="destinationAgentCode"/>
                            <html:input path="destinationAgentName" class="large" id="destinationAgentName"/>
                        </td>

                    </tr>

                    <tr>
                        <td></td>
                        <td>
                            <html:textarea path="loadingAgentContactDetails" rows="4" class="textareaL" id="loadingAgentContactDetails"/>
                        </td>
                        <td></td>
                        <td>
                            <html:textarea path="destinationAgentContactDetails" rows="4" class="textarea" id="destinationAgentContactDetails"/>
                        </td>

                    </tr>
                    <tr>
                        <td><label>ORIGIN WAREHOUSE</label></td>
                        <td>
                            <html:input path="originWareHouseName" class="large" id="originWareHouseName"/>
                            <html:input path="originWareHouse" class="small" id="originWareHouse"/>
                        </td>
                        <td><label>OTHER CHARGES</label></td>
                        <td><label>REMARKS</label></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td>
                            <html:textarea path="originWareHouseContactDetails" id="originWareHouseContactDetails" rows="4" class="textareaL"/>
                        </td>
                        <td><html:textarea path="otherCharges" rows="4" class="textarea"/></td>
                        <td><html:textarea path="remarks" rows="4" class="textarea"/></td>
                    </tr>
                    <tr>
                        <td><label>PREPARED BY</label></td>
                        <td>
                            <html:textarea path="createdBy" class="medium"/>
                        </td>
                        <td><label>AMENDED BY</label></td>
                        <td>
                            <html:textarea path="amendedBy" class="medium"/>
                        </td>
                    </tr>
                    <c:if test="${command.blNumber != null}">
                        <tr>
                            <td><label>SIGNATURE</label></td>
                            <td>
                                <html:select path="signature" class="medium">
                                    <html:option value=""/>
                                    <html:options items="${command.signatureMap}"/>
                                </html:select>
                            </td> 
                        </tr>
                    </c:if>
                </table>
            </div> 


            <div class="comdivfoot">

                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" class="finbutton">SAVE</button></td>
                        <c:if test="${command.blNumber != null}">
                            <td><INPUT onclick="print('${command.blNumber}','ORIGINAL')" type="button" value="PRINT" class="finbutton"></td>

                            <td><INPUT onclick="invoicing('${command.blNumber}')" type="button" value="INVOICE" class="finbutton"></td>
                            <td><INPUT class="finbutton" onclick="proforma(${command.blNumber})" type="button" value="PROFORMA"></td>
                            <td><INPUT class="finbutton" onclick="bos('${command.blNumber}')" type="button" value="BILL OF SUPPLY"></td>
                        </c:if>
                        <td><button type="reset" class="finbutton">RESET</button></td>
                        <td><button type="submit" class="finbutton">EXIT</button></td>
                    </tr>
                </table>
            </div>

                <header>
                    Container Details
                </header>
                <table id="example" class="display" cellspacing="0" width="100%">
                    <thead>
                        <tr>
                            <th>BKG REF NUMBER</th>
                            <th>SO NUMBER</th>
                            <th>SERVICE TERM</th>
                            <th>QTY</th>
                            <th>KGS</th>
                            <th>CHARG.WEIGHT</th>
                        </tr>
                    </thead>                
                </table>

        </html:form>
        <div id="dialog-confirm" title="Sailing Schedule Popup"/>
        <div id="dialog-container" title="BL Container"/>
    </body>
</html>
