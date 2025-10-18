
<%@ page isELIgnored="false" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="/finact/Bootstrap_jquery.jsp" %>
<%@include file="/finact/DataTable_Bootstrap_jquery.jsp" %>
<script src="finactjs/so.js"></script>
<script src="finactjs/jquery.validate.min.js"></script>
<!DOCTYPE html>
<html>
    <head>
        <html:form autocomplete="off" name="jobForm" command="jobBean" method="post" id="jobForm" action="saveJob.fin">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <title>JOB</title>

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

                function exitContainer() {
                    location.reload();
                }

                $(document).ready(function () {
                    var table = $('#example').DataTable({
                        ajax: "soContainerByJobGson.fin?jobNumber=${jobBean.jobNumber}",
                        "serverSide": true,
                        "bJQueryUI": true,
                        "bRetrieve": true,
                        "bProcessing": true,
                        "bFilter": false,
                        "dom": '<"top">t',
                        "deferLoading": 0,
                        "lengthMenu": [20, 30, 50],
                        "aoColumns": [
                            {"mData": "number"},
                            {"mData": "customSealNumber"},
                            {"mData": "lineSealNumber"},
                            {"mData": "serviceTerm"}


                        ]
                    });
                    drawContainerTable();
                });


                $(document).ready(carrierCode);
                $(document).ready(por);
                $(document).ready(pol);
                $(document).ready(polName);
                $(document).ready(pod);
                $(document).ready(dest);
                $(document).ready(loadingAgent);
                $(document).ready(destinationAgent);
                $(document).ready(carrierName);
                $(document).ready(loadingAgentName);
                $(document).ready(destinationAgentName);

                $(function () {
                    $("#jobDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#jobDate").datepicker('setDate', new Date());

                    $("#sobDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#cinvdate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#etd").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#eta").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#cfsDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#cyDate").datepicker({
                        dateFormat: 'yy-mm-dd',
                        defaultDate: new Date()
                    });

                    $("#jobSoBtn").on("click", function () {
                        dialogWindow("#dialog-so","soListInJob.fin?jobNumber=${jobBean.jobNumber}&param=${command.expImp}",
                                                    "1200", "['middle', 500]", "auto");

                    });

                    $("#addCargoBtn").on("click", function () {
                        $('#accordion').accordion({
                            active: false,
                            collapsible: true
                        });
                    });


                });

                function doPrint()
                {
                    if (confirm("DO U Wish To Print The Record??"))
                    {
                        window.open('jobPDF.fin?jobNumber=${jobBean.jobNumber}');
                    }
                }


                function drawContainerTable(event) {
                    if (${soBean.soNumber != ''}) {
                        $('#example').DataTable().draw();
                    }
                }

                $(document).ready(function () {
                    $("#jobForm").validate({
                        rules: {
                            jobDate:{required: true},
                            etd: {required: true},
                            eta: {required: true},
                            pol: {required: true},
                            pod: {required: true}
                        },
                        messages: {
                            jobDate: '<label class="tool">* field is required.</label>',
                            etd: '<label class="tool">* field is required.</label>',
                            eta: '<label class="tool">* field is required.</label>',
                            pol: '<label class="tool">* field is required.</label>',
                            pod: '<label class="tool">* field is required.</label>'
                        }
                    });
                });

            </script>
        </head>
        <body>
            <div id="main">

                <header>
                    Sea Job - ${command.expImp}
                </header>
                <table class="table-noborder" width="100%">
                    <c:if test="${soBean.errorMsg != null}">
                        <tr>
                            <th colspan="6"><c:out value="${jobBean.errorMsg}"/></th>
                        </tr>
                    </c:if>
                    <tr>
                        <td width="8%"><label class="required">JOB NUMBER</label></td>
                        <td width="29%">
                            <c:if test="${jobBean.jobNumber != null}">
                                <label><c:out value="${jobBean.jobNumber}"/></label>
                            </c:if>
                            <c:if test="${jobBean.jobNumber == null}">
                                <label><c:out value="AUTO GENERATED"/></label>
                            </c:if>
                            <html:hidden path="jobNumber" id="jobNo"/>
                            <html:hidden path="jobAutoSequence" />
                            <html:hidden path="expImp"/>
                        </td>
                        <td width="8%"><label class="required">JOB DATE</label></td>
                        <td width="29%"><html:input path="jobDate" id="jobDate" class="smallplus"/></td>
                    </tr>
                    <tr>
                        <td><label>POR</label></td>
                        <td>
                            <html:input path="porName" class="medium" id="porName"/>
                            <html:input path="por" class="small" id="por"/>
                        </td>
                        <td><label class="required">POL</label></td>
                        <td>
                            <html:input path="polName" class="medium" id="polName"/>
                            <html:input path="pol" class="small" id="pol"/>
                        </td>

                    </tr>

                    <tr>
                        <td><label class="required">POD</label></td>
                        <td>
                            <html:input path="podName" class="medium" id="podName"/>
                            <html:input path="pod" class="small" id="pod"/>
                        </td>
                        <td><label>DEST</label></td>
                        <td>
                            <html:input path="destName" class="medium" id="destName"/>
                            <html:input path="dest" class="small" id="dest"/>
                        </td>

                    </tr>

                    <tr>
                        <td><label class="required">VSL</label></td>
                        <td>
                            <html:input path="vsl" class="medium"/>
                        </td>
                        <td><label class="required">VOY</label></td>
                        <td>
                            <html:input path="voy" class="medium"/>
                        </td>

                    </tr>

                    <tr>
                        <td><label class="required">ETD</label></td>
                        <td>
                            <html:input path="etd" class="smallplus"/>
                        </td>
                        <td><label class="required">ETA</label></td>
                        <td>
                            <html:input path="eta" class="smallplus"/>                            
                        </td> 
                    </tr>
                    <tr>
                        <td><label>CARRIER</label></td>
                        <td>
                            <html:input path="carrierName" class="large"/>
                            <html:input path="carrierCode" class="small" id="carrierCode"/>

                        </td>
                        <td><label>SOB DATE</label></td>
                        <td>
                            <html:input path="sobDate" id="sobDate" class="smallplus"/>
                        </td>
                    </tr>
                    <tr>
                        <td><label>LOADING AGENT</label></td>
                        <td>
                            <html:input path="loadingAgentName" class="large" id="loadingAgentName"/>
                            <html:input path="loadingAgentCode" class="small" id="loadingAgentCode"/>

                        </td>
                        <td><label>DEST. AGENT</label></td>
                        <td>
                            <html:input path="destinationAgentName" class="large" id="destinationAgentName"/>
                            <html:input path="destinationAgentCode" class="small" id="destinationAgentCode"/>

                        </td>

                    </tr>

                    <tr>
                        <td><label>REMARK</label></td>
                        <td>
                            <html:textarea path="remarks" class="textareaL"/>
                        </td>
                        <td><label>STATUS</label></td>
                        <td>
                            <html:select path="status" class="smallplus">
                                <html:option value="A">Active</html:option>
                                <html:option value="S">Suspend</html:option>
                            </html:select>                     
                        </td>   
                    </tr>
                    <c:if test="${jobBean.jobNumber != ''}">
                        <tr>
                            <td><label>PREPARED BY</label></td>
                            <td>
                                <label><c:out value="${jobBean.createdBy}"/>:<c:out value="${jobBean.creationDate}"/></label>
                            </td>
                            <td><label>AMENDED BY</label></td>
                            <td>
                                <label><c:out value="${jobBean.amendedBy}"/>:<c:out value="${jobBean.amendedDate}"/></label>
                            </td>

                        </tr>
                    </c:if>
                </table>
            </div> 

            <div class="comdivfoot">
                <table class="tablefooter">
                    <tr>
                        <td><button type="submit" class="finbutton">SAVE</button></td>
                        <td><button type="reset" class="finbutton">RESET</button></td>
                        <td><button type="submit" class="finbutton">EXIT</button></td>
                    </tr>
                    <c:if test="${jobBean.jobNumber != null}">
                    <tr>
                        <td><button type="button" class="finbutton">REFRESH</button></td>
                        <td><button type="button" id="shippingAdvice" onclick="doPrint()" class="finbutton">SHIPPING ADVICE</button></td>
                        <td><button type="button" class="finbutton" id="jobSoBtn">SHIPPING ORDER</button></td>
                        <td><button type="button" class="finbutton">CONTAINER CONTROL</button></td>
                        <td><button type="button" class="finbutton">GENERATE B/L</button></td>
                    </tr>
                    </c:if>
                </table>
            </div>   

            <c:if test="${jobBean.jobNumber != null}">
                <table id="example" class="display" cellspacing="0" width="100%">
                    <thead>
                        <tr>
                            <th>CONTAINER NO/SIZE</th>
                            <th>C.SEAL</th>
                            <th>L.SEAL</th>
                            <th>SERVICE TERM</th>
                        </tr>
                    </thead>                
                </table>
            </c:if>            
        </html:form> 
        <div id="dialog-confirm" title="Sailing Schedule Popup"/>
        <div id="dialog-so" title="Shipping Orders"/>
    </body>
</html>
