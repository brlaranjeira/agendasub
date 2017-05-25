/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$('#btConfirma').click(function(){
    var ajaxData = {
        datainicio: $('[name=datainicio]').val(),
        datafim: $('[name=datafim]').val(),
        motivo: new Array(),
        componente: new Array(),
        professor: new Array(),
        dtaula: new Array(),
        dtrec: new Array()
//        hraula: new Array(),
//        hrrec: new Array()
    };
    
    $('[name=motivo]').each(function(){
        ajaxData.motivo.push($(this).val());
    });
    $('[name=componente]').each(function(){
        ajaxData.componente.push($(this).val());
    });
    $('[name=professor]').each(function(){
        ajaxData.professor.push($(this).val());
    });
    $('[name=dtaula]').each(function(){
        ajaxData.dtaula.push($(this).val());
    });
    $('[name=dtrec]').each(function(){
        ajaxData.dtrec.push($(this).val());
    });
    if ($('[name=outro]').length) {
        debugger;
        ajaxData.outro = encodeURIComponent($('[name=outro]').val());
    }
    var url = "ajax/confirma.action";
    $.ajax({
        url: url,
        type: "POST",
        data: ajaxData,
        success: function(response) {
            $.notific8(response.msg, {
                theme: 'lime',
                life: 3000
            });
            //alert('Aviso!\nEsta solicitação não substitui o pedido que ainda deve ser feito normalmente no SIE!');
            setTimeout(function(){
                window.location.href = 'form.htm?n=true';
            },3000);
        }, error: function(response) {
            if (response.status === 401) {
                window.location.href = 'logout.htm';
            } else {
                    $.notific8(response.responseJSON.msg, {
                    theme: 'ruby',
                    life: 3000
                });
                setTimeout(function(){
                    window.location.href = 'index.htm';
                },3000);
            }
            
        }
    });
});

$('#btCancela').click(function(){
    window.location.href = 'index.htm';
});