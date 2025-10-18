function polPopup(src, id) {
    autopopup(src, "popup3ViewAutoPort.fin", function (event, ui) {
        $(id).val(ui.item.data);
    });
}