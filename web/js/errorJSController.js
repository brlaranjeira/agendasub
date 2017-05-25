/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$('#enviaBug').click( function() {
    $.ajax({
        url: "ajax/bugtrack.action",
        method: "POST",
        data: {
            userMessage: $('textarea[name=userMessage]').val()
        }, success: function ( response ) {
            alert('Mensagem enviada ao SSI!');
            window.location.href = "index.htm";
        }, error: function ( response ) {
            alert('Não foi possível enviar o erro.');
        }
    });
} );