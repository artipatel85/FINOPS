/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//--------------------------------------------------date-------------------------------
//$(function () {
//    $("#date").datepicker({
//        dateFormat: 'yy-mm-dd',
//        defaultDate: new Date()
//    });
//    $("#date").datepicker('setDate', new Date());
//});
$(function () {
    $("#date").datepicker({
        changeMonth: true,
        changeYear: true,
        dateFormat: 'yy-mm-dd'
    });
});
$(function () {
    $("#date2").datepicker({
        changeMonth: true,
        changeYear: true,
        dateFormat: 'yy-mm-dd'
    });
});
$(function () {
    $("#date3").datepicker({
        changeMonth: true,
        changeYear: true,
        dateFormat: 'yy-mm-dd'
    });
});
$(function () {
    $("#date4").datepicker({
        changeMonth: true,
        changeYear: true,
        dateFormat: 'yy-mm-dd'
    });
});
//----------------------------------------------- autocomplete CURRENCY----------------------       
$(function () {
    var wordlist = ["AUD", "EUR", "FRF", "GBP",
        "HKD", "INR", "USD", "YEN"];

    $("#currency").autocomplete({
        source: function (req, responseFn) {
            var re = $.ui.autocomplete.escapeRegex(req.term);
            var matcher = new RegExp("^" + re, "i");
            var a = $.grep(wordlist, function (item, index) {
                return matcher.test(item);
            });
            responseFn(a);
        }
    });
});

$(function () {
    var wordlist = ["AUD", "EUR", "FRF", "GBP",
        "HKD", "INR", "USD", "YEN"];

    $("#currency1").autocomplete({
        source: function (req, responseFn) {
            var re = $.ui.autocomplete.escapeRegex(req.term);
            var matcher = new RegExp("^" + re, "i");
            var a = $.grep(wordlist, function (item, index) {
                return matcher.test(item);
            });
            responseFn(a);
        }
    });
});
//----------------------------------Salesmanwise autocomplete----------------------
 