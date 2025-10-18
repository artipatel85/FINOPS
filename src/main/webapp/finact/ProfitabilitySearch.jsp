
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<script src="finactjs/partner.js"></script>
<script src="finactjs/finance.js"></script>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <title>Profitability</title>
        <script>
            $(document).ready(function () {



                $("#profitabilityForm").validate({

                    rules: {
                        param4: {required: true, date: true},
                        param5: {required: true, date: true}


                    },
                    messages: {
                        param4: '<div class="tool">* field is required.</div>',
                        param5: '<div class="tool">* field is required.</div>'

                    }

                });

                $("#search").on("click", function () {
                    $('#profitabilityForm').attr("action", "profitability.fin");
                    $("#profitabilityForm").submit();
                });
            });
            function excel() {
                $('#profitabilityForm').attr("action", "profitabilityXLSReport.fin");
                $("#profitabilityForm").submit();
            }

        </script>
    </head>
    <body>
        <div class="main">
            <header>
                Profitability
            </header>
            <html:form method="post" id="profitabilityForm" commandName="reportBean">
                <table class="tablec">
                    <col width="10%">
                    <col width="80%">

                    <tr>
                        <td><label>Start Date</label></td>
                        <td><html:input type="text" path="param4" id="date" class="medium"/></td>

                    </tr>
                    <tr>
                        <td><label>End Date</label></td>
                        <td><html:input type="text" path="param5" id="date2" class="medium" /></td>
                    </tr>
                    <tr>
                        <td><label>Branch</label></td>
                        <td><html:select path="param3">
                                <html:option value="">SELECT</html:option>
                                <html:option value="SFP">SFP</html:option>
                                <html:option value="SFPM">SFPM</html:option>
                                <html:option value="SFPC">SFPC</html:option>
                                <html:option value="SFPK">SFPK</html:option>
                                <html:option value="SFPG">SFPG</html:option>
                            </html:select>
                        </td>
                    </tr>
                    <tr>
                        <td><label>SEA / AIR</label></td>
                        <td><html:select path="param8">
                                <html:option value="">SELECT</html:option>
                                <html:option value="SEA">SEA</html:option>
                                <html:option value="AIR">AIR</html:option>
                            </html:select>
                        </td>
                    </tr>
                    <tr>
                        <td><label>EXPORT / IMPORT</label></td>
                        <td><html:select path="param9">
                                <html:option value="">SELECT</html:option>
                                <html:option value="EXPORT">EXPORT</html:option>
                                <html:option value="IMPORT">IMPORT</html:option>
                            </html:select>
                        </td>
                    </tr>
                    <tr>
                        <td><label>BL NO</label></td>
                        <td><html:input type="text" path="param6" class="largeXL" id="param6"/></td>
                    </tr>
                    <tr>
                        <td><label>JOB NO</label></td>
                        <td><html:input type="text" path="param7" class="largeXL" id="param7"/></td>
                    </tr>
                    <tr>
                        <td><label>PARTY</label></td>
                        <td><html:input type="text" path="param10" class="largeXL" id="param10"/></td>
                    </tr>
                    <tr>
                        <td><label>SALESMAN</label></td>
                        <td><html:input type="text" path="param11" class="largeXL" onkeyup="partnerPopup('SALESMAN_1','#param11', '#param12')"/>
                            <html:input type="text" path="param12" class="medium" />
                        </td>
                    </tr>

                </table>
                <table class="tb2">
                    <tr>
                        <td><button type="button" class="finbutton"  id="search">VIEW</button></td>
                        <td><button type="button" class="finbutton" onClick="excel()" >Excel</button></td>
                    </tr>
                </table>

            </html:form>
        </div>
    </body>
</html>
