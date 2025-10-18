package com.finops.controller;

import com.finops.freight.service.SOService;
import com.finops.report.model.ReportBean;
import com.finops.service.AutoCompleteService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AutoCompleteController extends AbstractController {

    @Autowired
    private AutoCompleteService autoCompleteService;

    @Autowired
    private SOService soService;

    @RequestMapping("/autoComplete.fin")
    public @ResponseBody
    List<Map<String, String>> autoComplete(@ModelAttribute("reportBean") ReportBean bean,
                                  @RequestParam("param") String param,
                                  @RequestParam("term") String description,
                                  @RequestParam("type") int type,
                                  HttpSession session) {
        setSessionData(bean, session);
        List<Object> objects = new ArrayList<>();
        if(type >= 0){
            objects.add(description+"%");
        }

        if(type == 1){
            objects.add(bean.getLoadingAgent());
        }

        List<ReportBean> reportBeanList = autoCompleteService.autoComplete(param, objects.toArray());
        List<Map<String, String>> result = new ArrayList<>();

        for(ReportBean rb : reportBeanList){
            Map<String, String> tmap = new HashMap<>();
            tmap.put("value", rb.getParam1());
            tmap.put("param2", rb.getParam2());
            tmap.put("param3", rb.getParam3());
            tmap.put("param4", rb.getParam4());
            tmap.put("param5", rb.getParam5());
            tmap.put("param6", rb.getParam6());
            tmap.put("param7", rb.getParam7());
            tmap.put("param8", rb.getParam8());
            result.add(tmap);
        }

        return result;
    }

    @RequestMapping(value = "/popup3ViewGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String popup3ViewGson(@ModelAttribute("reportbean") ReportBean reportbean,
                                               HttpServletRequest request, HttpSession session) {
        Map<String, String> popupMap = (Map<String, String>)session.getAttribute(POPUP_METADATA);
        reportbean.setSearchFieldValueList(getSearchFieldValues(request, Integer.parseInt(popupMap.get(PARAM_LENGTH))));

        List<ReportBean> list = autoCompleteService.getRecons(reportbean, popupMap.get(QUERY), Integer.parseInt(popupMap.get(INDEX)));
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/popup3View.fin")
    public ModelAndView popup3View(@ModelAttribute("reportbean") ReportBean reportbean,
                                   @RequestParam(PARAM) String param, HttpSession session) {
        Map<String, String> map = new HashMap<>();
        map.put(PARAM,param);
        map.put(QUERY, "SELECT partner_code, description1, address1 FROM partner_account_d WHERE carrier = 'Y' AND partner_code like ? AND description1 like ? AND address1 like ? LIMIT 0, 50 ");
        map.put(INDEX, "2");
        map.put(PARAM_LENGTH, "3");
        session.setAttribute(POPUP_METADATA, map);
        return new ModelAndView("operation/Popup3View", "command", reportbean);
    }

    @RequestMapping("/autoCompletePartner.fin")
    public @ResponseBody
    List<Map<String, String>> autoCompletePartner(@ModelAttribute("reportBean") ReportBean bean,
                                           @RequestParam("param") String param,
                                           @RequestParam("term") String description,
                                           HttpSession session) {
        setSessionData(bean, session);
        List<Object> objects = new ArrayList<>();
        objects.add(description+"%");

        List<ReportBean> reportBeanList = autoCompleteService.autoComplete(param, objects.toArray());
        List<Map<String, String>> result = new ArrayList<>();

        for(ReportBean rb : reportBeanList){
            Map<String, String> tmap = new HashMap<>();
            tmap.put("value", rb.getParam1());
            tmap.put("param2", rb.getParam2());
            tmap.put("param3", rb.getParam3());
            tmap.put("param4", rb.getParam4());
            tmap.put("param5", rb.getParam5());

            result.add(tmap);
        }

        return result;
    }

    @RequestMapping(value = "/soExpensePopup.fin", method = RequestMethod.GET)
    public @ResponseBody List<ReportBean> soExpensePopup(@RequestParam("term") String soNumber,
                                                         @RequestParam("blNo") String blNo,@RequestParam("exportImport") String exportImport,
                                                         @RequestParam("seaAir") String seaAir, HttpSession session) {
        ReportBean rb = new ReportBean();
        setSessionData(rb, session);
        List<Object> objects = new ArrayList<>();
        objects.add(soNumber+"%");
        objects.add("%");
        if("EXPORT".equalsIgnoreCase(exportImport)) {
            objects.add(rb.getLoadingAgent());
            objects.add("%");
        }
        else{
            objects.add("%");
            objects.add(rb.getLoadingAgent());
        }
        List<ReportBean> thd = null;
        if("SEA".equals(seaAir)){
            thd = autoCompleteService.autoComplete("SO_EXPENSE",objects.toArray());
        }
        else{
            thd = autoCompleteService.autoComplete("SI_EXPENSE",objects.toArray());
        }
        return thd;
    }

}
