<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="Bootstrap_jquery.jsp" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>GST - JSON</title>
        <link type="text/css" href="<c:url value='/assets/css/bootstrap.min.css' />" rel="stylesheet" />
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
            .ui-accordion .ui-accordion-header {
                display: block;
                cursor: pointer;
                position: relative;
                text-align: center;
                margin: 1px 0 0 0;
                font-size: 13px;
                background-color: #23527c;
                margin-left: 0px;
                margin-right: 0px;
                color: white;
                margin-bottom: 5px;
                font-family: cursive;
                padding: 2px 2px 2px 2px;
                border-top: 1px solid #23527c;
            }
            .ui-accordion .ui-accordion-content {
                border-top: 0;
                margin-left: 0px;
                margin-right: 0px;
            }
            .fa{
                float: right;
                padding-right: 10px;
                padding-top: 5px;  
            }
        </style>
        <script lang="javascript">

        </script>
    </head>
    <body>
        <div class="comdiv">
            <header>
                <a href="billTemplateSearch.fin"><i class="fa fa-arrow-left" style="font-size:20px;color:white"></i></a>
                Settings
            </header>
            <html:form method="post" id="finactPropertiesBean" action="updateFinactProps.fin">
                <div id="accordion">
                    <table id="table" class="tablec">
                        <tr>
                            <td><label>Cut Off Date</label></td>
                            <td><html:input path="cutOffDate" id="date" class="medium"/></td>

                        </tr>
                        <tr>
                            <td><label>Report Path</label></td>
                            <td><html:input type="text" path="reportPath" class="largeXL readonly" readonly="true"/></td>
                        </tr>

                    </table>
                    <table class="tb2">
                        <tr>
                            <td><td><input type="submit" id="submitButton" class="finbutton" value="Update"/></td></td>

                        </tr>
                    </table>
                </div>
            </html:form>
        </div>
        
        <div class="comdiv">
            <header>
                Balance Transfer
            </header>
            <html:form method="post" id="finactPropertiesBean" action="balanceTransfer.fin">
                <div id="accordion">                    
                    <table class="tb2">
                        <tr>
                            <th colspan="4"><c:out value="${finactPropertiesBean.balanceMsg}"/></th>
                        </tr>
                        <tr>
                            <td><td><input type="submit" id="submitButton" class="finbutton" value="Transfer"/></td>
                        </tr>
                    </table>
                </div>
            </html:form>
        </div>
        

        
        <div class="comdiv">
            <header>
                Night Cron Job
            </header>
            <html:form method="post" id="f  inactPropertiesBean" action="processCron.fin">
                <div id="accordion">                    
                    <table class="tb2">
                        <tr>
                            <th colspan="4"><c:out value="${finactPropertiesBean.dailyMsg}"/></th>
                            <html:input type="text" path="acctYear" id="date" class="medium" readonly="true"/>
                        </tr>
                        <tr>
                            <td><td><input type="submit" id="submitButton" class="finbutton" value="Run"/></td>
                        </tr>
                    </table>
                </div>
            </html:form>
        </div>
</body>
</html>