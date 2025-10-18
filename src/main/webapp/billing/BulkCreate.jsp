
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

        <title>Bulk Create IRN</title>

        <script>
            $(document).ready(function () {
                $("#bulkCreate").on("click", function () {
                    $('#bulkCreateForm').attr("action", "performBulkCreateIRN.fin");
                    $("#bulkCreateForm").submit();
                });

            });

        </script>
    </head>
    <body>
        <div class="main">
            <header>
                Bulk Create IRN
            </header>

            <html:form method="post" action="processBulkCreateIRN.fin" id="bulkCreateForm" commandName="eInvoiceRequest">
                <table class="tablec">
                    <c:if test="${eInvoiceRequest.message != null}">
                        <tr>
                            <th colspan="8"><c:out value="${eInvoiceRequest.message}"/></th>
                        </tr>
                    </c:if>
                    <col width="10%">
                    <col width="80%">

                    <tr>
                        <td><label>Start Date</label></td>
                        <td><html:input type="text" path="startDate" id="date" class="medium"/></td>

                    </tr>
                    <tr>
                        <td><label>End Date</label></td>
                        <td><html:input type="text" path="endDate" id="date2" class="medium" /></td>
                    </tr>
                    <tr>
                        <td><label>Branch</label></td>
                        <td><html:select path="loadingAgent">
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
                        <td><label>Document Type</label></td>
                        <td><html:select path="documentType">
                                <html:option value="INVOICE">INVOICE</html:option>
                                <html:option value="CREDITNOTE">CREDITNOTE</html:option>
                                <html:option value="MISC">MISC</html:option>
                                <html:option value="DEBITNOTE">DEBITNOTE</html:option>
                            </html:select>
                        </td>
                    </tr>
                    
                    <tr>
                        <td><label>Type of Supply</label></td>
                        <td><html:select path="typeOfSupply">
                                <html:option value="B2B">B2B</html:option>
                                <html:option value="SEZWOP">SEZ WITHOUT PAYMENT</html:option>
                                <html:option value="EXPWP">EXPORT WITH PAYMENT</html:option>
                                <html:option value="EXPWOP">EXPORT WITHOUT PAYMENT</html:option>
                            </html:select>
                        </td>
                    </tr>
                </table>
                <table class="tb2">
                    <tr>
                        <td><button type="submit" class="finbutton" id="bulkCreate">Bulk Create</button></td>                        
                    </tr>
                </table>

            </html:form>
        </div>
    </body>
</html>
