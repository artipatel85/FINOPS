package com.finops.finance.controller;

import com.finops.controller.AbstractController;
import com.finops.finance.bean.AuditBean;
import com.finops.finance.service.AuditTrailService;
import com.finops.util.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;

@Controller
public class AuditTrailController extends AbstractController {

    @Autowired
    private AuditTrailService auditTrailService;

    @RequestMapping("/auditTrail.fin")
    public ModelAndView auditTrail(@ModelAttribute("auditBean") AuditBean auditBean,
                                   HttpSession session) {
        session.removeAttribute("auditTrail");
        String endDate = auditBean.getParam1();
        if (!StringUtils.hasText(endDate)) {
            endDate = DateUtil.getSystemDate();
            auditBean.setParam1(endDate);
        }
        session.setAttribute("auditTrail", auditBean);
        return new ModelAndView("finact/AuditTrail", "command", auditBean);
    }

    @RequestMapping(value = "/auditTrailGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String auditTrailGson(@ModelAttribute("reportBean") AuditBean auditBean, HttpSession session,
                          HttpServletRequest request) throws SQLException {
        auditBean = (AuditBean)session.getAttribute("auditTrail");
        setSessionData(auditBean, session);
        auditBean.setStart(Integer.parseInt(request.getParameter("start")));
        auditBean.setLength(Integer.parseInt(request.getParameter("length")));
        auditBean.setSearchFieldValueList(getSearchFieldValues(request, 11));
        List<AuditBean> list = auditTrailService.fetchAuditData(auditBean);
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }
}
