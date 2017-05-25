/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//$('.calendarioCelula').fir

var carrega =  function() {
    var $tdObj = $( this );
    $.ajax({
        url: "ajax/calendariodthora.action",
        method: "POST",
        data: {
            dia: $tdObj.attr( 'dt' ),
            hrIni: $tdObj.attr( 'hrIni' ),
            hrFim: $tdObj.attr( 'hrFim' )
        }, success: function( response ) {
            $tdObj.removeClass('loading');
            $tdObj.empty();
            var numAulas = response.aulas.length;
            if (numAulas === 0) {
                $tdObj.empty();
            } else {
                for (var i = 0; i < numAulas; i++) {
                    var aula = response.aulas[i];
                    var title = aula.tipo + " de aula de " + aula.componente + "|";
                    title += "Dia " + aula.datahora + "|";
                    title += "Solicitada por: " + aula.prof + "|";
                    title += "Para: " + aula.profsub + "|";
                    title += "Situação: " + aula.situacao;
                    var cls = "agendaItem " + aula.tipo.substring(0,3).toLowerCase();
                    var current = "<div class=\"" + cls + "\" title=\"" + title + "\">";
                    current += aula.datahora.replace(/[^ ]+ /i,'') + " ";
                    current += aula.tipo.charAt(0).toUpperCase() + ": <strong>" + aula.componente + "</strong>";
                    current += "</div>";
                    var $curr = $(current);
                    $tdObj.append($curr);
                    $curr.tooltip({
                        track: true,
                        content: title.replace(/\|/g,"<br/>"),
                        tooltipClass: "solicita-calendario-tooltip"
                    });
                }
            }
        }, error: function( response ) {
            $tdObj.text(response.responseJSON.message).removeClass('loading');
        }
    });
};

$('.calendarioCelula').each(carrega);
$('.calendarioCelula').click(carrega);


$('[name=datacalendario]').datepicker({
    dateFormat: 'dd/mm/yy',
    onSelect: function() {
        var dt = $(this).val().replace(/\//g,"");
        window.location.href="calendario.htm?dt=" + dt;
    }
});

$('.div-calendario-btn').on('click',function() {
//    alert('pn: ' + $(this).attr('pn') + "\ndt: " + $(this).parent().attr('dt').replace(/\//g,""));
    var pn = $(this).attr('pn');
    var dt = $(this).parent().attr('dt').replace(/\//g,"");
    window.location.href = "calendario.htm?pn=" + pn + "&dt=" + dt;
});