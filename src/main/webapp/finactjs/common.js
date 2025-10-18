/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//alert(2);
function toggleIcon(e) {
    $(e.target)
            .prev('.panel-heading')
            .find(".more-less")
            .toggleClass('glyphicon-plus glyphicon-minus');
}
$('.panel-group').on('hidden.bs.collapse', toggleIcon);
$('.panel-group').on('shown.bs.collapse', toggleIcon);

function autopopup(id, url, callback) {
    $(id).autocomplete({
        autoFocus: true,
        source: function (request, response) {
            $.getJSON(url, {
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
            });
        },
        select: callback
    });
}

function dialogWindow(id, url, width, pos, height) {
    $(id).dialog({
        autoOpen: true,
        resizable: false,
        height: height,
        width: width,
        modal: true,
        refresh: true,
        position: pos,
        show: {
            effect: "blind",
            duration: 1000
        }

    });
    $(id).load(url).dialog("open");
}