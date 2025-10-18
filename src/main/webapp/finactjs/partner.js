var source = "autoCompletePartner.fin?param=";

function selectEvent(event, ui, param2, param3, param4, param5){
    $(param2).val(ui.item.param2);
    $(param3).val(ui.item.param3);
    $(param4).val(ui.item.param4);
    $(param5).val(ui.item.param5);
}

function partnerPopup(param, id, value, param2, param3, param4, param5) {
    $(id).autocomplete({
        autoFocus: true,
        source: source+param,
        select: function (event, ui) {
            selectEvent(event, ui, value, param2, param3, param4, param5);
        }
    });
}


function originWareHouse(){
    $("#originWareHouse").autocomplete({
        autoFocus: true,
        source: source,
        select: function (event, ui) {
            selectEvent(event, ui, "#originWareHouseName", "#originWareHouseContactDetails");
        }
    });
}

function actConsignee(){
    $("#actConsignee").autocomplete({
        autoFocus: true,
        source: source,
        select: function (event, ui) {
            selectEvent(event, ui, "#actConsigneeName");
        }
    });
}

function shipper(){
    $("#shipper").autocomplete({
        autoFocus: true,
        source: source,
        select: function (event, ui) {
            selectEvent(event, ui, "#shipperName","#shipperContactDetails");
        }
    });
}

function carrierCode(){
    $("#carrierCode").autocomplete({
        autoFocus: true,
        source: source,
        select: function (event, ui) {
            selectEvent(event, ui, "#carrierName");
        }
    });
}

function consignee(){
    $("#consignee").autocomplete({
        autoFocus: true,
        source: source,
        select: function (event, ui) {
            $("#consigneeName").val(ui.item.param2);
            $("#consigneeContactDetails").val(ui.item.param3);
        }
    });
}

function soNotify(){
    $("#soNotify").autocomplete({
        autoFocus: true,
        source: source,
        select: function (event, ui) {
            selectEvent(event, ui, "#soNotifyName","#soNotifyContactDetails");
        }
    });
}

function alsoNotify(){
    $("#alsoNotify").autocomplete({
        autoFocus: true,
        source: source,
        select: function (event, ui) {
            selectEvent(event, ui, "#alsoNotifyName","#alsoNotifyContactDetails");
        }
    });
}

function por(){
    $("#por").autocomplete({
        autoFocus: true,
        source: port0,
        select: function (event, ui) {
            selectEvent(event, ui, "#porName");
        }
    });
}

function pol(){
    $("#pol").autocomplete({
        autoFocus: true,
        source: port0,
        select: function (event, ui) {
            selectEvent(event, ui, "#polName");
        }
    });
}

function deliveryAt(){
    $("#deliveryAt").autocomplete({
        autoFocus: true,
        source: port0,
        select: function (event, ui) {

        }
    });
}

function from(){
    $("#from").autocomplete({
        autoFocus: true,
        source: port0,
        select: function (event, ui) {
            selectEvent(event, ui, "#fromName");
        }
    });
}

function pod(){
    $("#pod").autocomplete({
        autoFocus: true,
        source: port0,
        select: function (event, ui) {
            selectEvent(event, ui, "#podName");
        }
    });
}

function dest(){
    $("#dest").autocomplete({
        autoFocus: true,
        source: port0,
        select: function (event, ui) {
            selectEvent(event, ui, "#destName");
        }
    });
}

function upTo(){
    $("#upTo").autocomplete({
        autoFocus: true,
        source: port0,
        select: function (event, ui) {
            selectEvent(event, ui, "#upToName");
        }
    });
}

function loadingAgent(){
    $("#loadingAgentCode").autocomplete({
        autoFocus: true,
        source: source,
        select: function (event, ui) {
            selectEvent(event, ui, "#loadingAgentName","#loadingAgentContactDetails");
        }
    });
}

function destinationAgent(){
    $("#destinationAgentCode").autocomplete({
        autoFocus: true,
        source: source,
        select: function (event, ui) {
            selectEvent(event, ui, "#destinationAgentName","#destinationAgentContactDetails");
        }
    });
}

function actShipperName(){
    $("#actShipperName").autocomplete({
        autoFocus: true,
        source: sourceName,
        select: function (event, ui) {
            selectEvent(event, ui, "#actShipper");
        }
    });
}

function carrierName(){
    $("#carrierName").autocomplete({
        autoFocus: true,
        source: sourceName,
        select: function (event, ui) {
            selectEvent(event, ui, "#carrierCode");
        }
    });
}

function actConsigneeName(){
    $("#actConsigneeName").autocomplete({
        autoFocus: true,
        source: sourceName,
        select: function (event, ui) {
            selectEvent(event, ui, "#actConsignee");
        }
    });
}

function shipperName(){
    $("#shipperName").autocomplete({
        autoFocus: true,
        source: sourceName,
        select: function (event, ui) {
            selectEvent(event, ui, "#shipper","#shipperContactDetails");
        }
    });
}

function originWareHouseName(){
    $("#originWareHouseName").autocomplete({
        autoFocus: true,
        source: sourceName,
        select: function (event, ui) {
            selectEvent(event, ui, "#originWareHouse", "#originWareHouseContactDetails");
        }
    });
}

function consigneeName(){
    $("#consigneeName").autocomplete({
        autoFocus: true,
        source: sourceName,
        select: function (event, ui) {
            $("#consignee").val(ui.item.param2);
            $("#consigneeContactDetails").val(ui.item.param3);
        }
    });
}

function soNotifyName(){
    $("#soNotifyName").autocomplete({
        autoFocus: true,
        source: sourceName,
        select: function (event, ui) {
            selectEvent(event, ui, "#soNotify","#soNotifyContactDetails");
        }
    });
}

function alsoNotifyName(){
    $("#alsoNotifyName").autocomplete({
        autoFocus: true,
        source: sourceName,
        select: function (event, ui) {
            selectEvent(event, ui, "#alsoNotify","#alsoNotifyContactDetails");
        }
    });
}

function destinationAgentName(){
    $("#destinationAgentName").autocomplete({
        autoFocus: true,
        source: sourceName,
        select: function (event, ui) {
            selectEvent(event, ui, "#destinationAgentCode","#destinationAgentContactDetails");
        }
    });
}

function loadingAgentName(){
    $("#loadingAgentName").autocomplete({
        autoFocus: true,
        source: sourceName,
        select: function (event, ui) {
            selectEvent(event, ui, "#loadingAgentCode","#loadingAgentContactDetails");
        }
    });
}