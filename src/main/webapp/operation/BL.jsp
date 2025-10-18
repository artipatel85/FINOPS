
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<script src="finactjs/so.js"></script>
<script src="finactjs/jquery.validate.min.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <html:form autocomplete="off" name="blForm" command="blBean" method="post" id="blForm" action="blSave.fin">
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
                        ajax: "blContainerGson.fin?blNo=${blBean.blNumber}",
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
                                "mData": "number",
                                "render": function (mData, full, row) {
                                    var lineNo = row['lineNumber'];
                                    var bkgRefNo = row['bookingRefNumber'];
                                    var lineSealNumber = row['lineSealNumber'];
                                    var size = row['size'];
                                    var href = "javascript:openContainerWindow("+lineNo+","+bkgRefNo+")";
                                    return '<a href="'+href+'">' + mData + '/'+size+'/'+lineSealNumber+'</a>';
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
                            {"mData": "unit"},
                            {"mData": "weight"},
                            {"mData": "netWeight"},
                            {"mData": "measurement"}

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
                $(document).ready(pod);
                $(document).ready(from);
                $(document).ready(dest);
                $(document).ready(upTo);
                $(document).ready(polName);
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

                    $("#cfsDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#cyDate").datepicker({
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

                    $("#mblDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#describedDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#igmDate").datepicker({
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

                function openContainerWindow(lineNo, bkgRefNo) {
                    dialogWindow("#dialog-container",
                            "retrieveBLContainer.fin?blNo="+ $("#blNo").val() +"&bkgRefNo="+bkgRefNo +"&lineNo="+lineNo,
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
                    s = s.replace('blSave.fin','blRetrieve.fin?blNumber='+$("#blNumber").val()+"&param="+$("#expImp").val());
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
                            url: "saveBLContainer.fin",
                            data: $('form[name=blContainerForm]').serialize(),
                            success: function (data) {
                                alert("record saved successfully");
                                var s = location.href;
                                s = s.replace('blSave.fin','blRetrieve.fin?blNumber='+$("#blNumber").val()+"&param="+$("#expImp").val());
                                window.location.assign(s);
                            }
                        });

                }

                function invoicing(blNo) {
                    window.open('loadBillingData.fin?blNo=' + blNo + '&seaAir=SEA&expImp=${command.expImp}&PARAM=REVENUE&billType=INVOICE&proforma=false');
                }

                function proforma(blNo) {
                    window.open('loadBillingData.fin?blNo=' + blNo + '&seaAir=SEA&expImp=${command.expImp}&PARAM=REVENUE&billType=INVOICE&proforma=true');
                }

                function bos(blNo) {
                    window.open('loadBillingData.fin?blNo=' + blNo + '&seaAir=SEA&expImp=${command.expImp}&PARAM=REVENUE&billType=BOS&proforma=false');
                }

                function doReportPrint(blNo) {
                    window.open('doPDF.fin?blNo='+blNo+'&type=1&author=undefined');
                }

                function print(blNo, type) {
                    window.open('blPDF.fin?blNo=' + blNo + '&type='+type);
                }

                function approveBL(blNo) {
                    document.forms[0].action = "approveBL.fin?param=${command.expImp}";
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
                    Bill of Lading - ${command.expImp}
                </h3>
                <table class="table-noborder" width="100%">
                    <c:if test="${blBean.errMsg != null}">
                        <tr>
                            <th colspan="6"><c:out value="${blBean.errMsg}"/></th>
                        </tr>
                    </c:if>
                    <tr>
                        <td width="8%"><label>BL NO.</label></td>
                        <td width="29%">
                            <html:input path="blNumber" class="large" style="text-transform:uppercase" id="blNo"/>
                            <html:hidden path="bkgRefNo"/>
                            <html:hidden path="bookingType"/>
                            <html:hidden path="expImp"/>
                            <html:hidden path="action"/>
                        </td>
                        <td width="8%"><label class="required">BL CREATION DATE</label></td>
                        <td width="29%"><html:input path="blReleaseDate" class="smallplus" readonly="true"/></td>
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
                        <td><label>M. BL NO</label></td>
                        <td>
                            <html:input path="mBlNumber" class="large" style="text-transform:uppercase"/>
                        </td>
                        <td><label>MBL DATE</label></td>
                        <td>
                            <html:input path="mblDate" class="smallplus"/>

                        </td>
                        <td><label>NO OF ORIGINAL</label></td>
                        <td>
                            <html:select path="noOfOriginal" class="small">
                                <html:option value="3"/>
                                <html:option value="1"/>
                                <html:option value="2"/>
                                <html:option value="0">SW</html:option>
                            </html:select>
                        </td>

                    </tr>

                    <tr>
                        <td><label class="required">A.SHPR</label></td>
                        <td>
                            <html:input path="actShipperName" style="TEXT-TRANSFORM: uppercase;" class="large" id="actShipperName"/>
                            <html:input path="actShipper" style="TEXT-TRANSFORM: uppercase;" class="small" id="actShipper"/>
                        </td>
                        <td><label class="required">A.CNEE</label></td>
                        <td>
                            <html:input path="actConsigneeName" style="TEXT-TRANSFORM: uppercase;" class="large" id="actConsigneeName"/>
                            <html:input path="actConsignee" style="TEXT-TRANSFORM: uppercase;" class="small" id="actConsignee"/>
                        </td>
                        <td width="8%"><label>JOB NO</label></td>
                        <td width="29%"><html:input path="jobNumber" class="medium" readonly="true"/></td>
                    </tr>
                    <tr>
                        <td colspan="6"><hr/></td>
                    </tr>
                    <tr>
                        <td><label class="required">SHPR</label></td>
                        <td>
                            <html:input path="shipperName" style="TEXT-TRANSFORM: uppercase;" class="large" id="shipperName"/>
                            <html:input path="shipper" style="TEXT-TRANSFORM: uppercase;" class="small" id="shipper"/>
                            <html:textarea path="shipperContactDetails" style="TEXT-TRANSFORM: uppercase;" rows="4" class="textareaL" id="shipperContactDetails"/>
                        </td>
                        <td><label class="required">CNEE</label></td>
                        <td>
                            <html:input path="consigneeName" style="TEXT-TRANSFORM: uppercase;" class="large" id="consigneeName"/>
                            <html:input path="consignee" style="TEXT-TRANSFORM: uppercase;" class="small"/>
                            <html:textarea path="consigneeContactDetails" style="TEXT-TRANSFORM: uppercase;" rows="4" class="textareaL"/>
                        </td>
                        <td><label>TO</label></td>
                        <td>
                            <html:textarea path="theManager" rows="4" class="textarea"/>
                        </td>
                    </tr>

                    <tr>
                        <td><label>NOTIFY</label></td>
                        <td>
                            <html:input path="soNotifyName" style="TEXT-TRANSFORM: uppercase;" class="large"/>
                            <html:input path="soNotify" style="TEXT-TRANSFORM: uppercase;" class="small"/>
                            <html:textarea path="soNotifyContactDetails" style="TEXT-TRANSFORM: uppercase;" rows="4" class="textareaL"/>
                        </td>
                        <td><label>ALSO NOTIFY</label></td>
                        <td>
                            <html:input path="alsoNotifyName" style="TEXT-TRANSFORM: uppercase;" class="large"/>
                            <html:input path="alsoNotify" style="TEXT-TRANSFORM: uppercase;" class="small"/>
                            <html:textarea path="alsoNotifyContactDetails" style="TEXT-TRANSFORM: uppercase;" rows="4" class="textareaL"/>
                        </td>

                    </tr>
                    <tr>
                        <td colspan="6"><hr/></td>
                    </tr>
                    <tr>
                        <td><label class="required">POR</label></td>
                        <td>
                            <html:input path="porName" class="large" id="porName"/>
                            <html:input path="por" class="small" id="por"/>
                            <input type="radio" name="porRadio" id="porRadio" value="por">
                        </td>
                        <td><label class="required">POL</label></td>
                        <td>
                            <html:input path="polName" class="large" id="polName"/>
                            <html:input path="pol" class="small" id="pol"/>
                            <input type="radio" name="porRadio" id="polRadio" value="pol">
                        </td>
                        <td><label class="required">FROM</label></td>
                        <td>
                            <html:input path="fromFreightName" class="medium" id="fromName"/>
                            <html:input path="fromFreightCode" class="small" id="from"/>
                        </td>
                    </tr>

                    <tr>
                        <td><label class="required">POD</label></td>
                        <td>
                            <html:input path="podName" class="large" id="podName"/>
                            <html:input path="pod" class="small" id="pod"/>
                            <input type="radio" name="podRadio" id="podRadio" value="pod">
                        </td>
                        <td><label>DEST</label></td>
                        <td>
                            <html:input path="destName" class="large" id="destName"/>
                            <html:input path="dest" class="small" id="dest"/>
                            <input type="radio" name="podRadio" id="destRadio" value="dest">
                        </td>
                        <td><label class="required">UP TO</label></td>
                        <td>
                            <html:input path="upToFreightName" class="medium" id="upToName"/>
                            <html:input path="upToFreightCode" class="small" id="upTo"/>
                        </td>
                    </tr>

                    <tr>
                        <td><label>PRE-CARRIAGE</label></td>
                        <td>
                            <html:input path="preCarriage" class="medium"/>
                        </td>
                        <td><label>PRE-VOYAGE</label></td>
                        <td>
                            <html:input path="preVoyage" class="medium"/>
                        </td>
                        <td><label>TERMINAL</label></td>
                        <td>
                            <html:input path="terminalCode" class="medium" id="upToName"/>
                            <html:input path="terminalName" class="small" id="upTo"/>
                        </td>
                    </tr>

                    <tr>
                        <td><label>VSL</label></td>
                        <td>
                            <html:input path="vsl" class="medium"/>
                            <input type="button" name="vslBtn" id="vslBtn" value="..." class="finbutton"/>
                        </td>
                        <td><label>VOY</label></td>
                        <td>
                            <html:input path="voy" class="medium"/>
                        </td>
                        <td><label>CARRIER</label></td>
                        <td>
                            <html:input path="carrierName" class="large"/>
                            <html:input path="carrierCode" class="small"/>
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
                        <td><label>RECEIVED DATE</label></td>
                        <td>
                            <html:input path="cargoReceivedDate" class="medium"/>
                        </td>

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

                        <td><label>ICD FACTORY</label></td>
                        <td>
                            <html:select path="icdFactory" styleClass="smallplus">
                                <html:option value=""></html:option>
                                <html:option value="ICD"></html:option>
                                <html:option value="FACTORY"></html:option>
                            </html:select>

                            <html:input path="describedDate" class="medium" id="describedDate"/>
                        </td>
                        <td><label>ITEM NUMBER</label></td>
                        <td>
                           <html:input path="itemNumber" class="smallplus" id="freightPayableAt"/>
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
                        <td><label>DO NUMBER</label></td>
                        <td>
                           <html:input path="doNumber" class="large"/>

                        </td>
                    </tr>
                    <tr>
                        <td><label class="required"></label></td>
                        <td>

                            <html:textarea path="loadingAgentContactDetails" rows="4" class="textareaL" id="loadingAgentContactDetails"/>
                        </td>
                        <td><label></label></td>
                        <td>

                            <html:textarea path="destinationAgentContactDetails" rows="4" class="textareaL" id="destinationAgentContactDetails"/>
                        </td>
                        <td><label>EMP RET LOC</label></td>
                        <td>
                           <html:textarea path="emptyReturnLocation" rows="4" class="textarea" id="emptyReturnLocation"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>REMARK</label></td>
                        <td>
                            <html:textarea path="remarks" class="textareaL"/>
                        </td>
                        <td><label>TOTAL IN WORDS</label></td>
                        <td>
                            <html:textarea path="totalInWords" class="textareaL"/>
                        </td>
                        <td><label>FREE DAYS</label></td>
                        <td>
                            <html:input path="freeDays" class="small"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>IGM NO</label></td>
                        <td>
                            <html:input path="igmNo" class="medium"/>
                        </td>
                        <td><label>IGM Date</label></td>
                        <td>
                            <html:input path="igmDate" class="medium" id="igmDate"/>
                        </td>
                        <td><label>PREPARED BY</label></td>
                        <td>
                            <label><c:out value="${command.createdBy}"/>:<c:out value="${command.creationDate}"/></label>
                        </td>
                    </tr>
                    <c:if test="${blBean.blNumber != null}">
                        <tr>
                            <td><label>AMENDED BY</label></td>
                            <td>
                                <label><c:out value="${command.amendedBy}"/>:<c:out value="${command.amendedDate}"/></label>
                            </td>
                            <td><label>SIGNATURE</label></td>
                            <td>
                                <html:select path="signature" class="medium">
                                    <html:option value=""/>
                                    <html:options items="${command.signatureMap}"/>
                                </html:select>
                            </td>
                            <td><label>SURVEYORS</label></td>
                            <td>
                                <html:input path="surveyors" class="large"/>
                            </td>
                        </tr>
                    </c:if>
                </table>
            </div> 


            <div class="comdivfoot">

                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" class="finbutton">SAVE</button></td>
                        <c:if test="${blBean.blNumber != null}">
                            <td><INPUT onclick="print('${blBean.blNumber}','ORIGINAL')" type="button" value="PRINT" class="finbutton"></td>
                            <td><INPUT onclick="print('${blBean.blNumber}','DRAFT')" type="button" value="DRAFT PRINT" class="finbutton"></td>
                            <c:if test="${command.isApproved == 'Y'}">
                                <rbac:rbac formId="1048" pattern="(.)\\|(.)\\|(.)\\|(.)\\|(Y)\\|(.)\\|(.)\\|(.)">
                                   <td><INPUT onclick="sysGeneratedPrint('${blBean.blNumber}')" type="button" value="SYSTEM GENERATED" class="finbutton"></td>
                                </rbac:rbac>
                            </c:if>
                            <c:if test="${command.isApproved != 'Y'}">
                                <ubac:ubac userId="${command.signature}" >
                                    <td><INPUT onclick="approveBL('${blBean.blNumber}')" type="button" value="APPROVE" class="finbutton"></td>
                                </ubac:ubac>
                            </c:if>
                            <td><INPUT onclick="invoicing('${blBean.blNumber}')" type="button" value="INVOICE" class="finbutton"></td>
                            <td><INPUT class="finbutton" onclick="proforma('${blBean.blNumber}')" type="button" value="PROFORMA"></td>
                            <td><INPUT class="finbutton" onclick="bos('${blBean.blNumber}')" type="button" value="BILL OF SUPPLY"></td>
                            <td><INPUT class="finbutton" onclick="doReportPrint('${blBean.blNumber}')" type="button" value="DO"></td>
                        </c:if>
                        <td><button type="reset" class="finbutton">RESET</button></td>
                        <td><button type="submit" class="finbutton">EXIT</button></td>
                        <td><button type="submit" class="finbutton">DND</button></td>
                    </tr>
                </table>
            </div>

                <header>
                    Container Details
                </header>
                <table id="example" class="display" cellspacing="0" width="100%">
                    <thead>
                        <tr>
                            <th>CONTAINER NO/SIZE/L.SEAL</th>
                            <th>SO NUMBER</th>
                            <th>SERVICE TERM</th>
                            <th>A.QTY</th>
                            <th>A.UNIT</th>
                            <th>A.GR.WT</th>
                            <th>A.NT.WT</th>
                            <th>A.CBM</th>
                        </tr>
                    </thead>                
                </table>

        </html:form>
        <div id="dialog-confirm" title="Sailing Schedule Popup"/>
        <div id="dialog-container" title="BL Container"/>
    </body>
</html>
