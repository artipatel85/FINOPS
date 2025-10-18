<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/finance.js"></script>
<script src="finactjs/partner.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <script>
        $(document).ready(groupAccount);
        $(document).ready(function () {
            $("#ledgerform").validate({

                rules: {
                    parentId: {required: true},
                    acctName: {required: true},
                    country: {required: false},
                    countrycode: {required: '#country1:filled'},
                    currencyName: {required: false},
                    currencyId: {required: '#currencyrate1:filled'},
                    stateName: {required: false},
                    stateCode: {required: '#state1:filled'}
                },
                messages: {
                    parentId: '<div class="tool">* field is required.</div>',
                    acctName: '<div class="tool">* field is required.</div>',
                    countrycode: '<div class="tool">* field is required.</div>',
                    currencyId: '<div class="tool">* field is required.</div>'
                }

            });
        });
        $(document).ready(function () {
            $("#openingBAL").keypress(function (event) {
                if (event.which !== 46 && (event.which < 48 || event.which > 57))
                {
                    event.preventDefault();
                }
            });
        });
        $(window).keypress(function (event) {
            if (!(event.which === 115 && event.shiftKey))
                return true;
            $("#btnsubmit").click();
            event.preventDefault();
            return false;
        });
    </script>
    <body>
        <div class="main">
            <header>
                <a href="ledgerSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
                LEDGER
            </header>
            <html:form method="post" id="ledgerform" action="ledgerSave.fin" command="ledgerBean">
                <table class="tablec">
                    <col width="160PX">
                    <col width="350PX">
                    <col width="160PX">
                    <col width="350PX">
                    <tr>
                        <th colspan="4" class="errmsg"><c:out value="${ledgerBean.errMsg}"/></th>
                    </tr>
                    <tr>
                        <td><label>UNDER</label></td>
                        <td><html:input path="parentName" id="under" class="largeXL"/>
                            <html:input path="parentId" id="parentAcctId" class="small readonly" readonly="true"/>
                            <html:input path="acctTypeId" id="acctTypeId" class="small readonly" readonly="true"/></td>
                        <td><label>CODE ID</label></td>
                        <td><html:input path="codeCombinationId" class="small readonly" readonly="true"/></td>

                    </tr>
                    <tr>
                        <td><label>LEDGER NAME</label></td>
                        <td><html:input path="acctName" class="largeXL"/></td>
                        <td><label>OPENING BAL</label></td>
                        <td>
                            <html:input path="openingBalance" id="openingBAL" class="medium"/>
                            <html:select path="lineDtrCtr" class="small">
                                <html:option value="DR">DR</html:option>
                                <html:option value="CR">CR</html:option>
                            </html:select>
                        </td>
                    </tr>
                    </table>
                    <table>
                    <tr>
                        <td colspan="4">
                    <header class="head">
                        Account Details
                    </header>
                            </td>
                    </tr>
                    <tr>
                        <td><label>CONTACT PERSON</label></td>
                        <td><html:input path="prtContactPerson" class="medium"/></td>
                        <td><label>WEBSITE</label></td>
                        <td><html:input path="prtWebsite" class="largeXL"/></td>
                    </tr>
                    <tr>
                        <td><label>ADDRESS</label></td>
                        <td><html:textarea  path="prtAddress" rows="4" cols="30"></html:textarea></td>
                            <td><label>ADDRESS</label></td>
                            <td><html:textarea  path="prtAddress2" rows="4" cols="30"></html:textarea></td>
                        </tr>
                        <tr>
                            <td><label>CITY</label></td>
                            <td><html:input path="city" class="medium"/></td>
                        <td><label>ZIP CODE</label></td>
                        <td><html:input path="zipcode" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>FAX</label></td>
                        <td><html:input path="faxCC" class="small"/>
                            <html:input path="faxAC" class="small"/>
                            <html:input path="faxNo" class="medium"/></td>
                        <td><label>PHONE</label></td>
                        <td><html:input path="telCC" class="small"/>
                            <html:input path="telAC" class="small"/>
                            <html:input path="telNo" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>COUNTRY</label></td>
                        <td>
                            <html:input path="country" class="medium" onkeyup="partnerPopup('COUNTRY_0','#country', '#countryCode')"/>
                            <html:input path="countrycode" class="small" id="countryCode"/>
                        </td>
                        <td><label>EMAIL</label></td>
                        <td><html:input path="prtEmail" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>BANKER NAME</label></td>
                        <td><html:input path="bankerName" class="medium"/></td>
                        <td><label>BANKER ADDRESS</label></td>
                        <td><html:textarea path="bankerAddress" rows="4" cols="30"/></td>
                    </tr>
                    <tr>
                        <td><label>CURRENCY</label></td>
                        <td>
                            <html:input path="currencyName" class="medium" onkeyup="partnerPopup('CURRENCY_0','#currencyName', '#currencyId')"/>
                            <html:input path="currencyId" class="small readonly" readonly="readonly"/>
                        </td>
                        <td><label>CREDIT DAYS</label></td>
                        <td><html:input path="creditDays" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>PAN NO</label></td>
                        <td><html:input path="panNo" class="medium"/></td>
                        <td><label>TAN NO</label></td>
                        <td><html:input path="tanNo" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>GSTIN NO</label></td>
                        <td><html:input path="gstinNo" class="large"/></td>
                        <td><label>STATE NAME</label></td>
                        <td><html:input path="stateName" id="stateName" class="medium" onkeyup="partnerPopup('STATE_0','#stateName', '#stateCode')"/>
                            <html:input path="stateCode" id="stateCode" class="small readonly" readonly="true"/></td>
                    </tr>
         
                </table>
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" id="btnsubmit" class="finbutton">Save</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                        <td><button type="submit" class="finbutton"><a href="ledgerSearch.fin" style="color: #FFF">Exit</a></button></td>
                    </tr> 
                </table>
            </html:form>
        </div>
    </body>
</html>
