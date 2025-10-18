<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="Popup.jsp" %>
<!DOCTYPE html>
<html>
    <html:form autocomplete="off" method="post" id= "popup3ViewForm">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>BL Print</title>
            <script>
                function printMe(type) {
                    var author = $("#author").val();
                    window.open('blSysGeneratedPDF.fin?blNo=${blBean.blNumber}&type=' + type+'&author='+author);
                }
            </script>
        </head>
        <body>
            <header>
                BL Print
            </header>
            <table id="example" class="display" cellspacing="0" height="100%">
                <tr>
                    <td><br/></td>
                </tr>
                <tr>
                    <td>
                        <html:hidden path="blNumber"/>
                        <label>No of Original:</label>
                    </td>
                    <td>
                        <html:select path="start">
                            <html:option value="1">1</html:option>
                            <html:option value="2">2</html:option>
                            <html:option value="3">3</html:option>
                            <html:option value="0">SW</html:option>
                        </html:select>
                    </td>
                    
                </tr>
                <tr>
                    <td><br/></td>
                </tr>
                <tr>
                    <c:if test="${blBean.start != 0}">
                        <td><button type="button" class="finbutton" id="original" onclick="printMe(1)">Original</button></td>
                    </c:if>
                    <td><button type="button" class="finbutton" id="non" onclick="printMe(2)">Non-Negotiable</button></td>
                    <td><button type="button" class="finbutton" id="tnc" onclick="printMe(3)">Terms & Condition</button></td>
                </tr>

            </table>
        </html:form>
    </body>
</html>
