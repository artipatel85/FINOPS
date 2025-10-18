
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/so.js"></script>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tracking - Sea/Export</title> 
        <style>
            .rows{
                margin-top: 0px;
                border-radius: 0px;
                width: 100%;
                border: 1px solid #c4c5c6;
                overflow-y: scroll;
            }
            .rows2{
                border-radius: 0px;
                height: 100%;
                width: 100%;
                border: 1px solid #c4c5c6;
            }
            .comdiv{
                border-radius: 0px;
                border: 1px solid #23527c;
                width: 100%;
                padding-bottom:10px;
            }
            .comdivfoot{
                border: 1px solid #ddd;
                margin-top: 5px;
                width: 100%;
            }
            .tablevou{
                border: 1px solid #c4c5c6;
                border-bottom: 0px;
                width: 100%;
            }
            .tableauto{
                border: 1px solid #c4c5c6;
                border-bottom: 0px;
            }
            th {
                text-align: left;
                padding: 5px;
                font-size:11px; 
                border: 1px solid #ddd;
                background-color: #e9e9e9;
                font-family: Verdana, Arial;
            }
            .tablevou td{
                padding: 5px;
                border-bottom: 1px solid #ddd;

            }
            .tablevou tr{
                border: 0px solid #ddd;
            }
            .logoplus{
                display: inline-block;
                width:16px;
                height:15px;
                background-size: auto 15px;
                background-image: url(<c:url value="/finactImages/plus.png"/>);
                background-repeat: no-repeat;
            }
            .logominus{
                display: inline-block;
                width:16px;
                height:15px;
                background-size: auto 15px;
                background-image: url(<c:url value="/finactImages/minus.png"/>);
                background-repeat: no-repeat;
            }
            .tablefooter{
                padding: 10px;
            }
            .head{
                height: 20px;
                background: #23527c;
                font-family: cursive;
                font-size: 13px;
                color: white;
                text-align: center;
            }
            .selectmin{
                width: 50px;

            }

        </style>
        <script lang="javaScript">
            $(document).ready(function () {
//                $("#doiStartDate").datepicker({
//                    dateFormat: 'yy-mm-dd'
//                });
//                $("#doiEndDate").datepicker({
//                    dateFormat: 'yy-mm-dd'
//                });
                $("#bkgStartDate").datepicker({
                    dateFormat: 'yy-mm-dd'
                });
                $("#bkgEndDate").datepicker({
                    dateFormat: 'yy-mm-dd'
                });

                $("#addGrid").on("click", function () {
                    $('#trackingForm').attr("action", "seaExportTrackingGrid.fin");
                    $("#trackingForm").submit();
                });
                
                $("#updateGrid").on("click", function () {
                    $('#trackingForm').attr("action", "seaExportTrackingUpdateGrid.fin");
                    $("#trackingForm").submit();
                });

                $("#shipperPopupBtn").on("click", function () {
                    dialogWindow("#dialog-confirm",
                                "soPopupPartner1.fin?param=SHIPPER",
                                1000, "['middle', 20]", "auto");
                });
                $("#consigneePopupBtn").on("click", function () {
                    dialogWindow("#dialog-confirm",
                                "soPopupPartner1.fin?param=CNEE",
                                1000, "['middle', 20]", "auto");
                });
                $("#destAgentPopupBtn").on("click", function () {
                    dialogWindow("#dialog-confirm",
                                "soPopupPartner1.fin?param=DESTAGENT",
                                1000, "['middle', 20]", "auto");
                });

                $(document).ready(shipperName);
            });

            function populate(code, name, param, address) {
                    if ('SHIPPER' === param) {
                        $("#shipper").val(name);
                        //$("#shipperName").val(name);
                        //$("#shipperContactDetails").val(address);
                    }
//                    if ('ACTCNEE' === param) {
//                        $("#actConsignee").val(code);
//                        $("#actConsigneeName").val(name);
//                    } 
                    else if ('CNEE' === param) {
                        $("#consignee").val(name);
                    } 
//                    else if ('LOADINGAGENT' === param) {
//                        $("#loadingAgentCode").val(code);
//                        $("#loadingAgentName").val(name);
//                        $("#loadingAgentContactDetails").val(address);
//                    } 
                    else if ('DESTAGENT' === param) {
                        $("#destinationAgentName").val(name);
                    }
//                    else if ('NOTIFY' === param) {
//                        $("#soNotify").val(code);
//                        $("#soNotifyName").val(name);
//                        $("#soNotifyContactDetails").val(address);
//                    } else if ('ALSONOTIFY' === param) {
//                        $("#alsoNotify").val(code);
//                        $("#alsoNotifyName").val(name);
//                        $("#alsoNotifyContactDetails").val(address);
//                    } else if ('ORIGINWH' === param) {
//                        $("#originWareHouse").val(code);
//                        $("#originWareHouseName").val(name);
//                        $("#originWareHouseContactDetails").val(address);
//                    }
                    $(".ui-dialog-titlebar-close").click();
                }    
        </script>
    </head>
    <body>
        <div class="main">
            <header>
                Tracking - Sea/Export
            </header>
            <html:form method="post" action="seaExportTrackingResult.fin" commandName="trackingBean" id="trackingForm">
                <table class="tablec">
                    <tr>
                        <td width="50%">
                            <table>
                                <tr>
                                    <td>
                                        <button type="button" class="finbutton" id="addGrid">ADD GRID</button>
                                        <button type="button" class="finbutton" id="updateGrid">EDIT GRID</button>
                                    </td>
                                </tr>
                            </table>
                            <div class="rows">
                                <table class="tablec" id="tbl">
                                    <thead>
                                        <tr>
                                            <th></th>
                                            <th>Sequence</th>
                                            <th>Name</th>

                                        </tr>
                                    </thead>
                                    <c:forEach items="${command.trackingVoList}" var="trackingVo" varStatus="status">
                                        <tr>
                                            <td>
                                                <html:checkbox path="trackingVoList[${status.index}].column3" id="display" value="Y"/>
                                            </td>                                
                                            <td>
                                                <LABEL>${trackingVo.column1}</LABEL>
                                                <html:hidden path="trackingVoList[${status.index}].column1"/>
                                            </td>
                                            <td>
                                                <LABEL>${trackingVo.column2}</LABEL>
                                                <html:hidden path="trackingVoList[${status.index}].column2"/>
                                            </td>

                                        </tr>
                                    </c:forEach>

                                </table>
                            </div>

                        <td>
                            <table>
                                <tr>
                                    <td><label>SHIPPER</label></td>
                                    <td>
                                        <html:input type="text" path="shipper" class="large" id="shipperName"/>

                                        <html:hidden path="formId"/>
                                        <html:hidden path="gridSeq"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td><label>CONSIGNEE</label></td>
                                    <td>
                                        <html:input type="text" path="consignee" class="large"/>
                                        <input type="button" name="consigneePopupBtn" id="consigneePopupBtn" value="..." class="finbutton"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td><label>DEST. AGENT</label></td>
                                    <td>
                                        <html:input type="text" path="destinationAgentName" class="large"/>
                                        <input type="button" name="destAgentPopupBtn" id="destAgentPopupBtn" value="..." class="finbutton"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td><label>NOTIFY</label></td>
                                    <td><html:input type="text" path="soNotify" class="large"/></td>
                                </tr>
                                <tr>
                                    <td><label>C.INV.NO</label></td>
                                    <td><html:input type="text" path="comInvNumber" class="large"/></td>
                                </tr>
                                <tr>
                                    <td><label>POL</label></td>
                                    <td><html:input type="text" path="pol" class="large"/></td>
                                </tr>
                                <tr>
                                    <td><label>POD</label></td>
                                    <td><html:input type="text" path="pod" class="large"/></td>
                                </tr>
                                <tr>
                                    <td><label>VSL/VOY</label></td>
                                    <td><html:input type="text" path="vsl" class="large"/></td>
                                </tr>
                                <tr>
                                    <td><label>BL NO</label></td>
                                    <td><html:input type="text" path="blNumber" class="large"/></td>
                                </tr>
                                <tr>
                                    <td><label>M BL NO</label></td>
                                    <td><html:input type="text" path="mBlNumber" class="large"/></td>
                                </tr>
                                <tr>
                                    <td><label>CONTAINER NO</label></td>
                                    <td><html:input type="text" path="cb.number" class="large"/></td>
                                </tr>
                                <tr>
                                    <td><label>BKG DATE</label></td>
                                    <td>
                                        <html:input type="text" path="bkgStartDate" id="bkgStartDate" class="medium"/>
                                        <html:input type="text" path="bkgEndDate" id="bkgEndDate" class="medium"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td><label>BKG TYPE</label></td>
                                    <td>
                                        <html:select path="bookingType" class="medium">
                                            <html:option value=""></html:option>
                                            <html:option value="FCL">FCL</html:option>
                                            <html:option value="LCL">LCL</html:option>
                                        </html:select>
                                    </td>
                                </tr>
                            </table>
                            <table class="tb2">
                                <tr>
                                    <td><button type="submit" class="finbutton">Search</button></td>
                                    <td><button type="reset" class="finbutton">Reset</button></td>
                                </tr>
                            </table>    
                        </td>
                    </tr>
                </table>


            </html:form>
            <div id="dialog-confirm" title="Sailing Schedule Popup"/>
        </div>
    </body>
</html>
