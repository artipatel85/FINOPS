
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<script src="finactjs/so.js"></script>
<script src="finactjs/jquery.validate.min.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <html:form autocomplete="off" name="siContainerForm" command="containerBean" method="post" id="siContainerForm">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Shipping Instruction - Container</title>

            <script>
                $(function () {
                    $("#containerSBDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });
                });

                 $(document).ready(function () {
                    jQuery.validator.addMethod("containerNumberRegex", function (value, element) {
                        return this.optional(element) || /^[A-Za-z]{4}[0-9]{7}$/i.test(value);
                    }, '<div class="tool">AAAANNNNNNN format</div>');


                 });

                 function calculateDimension(i){
                    var bcbm = 0;
                    var acbm = 0;
                    var qty1 = 0;
                    var qty2 = 0;
                    var DIMENSION = 6000;
                    var sumB = 0, sumA = 0;
                    for(var index=0; index<8; index++){
                        if(document.getElementsByName('sequence[]')[index].checked){
                            var bl = document.getElementsByName('blength[]')[index].value;
                            var bw = document.getElementsByName('bwidth[]')[index].value;
                            var bh = document.getElementsByName('bheight[]')[index].value;
                            var bqty = document.getElementsByName('bqty[]')[index].value;

                            var btotal = eval(bl) * eval(bw) * eval(bh) * eval(bqty);
                            sumB = sumB + eval(btotal);
                            btotal = eval(format_number(btotal/1000000, 3));
                            document.getElementsByName('btotal[]')[index].value = eval(btotal);
                            bcbm = bcbm + btotal;
                            qty1 = qty1 + eval(bqty);

                            var l = document.getElementsByName('alength[]')[index].value;
                            var w = document.getElementsByName('width[]')[index].value;
                            var h = document.getElementsByName('height[]')[index].value;
                            var qty = document.getElementsByName('qty[]')[index].value;

                            total = eval(l) * eval(w) * eval(h) * eval(qty);
                            sumA = sumA + eval(total);
                            total = eval(format_number(total/1000000, 3));
                            document.getElementsByName('total[]')[index].value = total;
                            acbm = acbm + total;
                            qty2 = qty2 + eval(bqty);
                        }
                    }
                    var vKgs = format_number((sumA / DIMENSION), 0);
                    var kgs = $("#weight").val();
                    //alert(kgs);

                    document.getElementById('qtyB').value = qty1;
                    document.getElementById('qtyA').value = qty1;
                    document.getElementById('measurementB').value = bcbm;
                    document.getElementById('measurementA').value = acbm;
                    document.getElementById('volumeWeight').value = vKgs;

                    if(vKgs > kgs){
                        document.getElementById('chargeableWeight').value = vKgs;
                    }
                    else{
                        document.getElementById('chargeableWeight').value = kgs;
                    }
                 }

                 function format_number(number, decimalplace)
                 {
                     var x=0.0, y=0;
                     var d = parseFloat(number) + 0.000005;
                     y=Math.pow (10, decimalplace);
                     x=parseFloat(d) * y;  //increase the decimal place digits
                     x=Math.round (x);
                     d = x / y;   //back to a number with normal digits
                     return d;
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
                <div>
                    <table class="tablec">
                        <TR>
                            <TD class=title1>&nbsp;</TD>
                            <TD class=title1>&nbsp;</TD>
                            <TD class=title1><label>L X W X H x B.qty = B.Cbm</label></TD>
                            <TD class=title1><label>L X W X H x Qty = A.Cbm</label></TD>
                        </TR>
                        <c:forEach begin="0" end="7" varStatus="loop">
                            <TR>
                                <TD>${loop.index + 1}</TD>
                                <TD><input type="checkbox" value="1" name="sequence[]"></TD>
                                <TD>
                                    <input type="text" maxLength="12" size="8" name="blength[]"> x
                                    <input maxLength="12" size="8" name="bwidth[]"> x
                                    <input maxLength="12" size="8" name="bheight[]"> x
                                    <input maxLength="8" size="8" name="bqty[]"> =
                                    <input disabled readOnly size=8 name="btotal[]">
                                </TD>
                                <TD>
                                    <input maxLength="12" size="8" name="alength[]"> x
                                    <input maxLength="12" size="8" name="width[]"> x
                                    <input maxLength="12" size="8" name="height[]"> x
                                    <input maxLength="8" size="8" name="qty[]"> =
                                    <input disabled readOnly size="8" name="total[]">
                                        <A onmouseover="window.status='Calculate Dimension'; return true;"
                                        onmouseout="window.status=''; return true;" href="javascript:calculateDimension('${loop.index}')">
                                        <IMG alt="Calculate Dimension" src="images/calculator.gif" border=0></A>
                                </TD>
                            </TR>
                        </c:forEach>
                    </table>
                </div>
                <div class="comdiv main">
                    <table class="tablec">

                        <tr>
                            <td>
                                <label class="required">B.QTY</label>
                            </td>
                            <td>
                                <html:input path="qty" id="qtyB" cssClass="medium"/>
                                <html:select path="unit" id="unit" cssClass="smallplus">
                                    <html:options items="${command.unitMap}"/>
                                </html:select>
                                <html:hidden path="action"/>
                                <html:hidden path="bookingRefNumber" id="bookingRefNumber" cssClass="medium" />
                                <html:hidden path="soNumber" />
                            </td>
                            <td>
                                <label class="required">B.GR.WT</label>
                            </td>
                            <td>
                                <html:input path="weight" id="weight" cssClass="medium"/>
                            </td>
                            <td>
                                <label class="required">B.CBM</label>
                            </td>
                            <td>
                                <html:input path="measurement" id="measurementB" cssClass="smallplus"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                A.QTY
                            </td>
                            <td>
                                <html:input path="actualQty" id="qtyA" cssClass="medium"/>
                                <html:select path="actualUnit" id="unit" cssClass="smallplus">
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
                                A.CBM
                            </td>
                            <td>
                                <html:input path="actualMeasurement" id="measurementA" cssClass="smallplus"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                VOLUME WEIGHT
                            </td>
                            <td>
                                <html:input path="volumeWeight" id="volumeWeight" cssClass="medium"/>
                            </td>
                            <td>
                                CHARGEABLE WEIGHT
                            </td>
                            <td>
                                <html:input path="chargeableWeight" id="chargeableWeight" cssClass="smallplus"/>
                            </td>
                        </tr>
                        <tr>
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
                        </tr>
                        <tr>
                            <td colspan=8>
                                <br><hr><br>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                MARKS & NO 
                            </td>
                            <td colspan="3">
                                <html:textarea cols="40" rows="30" id="markNumber" path="markNumber" cssClass="textareaL"/>
                            </td>
                            <td>
                                DESCRIPTION 
                            </td>
                            <td colspan="3">
                                <html:textarea cols="60" rows="30" id="markDetails" path="markDetails" cssClass="textareaL"/>
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
