
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<script src="finactjs/so.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <html:form autocomplete="off" name="clpForm" command="containerBean" method="post" id="clpForm" action="saveCLP.fin">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>JOB</title>
            <style>
                th {
                    text-align: left;
                    padding: 5px;
                    font-size:11px;
                    border: 1px solid #ddd;
                    background-color: #5c5a5a;
                    font-family: Verdana, Arial;
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




                $(document).ready(por);
                $(document).ready(pol);
                $(document).ready(pod);
                $(document).ready(polName);

                $(document).ready(from);
                $(document).ready(dest);
                $(document).ready(upTo);
                $(document).ready(carrierCode);
                $(document).ready(originWareHouse);
                $(document).ready(carrierName);
                $(document).ready(originWareHouseName);
                $(function () {

                    $("#loadPlanDate").datepicker({
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

                    $("#actualLoadingDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });


                    $("#addCargoBtn").on("click", function () {
                        $('#accordion').accordion({
                            active: false,
                            collapsible: true
                        });
                    });


                });

                function doPrint()
                {
                    if (confirm("DO U Wish To Print The Record??"))
                    {
                        window.open('CLPPDF.fin?loadPlanNumber=${containerBean.number}');
                    }
                }

                function searchSO()
                {
                    $('#clpForm').attr("action", "refreshCLP.fin?loadPlanNo=0");
                    $("#clpForm").submit();
                }

            </script>
        </head>
        <body>
            <div class="main">

                <header>
                    Container Load Plan
                </header>
                <table class="table-noborder" width="100%">
                    <c:if test="${soBean.errorMsg != null}">
                        <tr>
                            <th colspan="6"><c:out value="${containerBean.errorMsg}"/></th>
                        </tr>
                    </c:if>
                    <tr>
                        <td width="8%"><label>LOAD PLAN NUMBER</label></td>
                        <td width="29%">
                            <html:input path="number" class="medium readonly" id="clpNumber" readonly="true"/>
                        </td>
                        <td width="8%"><label>DATE</label></td>
                        <td width="29%">
                            <html:input path="loadPlanDate" id="loadPlanDate" class="smallplus"/>
                            <A accessKey=D href="javascript:searchSO()"><image src="images/search.gif"></A>
                        </td>
                    </tr>
                    <tr>
                        <td><label class="required">POL</label></td>
                        <td>

                            <html:input path="polName" class="largeXL" id="polName"/>
                            <html:input path="pol" class="small" id="pol"/>
                        </td>
                        <td><label class="required">POD</label></td>
                        <td>

                            <html:input path="podName" class="largeXL" id="podName"/>
                            <html:input path="pod" class="small" id="pod"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>CARRIER</label></td>
                        <td>

                            <html:input path="carrierName" class="largeXL"/>
                            <html:input path="carrierCode" class="small"/>
                        </td>
                        <td><label class="required">WAREHOUSE</label></td>
                        <td>

                            <html:input path="warehouseName" class="largeXL" id="originWareHouseName"/>
                            <html:input path="warehouse" class="small" id="originWareHouse"/>

                        </td>

                    </tr>

                    <tr>
                        <td><label>VSL</label></td>
                        <td>
                            <html:input path="vsl" class="medium"/>
                        </td>
                        <td><label>VOY</label></td>
                        <td>
                            <html:input path="voy" class="medium"/>
                        </td>

                    </tr>

                    <tr>
                        <td><label>ETD</label></td>
                        <td>
                            <html:input path="etd" class="medium"/>
                        </td>
                        <td><label>ETA</label></td>
                        <td>
                            <html:input path="eta" class="medium"/>
                        </td> 
                    </tr>
                    <tr>
                        <td><label>CONTAINER NO</label></td>
                        <td>
                            <html:input path="containerNo" class="medium"/>
                        </td>
                        <td><label>ACT STUFFING DATE</label></td>
                        <td>
                            <html:input path="actualLoadingDate" class="medium" id="actualLoadingDate"/>
                        </td>

                    </tr>
                    <tr>
                        <td><label>SIZE CODE</label></td>
                        <td>
                            <html:select path="size" id="size" cssClass="smallplus">
                                <html:options items="${command.sizeMap}"/>
                            </html:select>
                        </td>
                        <td><label>SERVICE TERM</label></td>
                        <td>
                            <html:select path="serviceTerm">
                                <html:option value="FCL/FCL"/>
                                <html:option value="FCL/LCL"/>
                                <html:option value="LCL/FCL"/>
                                <html:option value="LCL/LCL"/>
                            </html:select>
                        </td>

                    </tr>
                    <tr>
                        <td><label>L.SEAL NO</label></td>
                        <td>
                            <html:input path="lineSealNumber" class="medium" id="lineSealNumber"/>
                        </td>
                        <td><label>C.SEAL NO</label></td>
                        <td>
                            <html:input path="customSealNumber" class="medium" id="customSealNumber"/>
                        </td>

                    </tr>

                    <tr>
                        <td><label>REMARK</label></td>
                        <td>
                            <html:textarea path="containerRemarks" class="textareaL"/>
                        </td>
                        <td><label>LOAD METHOD</label></td>
                        <td>
                            <html:select path="loadMethod">
                                <html:option value="H">Head Load</html:option>
                                <html:option value="M">Middle Load</html:option>
                                 <html:option value="T">Tail Load</html:option>
                                <html:option value="F">Full Load</html:option>
                                <html:option value="N">NIL</html:option>
                            </html:select>
                        </td>
                    </tr>

                </table>
            </div>
                <div class="main">
                    <table id="docfa" class="display" cellspacing="0" width="100%" style="height:500px; overflow-x: scroll; overflow-y: scroll;display: block; word-break: break-all;">
                        <thead>
                            <tr>
                                <th></th>
                                <th>S/O NUMBER</th>
                                <th>Container NUMBER</th>
                                <th>SHIPPER</th>
                                <th>CONSIGNEE</th>
                                <th>DEST</th>
                                <th>A.QTY</th>
                                <th>A.UNIT</th>
                                <th>A.GR.WT.</th>
                                <th>A.NT.WT.</th>
                                <th>A.CBM</th>
                            </tr>
                        </thead>
                        <c:forEach items="${command.containerBeanList}" var="sor" varStatus="status">
                            <tr>
                                <td>
                                    <html:checkbox path="containerBeanList[${status.index}].uuid" value="${containerBean.number}"/>
                                </td>
                                <td>
                                    <html:input path="containerBeanList[${status.index}].soNumber" class="small readonly" readonly="true" value="${sor.soNumber}"/>
                                </td>
                                 <td>
                                    <html:input path="containerBeanList[${status.index}].number" class="smallplus readonly" readonly="true" value="${sor.number}"/>
                                </td>
                                <td>
                                    <html:input path="containerBeanList[${status.index}].shipperName" class="largeXL" value="${sor.shipperName}"/>
                                </td>
                                <td>
                                    <html:input path="containerBeanList[${status.index}].consigneeName" class="largeXL" value="${sor.consigneeName}"/>
                                </td>
                                <td>
                                    <html:input path="containerBeanList[${status.index}].dest" class="small" value="${sor.dest}"/>
                                </td>
                                <td>
                                    <html:input path="containerBeanList[${status.index}].actualQty" class="small" value="${sor.actualQty}"/>
                                </td>
                                <td>
                                    <html:input path="containerBeanList[${status.index}].actualUnit" class="small" value="${sor.actualUnit}"/>
                                </td>
                                <td>
                                    <html:input path="containerBeanList[${status.index}].actualWeight" class="small" value="${sor.actualWeight}"/>
                                </td>
                                <td>
                                    <html:input path="containerBeanList[${status.index}].actualNetWeight" class="small" value="${sor.actualNetWeight}"/>
                                </td>
                                <td>
                                    <html:input path="containerBeanList[${status.index}].actualMeasurement" class="small" value="${sor.actualMeasurement}"/>
                                </td>
                            </tr>
                        </c:forEach>

                    </table>
                </div>
                <div class="comdivfoot">
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" class="finbutton">SAVE</button></td>
                        <td><button type="button" id="clpPDF" onclick="doPrint()" class="finbutton">PRINT</button></td>
                        <td><button type="reset" class="finbutton">RESET</button></td>
                        <td><button type="submit" class="finbutton">EXIT</button></td>
                    </tr>

                </table>
            </div>   


        </html:form> 
        <div id="dialog-confirm" title="Sailing Schedule Popup"/>
        <div id="dialog-so" title="Shipping Orders"/>
    </body>
</html>
