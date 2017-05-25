/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$('button').click(function() {
    var btn = $(this);
    var id = btn.val();
    var acao = btn.attr("acao");
    if ( !confirm("Tem certeza que deseja " + acao + " esta solicitação?") ) {
        return;
    }
    var url = "ajax/responder.action";
    $.ajax({
        url: url,
        type: "POST",
        data: {
            id: id,
            aceita: acao === "aceitar"
        }, success: function(response) {
            var divToRemove = $('.aceita-recusa[num=' + id + ']');
//            $.notify(response.msg,'success');
            $.notific8(response.msg, {
                theme: 'lime',
                life: 3000
            });
            divToRemove.slideUp(400, function () {
                divToRemove.remove();
                var zebra = false;
                $('.aceita-recusa').each(function () {
                    if (zebra) {
                        $(this).addClass("zebra");
                    } else {
                        $(this).removeClass("zebra");
                    }
                    zebra = !zebra;
                });
                if ($('.aceita-recusa').length === 0) {
                    $('.paramim-empty.hidden').removeClass('hidden');
                }
            });
            updateCounts();
        }, error: function(response) {
            if (response.status === 401) {
                window.location.href = 'logout.htm';
            } else {
                $.notific8(response.responseJSON.msg, {
                    theme: 'ruby',
                    life: 3000
                });
                setTimeout(function () {
                    window.location.href = 'index.htm';
                }, 3000);
            }
        }
    });
});