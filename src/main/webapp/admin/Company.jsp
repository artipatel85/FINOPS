<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@include file="/finact/Bootstrap_jquery.jsp" %>
        <title>JSP Page</title>
        <script>
            $(document).ready(function () {
                $("#country1").autocomplete({
                    autoFocus: true,
                    source: function (request, response) {
                        $.getJSON("countrycode.fin", {
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
                            //response($.ui.autocomplete.filter(wordlist, request.term));
                        });
                    },
                    select: function (event, ui) {
                        $("#code").val(ui.item.data);
                    }
                });
            });
            
        </script>
    </head>
    <body>
        <header>
            <a href="companySearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
            COMPANY PROFILE
        </header>
        <div class="main">

            <html:form method="post" id="companyform" action="companySave.fin" command="adminBean">
                <table class="tablec">
                    <col width="160PX">
                    <col width="350PX">
                    <col width="160PX">
                    <col width="350PX">
                    <tr>
                        <td><label>COMPANY CODE</label></td>
                        <td><html:input path="companyCode" class="medium"/>
                        <html:input path="companyId" class="small readonly" readonly="true"/></td>
                        <td><label>COMPANY NAME</label></td>
                        <td><html:input path="companyName1" class="largeXL"/></td>
                    </tr>
                    <tr>
                        <td><label>Address</label></td>
                        <td><html:textarea  path="address" rows="4" cols="40"></html:textarea></td>
                            <td><label>Address1</label></td>
                            <td><html:textarea  path="address2" rows="4" cols="40"></html:textarea></td>   
                        </tr>
                        <tr>
                            <td><label>Tel No</label></td>
                            <td><html:input path="tel_Cc" class="small"/>
                            <html:input path="tel_Ac" class="small"/>
                            <html:input path="tel_No" class="medium"/></td>
                        <td><label>Fax No</label></td>
                        <td><html:input path="fax_Cc" class="small"/>
                            <html:input path="fax_Ac" class="small"/>
                            <html:input path="fax_No" class="medium"/></td>
                    </tr>
                     <tr>
                        <td><label>City</label></td>
                        <td><html:input path="cityName" class="medium"/></td>
                        <td><label>COUNTRY</label></td>
                        <td><html:input path="countryName" id="country1" class="large"/>
                        <html:input path="countryCode" id="code" class="small readonly" readonly="true"/></td>
                     </tr>
                      <tr>
                        <td><label>EMAIL</label></td>
                        <td><html:input path="eMail" class="large"/></td>
                        <td><label>WEBSITE</label></td>
                        <td><html:input path="webSite" class="large"/></td>
                     </tr>
                      <tr>
                        <td><label>C PERSON</label></td>
                        <td><html:input path="contactPerson" class="medium"/></td>
                        <td><label>STATUS</label></td>
                        <td><html:select path="selectedStatus" class="select2 medium">
                        <html:option value="A">Active</html:option>
                        <html:option value="S">Suspend</html:option>
                    </html:select> </td>
                     </tr>
                     <tr>
                        <td><label>PAN NO</label></td>
                        <td><html:input path="panNo" class="medium"/></td>
                        <td><label>TAN NO</label></td>
                        <td><html:input path="tanNo" class="medium"/></td>
                     </tr>
                     <tr>
                        <td><label>SERVICE TAX NO</label></td>
                        <td><html:input path="serviceTaxNo" class="large"/></td>
                        <td><label>NOTE</label></td>
                        <td><html:textarea  path="note" rows="4" cols="40"></html:textarea></td>   
                     </tr>
                </table>
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" id="btnsubmit" class="finbutton">Save</button></td>
                        <td><button type="reset" class="finbutton">Reset</button></td>
                        <td><button type="submit" class="finbutton"><a href="companySearch.fin" style="color: #FFF">Exit</a></button></td>
                    </tr> 
                </table>
            </html:form>
        </div>
    </body>
</html>
