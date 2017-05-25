/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ufsm.ctism.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ufsm.ctism.dao.AulaSolicitada;
import ufsm.ctism.service.AulaSolicitadaService;
import ufsm.ctism.service.UsuarioService;
import ufsm.ctism.utils.GrupoUsuarios;
import ufsm.ctism.utils.SessionUser;

/**
 *
 * @author SSI-Bruno
 */
@Controller
public class CalendarioController {
    
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    AulaSolicitadaService aulaSolicitadaService;
    
    @RequestMapping(value = "/calendario.htm")
    public String calendario( HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) @DateTimeFormat(pattern = "ddMMyyyy") Date dt,
            @RequestParam(required = false) String pn,
            Model model) throws IOException {
        
        SessionUser sUsr = SessionUser.getUser(request);
        if (sUsr == null) {
            response.sendRedirect("login.htm");
            return "login";
        }

        Integer permission = usuarioService.getPermissionsAndSetToModel(sUsr.getUid(), model);
        if ( ((permission & GrupoUsuarios.PERMISSAO_DEPTO_EDUCACAO) == 0) && ((permission & GrupoUsuarios.PERMISSAO_SSI) == 0) ) {
            model.addAttribute("user",sUsr);
            model.addAttribute("message","Você precisa ser administrador para acessar esta página.");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "error-pages/forbidden";
        }
        
        model.addAttribute("user",sUsr);
        Calendar dia = Calendar.getInstance();
        if (dt != null) {
            dia.setTime(dt);
            if (pn != null) {
                if (pn.equalsIgnoreCase("n")) {
                    dia.add(Calendar.DAY_OF_MONTH, 2);
                } else {
                    dia.add(Calendar.DAY_OF_MONTH, -7);
                }
            }
        }
        
        dia.add(Calendar.DAY_OF_MONTH, 1 - dia.get(Calendar.DAY_OF_WEEK));
        
        String[] diasSemana = new String[]{"Segunda-Feira","Terça-Feira","Quarta-Feira","Quinta-Feira","Sexta-Feira","Sábado"};
        String[] datas = new String[diasSemana.length];

        Integer[] horas = new Integer[] {0,8,10,12,14,16,18,20,24};
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
        for (int i = 0; i < diasSemana.length; i++) {
            dia.add(Calendar.DAY_OF_MONTH, 1);
            datas[i] = sdf.format(dia.getTime());
        }
        model.addAttribute("diasSemana",diasSemana);
        model.addAttribute("datas", datas);
        model.addAttribute("horas", horas);
//        model.addAttribute("", request)
        
        return "calendario";
    }
    
    @RequestMapping( value="/ajax/calendariodthora.action", method=RequestMethod.POST )
    @ResponseBody
    public Map<String,Object> calendarioDtHora(HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam @DateTimeFormat(pattern="dd/MM/yyyy") Date dia,
            @RequestParam Integer hrIni,
            @RequestParam Integer hrFim) throws IOException {
        Map<String,Object> ret = new HashMap<>();

        SessionUser sUsr = SessionUser.getUser(request);
        if (sUsr == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ret.put("login", false);
            response.sendRedirect("login.htm");
            return ret;
        }
        Integer permission = usuarioService.getPermissionsAndSetToModel(sUsr.getUid(), null);
        if ( ((permission & GrupoUsuarios.PERMISSAO_DEPTO_EDUCACAO) == 0) && ((permission & GrupoUsuarios.PERMISSAO_SSI) == 0) ) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ret.put("message", "NAO-PERMITIDO");
            return ret;
        }
        
        Calendar ini = Calendar.getInstance(), end = Calendar.getInstance();

        Stream.of(ini,end).forEach(d -> d.setTime(dia));
        
        ini.set(Calendar.HOUR_OF_DAY, hrIni);
        ini.set(Calendar.MINUTE, 0);
        ini.set(Calendar.SECOND, 0);
        
        end.set(Calendar.HOUR_OF_DAY, hrFim == 24 ? 23 : hrFim);
        end.set(Calendar.MINUTE, hrFim == 24 ? 59 : 0);
        end.set(Calendar.SECOND, hrFim == 24 ? 59 : 0);

        Collection<AulaSolicitada>[] aulas = new Collection[2];
        List<Map<String,String>> aulasJson = new ArrayList<>();
        aulas[0] = aulaSolicitadaService.getAllInDateInterval(ini, end);
        aulas[1] = aulaSolicitadaService.getAllWithRecInDateInterval(ini, end);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm");
        for (int i = 0; i < aulas.length; i++) {
            for (AulaSolicitada aula : aulas[i]) {
                Map<String,String> current = new HashMap<>();
                current.put( "tipo", i == 0 ? "Substituição" : "Recuperação");
                current.put( "datahora", sdf.format ( i == 0 ? aula.getDataAula() : aula.getDataRecuperacao() ) );
                try {
                    current.put( "prof", usuarioService.getByLdap(aula.getSolicitacao().getProfessorLdap()).getNome());
                } catch (Exception ex) {
                    current.put( "prof", "[" + aula.getSolicitacao().getProfessorLdap() + "]");
                }
                try {
                    current.put( "profsub", usuarioService.getByLdap(aula.getProfSubstitutoLdap()).getNome());
                } catch (Exception ex) {
                    current.put( "profsub", "[" + aula.getProfSubstitutoLdap() + "]");
                }
                current.put( "componente", aula.getComponente().getNome());
                current.put( "situacao", aula.getSituacao().getDescricao());
                aulasJson.add(current);
            }
        }
        Collections.sort(aulasJson, (x,y) -> {
            try {
                return sdf.parse(x.get("datahora")).compareTo(sdf.parse(y.get("datahora")));
            } catch (Exception ex) {
                return 0;
            }
        });
        
        ret.put("aulas", aulasJson);
        response.setStatus(HttpServletResponse.SC_OK);
         
       return ret;
    }
    
    
}
