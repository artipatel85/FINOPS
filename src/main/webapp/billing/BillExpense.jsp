
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/partner.js"></script>
<html>
    <html:form method="post" action="loadBillingData2.fin" command="billingBean" id="billingForm">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Pragma" content="no-cache">
        <meta http-equiv="Expires" content="-1">
        <title>BillTemplate</title>
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
            });
            
            $(document).ready(function () {
            $("#billToName").autocomplete({
                autoFocus: true,
                source: function (request, response) {
                    $.getJSON("expenseBillTo.fin", {
                        term: request.term

                    }, function (result) {

                        var wordlist = ($.map(result, function (item) {
                            return {value: item.param1, billToAddress: item.param2,
                            partyAcctCode:item.param3, gstin: item.param4, stateCode: item.param5 }

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
                    $("#billTo").val(ui.item.billTo);
                    $("#billToAddress").val(ui.item.billToAddress);
                    $("#stateCode").val(ui.item.stateCode);
                    if($("#localForeign").val() == 'LOCAL'){
                        $("#templateType").val('');
                    }
                    $("#partyAcctCode").val(ui.item.partyAcctCode);
                    $("#partyGstin").val(ui.item.gstin);
                }
            });
           });
            
            $(document).ready(function () {
                $("#billingForm").validate({

                    rules: {
                        partyAcctCode: {required: true, min: 1},
                        exportImport: {required: true},
                        revExpense: {required: true},
                        seaAir: {required: true},
                        localForeign: {required: true},
                        billType: {required: true},
                        templateType: {required: true}

                    },
                    messages: {
                        partyAcctCode: '<div class="tool">* field is invalid.</div>',
                        exportImport: '<div class="tool">* field is required.</div>',
                        revExpense: '<div class="tool">* field is required.</div>',
                        seaAir: '<div class="tool">* field is required.</div>',
                        localForeign: '<div class="tool">* field is required.</div>',
                        billType: '<div class="tool">* field is required.</div>',
                        templateType: '<div class="tool">* field is required.</div>'
                    }

                });
            });
        </script>
    </head>
    <body>
        <div class="comdiv">
            
                <header>
                    <a href="billTemplateSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
                    <c:out value="${command.revExp}"/> - Bl No :: <c:out value="${sessionScope.billingInvoice.blNo}"/>
                </header>
                <table class="tablec">
                    <tr>
                        <td><label>BILL TO</label></td>
                        <td>
                            <html:input path="billtoName" id="billtoName"
                            onkeyup="partnerPopup('EXPENSE_BILLTO','#billtoName', '#billtoContctDtls','#partyAcctCode','#partyGstin','#stateCode')" class="largeXL"/>
                            <html:input path="partyAcctCode" class="small readonly" readonly="true" id="partyAcctCode"/>
                        </td>
                        <td>
                            <html:input path="billTo" class="smallplus readonly" readonly="true" id="billTo"/>
                            <html:hidden path="templateId"/>
                        </td>
                        <td>
                            <html:textarea path="billtoContctDtls" id="billtoContctDtls" class="textareaL readonly"></html:textarea>
                        </td>
                        <td>
                            BL NO
                        </td>
                        <td>
                            <html:input path="blNo" class="medium readonly" readonly="true"/>
                            <html:input path="stateCode" id="stateCode" class="small readonly" readonly="true"/>
                            <c:out value="${blNo}"/>
                        </td>
                    </tr>
                    
                    <c:if test="${sessionScope.billingInvoice.blNo == null}">
                    <tr>
                        <td><label>SEA/AIR</label></td>
                        <td>
                            <html:select path="seaAir">
                                <html:option value="">SELECT</html:option>
                                <html:option value="SEA">SEA</html:option>
                                <html:option value="AIR">AIR</html:option>
                            </html:select>
                        </td>
                        <td><label>EXPORT/IMPORT</label></td>
                        <td><html:select path="expImp">
                                <html:option value="">SELECT</html:option>
                                <html:option value="EXPORT">EXPORT</html:option>
                                <html:option value="IMPORT">IMPORT</html:option>
                            </html:select>
                        </td>
                        <td><label>LOCAL/FOREIGN</label></td>
                        <td><html:select path="localForeign" id="localForeign">
                                <html:option value="">SELECT</html:option>
                                <html:option value="LOCAL">LOCAL</html:option>
                                <html:option value="FOREIGN">FOREIGN</html:option>
                            </html:select>
                        </td>
                    </tr>
                    <tr>
                        <td><label>BILL TYPE</label></td>
                        <td>
                            <html:select path="billType">
                                <html:option value="">SELECT</html:option>
                                <html:option value="EXPENSE">EXPENSE</html:option>
                                <html:option value="MISCEXP">MISCEXP</html:option>
                                <html:option value="CREDITNOTE">CREDIT NOTE</html:option>
                                <html:option value="DEBITNOTE">DEBIT NOTE</html:option>
                                <html:option value="SELF">SELF</html:option>
                            </html:select>
                        </td>
                        <td><label>TAX TYPE</label></td>
                        <td><html:select path="templateType" id="templateType">
                                <html:option value="">SELECT</html:option>
                                <html:option value="IGST">IGST</html:option>
                                <html:option value="SGST">SGST</html:option>
                                <html:option value="UTGST">UTGST</html:option>
                                <html:option value="SEZ">EXPORT-SEZ</html:option>
                                <html:option value="EXEMPTED">EXEMPTED</html:option>
                            </html:select>
                        </td>
                        <td><label>GSTIN NO</label></td>
                        <td><html:input path="partyGstin" id="partyGstin" class="medium readonly" readonly="true"/></td>
                    </tr>
                    </c:if>
                    <c:if test="${sessionScope.billingInvoice.blNo != null}">
                    <tr>
                        <td><label>SEA/AIR</label></td>
                        <td>
                            <html:input path="seaAir" id="seaAir" class="smallplus readonly" readonly="true"/>
                        </td>
                        <td><label>EXPORT/IMPORT</label></td>
                        <td>
                            <html:input path="exportImport" class="smallplus readonly" readonly="true"/>
                        </td>
                        <td><label>LOCAL/FOREIGN</label></td>
                        <td>
                            <html:input path="localForeign" id="localForeign" class="smallplus readonly" readonly="true"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>BILL TYPE</label></td>
                        <td>
                            <html:input path="billType" class="smallplus readonly" readonly="true"/>
                        </td>
                        <td><label>TAX TYPE</label></td>
                        <td>
                            <html:select path="templateType" id="templateType">
                                <html:option value="">SELECT</html:option>
                                <html:option value="IGST">IGST</html:option>
                                <html:option value="SGST">SGST</html:option>
                                <html:option value="UTGST">UTGST</html:option>
                                <html:option value="SEZ">EXPORT-SEZ</html:option>
                                <html:option value="EXEMPTED">EXEMPTED</html:option>
                            </html:select>
                        </td>
                        <td><label>GSTIN NO</label></td>
                        <td><html:input path="partyGstin" id="partyGstin" class="medium readonly" readonly="true"/></td>
                    </tr>
                    </c:if>
                </table>
            </div>

            <table class="tb2">
                <tr>
                    <td><button type="submit" class="finbutton">Confirm Party</button></td>
                    <td><button type="reset" class="finbutton">Reset</button></td>
                </tr>
            </table>   
        </html:form>
    </body>
</html>
