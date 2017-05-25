/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



$('button').click(function(){
    debugger;
    var acao = $(this).attr('acao');
    var id = $(this).attr('value');
    if ( !confirm("Tem certeza que deseja " + acao + " esta solicitação?") ) {
        return;
    }
    $.ajax({
        url: 'ajax/deferir.action',
        type: 'POST',
        data: {
            id: id,
            aceita: acao === 'deferir'
        }, success: function( response ) {
            var divToRemove = $( '.deferir[num=' + id + ']' );
            $.notific8(response.msg, {
                theme: 'lime',
                life: 3000
            });
            divToRemove.slideUp(400,function() {
               divToRemove.remove();
               //zebras
               var zebra = false;
                $('.deferir').each(function () {
                    if (zebra) {
                        $(this).addClass("zebra");
                    } else {
                        $(this).removeClass("zebra");
                    }
                    zebra = !zebra;
                });
                if ($('.deferir').length === 0) {
                    $('#deferir-empty').removeClass('hidden');
                }
            });
            updateCounts();
        }, error: function( response ) {
            if (response.status === 401) {
                window.location.href = 'logout.htm';
            } else {
                $.notific8(response.responseJSON.msg, {
                    theme: 'ruby',
                    life: 3000
                });
                window.location.href = 'index.htm';
            }
        }
    });
});