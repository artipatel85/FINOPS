/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finact.taglib;

import com.finops.admin.model.FormBean;
import com.finops.admin.model.LoginBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class RBAC extends TagSupport {
    
    private int formId;
    private String pattern;

    @Override
    public int doStartTag() throws JspException {
        try {
            JspWriter out=pageContext.getOut();
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            HttpSession session = request.getSession();
            LoginBean loginVo = (LoginBean) session.getAttribute("loginLst");
            boolean isFinUser = "Y".equals(loginVo.getUserBean().getIsFinUser());
            boolean isAdministrator = "A".equals(loginVo.getUserBean().getRole());
            if(!isAdministrator){
                Map<Integer, FormBean> ddlMap = loginVo.getFormBeanMap();
                FormBean dto = ddlMap.get(formId);
//                System.out.println("Form id --------- "+formId);
//                System.out.println("pattern --------- "+pattern);
//                System.out.println("dto pattern --------- "+dto.getPattern());
                if(pattern.length() < 34){
                    pattern += "\\|(.)";
                }
                if(dto == null || !Pattern.matches(pattern, dto.getPattern())){
                    return SKIP_BODY;
                }
            }
            return EVAL_BODY_INCLUDE; //To change body of generated methods, choose Tools | Templates.
        } catch (Exception ex) {
            Logger.getLogger(RBAC.class.getName()).log(Level.SEVERE, null, ex);
            return SKIP_BODY;
        }
    }

    /**
     * @return the formId
     */
    public int getFormId() {
        return formId;
    }

    /**
     * @param formId the formId to set
     */
    public void setFormId(int formId) {
        this.formId = formId;
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @param pattern the pattern to set
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    
}
