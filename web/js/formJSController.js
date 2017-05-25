/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$('.data').datepicker({
    dateFormat: 'dd/mm/yy'
});

//$('.ui-datepicker').css('font-size','12px');
$('[name=datainicio]')
//        .datepicker("setDate", +1)
        .on('change', function () {
//            var new_init = $(this).datepicker("getDate");
//            var end = $('[name=datafim]').datepicker("getDate");
//            if (end < new_init) {
//                end.setDate(new_init.getDate() + 1);
//                $('[name=datafim]').datepicker("setDate", end);
//            }
        });

$('[name=datafim]')
//        .datepicker("setDate", +2)
        .on('change', function () {
//            var new_end = $(this).datepicker('getDate');
//            var init = $('[name=datainicio]').datepicker('getDate');
//            if (new_end < init) {
//                init.setDate(new_end.getDate() - 1);
//                $('[name=datainicio]').datepicker('setDate', init);
//            }
        });

$('#div-addAula').on('click', function () {
    var fieldSets = $('[cod=field-aula]');
    var numAulas = fieldSets.length;
    var nextFieldSet = fieldSets.last().clone();
    nextFieldSet.find('legend').first().text("Aula " + (numAulas + 1));
    nextFieldSet.attr("num", (numAulas + 1));
    nextFieldSet.hide();
    fieldSets.last().after(nextFieldSet);
    nextFieldSet.slideDown(500, function () {
        $("html, body").animate({scrollTop: $(window).height()}, 1000);
        nextFieldSet.find('.data')
            .removeClass("hasDatepicker")
            .removeAttr("id")
            .datepicker({
                dateFormat: 'dd/mm/yy'
            });
    //nextFieldSet.find('.data').datepicker('setDate',new Date());
    });
});
    
$('#mainform').on('click', '.removeAula', function () {
    if ($('.aula').length > 1) {
        var aulaNum = $(this).parent().attr("num");
        $(this).parent().slideUp(400, function () {
            $('[cod=field-aula]').each(function () {
                var fieldNum = $(this).attr("num");
                if (fieldNum > aulaNum) {
                    $(this).attr("num", fieldNum - 1);
                    $(this).find("legend").first().text("Aula " + (fieldNum - 1));
                }
            });
            $(this).remove();
        });
    }
});

$('input[name=motivo][value=-1]').change(function () {
    if ($(this).prop('checked')) {
        $('#outro-label').removeClass("hiddenInput");
        $('input[name=outro]').removeClass("hiddenInput");
    } else {
        $('#outro-label').addClass("hiddenInput");
        $('input[name=outro]').addClass("hiddenInput").val("");
    }
});





$.validator.addMethod('intervaloValido', function (value, element) {
//    alert(value + '\n' + $('[name=datainicio]').val())
    return value >= $('[name=datainicio]').val();
}, 'Data de retorno é após a de saída.<br/>');
$.validator.addMethod('datasAulas', function (value, element) {
    var valid = true;
    var fieldSets = $('[cod=field-aula]').each(function () {
//        alert($(this).find('[name=dtaula]').first().val());
        if (!$(this).find('[name=dtaula]').first().val()) {
            valid = false;
        }
        if (!$(this).find('[name=horaaula]').first().val()) {
            valid=false;
        }
        if (!$(this).find('[name=minaula]').first().val()) {
            valid=false;
        }
    });
//    alert("valid: " + valid);
    return valid;
}, 'Todas as aulas devem ter as datas e horários especificadas.<br/>');

$.validator.addMethod('datasRecuperacoes', function (value, element) {
    var valid = true;
    var fieldSets = $('[cod=field-aula]').each(function () {
//        alert($(this).find('[name=dtaula]').first().val());
        if (!$(this).find('[name=dtrecupera]').first().val()) {
            valid = false;
        }
        if (!$(this).find('[name=horarecupera]').first().val()) {
            valid=false;
        }
        if (!$(this).find('[name=minrecupera]').first().val()) {
            valid=false;
        }
    });
//    alert("valid: " + valid);
    return valid;
}, 'Todas as aulas devem ter as datas e horários das recuperações especificadas.<br/>');

$.validator.addMethod('outroMotivo',function(value,element) {
    return !$('input[name=motivo][value=-1]').prop('checked') || ( $('input[name=outro]').val() && $('input[name=outro]').val() !== '');
},'Especifique qual o motivo marcado como "Outro".<br/>');

$("#mainform").validate({
    rules: {
        motivo: {
            required: true
        }, datainicio: {
            required: true
        }, datafim: {
            required: true,
            intervaloValido: true
        }, dtaula: {
            datasAulas: true
        }, dtrecupera: {
            datasRecuperacoes: true
        }, outro: {
            outroMotivo: true
        }
    }, messages: {
        motivo: "Você deve informar pelo menos um motivo.<br/>",
        datafim: {
            required: "Você deve especificar a data do final do afastamento<br/>"
        }, datainicio: "Você deve especificar a data do início do afastamento<br/>"
    }, errorLabelContainer: '#div-error-msg',
    invalidHandler: function (event, validator) {
        $("html, body").animate({scrollTop: 0}, 400);
    }
});

//$('input[name=isBolsista]').change( function( evt ){
$(document).on( 'click', '.isEstagiario' , function() {
//$('.isBolsista').change( function( evt ){
    var $container = $(this);
    var isEstagiario = $container.prop('checked');
    $.ajax({
        url: "ajax/getprofsorestagiarios.action",
        type: "POST",
        data: {'estagiarios': isEstagiario},
        success: function ( response ) {
            var $parent = $container.parent();
            var $select = $parent.find('select[name=professor]');
            $select.empty();
            var usuarios = response.usuarios;
            for (var i = 0; i < usuarios.length; i++) {
                $select.append('<option value="' + usuarios[i].ldap + '">' + usuarios[i].nome + '</option>');
            }
        }
    });
} );