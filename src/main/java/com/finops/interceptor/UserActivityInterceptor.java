package com.finops.interceptor;

import com.finops.admin.model.AuditTrail;
import com.finops.admin.model.LoginBean;
import com.finops.finance.service.AuditTrailService;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Component
public class UserActivityInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(UserActivityInterceptor.class);

    @Autowired
    private AuditTrailService auditTrailService; // Service to save activity logs

    @Value("${shikhar.audit.paths}")
    private String auditPaths;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession();
        String contextPath = request.getServletPath();

        // Store the start time in request attribute to calculate duration
        LoginBean loginVo = (LoginBean) session.getAttribute("loginLst");
        if(loginVo == null){
            return true;
        }
        String userId = loginVo.getUserBean().getUserId();
        int period = loginVo.getPeriodBean().getAcctYear();


        String body =
        new GsonBuilder().addSerializationExclusionStrategy(
                new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> aClass) {
                        return false;
                    }
                }
        ).create().toJson(request.getParameterMap()).replaceAll("\\[", "").replaceAll("\\]", "");

        String ctxPath = contextPath.substring(1);
        if(StringUtils.hasText(body) && auditPaths.contains(ctxPath)){
            if(body.length() > 4000){
                body = body.substring(0, 4000);
            }
            auditTrailService.insertAuditLog(new AuditTrail(userId, period, contextPath, "AUDIT", loginVo.getUserBean().getBranch(), body));
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex){
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}

