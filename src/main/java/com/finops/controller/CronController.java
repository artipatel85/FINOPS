package com.finops.controller;

import com.finops.freight.service.SOService;
import com.finops.report.model.ReportBean;
import com.finops.service.AutoCompleteService;
import com.finops.service.CronService;
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

@Controller
public class CronController extends AbstractController {

    @Autowired
    private CronService cronService;

    @RequestMapping("/cron.fin")
    public ModelAndView cron(@ModelAttribute("reportBean") ReportBean bean,
                             HttpSession session) {
        setSessionData(bean, session);
        return new ModelAndView("finact/Cron", "command", bean);
    }

    @RequestMapping("/1processCron.fin")
    public ModelAndView processCron(@ModelAttribute("reportBean") ReportBean bean,
                                  HttpSession session) {
        setSessionData(bean, session);
        cronService.performCron(bean);
        return new ModelAndView("finact/FinactUtil", "command", bean);
    }


}
