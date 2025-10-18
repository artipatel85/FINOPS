
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<script src="finactjs/so.js"></script>
<script src="finactjs/jquery.validate.min.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <html:form autocomplete="off" name="soContainerForm" command="containerBean" method="post" id="soContainerForm">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Shipping Order - Container</title>

            <script>
                $(function () {
                    $("#containerSBDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#actualStuffingDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });
                });

                 $(document).ready(function () {
                    jQuery.validator.addMethod("containerNumberRegex", function (value, element) {
                        return this.optional(element) || /^[A-Za-z]{4}[0-9]{7}$/i.test(value);
                    }, '<div class="tool">AAAANNNNNNN format</div>');

                    jQuery.validator.addMethod("invalidNetValue", function (value, element) {
                        if (eval($("#netWeight").val()) > eval($("#weight").val())) {
                            return false;
                         } else {
                            return true;
                         }
                    }, '<div class="tool">* Net Weight can not be greater than Gross.</div>');

                    $("#soContainerForm").validate({
                         rules: {
                             number: {containerNumberRegex: true},
                             netWeight: {invalidNetValue: true}
                         },
                         messages: {

                         }
                     });

                 });

                 function saveContainer() {
                    var rowCount = 1;
                    if($("#soContainerForm").valid()){
                        if ($("#bookingType").val() === 'F' || rowCount === 1) {
                            $.ajax({
                                method:"post",
                                url: "saveSOContainer.fin",
                                data: $('form[name=soContainerForm]').serialize(),
                                success: function (data) {
                                    alert("record saved successfully");
                                    var s = window.opener.location.href;
                                    s = s.replace('soSave.fin','soRetrieve.fin?bookingRefNumber='+$("#bookingRefNumber").val());
                                    window.opener.location.assign("soRetrieve.fin?bookingRefNumber="+$("#bookingRefNumber").val()+"&param="+$("#expImp").val());
                                    window.close();
                                }
                            });

                        } else {
                            alert('LCL Record - Can not create more containers!!!');
                        }
                    }
                }

                function addMarks(){
                    $("#markNumber").val($("#markNumber").val() + "\n" + $("#tmpMarkNumber").val());
                    $("#markDetails").val($("#markDetails").val() + "\n" + $("#tmpMarkDetails").val());
                }


            </script>
            <style>
                .hasDatepicker {
                  z-index: 1500 !important; /* has to be larger than 1050 */
                }
                .ui-datepicker {
                    z-index: 9999 !important;
                }
                .datepicker{ z-index:99999 !important; }
            </style>
        </head>
        <body>
                <div class="comdiv main">
                    <table class="tablec" width="100%">
                        <tr>
                            <td>
                                <label class="required">CONTAINER NO</label>
                                <html:hidden path="lineNumber" id="lineNumber" cssClass="medium"/>
                                <html:hidden path="bookingRefNumber" id="bookingRefNumber" cssClass="medium" />
                                <html:hidden path="blNumber" id="blNumber" cssClass="medium" />
                                <html:hidden path="soNumber" />
                                <html:hidden path="bookingType" />
                                <html:hidden path="expImp" />
                            </td>
                            <td>
                                <html:input path="number" id="number" cssClass="medium"/>
                                <html:select path="size" id="size" cssClass="smallplus">
                                    <html:option value="">Select</html:option>
                                    <html:options items="${command.sizeMap}"/>
                                </html:select>
                                
                            </td>
                            <td>
                                S.BILL NO
                            </td>
                            <td>
                                <html:input path="shippingBillNumber" id="shippingBillNumber" cssClass="medium"/>
                            </td>
                            <td>
                                S.BILL DT
                            </td>
                            <td>
                                <html:input path="shippingBillDate" id="containerSBDate" cssClass="smallplus"/>
                            </td>
                            <td>
                                REMARKS
                            </td>
                            <td>
                                <html:textarea cols="30" rows="2" path="containerRemarks" id="remarks" cssClass="textareaL"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label class="required">B.QTY</label>
                            </td>
                            <td>
                                <html:input path="qty" id="qty" cssClass="medium"/>
                                <html:select path="unit" id="unit" cssClass="smallplus">
                                    <html:option value="">Select</html:option>
                                    <html:options items="${command.unitMap}"/>
                                </html:select>                     
                            </td>
                            <td>
                                <label class="required">B.GR.WT</label>
                            </td>
                            <td>
                                <html:input path="weight" id="weight" cssClass="medium"/>
                            </td>
                            <td>
                                <label class="required">B.NT.WT</label>
                            </td>
                            <td>
                                <html:input path="netWeight" id="netWeight" cssClass="smallplus"/>
                            </td>
                            <td>
                                <label class="required">B.CBM</label>
                            </td>
                            <td>
                                <html:input path="measurement" id="measurement" cssClass="smallplus"/>
                            </td>
                        </tr>
                        <c:if test="${command.bookingType != 'F'}">
                        <tr>
                            <td>
                                A.QTY
                            </td>
                            <td>
                                <html:input path="actualQty" id="qty" cssClass="medium"/>
                                <html:select path="actualUnit" id="unit" cssClass="smallplus">
                                    <html:option value="">Select</html:option>
                                    <html:options items="${command.unitMap}"/>
                                </html:select>                     
                            </td>
                            <td>
                                A.GR.WT
                            </td>
                            <td>
                                <html:input path="actualWeight" id="weight" cssClass="medium"/>
                            </td>
                            <td>
                                A.NT.WT
                            </td>
                            <td>
                                <html:input path="actualNetWeight" id="netWeight" cssClass="smallplus"/>
                            </td>
                            <td>
                                A.CBM
                            </td>
                            <td>
                                <html:input path="actualMeasurement" id="measurement" cssClass="smallplus"/>
                            </td>
                        </tr>
                        </c:if>
                        <tr>
                            <c:if test="${command.bookingType != 'F'}">
                                <td>
                                    ACT. LDG DATE
                                </td>
                                <td>
                                    <html:input path="actualStuffingDate" id="actualStuffingDate" cssClass="medium"/>
                                </td>
                            </c:if>
                            <td>
                                C.SEAL
                            </td>

                            <td>
                                <html:input path="customSealNumber" id="customSealNumber" cssClass="medium"/>
                            </td>
                            <td>
                                L.SEAL
                            </td>
                            <td>
                                <html:input path="lineSealNumber" id="lineSealNumber" cssClass="medium"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan=8>
                                <br><hr><br>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <label>MARKS & NO
                            </td>
                            <td colspan="4">
                                <label>DESCRIPTION
                            </td>

                        </tr>
                        <tr>
                            <td colspan="4">
                                <html:textarea cols="40" rows="10" id="tmpMarkNumber" style="width:350px;height:150px;" path="tmpMarkNumber" oninput="this.value=this.value.toUpperCase()" cssClass="textareaL"/>
                            </td>
                            <td colspan="4">
                                <html:textarea cols="100" rows="10" id="tmpMarkDetails" style="width:450px;height:150px;" oninput="this.value=this.value.toUpperCase()" path="tmpMarkDetails" cssClass="textareaL"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="4">
                                <button type="button" class="finbutton" onclick="addMarks()">ADD MARKS & DESCRIPTION</button>
                            </td>
                            <td colspan="4">

                            </td>

                        </tr>
                        <tr>
                            <td colspan="4">
                                <html:textarea cols="40" rows="14" id="markNumber" style="width:350px;height:200px;" path="markNumber" oninput="this.value=this.value.toUpperCase()" cssClass="textareaL"/>
                            </td>
                            <td colspan="4">
                                <html:textarea cols="100" rows="14" id="markDetails" style="width:450px;height:200px;" oninput="this.value=this.value.toUpperCase()" path="markDetails" cssClass="textareaL"/>
                            </td>
                        </tr>
                    </table>
                    <table class="tablefooter">
                        <tr>
                            <td><button type="button" class="finbutton" onclick="saveContainer()">SAVE</button></td>
                        </tr>
                    </table>
                </div>

        </html:form>
    </body>
</html>
