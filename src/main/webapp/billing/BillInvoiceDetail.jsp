
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/partner.js"></script>
<script src="finactjs/finance.js"></script>
<!DOCTYPE html>
<html>
    <html:form method="post" command="billingBean" id="billingForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta http-equiv="Pragma" content="no-cache">
            <meta http-equiv="Expires" content="-1">
            <title>Bill Invoice Detail</title>
            <style>
                .rows{
                    margin-top: 0px;
                    border-radius: 0px;
                    height: 100%;
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
                    width: 100%;
                    height: 100%;
                    padding-bottom:10px;
                }
                .comdivfoot{
                    border: 1px solid #ddd;
                    margin-top: 5px;
                    width: 100%;
                    height: 100%;
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
                    font-family: system-ui;
                    padding: 5px 2px 5px 2px;
                    border: 1px solid #5c5a5a;
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

                function autoTwo(src, idx, id) {
                    $(src).autocomplete({
                        autoFocus: true,
                        source: function (request, response) {
                            $.getJSON('accountsTwo.fin', {
                                term: request.term
                            }, function (result) {
                                var wordlist = ($.map(result, function (item) {
                                    return {value: item.param2, data: item.param1}
                                }));
                                //alert(wordlist);
                                var re = $.ui.autocomplete.escapeRegex(request.term);
                                var matcher = new RegExp("^" + re, "i");
                                var a = $.grep(wordlist, function (item, index) {
                                    return matcher.test(item.value);
                                });
                                response(a);
                            });
                        },
                        select: function (event, ui) {
                            $('input[id="acOneCode"]').get(idx).value = ui.item.data;
                        }
                    });
                }

                function autoThree(src, id) {
                    $(src).autocomplete({
                        autoFocus: true,
                        source: function (request, response) {
                            $.getJSON('accountsThree.fin', {
                                term: request.term
                            }, function (result) {
                                var wordlist = ($.map(result, function (item) {
                                    return {value: item.param2, data: item.param1}
                                }));
                                //alert(wordlist);
                                var re = $.ui.autocomplete.escapeRegex(request.term);
                                var matcher = new RegExp("^" + re, "i");
                                var a = $.grep(wordlist, function (item, index) {
                                    return matcher.test(item.value);
                                });
                                response(a);
                            });
                        },
                        select: function (event, ui) {
                            $("#acTwoCode").val(ui.item.data);
                        }
                    });
                }

                function autoDesc(src, idx, id) {
                    $(src).autocomplete({
                        autoFocus: true,
                        source: function (request, response) {
                            $.getJSON("description.fin", {
                                term: request.term

                            }, function (result) {

                                var wordlist = ($.map(result, function (item) {
                                    return {value: item.param1, data: item.param2}

                                }));

                                var re = $.ui.autocomplete.escapeRegex(request.term);
                                var matcher = new RegExp("^" + re, "i");
                                var a = $.grep(wordlist, function (item, index) {
                                    return matcher.test(item.value);
                                });

                                response(a);
                                //alert(a);
                            });
                        },
                        select: function (event, ui) {
                            //                        $("#TaxHead").val(ui.item.code);
                            var codeid = 'input[id="TaxHead"]';
                            $(codeid).get(idx).value = ui.item.data;
                        }
                    });
                }

                function multiply(src, index) {
                    var derived = eval(src.value);
                    var ra = document.getElementsByClassName("revenueAmount");
                    ra[index].value = derived;
                    calculateTax();
                }

                $(document).ready(function () {
                    $("#billingForm").validate({
                        rules: {
                            dueDate: {required: true},
                            currencyCode: {required: true},
                            currencyId: {required: true},
                            exchangeRate: {required: true},
                            signatureFile: {required: true}
                        },
                        messages: {
                            dueDate: '<div class="tool">* field is required.</div>',
                            currencyCode: '<div class="tool">* field is required.</div>',
                            currencyId: '<div class="tool">* field is required.</div>',
                            exchangeRate: '<div class="tool">* field is required.</div>',
                            signatureFile: '<div class="tool">* field is required.</div>'
                        }
                    });

                    $("#billDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#dueDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });


                });

                $(document).ready(currency);

                function print()
                {
                    if (confirm("DO U Wish To Print The Record??"))
                    {
                        window.open('billingPDFReport.fin?PARAM=REVENUE&irn=${billingBean.irn}&trxid=${billingBean.billId}');
                    }
                }

                function printEInvoice()
                {
                    if (confirm("DO U Wish To Print The Record??"))
                    {
                        window.open('finReport.do?invoke=billingInvoice&PARAM=REVENUE&EInvoice=Y&trxid=${billingBean.billId}');
                    }
                }

                function calcIndividualTax(amount, taxValue) {
                    var tax = 0;
                    if (eval(taxValue) != 0) {
                        tax = eval(amount) * eval(taxValue) / 100;
                    }
                    //alert(tax);
                    return tax;
                }

                function statePopup() {
                    autopopup("#placeOfSupply", "statecode.fin", function (event, ui) {
                        $("#posCode").val(ui.item.data);
                    });
                }

                function calculateTax() {
                    //alert($(ra).length);
                    var ra = document.getElementsByClassName("revenueAmount");
                    var c6 = document.getElementsByClassName("chkBox6");
                    var t1 = document.getElementsByClassName("t1");
                    var t2 = document.getElementsByClassName("t2");
                    var t3 = document.getElementsByClassName("t3");
                    var t4 = document.getElementsByClassName("t4");
                    var t5 = document.getElementsByClassName("t5");
                    var totalTaxableAmount = 0;
                    var totalNonTaxableAmount = 0;
                    var totalTax1 = 0.0;
                    var totalTax2 = 0.0;
                    var totalTax3 = 0.0;
                    var totalTax4 = 0.0;
                    var totalTax5 = 0.0;

                    $(ra).each(function (index, element) {
                        if (c6[index].checked) {
                            totalNonTaxableAmount = eval(totalNonTaxableAmount) + eval(element.value);
                        } else {
                            totalTaxableAmount = eval(totalTaxableAmount) + eval(element.value);
                            totalTax1 = eval(totalTax1) + calcIndividualTax(element.value, t1[index].value);
                            totalTax2 = eval(totalTax2) + calcIndividualTax(element.value, t2[index].value);
                            totalTax3 = eval(totalTax3) + calcIndividualTax(element.value, t3[index].value);
                            totalTax4 = eval(totalTax4) + calcIndividualTax(element.value, t4[index].value);
                            totalTax5 = eval(totalTax5) + calcIndividualTax(element.value, t5[index].value);
                        }
                    });
                    var ta = document.getElementsByClassName("ta");
                    ta[0].value = totalTax1;
                    ta[1].value = totalTax2;
                    ta[2].value = totalTax3;
                    ta[3].value = totalTax4;
                    ta[4].value = totalTax5;
                    $("#totalTax").val(totalTax1 + totalTax2 + totalTax3 + totalTax4 + totalTax5);
                    $("#totalTaxable").val(totalTaxableAmount);
                    $("#totalNonTaxable").val(totalNonTaxableAmount);
                    $("#grandTotal").val(eval($("#totalTax").val()) + totalTaxableAmount
                            + totalNonTaxableAmount - eval($("#discountAmount").val()));

                }

                function ShowLoading(e) {
                    calculateTax();
                    if($("#grandTotal").val() == 0){
                        alert("0 Value Invoice Not Allowed");
                        return false;
                    }

                    if ($("#billingForm").valid()) {
                        $("#saveInvoice").attr("disabled", true);
                        var div = document.createElement('div');
                        var img = document.createElement('img');
                        img.src = 'loading_bar.GIF';
                        div.innerHTML = "<b><u><i><label>Please wait while transaction is being processed...</label></i></u></b><br />";
                        div.style.cssText = 'position: fixed; top: 95%; left: 2%; z-index: 5000; width: 922px;height:800px; text-align: left;';
                        div.appendChild(img);
                        document.body.appendChild(div);
                        $('#billingForm').attr("action", "saveBill.fin");
                        $("#billingForm").submit();
                        return true;
                    }
                }

                function createInvoice() {
                    calculateTax();
                    if ($("#billingForm").valid()) {
                        $("#genInvoice").attr("disabled", true);
                        $("#generateInvoice").val("true");
                        var div = document.createElement('div');
                        var img = document.createElement('img');
                        img.src = 'loading_bar.GIF';
                        div.innerHTML = "<b><u><i><label>Please wait while transaction is being processed...</label></i></u></b><br />";
                        div.style.cssText = 'position: fixed; top: 95%; left: 2%; z-index: 5000; width: 922px;height:800px; text-align: left;';
                        div.appendChild(img);
                        document.body.appendChild(div);
                        $('#billingForm').attr("action", "saveBill.fin");
                        $("#billingForm").submit();
                        return true;
                    }
                }

                function createIRN() {
                    $('#billingForm').attr("action", "createIRN.fin");
                    $("#billingForm").submit();
                }

                function cancelIRN() {
                    $('#billingForm').attr("action", "cancelIRN.fin");
                    $("#billingForm").submit();
                }

                function deleteCancelledInvoice() {
                    $('#billingForm').attr("action", "deleteCancelledInvoice.fin");
                    $("#billingForm").submit();
                }
            </script>
        </head>
        <body>
            <div class="comdiv">

                <header>
                    <a href="billTemplateSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
                    <c:out value="${billingBean.billType}"/> - Bl No :: <c:out value="${command.blNo}"/> ::
                    <c:out value="${billingBean.seaAir}"/> / <c:out value="${billingBean.localForeign}"/>
                    / <c:out value="${billingBean.expImp}"/> / <c:out value="${billingBean.revExp}"/>
                / <c:out value="${billingBean.templateType}"/> :: IRN - <c:out value="${billingBean.irn}"/>
                    <html:hidden cssClass="normalTextBox" path="saveToken"/>
                    <html:input cssClass="normalTextBox" path="irn"/>
                </header>
                <table class="tablec">
                    <c:if test="${billingBean.errMsg != null}">
                        <tr>
                            <th colspan="8"><c:out value="${billingBean.errMsg}"/></th>
                        </tr>
                    </c:if>

                    <tr>
                        <td><label>BILL NO</label></td>
                        <td>
                            <html:input path="billNo" class="medium"/>
                            <html:input path="billId" class="small readonly"/>
                        </td>
                        <td><label class="required">BILL DATE</label></td>
                        <td><html:input path="billDate" class="smallplus" id="billDate"/></td>
                        <td><label class="required">DUE DATE</label></td>
                        <td><html:input path="dueDate" class="smallplus" id="dueDate"/></td>
                        <td><label>CBM</label></td>
                        <td><html:input path="actCbm" class="smallplus rightAlign readonly"/></td>
                    </tr>
                    <tr>
                        <td><label>BILL TO</label></td>
                        <td>
                            <h3>${billingBean.billtoName}</h3>
                        </td>
                        <td><h3>${billingBean.billTo}</h3></td>
                        <td width="25%"><h3>${billingBean.billtoContctDtls}</h3></td>
                        <td><label>BL DATE</label></td>
                        <td><html:input path="awbBlDate" class="smallplus rightAlign readonly"/></td>
                        <td><label>JOB NUMBER</label></td>
                        <td><html:input path="jobNumber" class="smallplus rightAlign readonly"/></td>

                    </tr>
                    <tr>
                        <td><label>PKGS</label></td>
                        <td><html:input path="noOfPkgs" class="smallplus rightAlign readonly"/></td>
                        <td><label>CONTAINER NO</label></td>
                        <td><html:textarea path="contnrNo" class="textareaL"/></td>
                        <td><label>GROSS WEIGHT</label></td>
                        <td><html:input path="actGrossWt" class="smallplus rightAlign readonly"/></td>
                        <td><label>POL</label></td>
                        <td><html:input path="pol" class="smallplus rightAlign readonly"/></td>
                    </tr>
                    <tr>
                        <td><label>POD</label></td>
                        <td><html:input path="pod" class="smallplus rightAlign readonly"/></td>
                        <td><label>CARRIER</label></td>
                        <td><html:input path="carrierCode" class="small"/></td>
                        <td><label>SHPR </label><html:input path="shpr" class="small"/></td>
                        <td><html:input path="shprName" class="large"/></td>
                        <td><label>CNEE </label><html:input path="cnee" class="small"/></td>
                        <td><html:input path="cneeName" class="large"/></td>
                    </tr>
                    <tr>
                        <td><label>GSTIN NO</label></td>
                        <td>
                            <html:input path="partyGstin" class="medium readonly"/>
                        </td>
                        <td><label>SB NO/INV NO</label></td>
                        <td>
                            <html:input path="invSbNo" class="medium"/>

                            <html:input path="invNo" class="medium"/>
                            <html:hidden path="proforma" class="medium"/>
                            <html:hidden path="generateInvoice" id="generateInvoice" class="medium" value="false"/>
                        </td>
                        <td><label class="required">CURRENCY</label></td>
                        <td>
                            <html:input path="currencyCode" id="currencyName" class="small"/>
                            <html:input path="currencyId" id="currencyId" class="small readonly" readonly="true"/>
                        </td>
                        <td><label class="required">EXCHANGE RATE</label></td>
                        <td><html:input path="exchangeRate" id="rate" type="text" class="smallplus rightAlign readonly" readonly="true"/></td>
                    </tr>
                    <tr>
                        <td><label>RCM</label></td>
                        <td><html:checkbox path="rcm" value="Y"/></td>
                        <td><label>PLACE OF SUPPLY</label></td>
                        <td><html:input path="placeOfSupply" class="medium" onkeyup="partnerPopup('STATE_0','#placeOfSupply', '#posCode')"/><html:input path="posCode" class="small"/></td>
                        <td><label>MAIN INVOICE NO</label></td>
                        <td><html:input path="mainInvoiceNo" class="medium"/></td>
                        <td><label class="required">Authorised Signatory</label></td>
                        <td>
                            <html:select path="signatureFile" class="medium">
                                <html:option value=""/>
                                <html:options items="${billingBean.signatureMap}"/>
                            </html:select>
                        </td>
                    </tr>
                </table>
            </div>
            <div id="accordion">
                            <h3>REVENUE HEAD</h3>
                            <div class="rows">
                                <iframe frameborder="0" style="position:absolute;width:1px;height:50px"></iframe>
                                <table class="tablevou" id="tbl" width="300" height="50">
                                    <thead>
                                        <tr>
                                            <th>DESCRIPTION</th>
                                            <th>SAC</th>
                                            <th>ADDL.DESC.</th>
                                            <th>TAXABLE</th>
                                            <th>N</th>
                                            <th>AMOUNT</th>
                                        </tr>
                                    </thead>
                                    <c:forEach items="${command.billTemplateRows}" var="billTemplateRow" varStatus="status">
                                        <tr>
                                            <td>
                                                <html:hidden path="billTemplateRows[${status.index}].acctName" class="smallM readonly" readonly="true" value="${billTemplateRow.acctName}"/>
                                                <html:input path="billTemplateRows[${status.index}].codeCombinationId" class="smallM readonly" id="acZeroCode" readonly="true" value="${billTemplateRow.codeCombinationId}"/>
                                                <html:input path="billTemplateRows[${status.index}].codeDesc" class="large" value="${billTemplateRow.codeDesc}"/>

                                            </td>
                                            <td>
                                                <html:input path="billTemplateRows[${status.index}].sacCode" class="medium" value="${billTemplateRow.sacCode}"/>
                                            </td>
                                            <td>
                                                <html:textarea path="billTemplateRows[${status.index}].additionalDesc" onchange="multiply(this, ${status.index})" class="medium" value="${billTemplateRow.additionalDesc}"/>
                                            </td>
                                            <td>
                                                <html:input path="billTemplateRows[${status.index}].tax1Per" class="smallM rightAlign t1 readonly" value="${billTemplateRow.tax1Per}" readonly="true"/>
                                                <html:input path="billTemplateRows[${status.index}].tax2Per" class="smallM rightAlign t2 readonly" value="${billTemplateRow.tax2Per}" readonly="true"/>
                                                <html:input path="billTemplateRows[${status.index}].tax3Per" class="smallM rightAlign t3 readonly" value="${billTemplateRow.tax3Per}" readonly="true"/>
                                                <html:input path="billTemplateRows[${status.index}].tax4Per" class="smallM rightAlign t4 readonly" value="${billTemplateRow.tax4Per}" readonly="true"/>
                                                <html:input path="billTemplateRows[${status.index}].tax5Per" class="smallM rightAlign t5 readonly" value="${billTemplateRow.tax5Per}" readonly="true"/>
                                            </td>
                                            <td><html:checkbox path="billTemplateRows[${status.index}].chkBox6" value="6" class="chkBox6"/></td>
                                            <td><html:input path="billTemplateRows[${status.index}].amount" class="smallplus rightAlign revenueAmount" value="${billTemplateRow.amount}" id="revenueAmount" onchange="calculateTax()"/></td>
                                        </tr>
                                    </c:forEach>
                                    <tr>
                                        <td colspan="5" class="rightAlign">
                                            Total Taxable
                                        </td>
                                        <td>
                                            <html:input path="totalTaxable" class="smallplus rightAlign" id="totalTaxable"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="5" class="rightAlign">
                                            Total Non-Taxable
                                        </td>
                                        <td>
                                            <html:input path="totalNonTaxable" class="smallplus rightAlign" id="totalNonTaxable"/>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </div>
            <div class="rows2">
                <header class="header">
                    TAX HEAD
                </header>
                <table class="tableauto">
                    <thead>
                        <tr>
                            <th></th>
                            <th>ACCOUNT NAME</th>
                            <th>DESCRIPTION</th>
                            <th>% TAX</th>
                            <th>ON VALUE</th>
                            <th>TAX AMOUNT</th>
                        </tr>
                    </thead>
                    <c:forEach items="${command.billTemplateTaxs}" var="billTemplateRow" varStatus="status">
                        <tbody>
                            <tr>
                                <td>
                                    <html:select path="billTemplateTaxs[${status.index}].taxDrCrArr" class="selectmin">
                            <option value="CR">CR</option>
                            <option value="DR">DR</option>
                        </html:select>
                        </td>
                        <td><html:input path="billTemplateTaxs[${status.index}].acctName" value="${billTemplateRow.acctName}" type="text" id="acOneName" class="largeXL" onkeyup="autoTwo(this, '${status.index}', 0)"/>
                            <html:input path="billTemplateTaxs[${status.index}].codeCombinationId" value="${billTemplateRow.codeCombinationId}" type="text" id="acOneCode" class="smallM readonly" readonly="true"/></td>
                        <td><html:input path="billTemplateTaxs[${status.index}].codeDesc" id="DescArr"  value="${billTemplateRow.codeDesc}" type="text" class="large" onkeyup="autoDesc(this, '${status.index}', 0)"/></td>
                        <td><html:input path="billTemplateTaxs[${status.index}].taxPercentageArr" id="TaxHead" value="${billTemplateRow.taxPercentageArr}" type="text" class="medium rightAlign"/></td>
                        <td><html:input path="billTemplateTaxs[${status.index}].taxOnValueArr" value="${billTemplateRow.taxOnValueArr}" type="text" class="medium rightAlign"/></td>
                        <td><html:input path="billTemplateTaxs[${status.index}].amount" value="${billTemplateRow.amount}" class="smallplus rightAlign ta"/></td>
                        </tr>
                        </tbody>
                    </c:forEach>
                    <tr>
                        <td colspan="4" class="rightAlign">
                            <button type="button" class="finbutton" onclick="calculateTax()">CALCULATE</button></td>
                        </td>
                        <td>
                            <label>Total Tax</label>
                        </td>
                        <td>
                            <html:input path="totalTax" class="smallplus rightAlign" id="totalTax"/>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="rows2">
                            <header class="header">
                                DISCOUNT HEAD
                            </header>
                            <table class="tableauto">
                                <thead>
                                    <tr>
                                        <th>ACCOUNT NAME</th>
                                        <th>DESCRIPTION</th>
                                        <th>% TAX</th>
                                        <th>ON VALUE</th>
                                        <th>AMOUNT</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><html:input path="discountAcctName" type="text" id="acTwoName" class="largeXL"  onkeyup="autoThree(this, 0)"/>
                                            <html:input path="discountAcctCode" type="text" id="acTwoCode" readonly="true" class="smallM readonly"/></td>
                                        <td><html:input path="discountDesc" type="text" class="large"/></td>
                                        <td><html:input path="discountPercentage" type="text" class="medium rightAlign"/></td>
                                        <td><html:input path="discountOnValue" type="text" class="medium rightAlign"/></td>
                                        <td><html:input path="discountAmount" type="text" class="smallplus rightAlign" id="discountAmount"/></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <div class="rows2">
                            <header class="header">
                                OTHER CHARGES
                            </header>
                            <table class="tableauto">
                                <thead>
                                    <tr>
                                        <th>ACCOUNT NAME</th>
                                        <th>DESCRIPTION</th>
                                        <th>% TAX</th>
                                        <th>ON VALUE</th>
                                        <th>AMOUNT</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><html:input type="text" path="otherChargesAcctName" class="largeXL" id="acThreeName"/>
                                            <html:input type="text" path="otherChargesAcctCode" id="acThreeCode" readonly="true" class="smallM readonly"/></td>
                                        <td><html:input type="text" path="otherChargesDesc" class="large"/></td>
                                        <td><html:input type="text" path="otherChargesPercentage" class="medium rightAlign"/></td>
                                        <td><html:input type="text" path="otherChargesOnValue" class="medium rightAlign"/></td>
                                        <td><html:input type="text" path="otherChargesAmount" class="smallplus rightAlign"/></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <div>
                            <table class="tablec">
                                <col width="260PX">
                                <col width="200PX">
                                <col width="300PX">
                                <col width="200PX">

                                <tr>
                                    <td><label>Remarks</label></td>
                                    <td><html:textarea path="remarks" class="textareaL"/></td>
                                    <td><label>Grand Total</label></td>
                                    <td>
                                        <html:input path="trxTotal" id="grandTotal" class="smallplus rightAlign"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td><label class="required">Sales By</label></td>
                                    <td>
                                        <html:input path="salesBy" onkeyup="partnerPopup('SALESMAN_1','#salesBy', '#salesManCode')" class="medium"/>
                                        <html:hidden path="salesManCode" class="medium"/>
                                    </td>
                                    <td><label class="required">Prepared By</label></td>
                                    <td>
                                        <html:input path="preparedBy" class="smallplus"/>
                                    </td>
                                </tr>

                            </table>
                        </div>
            <table class="tb2">
                            <tr>

                                <c:if test="${billingBean.billId > 0 && billingBean.irn == null}">
                                        <rbac:rbac formId="1142" pattern="(.)\\|(.)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)">
                                        <td>
                                            <button type="button" class="finbutton" id="saveInvoice" onclick="ShowLoading()">Calculate & Save</button>
                                        </td>
                                        </rbac:rbac>
                                    <td><input type="button" class="finbutton" value="Print" onclick="print()"></td>
                                    </c:if>
                                    <c:if test="${billingBean.billId == 0}">
                                        <rbac:rbac formId="1142" pattern="(.)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)\\|(.)">
                                        <td>
                                            <button type="button" class="finbutton" id="saveInvoice" onclick="ShowLoading()">Calculate & Save</button>
                                        </td>
                                    </rbac:rbac>
                                </c:if>

                                <rbac:rbac formId="9942" pattern="(.)\\|(Y)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)">
                                    <c:if test="${(billingBean.irn == null || billingBean.irn == '') && billingBean.billId > 0}">
                                        <c:if test="${billingBean.billType != 'BOS'}">
                                            <td>
                                                <button type="button" class="finbutton" id="genIRN" onclick="createIRN()">Generate IRN</button>
                                            </td>
                                        </c:if>
                                    </c:if>
                                    <c:if test="${billingBean.irn != null && billingBean.irn != '' && billingBean.irnAction != 'CANCEL'}">
                                        <td>
                                            <button type="button" class="finbutton" id="cancIRN" onclick="cancelIRN()">Cancel IRN</button>
                                        </td>
                                    </c:if>
                                    <c:if test="${billingBean.irn != null && billingBean.irnAction == 'CANCEL'}">
                                        <td>
                                            <button type="button" class="finbutton" id="deleteIRN" onclick="deleteCancelledInvoice()">Delete</button>
                                        </td>
                                    </c:if>
                                </rbac:rbac>
                                <rbac:rbac formId="1142" pattern="(Y)\\|(.)\\|(.)\\|(.)\\|(Y)\\|(.)\\|(.)">
                                    <c:if test="${billingBean.irn != null && billingBean.irnAction != 'CANCEL'}">
                                        <td><input type="button" class="finbutton" value="PrintEInvoice" onclick="print()"></td>
                                        </c:if>
                                    </rbac:rbac>
                            </tr>
                        </table>
        </html:form>
    </body>
</html>