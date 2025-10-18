<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/partner.js"></script>
<!DOCTYPE html>
<html>
    <html:form id="partnerKycForm" autocomplete="off" method="post" commandName="partnerBean" action="updatePartnerAccount.fin" enctype="multipart/form-data">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta http-equiv="Pragma" content="no-cache">
            <meta http-equiv="Expires" content="-1">
            <title>Partner Account</title>
            <script>

                function salesManPopup() {
                    autopopup("#salesManName", "salesman.fin", function (event, ui) {
                        $("#salesManCode").val(ui.item.data);
                    });
                }

                function currencyPopup() {
                    autopopup("#currencyName", "currencyrate.fin", function (event, ui) {
                        $("#currencyId").val(ui.item.code);
                    });
                }
                $(document).ready(function () {
                    $("#salesman2").autocomplete({
                        autoFocus: true,
                        source: function (request, response) {

                            $.getJSON("salesman.fin", {
                                term: request.term

                            }, function (result) {
                                //alert();
                                var wordlist = ($.map(result, function (item) {
                                    //alert(item.param2);
                                    return {value: item.param1, data: item.param2}
                                }));
                                var re = $.ui.autocomplete.escapeRegex(request.term);
                                var matcher = new RegExp("^" + re, "i");
                                var a = $.grep(wordlist, function (item, index) {
                                    return matcher.test(item.value);
                                });
                                response(a);
                            });
                        },
                        select: function (event, ui) {

                            $("#salecode2").val(ui.item.data);
                        }
                    });
                });
                $(function () {
                    $("#replicateBtn").on("click", function () {
                        alert(1);
                        $('#partnerKycForm').attr("action", "replicatePartner.fin");
                        $("#partnerKycForm").submit();
                    });
                    $("#financeBtn").on("click", function () {
                        $('#partnerKycForm').attr("action", "importToFinance.fin");
                        $("#partnerKycForm").submit();
                    });
                });

                $(document).ready(function () {
                    jQuery.validator.addMethod("nameRegex", function (value, element) {
                        return this.optional(element) || /^[a-zA-Z0-9- ]+$/i.test(value);
                    }, "Name should be alpha numeric");

                     jQuery.validator.addMethod("gstinNotValid", function (value, element) {
                        return ($("#gstinNo").val().startsWith($("#stateCode").val()));
                    }, "Invalid GSTIN No");

                    $("#partnerKycForm").validate({

                        rules: {
                            company: {required: true,
                                nameRegex: true
                            },
                            countryCode: {required: true},
                            panNo: {
                                required: function () {
                                    if ($("#countryName").val() === 'INDIA') {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                },
                                minlength: 10
                            },
                            gstinNo: {
                                required: function () {
                                    if ($("#countryName").val() == 'INDIA') {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                },
                                gstinNotValid: true,
                                minlength: 15
                            },
                            tanNo: {
                                required: function () {
                                    if ($("#countryName").val() == 'INDIA') {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            },
                            email: {
                                required: function () {
                                    if ($("#countryName").val() == 'INDIA') {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            },
                            currencyId: {required: true, min: 1000},
                            salesManCode:{
                                required: function () {
                                    if ($("#countryName").val() == 'INDIA') {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            },
                            stateCode: {
                                required: function () {
                                    if ($("#countryName").val() == 'INDIA') {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            },
                            creditPeriod: {
                                min: function () {
                                    if ($("#countryName").val() == 'INDIA') {
                                        return 1;
                                    } else {
                                        return 0;
                                    }
                                },
                                number:true
                            },
                            zipCode: {
                                required: function () {
                                    if ($("#countryName").val() == 'INDIA') {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            }
                        },
                        messages: {
                            company: {required: '<div class="tool">* field is required.</div>',
                                nameRegex: '<div class="tool">* special characters are not allowed.</div>'
                            },
                            countryCode: '<div class="tool">* field is required.</div>',
                            panNo: {required: '<div class="tool">* field is required.</div>',
                                minlength: '<div class="tool">* length should be 10.</div>'
                            },
                            gstinNo: {required: '<div class="tool">* field is required.</div>',
                                minlength: '<div class="tool">* length should be 15.</div>',
                                gstinNotValid:'<div class="tool">* Invalid Gstin No</div>'
                            },
                            currencyId: '<div class="tool">* field is required.</div>',
                            salesManCode: '<div class="tool">* field is required.</div>',
                            stateCode: '<div class="tool">* field is required.</div>',
                            creditPeriod: '<div class="tool">* credit period must be greater than 0.</div>',
                            tanNo: '<div class="tool">* field is required.</div>',
                            email: '<div class="tool">* field is required.</div>',
                            zipCode: '<div class="tool">* field is required.</div>',
                        }

                    });
                });
            </script>
        </head>
        <body>
            <div class="comdiv">
                <header>
                    PARTNER ACCOUNT
                </header>

                <table class="tablec">
                    <col width="160PX">
                    <col width="350PX">
                    <col width="160PX">
                    <col width="350PX">
                    <c:if test="${partnerBean.errorMsg != null}">
                        <tr>
                            <th colspan="4"><label><c:out value="${partnerBean.errorMsg}"/></label></th>
                        </tr>
                    </c:if>

                    <tr>
                        <td><label>Type</label></td>
                        <td colspan="3">
                            <html:checkbox path="clientType" value="Y"/>
                            <label>Client</label>
                            <html:checkbox path="bpType" value="Y"/>
                            <label>Billing Party</label>
                            <html:checkbox path="shprType" value="Y"/>
                            <label>Shpr</label>
                            <html:checkbox path="cneeType" value="Y"/>
                            <label>Cnee</label>
                            <html:checkbox path="agntType" value="Y"/>
                            <label>Agent</label>
                            <html:checkbox path="coloadrType" value="Y"/>
                            <label>Co-loader</label>
                            <html:checkbox path="carrierType" value="Y"/>
                            <label>Carrier</label>
                            <html:checkbox path="cntrctrType" value="Y"/>
                            <label>Sub-contractor</label>

                        </td>
                    </tr>

                    <tr>
                        <td><label>Company Name</label></td>
                        <td>
                            <html:input path="description1" class="largeXL"/>
                            <html:hidden path="id"/>
                            <html:hidden path="action"/>
                            <html:hidden path="partnerAcctCode"/>
                            <html:hidden path="loadingAgentCode"/>
                        </td>
                        <td><label>Partner Code</label></td>
                        <td>
                            ${partnerBean.partnerCode}
                            <html:hidden path="partnerCode"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>Address</label></td>
                        <td>
                            <html:textarea  path="address1" rows="4" cols="30"/>
                        </td>
                        <td><label>City</label></td>
                        <td><html:input path="city" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>State</label></td>
                        <td><html:input path="stateName" class="medium" id="stateName" onkeyup="partnerPopup('STATE_0','#stateName', '#stateCode')"/>
                        <html:input path="stateCode" class="small" id="stateCode"/></td>
                        <td><label>Zip Code</label></td>
                        <td><html:input path="zipCode" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>Country</label></td>
                        <td>
                            <html:input path="countryName" class="medium" onkeyup="partnerPopup('COUNTRY_0','#countryName', '#countryCode')"/>
                            <html:input path="countryCode" class="small" id="countryCode"/>
                        </td>
                        <td><label>Email Id</label></td>
                        <td><html:input path="email" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>Tel No</label></td>
                        <td><html:input path="telCc" class="small"/>
                            <html:input path="telAc" class="small"/>
                            <html:input path="telNo" class="medium"/></td>
                        <td><label>Fax No</label></td>
                        <td><html:input path="faxCc" class="small"/>
                            <html:input path="faxAc" class="small"/>
                            <html:input path="faxNo" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>Credit Period</label></td>
                        <td><html:input path="creditPeriod" class="medium"/></td>
                        <td><label>CT.Person</label></td>
                        <td><html:input path="contactPerson" class="medium"/></td>
                    </tr>

                    <tr>
                        <td><label>PAN No</label></td>
                        <td><html:input path="panNo" class="medium"/></td>
                        <td><label>TAN No</label></td>
                        <td><html:input path="tanNo" class="medium"/></td>
                    </tr>

                    <tr>
                        <td><label>Currency</label></td>
                        <td>
                            <html:input path="currencyCode" class="medium" onkeyup="partnerPopup('CURRENCY_0','#currencyCode', '#currencyId')"/>
                            <html:input path="currencyId" class="small readonly" readonly="true"/>
                        </td>
                        <td><label>CR.Limit</label></td>
                        <td><html:input path="creditLimit" class="medium"/></td>

                    </tr>
                    <tr>
                        <td><label>Bankers Name</label></td>
                        <td><html:input path="bankerName" class="medium"/></td>
                        <td><label>Bank Address</label></td>
                        <td>
                            <html:textarea  path="bankAddress" rows="4" cols="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>Bank City</label></td>
                        <td><html:input path="bankCity" class="medium"/></td>
                        <td><label>Bank Tel No</label></td>
                        <td>
                            <html:input path="bankerTelCC" class="small"/>
                            <html:input path="bankerTelAC" class="small"/>
                            <html:input path="bankerTelNo" class="medium"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>Bank State</label></td>
                        <td><html:input path="bankStateName" class="medium" id="bankStateName" onkeyup="partnerPopup('STATE_0','#bankStateName', '#bankStateCode')"/>
                        <html:input path="bankStateCode" class="small" id="bankStateCode"/></td>

                        <td><label>Bank Zip Code</label></td>
                        <td><html:input path="bankZipCode" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>Bank A/C No</label></td>
                        <td><html:input path="bankAccountNo" class="large"/></td>
                        <td><label>IFSC Code</label></td>
                        <td><html:input path="ifscCode" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>SWIFT Code</label></td>
                        <td><html:input path="swiftCode" class="medium"/></td>
                        <td><label>IEC No</label></td>
                        <td><html:input path="iecNo" class="medium"/></td>
                    </tr>
                    <tr>

                        <td><label>Note</label></td>
                        <td><html:input path="note" class="medium"/></td>
                        <td><label>GSTIN No</label></td>
                        <td><html:input path="gstinNo" class="medium"/></td>
                    </tr>

                    <tr>
                        <td><label>Salesman</label></td>
                        <td>
                            <html:input path="salesManName" onkeyup="partnerPopup('SALESMAN_1','#salesManName', '#salesManCode')" class="medium"/>
                            <html:input path="salesManCode" class="medium"/>
                        </td>
                        <td><label>Update images?</label></td>
                        <td>
                            <html:checkbox path="updateAttachments" value="Y"/>
                        </td>
                    </tr>

                    <tr>
                        <td><label>Disable Print?</label></td>
                        <td>
                            <html:checkbox path="disablePrint" value="Y"/>
                        </td>
                        <td><label>Status</label></td>
                        <td>
                            <html:select path="status">
                                <html:option value="A">Active</html:option>
                                <html:option value="S">Suspend</html:option>
                            </html:select>
                        </td>
                    </tr>

                </table>
                <table class="tablee">
                    <tr>
                        <td>
                            <label for="file1">Attachment 1</label>
                        </td>
                        <td>
                            <input type="file" name="param100" id="param100">
                            <label>
                                <a target="new" href="kycAttachment.fin?kycId=${partnerBean.id}&index=1&partnerName=${partnerBean.companyName}&fileName=${command.attachment1FileName}"><c:out value="${command.attachment1FileName}"/></a>
                            </label>
                        </td>
                        <td>
                            <label for="file1">Attachment 2</label>
                        </td>
                        <td>
                            <input type="file" name="param101" id="param101">
                            <label>
                                <a target="new" href="kycAttachment.fin?kycId=${partnerBean.id}&index=2&partnerName=${partnerBean.companyName}&fileName=${command.attachment2FileName}"><c:out value="${command.attachment2FileName}"/></a>
                            </label>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for="file1">Attachment 3</label>
                        </td>
                        <td>
                            <input type="file" name="param102" id="param102">
                            <a target="new" class="label" href="kycAttachment.fin?kycId=${partnerBean.id}&index=3&partnerName=${partnerBean.companyName}&fileName=${partnerBean.attachment3FileName}"><c:out value="${command.attachment3FileName}"/></a>
                        </td>
                        <td>
                            <label for="file1">Attachment 4</label>
                            <a target="new" class="label" href="kycAttachment.fin?kycId=${partnerBean.id}&index=4&partnerName=${partnerBean.companyName}&fileName=${partnerBean.attachment4FileName}"><c:out value="${command.attachment4FileName}"/></a>
                        </td>
                        <td>
                            <input type="file" name="param103" id="param103">
                        </td>
                    </tr>
                </table>
                <table class="tablefooter">
                    <tr>
                        <c:if test="${partnerBean.partnerCode == null || partnerBean.partnerCode == '' || partnerBean.id == 0}">
                            <td><button type="submit" class="finbutton">Save</button></td>
                            <td><button type="reset" class="finbutton">Reset</button></td>
                        </c:if>
                        <c:if test="${partnerBean.administrator == true}">
                            <td>
                                <html:select path="branch">
                                    <html:option value="SFP">SFP</html:option>
                                    <html:option value="SFPM">SFPM</html:option>
                                    <html:option value="SFPC">SFPC</html:option>
                                    <html:option value="SFPK">SFPK</html:option>
                                    <html:option value="SFPG">SFPG</html:option>
                                </html:select>
                            </td>
                            <td><button type="button" class="finbutton" id="replicateBtn">Replicate</button></td>
                            <c:if test="${command.partnerCode != null && command.partnerAcctCode == 0}">
                                <td>
                                    <html:checkbox path="creditorType" value="Y"/>
                                    <label>Creditor</label>
                                    <html:checkbox path="debtorType" value="Y"/>
                                    <label>Debtor</label>
                                </td>
                                <rbac:rbac formId="1145" pattern="(.)\\|(Y)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)">
                                    <td><button type="button" id="financeBtn" class="finbutton">Import To Finance</button></td>
                                </rbac:rbac>
                            </c:if>
                        </c:if>

                        <td><button type="submit" class="finbutton">Exit</button></td>
                    </tr>
                </table>

            </html:form>
            <div id="dialog-confirm" title="Partner Account Detail"/>

    </body>
</html>
