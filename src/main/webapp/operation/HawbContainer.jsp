
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<script src="finactjs/so.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <html:form autocomplete="off" name="hawbContainerForm" command="containerBean" method="post" id="hawbContainerForm">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>Shipping Order - Container</title>

            <script>
                $(function () {
                //alert($("#containerSBDate"));
                    $("#containerSBDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });
                });

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
                                HAWB NO
                            </td>
                            <td>
                                <label>${command.blNumber}</label>
                            </td>
                            <td>
                                BOOKING REF.
                                <html:hidden path="lineNumber" id="lineNumber" cssClass="medium"/>
                                <html:hidden path="bookingRefNumber" id="bookingRefNumber" cssClass="medium" />
                                <html:hidden path="blNumber" id="blNumber" cssClass="medium" />
                            </td>
                            <td>
                                <label>${command.bookingRefNumber}</label>
                            </td>
                            <td>SERVICE TERM</td>
                            <td>
                                <html:select path="serviceTerm">
                                    <html:option value=""/>
                                    <html:option value="FCL/FCL"/>
                                    <html:option value="FCL/LCL"/>
                                    <html:option value="LCL/FCL"/>
                                    <html:option value="LCL/LCL"/>
                                </html:select>
                            </td>

                        </tr>
                        <tr>
                            <td>
                                QTY
                            </td>
                            <td>
                                <html:input path="qty" id="qty" cssClass="medium"/>
                                <html:select path="unit" id="unit" cssClass="smallplus">
                                    <html:options items="${command.unitMap}"/>
                                </html:select>
                            </td>
                            <td>
                                KGS
                            </td>
                            <td>
                                <label>${command.actualWeight}</label>
                            </td>

                            <td>
                                CBM
                            </td>
                            <td>
                                <html:input path="measurement" id="measurement" cssClass="smallplus"/>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                VOLUME WEIGHT
                            </td>
                            <td>
                                <label>${command.volumeWeight}</label>
                            </td>
                            <td>
                                CHARGEABLE WT.
                            </td>
                            <td>
                                <label>${command.chargeableWeight}</label>
                            </td>
                            <td>
                                REMARKS
                            </td>
                            <td>
                                <label>${command.containerRemarks}</label>
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
                            <td><button type="button" class="finbutton" onclick="exitContainer()">EXIT</button></td>
                        </tr>
                    </table>
            </div>

        </html:form>
    </body>
</html>
