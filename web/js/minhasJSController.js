/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


//$('#div-solicitacoes').accordion({
//    collapsible: true,
//    active: false,
//    heightStyle: "content"
//});

$('input[type=radio][name=tipo-filtro]').change( function() {
    var tipo = $(this).attr('tipo');
    window.location.href = "minhas.htm?tipo=" + tipo;
});

$('.cancelaAula').click( function( evt ) {
    var id = $( this ).attr('aulaId');
    var $aulaDiv = $(this).parentsUntil('.solicitacao').last().parent();
    var text = "Tem certeza que deseja cancelar esta substituição?\n";
    text += "Aula de " + $aulaDiv.attr('componente') + "\nDia " + $aulaDiv.attr('dtaula') + ", a ser recuperada no dia " + $aulaDiv.attr('dtrec');
    text += "\nSolicitada para " + $aulaDiv.attr('profsub');
    if (confirm(text.replace(/<[^>]*>/g,""))) {
        $.ajax({
           url: 'ajax/cancelaAula.action',
           method: 'POST',
           data: {
               idAula: id
           }, success: function( response ) {
                $.notific8(response.msg, {
                    theme: 'lime',
                    life: 3000
                });
               $aulaDiv.slideUp( 400 , function() {
                    var zebra = false;
                    $('.solicitacao').each(function () {
                        if ( $(this).find('.cancelaAula[aulaId='+id+']').length === 0 ) {
                            $(this).removeClass('zebra');
                            if (zebra) {
                                $(this).addClass('zebra');
                            }
                            zebra = !zebra;
                        }
                    });
               });
           }, error: function(response) {
               if (response.status === 401) {
                   window.location.href = 'logout.htm';
               } else {
                    $.notific8(response.responseJSON.msg, {
                        theme: 'ruby',
                        life: 3000
                    });
                    setTimeout(function () {
                        window.location.href="index.htm";
                    }, 3000);
               }
               
               
           }
        });
    }
});