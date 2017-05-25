/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$('.data').datepicker({
    dateFormat: 'dd/mm/yy'
});

$.validator.addMethod('intervaloValido', function (value, element) {
    return value >= $('[name=datainicio]').val();
}, 'Data final é após a data de início.<br/>');

$('#form-relatorio').validate({
    rules: {
        situacao: {
            required: true
        }, tiporelatorio: {
            required: true
        }, datainicio: {
            required: true
        }, datafim: {
            required: true,
            intervaloValido: true
        }
    }, messages: {
        situacao: "Você deve especificar pelo menos um estado para as solicitações<br/>",
        tiporelatorio: "Você deve especificar um tipo de relatório a ser gerado<br/>",
        datainicio: "Você deve especificar a data inicial das solicitações que deseja recuperar<br/>",
        datafim: {
            required: "Você deve especificar a data final das solicitações que deseja recuperar<br/>"
        }
    }, errorLabelContainer: '#div-error-msg'
});

