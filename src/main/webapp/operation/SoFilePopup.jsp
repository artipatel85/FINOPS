si<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="Popup.jsp" %>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form commandName="fileUploadBean" id="fileUploadForm" method="post" enctype="multipart/form-data">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>So File Upload</title>
            <script>
                function printMe(type) {
                    var author = $("#author").val();
                    window.open('blSysGeneratedPDF.fin?blNo=${blBean.blNumber}&type=' + type + '&author=' + author);
                }

                function uploadAttachments()
                {
                    $('#fileUploadForm').attr("action", "uploadSoFile.fin");
                    $("#fileUploadForm").submit();
                }
            </script>
        </head>
        <body>
            <header>
                So File Upload
            </header>
            <table id="example" class="display" cellspacing="0" height="100%">
                <tr>
                    <td><br/></td>
                </tr>
                <tr>
                    <td>
                        <label>Booking Ref Number</label>
                        <html:input path="param1"/>
                    </td>
                    <td>
                        <label>So Number</label>
                        <html:input path="param2"/>
                    </td>
                    <td>
                        <label>POD</label>
                        <html:input path="param3"/>
                    </td>
                </tr>
                <c:forEach items="${command.rowList}" var="fileUploadRow" varStatus="status">
                <tr>
                    <td>
                        <c:if test="${fileUploadRow.fileName == null}">
                            <label for="file1"><html:input path="rowList[${status.index}].description" value="${fileUploadRow.description}"/></label>
                        </c:if>
                        <c:if test="${fileUploadRow.fileName != null}">
                            <label for="file1"><html:input path="rowList[${status.index}].description" value="${fileUploadRow.description}"/></label>
                        </c:if>
                    </td>
                    <td>
                        <c:if test="${fileUploadRow.fileName == null}">
                            <input type="file" name="rowList[${status.index}].document">
                        </c:if>
                        <c:if test="${fileUploadRow.fileName != null}">
                            <a target="new" href="soFileDownload.fin?bookingNum=${fileUpload.bookingRefNumber}&fileName=${fileUploadRow.fileName}">${fileUploadRow.fileName}</a>
                            <input type="file" name="rowList[${status.index}].document">
                        </c:if>
                    </td>
                </tr>
                </c:forEach>
                <tr>
                    <td><button type="button" class="finbutton" onclick="uploadAttachments()">Upload</button></td>
                    
                </tr>
            </table>
        </html:form>
    </body>
</html>
