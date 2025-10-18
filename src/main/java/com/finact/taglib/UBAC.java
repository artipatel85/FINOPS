/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finact.taglib;

import com.finops.admin.model.LoginBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.TagSupport;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chiragkhetani
 */
public class UBAC extends TagSupport {

    @lombok.Setter @lombok.Getter
    private String userId;
    //private String pattern;

    @Override
    public int doStartTag() throws JspException {
        try {
            JspWriter out=pageContext.getOut();
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            HttpSession session = request.getSession();
            LoginBean loginVo = (LoginBean) session.getAttribute("loginLst");
            //boolean isFinUser = "Y".equals(loginVo.getIsFinUser());
            boolean isAdministrator = loginVo.getUserBean().isAdministrator();
            if(!isAdministrator){
                String sessionUserId = loginVo.getUserBean().getUserId();
                if(sessionUserId == null || this.userId == null || !this.userId.equalsIgnoreCase(sessionUserId)){
                    return SKIP_BODY;
                }              
            }
            return EVAL_BODY_INCLUDE; //To change body of generated methods, choose Tools | Templates.
        } catch (Exception ex) {
            Logger.getLogger(UBAC.class.getName()).log(Level.SEVERE, null, ex);
            return SKIP_BODY;
        }
    }

    
    
}
