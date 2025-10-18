/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.admin.controller;

import com.finops.admin.dao.PartnerAccountDAO;
import com.finops.admin.model.FormBean;
import com.finops.admin.model.LoginBean;
import com.finops.admin.model.PartnerAccount;
import com.finops.admin.model.UserBean;
import com.finops.admin.service.AdminService;
import com.finops.admin.service.LoginService;
import com.finops.controller.AbstractController;
import com.finops.finance.bean.EInvoiceRequest;
import com.finops.finance.bean.EInvoiceResponse;
import com.finops.finance.bean.PeriodBean;
import com.finops.finance.util.RestClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


@Controller
public class LoginController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static Properties prop = null;

    @Autowired
    private LoginService loginService;

    @Autowired
    private AdminService adminService;

    @RequestMapping("/login.fin")
    public ModelAndView login(@ModelAttribute("loginBean") UserBean loginBean,
                              HttpSession session, HttpServletRequest request)  {

//        HttpEntity requestEntity = new HttpEntity(getHeaders());
//        HttpEntity<String> response = new RestTemplate().exchange("http://3.237.176.171:8080/v1/coa/getByPage", HttpMethod.GET, requestEntity, String.class);
//        System.out.println(response.getBody());

        UserBean user = loginService.login(loginBean);
        List<PartnerAccount> branchList = loginService.getBranchDetails(user.getBranch());
        Map<Integer, FormBean> formBeanMap = loginService.getRoleFormAccessMap(user.getRole());
        PeriodBean currentPeriod = loginService.getCurrentPeriod(user.getCompanyId());

        Map<String, PartnerAccount> branchDetails = new HashMap<>();
        for(PartnerAccount pa : branchList){
            branchDetails.put(pa.getPartnerCode(), pa);
        }

        Map<String, String> finactProperties = adminService.getFinactProperties();

        LoginBean vo = new LoginBean();
        vo.setUserBean(user);
        vo.setPartnerAccount(branchDetails.get(user.getBranch()));
        vo.setFormBeanMap(formBeanMap);
        vo.setPeriodBean(currentPeriod);
        vo.setBranchDetails(branchDetails);
        vo.setFinactProperties(finactProperties);

        session.setAttribute("loginLst", vo);
        session.setAttribute("privList", formBeanMap.values());

        logger.debug("User {} logged in successfully.",loginBean.getUserId());
        return new ModelAndView("loginPopup", "command", loginBean);

    }

    @RequestMapping("/logout.fin")
    public ModelAndView logout(@ModelAttribute("loginBean") UserBean loginBean,
            HttpSession session) {
        session.removeAttribute("loginLst");
        session.invalidate();
        return new ModelAndView("index", "command", loginBean);
    }

    @RequestMapping("/periodClosed.fin")
    public ModelAndView periodClosed(@ModelAttribute("loginBean") UserBean loginBean,
                               HttpSession session) {
        session.removeAttribute("loginLst");
        session.invalidate();
        return new ModelAndView("periodClosed", "command", loginBean);
    }

    public HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiMjU3MjM4MTYtMTA4YS0zZGYxLWE4M2ItMzZiMWU0NGY5OWRkIiwiaXNBZG1pbiI6ZmFsc2UsInN1YiI6ImJoYXVtaWsucGF0ZWxAdGVrYW50aGVtLmNvbSIsImlhdCI6MTcyNDU1NzY5OCwiZXhwIjoxNzI0NTc1Njk4fQ.pxDxnVlkjtDHlXQ4nOwX6HrlS1PLsSwj07jrSuEJ-l0");
        return headers;
    }
}
