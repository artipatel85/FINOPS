<%-- 
    Document   : PartnerAccount
    Created on : Sep 9, 2017, 9:28:25 PM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form id="partnerForm" commandName="partnerBean" action="createPartnerKYC.fin" enctype="multipart/form-data">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>KYC</title>
            <script>
                $(function () {
                    $("#dialog-confirm").dialog({
                        autoOpen: false,
                        resizable: false,
                        height: "auto",
                        width: 1000,
                        modal: true,
                        refresh: true,
                        show: {
                            effect: "blind",
                            duration: 1000
                        },
                        close: function (e) {
                            window.location = "partnerAccount.fin?id=${partnerBean.partnerCode}";
                        }
                    });
                    $("#SFP").on("click", function () {
                        $("#dialog-confirm").load("importPartnerAccount.fin?id=${partnerBean.partnerCode}&branch=SFP").dialog("open");

                    });
                    $("#SFPM").on("click", function () {
                        $("#dialog-confirm").load("importPartnerAccount.fin?id=${partnerBean.partnerCode}&branch=SFPM").dialog("open");

                    });
                    $("#SFPC").on("click", function () {
                        $("#dialog-confirm").load("importPartnerAccount.fin?id=${partnerBean.partnerCode}&branch=SFPC").dialog("open");

                    });
                    $("#SFPK").on("click", function () {
                        $("#dialog-confirm").load("importPartnerAccount.fin?id=${partnerBean.partnerCode}&branch=SFPK").dialog("open");

                    });
                });
                
                
                function countryPopup() {
                    autopopup("#countryName", "countrycode.fin", function (event, ui) {
                        $("#countryCode").val(ui.item.data);
                    });
                }
                
                function statePopup() {
                    autopopup("#stateName", "statecode.fin", function (event, ui) {
                        $("#stateCode").val(ui.item.data);
                    });
                }

                $(document).ready(function () {
                    $("#addAccount").validate({

                        rules: {
                            fullName1: {required: true},
                            countryCode: {required: true}

                        },
                        messages: {
                            fullName1: '<div class="tool">* field is required.</div>',
                            countryCode: '<div class="tool">* field is required.</div>'
                        }

                    });
                });
            </script>
        </head>
        <body>
            <div class="main">
                <header>
                    KYC
                </header>

                <table class="tablec">
                    <col width="160PX">
                    <col width="350PX">
                    <col width="160PX">
                    <col width="350PX">
                    <tr>
                        <td><label>Company Name</label></td>
                        <td>
                            <html:input path="company" class="largeXXL"/>
                        </td>
                        <td><label>PARTNER CODE</label></td>
                        <td><c:out value="${partnerCode}"/></td>
                    </tr>
                    <tr>
                        <td><label>Address</label></td>
                        <td><html:textarea  path="address1" rows="4" cols="30"></html:textarea></td>
                        <td><label>City</label></td>
                        <td><html:input path="city" class="medium"/></td>    
                    </tr>
                    <tr>                        
                        <td><label>State</label></td>
                        <td><html:input path="stateName" class="medium" onkeyup="statePopup()"/><html:input path="stateCode" class="small"/></td>
                        <td><label>Zip Code</label></td>
                        <td><html:input path="zipCode" class="medium"/></td>
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
                        <td><label>CT.Person</label></td>
                        <td><html:input path="contactPerson" class="medium"/></td>
                        <td><label>Email Id</label></td>
                        <td><html:input path="email" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>Credit Period</label></td>
                        <td><html:input path="creditPeriod" class="medium"/></td>
                        <td><label>Country</label></td>
                        <td><html:input path="countryName" id="countryName" class="medium" onkeyup="countryPopup()"/>
                            <html:input path="countryCode" id="countryCode" class="small"/></td>
                    </tr>
                    <tr>
                        <td><label>Attachment 1</label></td>
                        <td>
                            <input type="file" name="param100" id="param100"  value="Bhaumik.pdf">
                            <a href="kycAttachment.fin?kycId=<c:out value="${partnerBean.kycId}" />&index=1" target="new"><c:out value="${partnerBean.attachment1FileName}"/></a>
                        </td>
                        <td><label>Attachment 2</label></td>
                        <td>
                            <input type="file" name="param101" id="param101">
                            <a href="kycAttachment.fin?kycId=<c:out value="${partnerBean.kycId}" />&index=2" target="new"><c:out value="${partnerBean.attachment2FileName}"/></a>
                        </td>
                    </tr>
                    <tr>
                        <td><label>Attachment 3</label></td>
                        <td><input type="file" name="param102" id="param102"></td>
                        <td><label>Attachment 4</label></td>
                        <td><input type="file" name="param103" id="param103"></td>
                    </tr>
                    
                </table>
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" class="finbutton">Save</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                        <td><button type="submit" class="finbutton">Exit</button></td>
                    </tr> 
                </table>

            </html:form>
            <div id="dialog-confirm" title="Partner Account Detail"/>
        
    </body>
</html>
