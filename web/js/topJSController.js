/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$('#barra-top').find('td').click(function () {
    var location = $(this).attr("val");
    if (location === 'form.htm') {
        location += '?n=true';
    }
    window.location.href = location;
});
var updateCounts = function () {
    $('.span-count').each(function () {
        var $tdObj = $(this);
        var tipo = $tdObj.attr('val');
        $.ajax({
            url: 'ajax/countsolicitacoes.action',
            type: 'POST',
            data: {
                tipo: tipo
            }, success: function (response) {
                $tdObj.text(' (' + response.cnt + ')');
            }, error: function (response) {
                if (response.status === 401) {
                    window.location.href = 'logout.htm';
                }
            }
        });
    });
};

updateCounts();