package com.finops.finance.controller;

import com.finops.admin.model.FormBean;
import com.finops.admin.model.LoginBean;
import com.finops.controller.AbstractController;
import com.finops.finance.bean.PeriodBean;
import com.finops.finance.service.PeriodService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class PeriodController extends AbstractController {

    @Autowired
    private PeriodService periodService;

    @RequestMapping("/period.fin")
    public ModelAndView formSearch(@ModelAttribute("periodBean") PeriodBean periodBean) {
        return new ModelAndView("finact/AccountYearSearch", "command", periodBean);
    }

    @RequestMapping(value = "/periodGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String formGson(@ModelAttribute("periodBean") PeriodBean periodBean,
                    HttpServletRequest request, HttpSession session) {
        setSessionData(periodBean, session);
        List<PeriodBean> list = periodService.findAll(periodBean);
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/setAccountYear.fin")
    public ModelAndView setAccountYear(@ModelAttribute("periodBean") PeriodBean periodBean,
                                       HttpSession session) {
        setSessionData(periodBean, session);
        int[] ids = periodBean.getIds();
        if(ids.length > 1){
            return null;
        }
        int id = ids[0];
        List<PeriodBean> list = periodService.findAll(periodBean);
        for(PeriodBean pb : list){
            if(pb.getAcctYear() == id){
                LoginBean sessionBean = (LoginBean) session.getAttribute("loginLst");
                sessionBean.setPeriodBean(pb);
            }
        }
        return new ModelAndView("loginPopup", "command", periodBean);
    }

    @RequestMapping("/closeAccountYear.fin")
    public ModelAndView closeAcctYear(@ModelAttribute("periodBean") PeriodBean periodBean) {
        periodService.close(periodBean);
        return new ModelAndView("finact/AccountYearSearch", "command", periodBean);
    }

    @RequestMapping("/openAccountYear.fin")
    public ModelAndView openAcctYear(@ModelAttribute("periodBean") PeriodBean periodBean) {
        periodService.open(periodBean);
        return new ModelAndView("finact/AccountYearSearch", "command", periodBean);
    }

    @RequestMapping("/periodSave.fin")
    public ModelAndView savePeriod(@ModelAttribute("periodBean") PeriodBean periodBean,
                                   HttpSession session) {
        setSessionData(periodBean, session);
        periodService.save(periodBean);
        return new ModelAndView("finact/AccountYearSearch", "command", periodBean);
    }

    @RequestMapping("/periodNew.fin")
    public ModelAndView newPeriod(@ModelAttribute("periodBean") PeriodBean periodBean,
                                  HttpSession session) {
        setSessionData(periodBean, session);
        return new ModelAndView("finact/AccountYear", "command", periodBean);
    }
}
