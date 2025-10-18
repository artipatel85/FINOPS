<%-- 
    Document   : BillTemplate
    Created on : Oct 14, 2017, 3:28:37 PM
    Author     : BirenDesai
--%>
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
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

            function removeRow(src)
            {
                var oRow = src.parentElement.parentElement;
                $('#templateForm').attr('action', "removeTemplateRow.fin?index=" + (oRow.rowIndex - 1)).submit();
                //document.all("tbl").deleteRow(oRow.rowIndex);

            }

            function addRow()
            {
                $('#templateForm').attr('action', "addTemplateRow.fin").submit();
            }

            function autozero(src, idx, id) {
                //alert(src);
                //alert(idx);
                var json = 'accountsThree.fin';
                var textId = 'acZeroCode';
                if (id == 1) {
                    json = 'accountsTwo.fin';
                    textId = 'acOneCode';
                }

                $(src).autocomplete({
                    autoFocus: true,
                    source: function (request, response) {
                        $.getJSON(json, {
                            term: request.term

                        }, function (result) {

                            var wordlist = ($.map(result, function (item) {

                                return {value: item.param2, data: item.param1}
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
                        var codeid = 'input[id=' + textId + ']';
                        $(codeid).get(idx).value = ui.item.data;
                    }
                });
            }

            function autoTwo(src, id) {

                var json = 'accountsThree.fin';
                var textId = 'acTwoCode';
                if (id == 1) {
                    json = 'accountsThree.fin';
                    textId = 'acThreeCode';
                }
                $(src).autocomplete({
                    autoFocus: true,
                    source: function (request, response) {
                        $.getJSON(json, {
                            term: request.term

                        }, function (result) {

                            var wordlist = ($.map(result, function (item) {

                                return {value: item.param2, data: item.param1}
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
                        var codeid = 'input[id=' + textId + ']';
                        $(codeid).val(ui.item.data);
                        //$("#acTwoCode").val(ui.item.data);
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
            $(document).ready(function () {
                $("#templateForm").validate({

                    rules: {
                        templateName: {required: true},
                        expImp: {required: true},
                        revExp: {required: true},
                        seaAir: {required: true},
                        localForeign: {required: true},
                        billType: {required: true},
                        templateType: {required: true}

                    },
                    messages: {
                        templateName: '<div class="tool">* field is required.</div>',
                        expImp: '<div class="tool">* field is required.</div>',
                        revExp: '<div class="tool">* field is required.</div>',
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
            <html:form method="post" action="billTemplateSave.fin" command="billTemplateBean" id="templateForm">
                <header>
                    <a href="billTemplateSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
                    Bill Template
                </header>
                <table class="tablec">
                    <col width="140PX">
                    <col width="200PX">
                    <col width="150PX">
                    <col width="200PX">
                    <col width="150PX">
                    <col width="150PX">
                    <tr>
                        <td><label>TEMPLATE NAME</label></td>
                        <td><html:input path="templateName" class="medium"/><html:hidden path="templateId"/></td>
                        <td><label>SEA/AIR</label></td>
                        <td><html:select path="seaAir">
                                <html:option value="">SELECT</html:option>
                                <html:option value="SEA">SEA</html:option>
                                <html:option value="AIR">AIR</html:option>
                            </html:select></td>
                        <td><label>EXPORT/IMPORT</label></td>    
                        <td><html:select path="expImp">
                                <html:option value="">SELECT</html:option>
                                <html:option value="EXPORT">EXPORT</html:option>
                                <html:option value="IMPORT">IMPORT</html:option>
                            </html:select></td>    
                    </tr>
                    <tr>
                        <td><label>REVENUE/EXPENSE</label></td>
                        <td><html:select path="revExp">
                                <html:option value="">SELECT</html:option>
                                <html:option value="REVENUE">REVENUE</html:option>
                                <html:option value="EXPENSE">EXPENSE</html:option>
                            </html:select></td>
                        <td><label>LOCAL/FOREIGN</label></td>
                        <td><html:select path="localForeign">
                                <html:option value="">SELECT</html:option>
                                <html:option value="LOCAL">LOCAL</html:option>
                                <html:option value="FOREIGN">FOREIGN</html:option>
                            </html:select></td>
                        <td><label>BILL TYPE</label></td>
                        <td><html:select path="billType">
                                <html:option value="">SELECT</html:option>
                                <html:option value="INVOICE">INVOICE</html:option>
                                <html:option value="BOS">BILL OF SUPPLY</html:option>
                                <html:option value="CREDITNOTE">CREDIT NOTE</html:option>
                                <html:option value="DEBITNOTE">DEBIT NOTE</html:option>
                                <html:option value="MISC">MISC</html:option>
                                <html:option value="MISCEXP">MISCEXP</html:option>
                                <html:option value="EXPENSE">EXPENSE</html:option>
                            </html:select></td>
                    </tr>
                    <tr>
                        <td><label>TAX TYPE</label></td>
                        <td><html:select path="templateType">
                                <html:option value="">SELECT</html:option>
                                <html:option value="IGST">IGST</html:option>
                                <html:option value="SGST">SGST</html:option>
                                <html:option value="UTGST">UTGST</html:option>
                                <html:option value="SEZ">EXPORT-SEZ</html:option>
                                <html:option value="EXEMPTED">EXEMPTED</html:option>
                            </html:select></td>
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
                                <th style="width:10px"><a href="#" onclick="addRow();"><div class="logoplus"></div></a></th>

                                <th>ACCOUNT NAME</th>
                                <th>DESCRIPTION</th>
                                <th>SAC</th>
                                <th>TAXABLE</th>
                                <th>NON-TAXABLE</th>
                                <th></th>
                            </tr>
                        </thead>
                        <c:forEach items="${command.billTemplateRows}" var="billTemplateRow" varStatus="status">
                            <tr>
                                <td><a href="#" onclick="removeRow(this);"><div class="logominus"></div></a></td>

                                <td><html:input path="billTemplateRows[${status.index}].accountname" class="largeXL" id="acZeroName" value="${billTemplateRow.accountname}" onkeyup="autozero(this,'${status.index}',0)"/>
                                    <html:input path="billTemplateRows[${status.index}].accountcode" class="smallM readonly" id="acZeroCode" readonly="true" value="${billTemplateRow.accountcode}"/></td>
                                <td><html:input path="billTemplateRows[${status.index}].des" class="medium" value="${billTemplateRow.des}"/></td>
                                <td><html:input path="billTemplateRows[${status.index}].sac" class="medium" value="${billTemplateRow.sac}"/></td>
                                <td>
                                    <html:input path="billTemplateRows[${status.index}].tax1PerArr" class="smallM rightAlign" value="${billTemplateRow.tax1PerArr}"/>
                                    <html:input path="billTemplateRows[${status.index}].tax2PerArr" class="smallM rightAlign" value="${billTemplateRow.tax2PerArr}"/>
                                    <html:input path="billTemplateRows[${status.index}].tax3PerArr" class="smallM rightAlign" value="${billTemplateRow.tax3PerArr}"/>
                                    <html:input path="billTemplateRows[${status.index}].tax4PerArr" class="smallM rightAlign" value="${billTemplateRow.tax4PerArr}"/>
                                    <html:input path="billTemplateRows[${status.index}].tax5PerArr" class="smallM rightAlign" value="${billTemplateRow.tax5PerArr}"/>
                                </td>
                                <td><html:checkbox path="billTemplateRows[${status.index}].chkBox6" value="6"/></td>
                                <td class="finErrMsg"><c:out value="${billTemplateRow.errMsg}"/></td>

                            </tr>
                        </c:forEach>

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
                            <th></th>
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
                        <td><html:input path="billTemplateTaxs[${status.index}].taxAcctNameArr" value="${billTemplateRow.taxAcctNameArr}" type="text" id="acOneName" class="largeXL" onkeyup="autozero(this, '${status.index}', 1)"/>
                            <html:input path="billTemplateTaxs[${status.index}].taxAcctCodeArr" value="${billTemplateRow.taxAcctCodeArr}" type="text" id="acOneCode" class="smallM readonly" readonly="true"/></td>   
                        <td><html:input path="billTemplateTaxs[${status.index}].taxAcctDescArr" id="DescArr"  value="${billTemplateRow.taxAcctDescArr}" type="text" class="large" onkeyup="autoDesc(this, '${status.index}', 0)"/></td>  
                        <td><html:input path="billTemplateTaxs[${status.index}].taxPercentageArr" id="TaxHead" value="${billTemplateRow.taxPercentageArr}" type="text" class="medium rightAlign"/></td>  
                        <td><html:input path="billTemplateTaxs[${status.index}].taxOnValueArr" value="${billTemplateRow.taxOnValueArr}" type="text" class="medium rightAlign"/></td>  
                        </tr>
                        </tbody>  
                    </c:forEach>
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
                            <th></th>
                        </tr>
                    </thead>   
                    <tbody>
                        <tr>
                            <td><html:input path="discountAcctName" type="text" id="acTwoName" class="largeXL" onkeyup="autoTwo(this, 0)"/>
                                <html:input path="discountAcctCode" type="text" id="acTwoCode" readonly="true" class="smallM readonly"/></td>   
                            <td><html:input path="discountDesc" type="text" class="large"/></td>  
                            <td><html:input path="discountPercentage" type="text" class="medium rightAlign"/></td>  
                            <td><html:input path="discountOnValue" type="text" class="medium rightAlign"/></td>  
                        </tr>
                    </tbody>  
                </table>
            </div>
            <div class="rows2">
                <header class="head">
                    OTHER CHARGES
                </header>
                <table class="tableauto">
                    <thead>
                        <tr>
                            <th>ACCOUNT NAME</th>
                            <th>DESCRIPTION</th>
                            <th>% TAX</th>
                            <th>ON VALUE</th>
                            <th></th>
                        </tr>
                    </thead>   
                    <tbody>
                        <tr>
                            <td><html:input type="text" path="otherChargesAcctName" class="largeXL" id="acThreeName" onkeyup="autoTwo(this, 1)"/>
                                <html:input type="text" path="otherChargesAcctCode" id="acThreeCode" readonly="true" class="smallM readonly"/></td>   
                            <td><html:input type="text" path="otherChargesDesc" class="large"/></td>  
                            <td><html:input type="text" path="otherChargesPercentage" class="medium rightAlign"/></td>  
                            <td><html:input type="text" path="otherChargesOnValue" class="medium rightAlign"/></td>  
                        </tr>
                    </tbody>  
                </table>
            </div>
            <table class="tb2">
                <tr>
                    <td><button type="submit" class="finbutton">Save</button></td>
                    <td><button type="reset" class="finbutton">Reset</button></td>
                </tr>
            </table>   
        </html:form>
    </body>
</html>
