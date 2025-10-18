<%--
    Document   : Currency
    Created on : Jan 11, 2018, 8:51:14 AM
    Author     : BirenDesai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/partner.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@include file="/finact/Bootstrap_jquery.jsp" %>
        <title>FORM</title>
        <script>

            function countryPopup() {
                autopopup("#countryName", "countrycode.fin", function (event, ui) {
                    $("#countryCode").val(ui.item.data);
                });
            }

            function rolePopup() {
                autopopup("#userRole", "rolePopup.fin", function (event, ui) {
                    $("#userRole").val(ui.item.data);
                });
            }

            $(document).ready(function () {
                $("#userForm").validate({

                    rules: {
                        userProfileId: {required: true},
                        description: {required: true},
                        eMail: {required: true},
                        countryCode: {required: true},
                        userRole: {required: true},
                        password: {
                            required: true
                        },
                        confirmPassword: {
                            required: true,
                            equalTo: "#password"
                        }

                    },
                    messages: {
                        userProfileId: '<div class="tool">* field is required.</div>',
                        description: '<div class="tool">* field is required.</div>',
                        eMail: '<div class="tool">* field is required.</div>',
                        countryCode: '<div class="tool">* field is required.</div>',
                        userRole: '<div class="tool">* field is required.</div>',
                        password: {
                            required:'<div class="tool">* field is required.</div>'
                        },
                        confirmPassword: {
                            required:'<div class="tool">* field is required.</div>',
                            equalTo: '<div class="tool">Password and Confirm Password do not match.</div>'
                        }
                    }

                });
            });
        </script>
    </head>
    <body>
    <html:form method="post" id="userForm" action="userSave.fin" command="adminBean">
        <header>
            <a href="userSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
            USERS
        </header>
        <div class="main">
                <table class="tablec">
                    <col width="160PX">
                    <col width="350PX">
                    <col width="160PX">
                    <col width="350PX">
                    <tr>
                        <td><label>USER ID</label></td>
                        <td><html:input path="userProfileId" class="medium"/><html:hidden path="action" class="small"/></td>
                        <td><label>USER NAME</label></td>
                        <td><html:input path="description" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>PASSWORD</label></td>
                        <td><html:password path="password" class="medium" showPassword="true"/></td>
                        <td><label>CONFIRM PASSWORD</label></td>
                        <td><html:password path="confirmPassword" class="medium" showPassword="true"/></td>
                    </tr>
                    <tr>
                        <td><label>PHONE</label></td>
                        <td>
                            <html:input path="telCc" class="small"/>
                            <html:input path="telAc" class="small"/>
                            <html:input path="telNo" class="medium"/>
                        </td>
                        <td><label>FAX</label></td>
                        <td><html:input path="faxCc" class="small"/>
                            <html:input path="faxAc" class="small"/>
                            <html:input path="faxNo" class="medium"/></td>
                    </tr>
                    <tr>
                        <td><label>CITY</label></td>
                        <td><html:input path="cityName" class="medium"/></td>    
                        <td><label>COUNTRY</label></td>
                        <td>
                            <html:input path="countryName" class="medium" onkeyup="partnerPopup('COUNTRY_0','#countryName', '#countryCode')"/>
                            <html:input path="countryCode" class="small" id="countryCode"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>EMAIL</label></td>
                        <td><html:input path="emailId" class="large"/></td>
                        <td><label>REMARKS</label></td>
                        <td>
                            <html:textarea path="remarks" rows="4" cols="30"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>ROLE</label></td>
                        <td>
                        <html:select path="userRole" class="large">
                            <html:option value=""/>
                            <html:options items="${command.roleList}"/>
                        </html:select>
                        </td>
                        <td><label>IS FIN USER?</label></td>
                        <td>
                            <html:checkbox path="isFinanceUser" value="Y"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label>STATUS</label>
                        </td>
                        <td>
                            <html:select path="status" class="medium">
                                <html:option value="A">Active</html:option>
                                <html:option value="S">Suspend</html:option>
                            </html:select>
                        </td>
                    </tr>
                </table>
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" id="btnsubmit" class="finbutton">Save</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                        <td><button type="submit" class="finbutton"><a href="userSearch.fin" style="color: #FFF">Exit</a></button></td>
                    </tr> 
                </table>
            </html:form>
        </div>
    </body>
</html>
