package com.finops.finance.controller;

import com.finops.admin.service.LoginService;
import com.finops.controller.AbstractController;
import com.finops.finance.bean.LedgerBean;
import com.finops.finance.service.AccountService;
import com.finops.freight.bean.FinactPropertiesBean;
import com.finops.report.model.ReportBean;
import com.finops.service.CronService;
import com.finops.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

@Controller
public class AccountController extends AbstractController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private CronService cronService;

    @RequestMapping("/ledgerSearch.fin")
    public ModelAndView ledgerSearch(@ModelAttribute("ledgerBean") LedgerBean ledgerBean) {
        return new ModelAndView("finact/LedgerSearch", "command", ledgerBean);
    }

    @RequestMapping("/ledger.fin")
    public ModelAndView ledger(@ModelAttribute("ledgerBean") LedgerBean ledgerBean) {
        ledgerBean.setOpeningBalance(0.00);
        ledgerBean.setParentName(null);
        return new ModelAndView("finact/Ledger", "command", ledgerBean);
    }

    @RequestMapping(value = "/ledgerGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String ledgerGson(@ModelAttribute("ledgerBean") LedgerBean ledgerBean, HttpSession session, HttpServletRequest request) {

        setSessionData(ledgerBean, session);
        ledgerBean.setStart(Integer.parseInt(request.getParameter("start")));
        ledgerBean.setLength(Integer.parseInt(request.getParameter("length")));
        ledgerBean.setSearchFieldValueList(getSearchFieldValues(request, 11));
        List<LedgerBean> list = accountService.ledgerView(ledgerBean);
        ledgerBean.setAaData(list);
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(ledgerBean);
    }

    @RequestMapping(value = "/retrieveLedger.fin")
    public ModelAndView retrieveLedger(@ModelAttribute("ledgerBean") LedgerBean ledgerBean,
                                             @RequestParam("id") int id, HttpSession session) throws SQLException {
        ledgerBean.setCodeCombinationId(id);
        setSessionData(ledgerBean, session);
        ledgerBean = accountService.retrieveLedger(ledgerBean);
        return new ModelAndView("finact/Ledger", "command", ledgerBean);
    }

    @RequestMapping(value = "/retrieveFinactProps.fin")
    public ModelAndView retrieveFinactProps(@ModelAttribute("finactPropertiesBean") FinactPropertiesBean finactPropertiesBean,
                                            HttpSession session) throws SQLException {
        setSessionData(finactPropertiesBean, session);
        loginService.retrieveFinactProperties(finactPropertiesBean);
        return new ModelAndView("finact/FinactUtil", "command", finactPropertiesBean);
    }

    @RequestMapping("/taxMasterSearch.fin")
    public ModelAndView taxMasterSearch(@ModelAttribute("ledgerBean") LedgerBean ledgerBean) {
        return new ModelAndView("finact/TaxMasterSearch", "command", ledgerBean);
    }

    @RequestMapping("/taxMaster.fin")
    public ModelAndView taxMaster(@ModelAttribute("ledgerBean") LedgerBean ledgerBean) {
        return new ModelAndView("finact/TaxMaster", "command", ledgerBean);
    }

    @RequestMapping(value = "/taxMasterGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String taxMasterGson(@ModelAttribute("ledgerBean") LedgerBean ledgerBean, HttpServletRequest request, HttpSession session) {
        setSessionData(ledgerBean, session);
        List<LedgerBean> list = accountService.taxMasterView(ledgerBean);
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping(value = "/retrieveTaxMaster.fin")
    public ModelAndView retrieveTaxMaster(@ModelAttribute("ledgerBean") LedgerBean ledgerBean,
                                          @RequestParam("id") int id, HttpSession session) throws SQLException {
        ledgerBean.setAcctID(id);
        setSessionData(ledgerBean, session);
        List<LedgerBean> list = accountService.taxMasterView(ledgerBean);
        ledgerBean = list.get(0);
        return new ModelAndView("finact/TaxMaster", "command", ledgerBean);
    }

    @RequestMapping("/taxMasterSave.fin")
    public ModelAndView taxMasterSave(@ModelAttribute("ledgerBean") LedgerBean ledgerBean, HttpSession session) {
        setSessionData(ledgerBean, session);
        //accountDAO.saveTaxMaster(ledgerBean);
        return new ModelAndView("finact/TaxMasterSearch", "command", ledgerBean);
    }

    @RequestMapping("/ledgerSave.fin")
    public ModelAndView ledgerSave(@ModelAttribute("ledgerBean") LedgerBean ledgerBean, HttpSession session) throws ParseException {
        setSessionData(ledgerBean, session);

        if (ledgerBean.getCodeCombinationId() == 0) {
            if (!ledgerBean.isAdministrator() && !DateUtil.isDateInLimit(DateUtil.getSystemDate(), ledgerBean.getYrStartDate(),
                    ledgerBean.getYrEndDate())) {
                ledgerBean.setErrMsg("Ledger can be opened/imported in current accounting year only!");
                return new ModelAndView("finact/Ledger", "command", ledgerBean);
            }
            LedgerBean account = accountService.retrieveLedgerByName(ledgerBean);
            if (account != null) {
                ledgerBean.setErrMsg("Ledger account already exists with id :: " + account.getCodeCombinationId());
                return new ModelAndView("finact/Ledger", "command", ledgerBean);
            }
            accountService.saveLedger(ledgerBean);
        }
        else{
            accountService.updateLedger(ledgerBean);
        }

        return new ModelAndView("finact/LedgerSearch", "command", ledgerBean);
    }

    @RequestMapping("/processCron.fin")
    public ModelAndView processCron(@ModelAttribute("finactPropertiesBean") FinactPropertiesBean bean,
                                    HttpSession session) {
        setSessionData(bean, session);
        ReportBean rb = new ReportBean();
        rb.setAcctYear(bean.getAcctYear());
        cronService.performCron(rb);
        loginService.retrieveFinactProperties(bean);
        return new ModelAndView("finact/FinactUtil", "command", bean);
    }

    @RequestMapping(value = "/updateFinactProps.fin")
    public ModelAndView updateFinactProps(@ModelAttribute("finactPropertiesBean") FinactPropertiesBean finactPropertiesBean,
                                          HttpSession session) throws SQLException {
        //setSessionData(finactPropertiesBean, session);
        accountService.updateFinactProperties(finactPropertiesBean);
        return new ModelAndView("finact/FinactUtil", "command", finactPropertiesBean);
    }

    @RequestMapping(value = "/balanceTransfer.fin")
    public ModelAndView balanceTransfer(@ModelAttribute("finactPropertiesBean") FinactPropertiesBean finactPropertiesBean,
                                        HttpSession session) throws SQLException {
        setSessionData(finactPropertiesBean, session);
//        try {
//            if(DateUtil.getCurrentYear() != (finactPropertiesBean.getAcctYear()/10000) ){
//                finactPropertiesBean.setBalanceMsg("Invalid Accounting year. Balance Transfer failed.");
//                return retrieveFinactProps(finactPropertiesBean, session);
//            }
//        } catch (ParseException ex) {
//            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
//        }
        accountService.balanceTransfer(finactPropertiesBean);
        finactPropertiesBean.setBalanceMsg("Balance Transfer is started. This may take few minutes. Once completed an email will be sent.");
        return retrieveFinactProps(finactPropertiesBean, session);
    }
}
