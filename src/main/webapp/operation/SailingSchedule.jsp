
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/so.js"></script>
<script src="finactjs/jquery.validate.min.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <html:form autocomplete="off" method="post" id="sailingScheduleForm" action="sailingScheduleSave.fin">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <link href="<c:url value="/finactcss/rowForm.css"/>" rel="stylesheet">
            <title>Sailing Schedule</title>

            <script lang="javascript">
                $(document).ready(carrierCode);
                $(document).ready(carrierName);
                $(function () {
                    $("#polETD").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#carrierBtn").on("click", function () {
                        dialogWindow("#dialog-confirm", "popup3View.fin?param=CARRIER",
                                1000, "['middle', 20]", "auto");
                    });

                    $("#connectBySSBtn").on("click", function () {
                        dialogWindow("#dialog-confirm",
                                "connectBySSPopup.fin?param=CONNECTBYSS&ETD=" + $(".polETD").get(0).value+"&POD=" + $(".podCode").get(0).value,
                                1000, "['middle', 20]", "auto");
                    });

                    $(".poldate1").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });
                });
                
                function populateSS(key, value1, value2, value3, value4, value5, value6, value7, value8, value9){
                    var value = value1 +'/'+value2+'/'+value3+'/'+value6+'/'+value7;
                    document.getElementById("connectById").value = key;
                    document.getElementById("connectByValue").value = value;
                    $(".ui-dialog-titlebar-close").click();
                }

                function carrierPopup() {
                    autopopup("#partnerCode", "popup3ViewAuto.fin", function (event, ui) {
                        $("#description").val(ui.item.data);
                    });
                }

                function polPopup(src, idx) {
                    autopopup(src, "autoComplete.fin?param=PORT_0&type=0", function (event, ui) {
                        $('input[id="polDescription"]').get(idx).value = ui.item.data;
                    });
                }

                function podPopup(src, idx) {
                    autopopup(src, "autoComplete.fin?param=PORT_0&type=0", function (event, ui) {
                        $('input[id="podDescription"]').get(idx).value = ui.item.data;
                    });
                }

                $("#sailingScheduleForm").validate({
                    rules: {
                        vesselCode: {required: true},
                        voyageCode: {required: true},
                        partnerCode: {required: true},
                        description: {required: true}
                    },
                    messages: {
                        vesselCode: '<label class="tool">* field is required.</label>',
                        voyageCode: '<label class="tool">* field is required.</label>',
                        partnerCode: '<label class="tool">* field is required.</label>',
                        description: '<label class="tool">* field is required.</label>'
                    }
                });

            </script>
        </head>
        <body>
            <div class="comdiv">

                <header>
                    Sailing Schedule
                </header>
                <table class="tablec" width="100%">
                    <c:if test="${sailingScheduleBean.errorMsg != null}">
                        <tr>
                            <th colspan="4"><c:out value="${sailingScheduleBean.errorMsg}"/></th>
                        </tr>
                    </c:if>
                    <tr>
                        <td width="10%"><label>VSL</label></td>
                        <td width="50%">
                            <html:input path="vesselCode" class="medium"/>
                            <html:hidden path="salingScheduleSeqId" />
                            <html:hidden path="salingScheduleId" />
                        </td>
                        <td width="10%"><label>VOY</label></td>
                        <td><html:input path="voyageCode" class="small"/></td>
                    </tr>
                    <tr>
                        <td><label>CARRIER</label></td>
                        <td>
                            <html:input path="partnerCode" id="carrierCode" class="medium" onkeyup="carrierPopup()"/>
                            <html:input path="description" id="carrierName" class="largeXL"/>
                        </td>
                        <td><label>REMARKS</label></td>
                        <td><html:textarea path="remark" cssClass="textareaL"/></td>
                    </tr>
                </table>
            </div> 

            <table>
                <tr>
                    <td width="70%">        
                        <div class="rows2 rows">
                            <iframe frameborder="0" style="position:absolute;width:1px;"></iframe>
                            <table class="tablevou" id="tbl" >
                                <thead>
                                    <tr>
                                        <th></th>
                                        <th><label>POL</label></th>     
                                        <th><label>CFS DATE</label></th>
                                        <th><label>CY DATE</label></th>
                                        <th><label>ETD</label></th>
                                    </tr>
                                </thead>
                                <c:forEach items="${command.polList}" var="polData" varStatus="status">
                                    <tr>
                                        <td><label>${status.index+1}</label></td>
                                        <td>
                                            <html:input path="polList[${status.index}].value1" onkeyup="polPopup(this,'${status.index}')" value="${polData.value1}" cssClass="small"/>
                                            <html:input path="polList[${status.index}].value2" value="${polData.value2}" cssClass="medium" id="polDescription"/>
                                        </td>
                                        <td>
                                            <html:input path="polList[${status.index}].value3" value="${polData.value3}" cssClass="smallplus poldate1"/>
                                            <html:input path="polList[${status.index}].value4" value="${polData.value4}" cssClass="small"/>
                                        </td>
                                        <td>
                                            <html:input path="polList[${status.index}].value5" value="${polData.value5}" cssClass="smallplus poldate1"/>
                                            <html:input path="polList[${status.index}].value6" value="${polData.value6}" cssClass="small"/>
                                        </td>
                                        <td>
                                            <html:input path="polList[${status.index}].value7" value="${polData.value7}" cssClass="smallplus poldate1 polETD"/>
                                        </td>

                                    </tr>
                                </c:forEach>

                            </table>
                        </div>
                    </td>
                    <td style="vertical-align: top">
                        <div class="rows">
                            <iframe frameborder="0" style="position:absolute;width:1px;"></iframe>
                            <table class="tablevou" id="tbl2" >
                                <thead>
                                    <tr>
                                        <th></th>
                                        <th><label>POD</label></th>     
                                        <th><label>ETA</label></th>
                                    </tr>
                                </thead>
                                <c:forEach items="${command.podList}" var="podData" varStatus="status">
                                    <tr>
                                        <td><label>${status.index+1}</label></td>
                                        <td>
                                            <html:input path="podList[${status.index}].value1" onkeyup="podPopup(this,'${status.index}')" value="${podData.value1}" cssClass="small podCode"/>
                                            <html:input path="podList[${status.index}].value2" id="podDescription" value="${podData.value2}" cssClass="medium"/>
                                        </td>
                                        <td>
                                            <html:input path="podList[${status.index}].value3" value="${podData.value3}" cssClass="smallplus poldate1"/>
                                        </td>                            
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>   
                    </td>
                </tr>                
            </table>
            <c:if test="${sailingScheduleBean.salingScheduleId != null}">
                <table class="tablec">
                    <tr>
                        <td><label>CONNECT TO</label></td>
                        <td>

                        </td>
                        <td>

                        </td>
                    </tr>
                    <tr>
                        <td><html:textarea cols="30" rows="2" path="connectByValue" cssClass="textareaL"/></td>
                        <td><input type="button" name="connectBySSBtn" id="connectBySSBtn" value="..." class="finbutton"/></td>
                        <td>
                            <html:input path="connectById" class="medium readonly" readonly="true"/>
                        </td>

                    </tr>
                </table>
            </c:if>
            <div class="comdivfoot">

                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" class="finbutton">SAVE</button></td>
                        <td><button type="reset" class="finbutton">RESET</button></td>
                    </tr>
                </table>
            </div>   
        </html:form>
        <div id="dialog-confirm" title="Sailing Schedule Popup"/>
    </body>
</html>
