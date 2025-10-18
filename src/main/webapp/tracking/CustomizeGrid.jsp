
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<!DOCTYPE html>
<html>
    <html:form method="post" command="trackingBean" action="seaExportTrackingGridSave.fin">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta http-equiv="Pragma" content="no-cache">
            <meta http-equiv="Expires" content="-1">
            <title>E-Tracking</title>
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

                function moveUp(index) {
                    swap($('input[id="original"]').get(index), $('input[id="original"]').get(index - 1));
                    swap2($('input[id="display"]').get(index), $('input[id="display"]').get(index - 1));
                    swap($('input[id="priority"]').get(index), $('input[id="priority"]').get(index - 1));
                    swap($('input[id="description"]').get(index), $('input[id="description"]').get(index - 1));
                    swap($('input[id="datalength"]').get(index), $('input[id="datalength"]').get(index - 1));
                }

                function moveDown(index) {
                    swap($('input[id="original"]').get(index), $('input[id="original"]').get(index + 1));
                    swap2($('input[id="display"]').get(index), $('input[id="display"]').get(index + 1));
                    swap($('input[id="priority"]').get(index), $('input[id="priority"]').get(index + 1));
                    swap($('input[id="description"]').get(index), $('input[id="description"]').get(index + 1));
                    swap($('input[id="datalength"]').get(index), $('input[id="datalength"]').get(index + 1));
                }

                function swap(src, dest) {
                    var tmp = src.value;
                    src.value = dest.value;
                    dest.value = tmp;
                }

                function swap2(src, dest) {
                    var tmp = src.checked;
                    src.checked = dest.checked;
                    dest.checked = tmp;
                }


            </script>
        </head>
        <body>
            <div class="comdiv">
                <header>
                    Customize Grid
                </header>
                 <table class="tablec">
                     <c:if test="${trackingBean.errorMsg != null}">
                    <tr>
                        <th colspan="2"><c:out value="${trackingBean.errorMsg}"/></th>
                    </tr>
                    </c:if>
                    <tr>
                        <td><label>GRID NAME</label></td>
                        <td>
                            <html:input path="gridName" class="medium"/>
                            <html:input path="gridSeq" class="small readonly"/>
                            <html:hidden path="formId" class="small readonly"/>
                        </td>
                        
                    </tr>
                 </table>
            </div>
            <div>
                <div class="rows">
                    <table class="tablec" id="tbl">
                        <thead>
                            <tr>
                                <th>Priority</th>
                                <th>Original</th>
                                <th>Display</th>
                                <th>Description</th>
                                <th>Data Length</th>
                                <th></th>
                            </tr>
                        </thead>
                        <c:forEach items="${command.trackingVoList}" var="trackingVo" varStatus="status">
                            <tr>
                                <td>

                                    <html:input path="trackingVoList[${status.index}].column5" class="small" id="priority"/>
                                </td>                                
                                <td>
                                    <html:input path="trackingVoList[${status.index}].column1" id="original" class="large"/>
                                </td>
                                <td>
                                    <html:checkbox path="trackingVoList[${status.index}].column4" id="display" value="Y"/>
                                </td>
                                <td>
                                    <html:input path="trackingVoList[${status.index}].column2" id="description" class="large"/>
                                </td>
                                <td>
                                    <html:input path="trackingVoList[${status.index}].column3" class="small" id="datalength"/>
                                </td>
                                <td>
                                    <input type="button" value="Up" onClick="moveUp(${status.index})">
                                    <input type="button" value="Down" onClick="moveDown(${status.index})">
                                </td>
                            </tr>
                        </c:forEach>

                    </table>
                </div>
            </div>

            <table class="tb2">
                <tr>
                    <td><input type="submit" class="finbutton" value="Save"></td>
                    <td><button type="reset" class="finbutton">Reset</button></td>
                </tr>
            </table>   
        </html:form>
    </body>
</html>