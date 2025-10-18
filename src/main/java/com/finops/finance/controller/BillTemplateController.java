package com.finops.finance.controller;

import com.finops.controller.AbstractController;
import com.finops.finance.bean.BillTemplateBean;
import com.finops.finance.bean.BillTemplateRow;
import com.finops.finance.service.BillTemplateService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class BillTemplateController extends AbstractController {

    @Autowired
    private BillTemplateService billTemplateService;

    @RequestMapping("/billTemplateSearch.fin")
    public ModelAndView billTempSearch(@ModelAttribute("billTemplateBean") BillTemplateBean billTemplateBean) {
        return new ModelAndView("billing/BillTemplateSearch", "command", billTemplateBean);
    }

    @RequestMapping("/billTemplate.fin")
    public ModelAndView billTemplate(@ModelAttribute("billTemplateBean") BillTemplateBean billTemplateBean) {
        billTemplateBean.setBillTemplateRows(dummyVoucherList());
        billTemplateBean.setBillTemplateTaxs(dummyVoucherList());
        return new ModelAndView("billing/BillTemplate", "command", billTemplateBean);
    }

    @RequestMapping(value = "/billTemplateGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String billTemplateGson(@ModelAttribute("billTemplateBean") BillTemplateBean billTemplateBean, HttpServletRequest request) {
        List<BillTemplateBean> list = billTemplateService.billTempView(billTemplateBean);
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping(value = "/addTemplateRow", method = RequestMethod.POST)
    public ModelAndView addTemplateRow(@ModelAttribute("billTemplateBean") BillTemplateBean billTemplateBean) {
        List<BillTemplateRow> billTemplateRows = billTemplateBean.getBillTemplateRows();
        if (validate(billTemplateBean)) {
            billTemplateRows.add(new BillTemplateRow());
        }
        return new ModelAndView("billing/BillTemplate", "command", billTemplateBean);
    }

    @RequestMapping(value = "/removeTemplateRow", method = RequestMethod.POST)
    public ModelAndView removeTemplateRow(@ModelAttribute("billTemplateBean") BillTemplateBean billTemplateBean,
                                          @RequestParam("index") int index) {
        List<BillTemplateRow> billTemplateRows = billTemplateBean.getBillTemplateRows();
        Iterator<BillTemplateRow> itr = billTemplateRows.iterator();
        int i = 0;
        while (itr.hasNext()) {
            BillTemplateRow row = itr.next();
            if (i == index) {
                itr.remove();
            }
            i++;
        }
        return new ModelAndView("billing/BillTemplate", "command", billTemplateBean);
    }

    private List<BillTemplateRow> dummyVoucherList() {
        List<BillTemplateRow> billTemplateRows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            billTemplateRows.add(new BillTemplateRow());
        }
        return billTemplateRows;
    }

    @RequestMapping("/billTemplateSave.fin")
    public ModelAndView billTemplateSave(@ModelAttribute("billTemplateBean") BillTemplateBean billTemplateBean, HttpSession session) {
        setSessionData(billTemplateBean, session);
        billTemplateService.saveBillTemplate(billTemplateBean);
        return new ModelAndView("billing/BillTemplateSearch", "command", billTemplateBean);
    }

    private boolean validate(BillTemplateBean billTemplateBean) {
        List<BillTemplateRow> billTemplateRows = billTemplateBean.getBillTemplateRows();
        for (BillTemplateRow vr : billTemplateRows) {
            if (!StringUtils.hasText(vr.getAccountname()) || !StringUtils.hasText(vr.getAccountcode())
                    || StringUtils.hasText(vr.getDes())) {
                vr.setErrMsg("Invalid Row.");
                return false;
            }
        }
        return true;
    }

    @RequestMapping(value = "/retrieveBillTemplate.fin")
    public ModelAndView retrieveBillTemplate(@ModelAttribute("billTemplateBean") BillTemplateBean billTemplateBean,
                                             @RequestParam("templateName") String templateName, HttpSession session) {
        billTemplateBean.setTemplateName(templateName);
        setSessionData(billTemplateBean, session);
        billTemplateBean = billTemplateService.fetchBillTemplate(billTemplateBean);
        return new ModelAndView("billing/BillTemplate", "command", billTemplateBean);
    }

}
