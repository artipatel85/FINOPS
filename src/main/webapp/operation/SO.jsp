
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
                    {"mData": "weight"},
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

                    $('input[type=radio][id=porRadio]').on('change', function() {
                      switch ($(this).val()) {
                        case '1':
                          $("#from").val($("#por").val());
                          $("#fromName").val($("#porName").val());
                          break;
                        case '2':
                          $("#from").val($("#pol").val());
                          $("#fromName").val($("#polName").val());
                          break;
                      }
                    });

                    $('input[type=radio][id=podRadio]').on('change', function() {
                      switch ($(this).val()) {
                        case '1':
                          $("#upTo").val($("#pod").val());
                          $("#upToName").val($("#podName").val());
                          break;
                        case '2':
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
                            upToFreightCode: {required: true},
                            shipmentFHAN: {required: true}
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
                            upToFreightCode: '<label class="tool">* field is required.</label>',
                            shipmentFHAN: '<label class="tool">* field is required.</label>'
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
                    <c:if test="${soBean.errMsg != ''}">
                        <tr>
                            <td colspan="6" class="finErrMsg"><c:out value="${soBean.errMsg}"/></td>
                        </tr>
                    </c:if>
                    <tr>
                        <td width="8%"><label>BOOKING REF</label></td>
                        <td width="29%">
                            <label><c:out value="${soBean.bkgRefNo}"/></label>
                            <html:hidden path="bkgRefNo" />
                            <html:hidden path="expImp"/>
                        </td>
                        <td width="8%"><label class="required">BKG DATE</label></td>
                        <td width="29%"><html:input path="bookingRefDate" class="smallplus"/></td>
                        <td width="10%"><label class="required">BKG TYPE</label></td>

                        <td width="15%">
                            <c:if test="${soBean.bkgRefNo == 0}">
                                <html:select path="bookingType" cssClass="smallplus">
                                    <html:option value=""/>
                                    <html:option value="L">LCL</html:option>
                                    <html:option value="F">FCL</html:option>
                                </html:select>
                            </c:if>
                            <c:if test="${soBean.bkgRefNo != 0}">
                                <label><c:out value="${soBean.bookingType == 'L' ? 'LCL' : 'FCL'}"/></label>
                                <html:hidden path="bookingType" />
                            </c:if>
                            &nbsp;&nbsp;<label class="required">FHAN</label>
                            <html:select path="shipmentFHAN" cssClass="smallplus">
                                <html:option value=""/>
                                <html:option value="FREEHAND">FREEHAND</html:option>
                                <html:option value="NOMINATION">NOMINATION</html:option>
                            </html:select>
                        </td>
                    </tr>

                    <tr>
                        <td><label>S/O NO</label></td>
                        <td>
                            <label><c:out value="${soBean.loadingAgentCode}/${soBean.pod}/${soBean.soNumber}"/></label>
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
                            <c:out value="${soBean.jobNumber}"/>
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
                        <td><label>NO. OF ORIGINAL</label></td>
                        <td>
                            <html:select path="noOfOriginal" class="small">
                                <html:option value="3"/>
                                <html:option value="1"/>
                                <html:option value="2"/>                                
                                <html:option value="4"/>
                            </html:select>                           
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
                            <html:input path="actShipperName" style="TEXT-TRANSFORM: uppercase;" class="large" id="actShipperName"/>
                            <html:input path="actShipper" style="TEXT-TRANSFORM: uppercase;" class="small" id="actShipper"/>
                        </td>
                        <td><label class="required">A.CNEE</label></td>
                        <td>
                            <html:input path="actConsigneeName" style="TEXT-TRANSFORM: uppercase;" class="large" id="actConsigneeName"/>
                            <html:input path="actConsignee" style="TEXT-TRANSFORM: uppercase;" class="small" id="actConsignee"/>
                        </td>
                        <td></td>
                        <td>
                            <html:textarea path="originWareHouseContactDetails" id="originWareHouseContactDetails" style="TEXT-TRANSFORM: uppercase;" rows="4" class="textareaL"/>
                        </td>
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
                            <html:input path="consigneeName" style="TEXT-TRANSFORM: uppercase;" class="large"/>
                            <html:input path="consignee" style="TEXT-TRANSFORM: uppercase;" class="small"/>
                            <html:textarea path="consigneeContactDetails" style="TEXT-TRANSFORM: uppercase;" rows="4" class="textareaL"/>
                        </td>
                        <td><label class="required">LOADING AGENT</label></td>
                        <td>
                            <html:input path="loadingAgentName" style="TEXT-TRANSFORM: uppercase;" class="large" id="loadingAgentName"/>
                            <html:input path="loadingAgentCode" style="TEXT-TRANSFORM: uppercase;" class="small" id="loadingAgentCode"/>
                            <html:textarea path="loadingAgentContactDetails" style="TEXT-TRANSFORM: uppercase;" rows="4" class="textareaL" id="loadingAgentContactDetails"/>
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
                        <td><label>DEST. AGENT</label></td>
                        <td>
                            <html:input path="destinationAgentName" style="TEXT-TRANSFORM: uppercase;" class="large" id="destinationAgentName"/>
                            <html:input path="destinationAgentCode" style="TEXT-TRANSFORM: uppercase;" class="small" id="destinationAgentCode"/>
                            <html:textarea path="destinationAgentContactDetails" style="TEXT-TRANSFORM: uppercase;" rows="4" class="textareaL" id="destinationAgentContactDetails"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="required">POR</label></td>
                        <td>
                            <html:input path="porName" class="large" id="porName"/>
                            <html:input path="por" class="small" id="por"/>
                            <html:radiobutton path="porInd" id="porRadio" value="1"/>
                        </td>
                        <td><label class="required">POL</label></td>
                        <td>
                            <html:input path="polName" class="large" id="polName"/>
                            <html:input path="pol" class="small" id="pol"/>
                            <html:radiobutton path="porInd" id="porRadio" value="2"/>
                        </td>
                        <td><label class="required">FROM</label></td>
                        <td>
                            <label class="required">ROUTE FOR FREIGHTAGE</label><br>
                            <html:input path="fromFreightName" class="medium" id="fromName"/>
                            <html:input path="fromFreightCode" class="small" id="from"/>
                        </td>
                    </tr>

                    <tr>
                        <td><label class="required">POD</label></td>
                        <td>
                            <html:input path="podName" class="large" id="podName"/>
                            <html:input path="pod" class="small" id="pod"/>
                            <html:radiobutton path="podInd" id="podRadio" value="1"/>
                        </td>
                        <td><label>DEST</label></td>
                        <td>
                            <html:input path="destName" class="large" id="destName"/>
                            <html:input path="dest" class="small" id="dest"/>
                            <html:radiobutton path="podInd" id="podRadio" value="2"/>
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
                            <html:input path="carrierName" class="large" id="carrierName"/>
                            <html:input path="carrierCode" class="small" id="carrierCode"/>
                        </td> 
                    </tr>

                    <tr>
                        <td><label>PAYABLE AT</label></td>
                        <td>
                            <html:select path="freightType" id="freightType">
                                <html:option value=""></html:option>
                                <html:option value="P">Prepaid</html:option>
                                <html:option value="C">Collect</html:option>
                            </html:select>
                            AT
                            <html:input path="freightPayableAt" class="smallPlus" readonly="true" id="freightPayableAt"/>
                        </td>
                        <td><label>CFS DATE</label></td>
                        <td>
                            <html:input path="cfsDate" class="smallplus"/>
                            <html:input path="cfsTime" class="small"/>

                        </td>
                        <td><label>CY DATE</label></td>
                        <td>
                            <html:input path="cyDate" class="smallplus"/>
                            <html:input path="cyTime" class="small"/>                            
                        </td> 
                    </tr>

                    <tr>
                        <td><label>RECEIVED DATE</label></td>
                        <td>
                            <html:input path="cargoReceivedDate" class="medium"/>
                        </td>
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
                        <td><label>C.INV NO</label></td>
                        <td>
                            <html:input path="comInvNumber" class="medium"/>
                        </td>
                        <td><label>C.INV Date</label></td>
                        <td>
                            <html:input path="comInvDate" id="cinvdate" class="smallplus"/>
                        </td>
                        <td><label>BL NO</label></td>
                        <td>
                            <html:input path="blNumber" class="medium" readonly="true"/>
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
                            <label><c:out value="${soBean.createdBy}"/>:<c:out value="${soBean.creationDate}"/></label>
                        </td>
                        <td><label>AMENDED BY</label></td>
                        <td>
                            <label><c:out value="${soBean.amendedBy}"/>:<c:out value="${soBean.amendedDate}"/></label>
                        </td>

                    </tr>

                    <tr>
                        <td colspan="4">
                            <table class="tablefooter">
                                <tr>
                                    <td><button type="submit" class="finbutton">SAVE</button></td>

                                    <c:if test="${soBean.soNumber != null}">
                                        <td><input type="button" class="finbutton" id="addContainerBtn" value="ADD CONTAINER"/>
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
        <div id="dialog-confirm" title="Sailing Schedule Popup"/>
        <div id="dialog-container-so" title="SO Container"/>
        <div id="dialog-container-attachments" title="Attachments"/>
    </body>
</html>
