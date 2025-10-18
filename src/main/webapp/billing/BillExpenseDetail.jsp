<%-- 
    Document   : BillTemplate
    Created on : Oct 14, 2017, 3:28:37 PM
    Author     : BirenDesai
--%>
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/finance.js"></script>
<script src="finactjs/partner.js"></script>
<!DOCTYPE html>
<html>
    <html:form method="post" command="billingBean" id="billingForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta http-equiv="Pragma" content="no-cache">
            <meta http-equiv="Expires" content="-1">
            <title>Bill Expense Detail</title>
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
                    border: 1px solid #23527c;
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
                    text-align: center;
                    margin: 1px 0 0 0;
                    font-size: 13px;
                    background-color: #23527c;
                    margin-left: 0px;
                    margin-right: 0px;
                    color: white;
                    margin-bottom: 5px;
                    font-family: cursive;
                    padding: 2px 2px 2px 2px;
                    border-top: 1px solid #23527c;
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


                    $("#expenseDetailSOBtn").on("click", function () {
                        window.open(encodeURI('soPopupExpense.fin?soNumber=' + $("#soNo").val()));
                    });

                });

                $(document).ready(currency);

                function autoDesc(src, idx, id) {
                    $(src).autocomplete({
                        autoFocus: true,
                        source: function (request, response) {
                            $.getJSON("autoComplete.fin?param=TAXMASTER_0&type=-1", {
                                term: request.term

                            }, function (result) {

                                var wordlist = ($.map(result, function (item) {
                                    return {value: item.value, data: item.param2,
                                        taxpercentage: item.param3, taxcode: item.param4}

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
                            $('input[id="tdsDescription"]').get(idx).value = ui.item.data;
                            $('input[id="tdsPercentage"]').get(idx).value = ui.item.taxpercentage;
                            $('input[id="tdsCode"]').get(idx).value = ui.item.taxcode;
                        }
                    });
                }

                $(document).ready(function () {
                    $("#billingForm").validate({
                        rules: {
                            dueDate: {required: true},
                            invSbNo: {required: true},
                            invDate: {required: true},
                            currencyName: {required: true},
                            currencyId: {required: true},
                            exchangeRate: {required: true},
                            irn: {minlength: 64, maxlength: 64}
                        },
                        messages: {
                            dueDate: '<div class="tool">* field is required.</div>',
                            invSbNo: '<div class="tool">* field is required.</div>',
                            invDate: '<div class="tool">* field is required.</div>',
                            currencyName: '<div class="tool">* field is required.</div>',
                            currencyId: '<div class="tool">* field is required.</div>',
                            exchangeRate: '<div class="tool">* field is required.</div>',
                            irn: '<div class="tool">* Length must be 64.</div>'
                        }
                    });

                    $("#billDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });
                    //$("#billDate").datepicker('setDate', new Date());
                    $("#dueDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#invDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#soNo").autocomplete({
                        autoFocus: true,
                        source: function (request, response) {
                            $.getJSON("soExpensePopup.fin", {
                                term: request.term,
                                blNo: $("#blNo").val(),
                                exportImport: '<c:out value="${billingBean.expImp}"/>',
                                seaAir: '<c:out value="${billingBean.seaAir}"/>'
                            }, function (result) {
                                var wordlist = ($.map(result, function (item) {
                                    return {value: item.param1, pol: item.param2, pod: item.param3, jobNumber: item.param4,
                                        blNo: item.param5, carrierCode: item.param6, blDate: item.param7}
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
                            $("#pol").val(ui.item.pol);
                            $("#pod").val(ui.item.pod);
                            $("#jobNumber").val(ui.item.jobNumber);
                            $("#blNo").val(ui.item.blNo);
                            $("#carrierCode").val(ui.item.carrierCode);
                            $("#blDate").val(ui.item.blDate);
                        }
                    });

                    $("#replicateSOBLBtn").on("click", function () {
                        $('input[id="soNumber"]').each(function (index, element) {
                            $('input[id="soNumber"]').get(index).value = $("#soNo").val();
                            $('input[id="blNumber"]').get(index).value = $("#blNo").val();
                        });

                    });



                });

                function statePopup() {
                    autopopup("#placeOfSupply", "statecode.fin", function (event, ui) {
                        $("#posCode").val(ui.item.data);
                    });
                }

                function print()
                {
                    if (confirm("DO U Wish To Print The Record??"))
                    {
                        window.open('billingPDFReport.fin?PARAM=EXPENSE&irn=&trxid=${billingBean.billId}');
                    }
                }

                function printEInvoice()
                {
                    if (confirm("DO U Wish To Print The Record??"))
                    {
                        window.open('billingPDFReport.fin?PARAM=EXPENSE&irn=${billingBean.irn}&trxid=${billingBean.billId}');
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

                function multiply(src, index) {
                    var derived = eval(src.value);
                    var ra = document.getElementsByClassName("revenueAmount");
                    ra[index].value = derived;
                    calculateTax();
                }

                function soPopup(src, idx) {
                    $(src).autocomplete({
                        minLength: 5,
                        autoFocus: true,
                        source: function (request, response) {

                            $.getJSON('soExpensePopup.fin', {
                                term: request.term,
                                blNo: '',
                                seaAir: '${billingBean.seaAir}',
                                exportImport: '${billingBean.expImp}'
                            }, function (result) {
                                var wordlist = ($.map(result, function (item) {
                                    return {value: item.param1, data: item.param5}
                                }));
                                var re = $.ui.autocomplete.escapeRegex(request.term);
                                var matcher = new RegExp("^" + re, "i");
                                var a = $.grep(wordlist, function (item) {
                                    return matcher.test(item.value);
                                });
                                response(a);
                            });
                        },
                        select: function (event, ui) {
                            if(ui.item.data !== undefined){
                                $('input[id="blNumber"]').get(idx).value = ui.item.data;
                            }
                        }
                    });
                }

               function openSOPopup(src, idx) {
                    var soNumber = $('input[id="soNumber"]').get(idx).value;
                    window.open(encodeURI('soPopupExpense.fin?soNumber='+soNumber));
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
                    var tdss = document.getElementsByClassName("tdss");
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
                    ta[0].value = Number(totalTax1).toFixed(2);
                    ta[1].value = Number(totalTax2).toFixed(2);
                    ta[2].value = Number(totalTax3).toFixed(2);
                    ta[3].value = Number(totalTax4).toFixed(2);
                    ta[4].value = Number(totalTax5).toFixed(2);
                    $("#totalTax").val(Number(totalTax1 + totalTax2 + totalTax3 + totalTax4 + totalTax5).toFixed(2));
                    $("#totalTaxable").val(totalTaxableAmount);
                    $("#totalNonTaxable").val(totalNonTaxableAmount);
                    $("#grandTotal").val(eval($("#totalTax").val()) + totalTaxableAmount
                            + totalNonTaxableAmount - eval($("#discountAmount").val()));

                    var tdsa = document.getElementsByClassName("tdsa");
                    var tdsAppliedAmount = totalTaxableAmount + totalNonTaxableAmount - eval($("#discountAmount").val());
                    //alert(tdsAppliedAmount);
                    tdsa[0].value = tdsAppliedAmount * eval(tdss[0].value) / 100;
                    tdsa[1].value = tdsAppliedAmount * eval(tdss[1].value) / 100;
                    tdsa[2].value = tdsAppliedAmount * eval(tdss[2].value) / 100;

                    var totalTDSValue = eval(tdsa[0].value) + eval(tdsa[1].value) + eval(tdsa[2].value);
                    //alert(totalTDSValue);
                    $("#totalTDS").val(totalTDSValue);
                    $("#partyTDSValue").val(totalTDSValue);
                }

                function ShowLoading(e) {
                    calculateTax();
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

                function createIRN() {
                    $('#billingForm').attr("action", "createIRN.fin");
                    $("#billingForm").submit();
                }

            </script>
        </head>
        <body>
            <div class="comdiv">

                <header>
                    <a href="billTemplateSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
                    <c:out value="${billingBean.revExp}"/> ::
                    <c:out value="${billingBean.seaAir}"/> / <c:out value="${billingBean.localForeign}"/> 
                    / <c:out value="${billingBean.expImp}"/> / <c:out value="${billingBean.revExp}"/>
                    / <c:out value="${billingBean.templateType}"/>
                    <html:input cssClass="normalTextBox" path="saveToken"/>
                </header>
                <table class="tablec">
                    <c:if test="${billingBean.errMsg != null}">
                        <tr>
                            <th colspan="6"><c:out value="${billingBean.errMsg}"/></th>
                        </tr>
                    </c:if>
                    <tr>
                        <td><label>VOUCHER NO</label></td>
                        <td>
                            <html:input path="billNo" class="medium"/><html:input path="billId" class="small readonly"/>
                        </td>
                        <td><label class="required">BILL DATE</label></td>
                        <td><html:input path="billDate" class="smallplus" id="billDate"/></td>
                        <td><label class="required">DUE DATE</label></td>
                        <td><html:input path="dueDate" class="smallplus" id="dueDate"/></td>

                    </tr>

                    <tr>
                        <td><label>C.L.</label></td>
                        <td>${billingBean.billtoName}</td>
                        <td>${billingBean.billTo}</td>
                        <td><html:textarea path="billtoContctDtls" class="textareaL"/></td>
                        <td><label>CONTAINER NO</label></td>
                        <td><html:textarea path="contnrNo" class="textareaL"/></td>
                    </tr>
                    <tr>
                        <td><label>SO NUMBER</label></td>
                        <td>
                            <html:input path="soNo" id="soNo" class="smallplus"/>
                            <button type="button" id="replicateSOBLBtn" class="finbutton">R</button>
                        </td>
                        <td><label>BL NUMBER</label></td>
                        <td><html:input path="blNo" id="blNo" class="medium"/></td>
                        <td><label>JOB NUMBER</label></td>
                        <td><html:input path="jobNumber" id="jobNumber" class="medium"/></td>

                    </tr>
                    <tr>                    
                        <td><label>IRN</label></td>
                        <td><html:input path="irn" id="irn" class="largeXL"/></td>

                        <td><label class="required">CURRENCY</label></td>
                        <td>
                            <html:input path="currencyCode" id="currencyName" class="small"/>
                            <html:input path="currencyId" id="currencyId" class="small readonly" readonly="true"/>
                        </td>
                        <td><label class="required">EXCHANGE RATE</label></td>
                        <td><html:input path="exchangeRate" id="rate" type="text" class="smallplus rightAlign"/></td>

                    </tr>
                    <tr>
                        <td><label>BL DATE</label></td>
                        <td><html:input path="blBean.blReleaseDate" id="blDate" class="smallplus"/></td>
                        <td><label class="required">INV NO</label></td>
                        <td>
                            <html:input path="invSbNo" class="medium" id="invSbNo"/>
                        </td> 
                        <td><label class="required">INV DATE</label></td>
                        <td>
                            <html:input path="invDate" class="smallplus" id="invDate"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>PLACE OF SUPPLY</label></td>
                        <td><html:input path="placeOfSupply" class="medium" onkeyup="statePopup()"/><html:input path="posCode" class="small"/></td>
                    </tr>

                </table>
            </div>
            <div id="accordion">
                <h3>REVENUE HEAD</h3>
                <div class="rows">
                    <iframe frameborder="0" style="position:absolute;width:1px;"></iframe>
                    <table class="tablevou" id="tbl" width="300px;height:150px;">
                        <thead>
                            <tr>
                                <th>DESCRIPTION</th>
                                <th>SAC</th>
                                <th>SO / BL</th>
                                <th>ADDL.DESC.</th>
                                <th>TAXABLE</th>
                                <th>N</th>
                                <th>AMOUNT</th>
                            </tr>
                        </thead>
                        <c:forEach items="${command.billTemplateRows}" var="billTemplateRow" varStatus="status">
                            <tr class="${billTemplateRow.errMsg}">
                                <td>
                                    <html:hidden path="billTemplateRows[${status.index}].acctName" class="smallM readonly" readonly="true" value="${billTemplateRow.acctName}"/>
                                    <html:input path="billTemplateRows[${status.index}].codeCombinationId" class="smallM readonly" id="acZeroCode" readonly="true" value="${billTemplateRow.codeCombinationId}"/>
                                    <html:input path="billTemplateRows[${status.index}].codeDesc" class="large" value="${billTemplateRow.codeDesc}"/>

                                </td>                                
                                <td>
                                    <html:input path="billTemplateRows[${status.index}].sacCode" class="medium" value="${billTemplateRow.sacCode}"/>
                                </td>
                                <td>
                                    <html:input path="billTemplateRows[${status.index}].soNo" value="${billTemplateRow.soNo}" cssClass="smallplus" onkeyup="soPopup(this,'${status.index}')" id="soNumber"/>
                                    <button type="button" class="finbutton" onclick="openSOPopup(this,'${status.index}')">...</button>
                                    <html:input path="billTemplateRows[${status.index}].blNo" value="${billTemplateRow.blNo}" id="blNumber" cssClass="smallplus" readonly="true"/>
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
                <header class="head">
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
                                    <c:if test="${billingBean.billType == 'DEBITNOTE'}">
                                        <html:select path="billTemplateTaxs[${status.index}].taxDrCrArr" class="selectmin">
                                <option value="CR">CR</option>
                                <option value="DR">DR</option>
                            </html:select>
                        </c:if>
                        <c:if test="${billingBean.billType != 'DEBITNOTE'}">
                            <html:select path="billTemplateTaxs[${status.index}].taxDrCrArr" class="selectmin">
                                <option value="DR">DR</option>
                                <option value="CR">CR</option>
                            </html:select>
                        </c:if>        
                        </td>
                        <td><html:input path="billTemplateTaxs[${status.index}].acctName" value="${billTemplateRow.acctName}" type="text" id="acOneName" class="largeXL"/>
                            <html:input path="billTemplateTaxs[${status.index}].codeCombinationId" value="${billTemplateRow.codeCombinationId}" type="text" id="acOneCode" class="smallM readonly" readonly="true"/></td>
                        <td><html:input path="billTemplateTaxs[${status.index}].codeDesc" id="DescArr"  value="${billTemplateRow.codeDesc}" type="text" class="large"/></td>
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
                <header class="head">
                    TDS HEAD
                </header>
                <table class="tableauto">
                    <thead>
                        <tr>
                            <th></th>
                            <th>TDS ACCOUNT</th>
                            <th>DESCRIPTION</th>
                            <th>% TDS</th>
                            <th>ON VALUE</th>
                            <th>TDS AMOUNT</th>
                        </tr>
                    </thead>
                    <c:forEach items="${command.billTemplateTDS}" var="billTemplateRow" varStatus="status">
                        <tbody>
                            <tr>
                                <td>
                                    <html:select path="billTemplateTDS[${status.index}].taxDrCrArr" class="selectmin">
                                        <option value="CR">CR</option>
                                        <option value="DR">DR</option>
                                    </html:select>
                                </td>
                        <td><html:input path="billTemplateTDS[${status.index}].acctName" value="${billTemplateRow.acctName}" type="text" id="tdsName" class="largeXL" onkeyup="autoDesc(this, '${status.index}', 0)"/>
                            <html:input path="billTemplateTDS[${status.index}].codeCombinationId" value="${billTemplateRow.codeCombinationId}" type="text" id="tdsCode" class="smallM readonly" readonly="true"/></td>
                        <td><html:input path="billTemplateTDS[${status.index}].codeDesc" id="tdsDescription"  value="${billTemplateRow.codeDesc}" type="text" class="large"/></td>
                        <td><html:input path="billTemplateTDS[${status.index}].taxPercentageArr" id="tdsPercentage" value="${billTemplateRow.taxPercentageArr}" type="text" class="medium rightAlign tdss"/></td>
                        <td><html:input path="billTemplateTDS[${status.index}].taxOnValueArr" value="${billTemplateRow.taxOnValueArr}" type="text" class="medium rightAlign"/></td>  
                        <td><html:input path="billTemplateTDS[${status.index}].amount" value="${billTemplateRow.amount}" class="smallplus rightAlign tdsa"/></td>  
                        </tr>
                        </tbody>  
                    </c:forEach>

                    <tr>
                        <td colspan="4" class="rightAlign">                                
                        </td>
                        <td>
                            <label>Total TDS</label>
                        </td>
                        <td>
                            <html:input path="totalTDS" class="smallplus rightAlign" id="totalTDS"/>
                        </td>
                    </tr>
                    <thead>
                        <tr>
                            <th></th>
                            <th>PARTY HEAD</th>
                            <th colspan="3">DESCRIPTION</th>
                            <th>TDS</th>
                        </tr>
                    </thead>
                    <tr>
                        <td>
                            <html:select path="partyTDSDrCr" class="selectmin">
                        <option value="DR">DR</option>
                        <option value="CR">CR</option>
                    </html:select>
                    </td>
                    <td>
                        <html:input type="text" path="partyTDSAcctName" class="largeXL" id="partyTDSAcctName"
                        onkeyup="partnerPopup('EXPENSE_BILLTO','#partyTDSAcctName', '#billtoContctDtls','#partyTDSAcctCode')"/>
                        <html:input type="text" path="partyTDSAcctCode" id="partyTDSAcctCode" readonly="true" class="smallM readonly"/>
                    </td>   
                    <td colspan="3">
                        <html:input type="text" path="partyTDSDesc" class="large"/>
                    </td>  
                    <td><html:input type="text" path="partyTDSValue" id="partyTDSValue" class="smallplus rightAlign"/></td>
                    </tr>
                </table>
            </div>                
            <div class="rows2">
                <header class="head">
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
                            <td><html:input path="discountAcctName" type="text" id="acTwoName" class="largeXL" onkeyup="autoTwo(this, 0)"/>
                                <html:input path="discountAcctCode" type="text" id="acTwoCode" readonly="true" class="smallM readonly"/></td>   
                            <td><html:input path="discountDesc" type="text" class="large"/></td>  
                            <td><html:input path="discountPercentage" type="text" class="medium rightAlign"/></td>  
                            <td><html:input path="discountOnValue" type="text" class="medium rightAlign"/></td>  
                            <td><html:input path="discountAmount" type="text" class="smallplus rightAlign" id="discountAmount"/></td>  
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
                        <td><label>Sales By</label></td>
                        <td><html:input path="salesBy" class="medium"/></td>
                        <td><label>Prepared By</label></td>
                        <td>
                            <html:input path="preparedBy" class="smallplus"/>
                        </td>                            
                    </tr>

                </table>
            </div>
            <table class="tb2">
                <tr>
                    <td>
                        <c:if test="${billingBean.billId > 0}">
                            <rbac:rbac formId="1144" pattern="(.)\\|(Y)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)">
                                <button type="button" class="finbutton" id="saveInvoice" onclick="ShowLoading()">Calculate & Save</button>
                            </rbac:rbac>
                        </c:if>
                        <c:if test="${billingBean.billId == 0}">
                            <rbac:rbac formId="1144" pattern="(.)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)\\|(.)">
                                <button type="button" class="finbutton" id="saveInvoice" onclick="ShowLoading()">Calculate & Save</button>
                            </rbac:rbac>
                        </c:if></td>
                    <td><input type="button" class="finbutton" value="Print" onclick="print()"></td>
                    <td><button type="reset" class="finbutton">Reset</button></td>
                    <rbac:rbac formId="9942" pattern="(.)\\|(Y)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)">
                        <c:if test="${billingBean.irn == null}">                        
                            <c:if test="${billingBean.billType == 'CREDITNOTE'}">
                                <td>
                                    <button type="button" class="finbutton" id="genIRN" onclick="createIRN()">Generate IRN</button>
                                </td>
                            </c:if>
                        </c:if>
                        <c:if test="${billingBean.billType == 'CREDITNOTE' && billingBean.irn != null && billingBean.irnAction != 'CANCEL'}">
                            <td>
                                <button type="button" class="finbutton" id="cancIRN" onclick="cancelIRN()">Cancel IRN</button>
                            </td>
                        </c:if>
                    </rbac:rbac>
                    <rbac:rbac formId="1142" pattern="(Y)\\|(.)\\|(.)\\|(.)\\|(Y)\\|(.)\\|(.)">
                        <c:if test="${billingBean.billType == 'CREDITNOTE' && billingBean.irn != null && billingBean.irnAction != 'CANCEL'}">
                            <td><input type="button" class="finbutton" value="PrintEInvoice" onclick="printEInvoice()"></td>
                            </c:if>
                        </rbac:rbac>
                </tr>
            </table>   
            <div id="expenseDetailView" title="expenseDetailView"/>             
        </html:form>
    </body>
</html>
