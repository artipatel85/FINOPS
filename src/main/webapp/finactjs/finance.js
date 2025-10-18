var currencyEP = "autoComplete.fin?param=CURRENCY&type=-1";
var lineAccountEP = "autoComplete.fin?param=DEBTORCREDITOR_0&type=0";
var cashBankEP = "autoComplete.fin?param=CASHBANK_0&type=0";
var billTo = "autoComplete.fin?param=DEBTORCREDITOR_0&type=0";
var ga = "autoComplete.fin?param=UNDER_1&type=0";

function currency(){
    $("#currencyName").autocomplete({
        autoFocus: true,
        source: function (request, response) {
                $.getJSON(currencyEP, {
                    term: request.term

                }, function (result) {

                    var wordlist = ($.map(result, function (item) {
                        return {value: item.value, data: item.param2, code: item.param3}

                    }));
                    var re = $.ui.autocomplete.escapeRegex(request.term);
                    var matcher = new RegExp("^" + re, "i");
                    var a = $.grep(wordlist, function (item, index) {
                        return matcher.test(item.value);
                    });
                    response(a);
                    //alert(a);
                });
       },
        select: function (event, ui) {
            $("#rate").val(ui.item.data);
            $("#currencyId").val(ui.item.code);
        }
    });
}

function selectEvent(event, ui, param2, param3, param4){
    $(param2).val(ui.item.param2);
    $(param3).val(ui.item.param3);
}

function lineAccounts(src, idx){
    $(src).autocomplete({
        autoFocus: true,
        source: lineAccountEP,
        select: function (event, ui) {
            $('input[id="lineAcctId"]').get(idx).value = ui.item.param2;
            if(ui.item.param3 == 22){
                $('input[id="lineParentId"]').get(idx).value = 'D';
            }
            else if(ui.item.param3 == 29){
                $('input[id="lineParentId"]').get(idx).value = 'C';
            }
            else{
                $('input[id="lineParentId"]').get(idx).value = '';
            }
        }
    });
}

function hdrAccount(){
    $("#hdrAcctName").autocomplete({
        autoFocus: true,
        source: cashBankEP,
        select: function (event, ui) {
            selectEvent(event, ui, "#codeCombinationId");
        }
    });
}

function groupAccount(){
    $("#under").autocomplete({
        autoFocus: true,
        source: ga,
        select: function (event, ui) {
            selectEvent(event, ui, "#parentAcctId", "#acctTypeId");
        }
    });
}

function billToName(){
    $("#billToName").autocomplete({
        autoFocus: true,
        source: billTo,
        select: function (event, ui) {
            selectEvent(event, ui, "#partyAcctCode");
        }
    });
}