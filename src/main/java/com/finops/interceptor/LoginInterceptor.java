package com.finops.interceptor;

import com.finops.admin.model.LoginBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if("/login.fin".equals(request.getServletPath()) || "/logout.fin".equals(request.getServletPath())){
            return true;
        }

//        if (!"Y".equals(prop.getProperty("isPrimary")) && blockedPaths.contains(request.getServletPath())) {
//            return false;
//        }
        //session.setAttribute("isPrimary", prop.getProperty("isPrimary"));
        if(session.getAttribute("loginLst") == null){
            request.getRequestDispatcher("/logout.fin").forward(request, response);
            return false;
        }
        else{
            LoginBean loginVo = (LoginBean) session.getAttribute("loginLst");
            String userId = loginVo.getUserBean().getUserId();
            String periodStatus = loginVo.getPeriodBean().getPeriodStatus();
            if("C".equalsIgnoreCase(periodStatus) && writableEndpoints.contains(request.getServletPath())){
                System.out.println("Account is closed!");
                request.getRequestDispatcher("/periodClosed.fin").forward(request, response);
                return false;
            }
            MDC.put("userId",userId);
        }
        return true;
    }

    private static Set<String> writableEndpoints = Stream.of("/saveBill.fin","/performBulkCreateIRN.fin","/saveVoucher.fin","/saveJournalVoucher.fin")
            .collect(Collectors.toCollection(HashSet::new));


}
