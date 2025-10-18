
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <head>
        <html:form autocomplete="off" method="post" id="adminForm" action="saveDocSerialNo.fin">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <link href="<c:url value="/finactcss/rowForm.css"/>" rel="stylesheet">
            <title>Doc Serial Number</title>

            <script lang="javascript">

                
            </script>
        </head>
        <body>
            <div class="comdiv">

                <header>
                    Doc Serial Number
                </header>
                <div class="rows2 rows">
                    <iframe frameborder="0" style="position:absolute;width:1px;"></iframe>
                    <table class="tablevou" id="tbl" >
                        <thead>
                            <tr>
                                <th><label>TYPE</label></th>     
                                <th><label>SFP/DELHI</label></th>
                                <th><label>SFPM/MUMBAI</label></th>
                                <th><label>SFPC/CHENNAI</label></th>
                                <th><label>SFPK/KOCHI</label></th>
                                <th><label>SFPG/GANDHIDHAM</label></th>
                            </tr>
                        </thead>
                        <c:forEach items="${command.reconList}" var="docData" varStatus="status">
                            <tr>
                                <td>
                                    <html:input path="reconList[${status.index}].param1" value="${docData.param1}" cssClass="smallplus readonly" readonly="true"/>
                                    
                                </td>
                                <td>
                                    <html:input path="reconList[${status.index}].param2" value="${docData.param2}" cssClass="small"/>
                                </td>
                                <td>
                                    <html:input path="reconList[${status.index}].param3" value="${docData.param3}" cssClass="small"/>
                                </td>
                                <td>
                                    <html:input path="reconList[${status.index}].param4" value="${docData.param4}" cssClass="small"/>
                                </td>
                                <td>
                                    <html:input path="reconList[${status.index}].param5" value="${docData.param5}" cssClass="small"/>
                                </td>
                                <td>
                                    <html:input path="reconList[${status.index}].param6" value="${docData.param6}" cssClass="small"/>
                                </td>
                            </tr>
                        </c:forEach>

                    </table>
                </div>

                <div class="comdivfoot">

                    <table class="tablefooter">
                        <tr>
                            <td><button type="submit" class="finbutton">SAVE</button></td>
                            <td><button type="reset" class="finbutton">RESET</button></td>
                        </tr>
                    </table>
                </div>   
            </html:form>

    </body>
</html>
