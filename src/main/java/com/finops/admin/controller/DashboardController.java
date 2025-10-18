package com.finops.admin.controller;

import com.finops.admin.model.DashboardBean;
import com.finops.admin.service.DashboardService;
import com.finops.controller.AbstractController;
import com.finops.finance.bean.InvoiceBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.IntStream;

@Controller
public class DashboardController extends AbstractController {

    @Autowired
    private DashboardService dashboardService;

    @RequestMapping(value = "/dashboard.fin")
    public @ResponseBody DashboardBean dashboard(@RequestParam("type") String type,
                                                 HttpSession session) {
        DashboardBean  dashboardBean = new DashboardBean();
        dashboardBean.setLabels(new String[]{"April", "May", "June", "July","August","September","October","November","December","January", "February", "March", });
        InvoiceBean invoiceBean = new InvoiceBean();
        setSessionData(invoiceBean, session);
        invoiceBean.setBillType(type);
        List<Integer> dataList = dashboardService.getInvoiceList(invoiceBean);
        dashboardBean.setData(dataList.stream().mapToInt(Integer::intValue).toArray());
        return dashboardBean;
    }
}
