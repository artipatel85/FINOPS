package com.finops.freight.controller;

import com.finops.controller.AbstractController;
import com.finops.freight.bean.BLBean;
import com.finops.freight.bean.JobBean;
import com.finops.freight.bean.SOBean;
import com.finops.freight.service.BLService;
import com.finops.freight.service.JobService;
import com.finops.freight.service.SOService;
import com.finops.partner.model.PartnerBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class JobController extends AbstractController {

    @Autowired
    private JobService jobService;

    @Autowired
    private SOService soService;

    @RequestMapping("/job.fin")
    public ModelAndView job(@ModelAttribute("jobBean") JobBean bean,
                           @RequestParam("param") String param,
                            HttpSession session) {
        bean.setExpImp(param);
        session.setAttribute("JOBSEARCHBEAN", bean);
        return new ModelAndView("operation/JobSearch", "command", bean);
    }

    @RequestMapping(value = "/jobGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String jobGson(@ModelAttribute("jobBean") JobBean bean,
                  HttpServletRequest request, HttpSession session,
                  @RequestParam("param") String param) {
        bean = (JobBean) session.getAttribute("JOBSEARCHBEAN");
        setSessionData(bean, session);
        bean.setExpImp(param);
        bean.setStart(Integer.parseInt(request.getParameter("start")));
        bean.setLength(Integer.parseInt(request.getParameter("length")));
        bean.setSearchFieldValueList(getSearchFieldValues(request, 14));
        List list = jobService.findJobByPage(bean);

        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/jobCreate.fin")
    public ModelAndView createJob(@ModelAttribute("jobBean") JobBean bean,
                                  @RequestParam("param") String param, HttpSession session) {
        setSessionData(bean, session);
        Map<String, PartnerBean> partnerBeanMap = partnerService.fetchAllPartners(bean.getLoadingAgent());
        if("EXPORT".equalsIgnoreCase(param)) {
            bean.setLoadingAgentCode(bean.getLoadingAgent());
            bean.setLoadingAgentName(partnerBeanMap.get(bean.getLoadingAgent()).getDescription1());
            bean.setLoadingAgentContactDetails(bean.getCompanyAddress());
        }
        else{
            bean.setDestinationAgentCode(bean.getLoadingAgent());
            bean.setDestinationAgentName(partnerBeanMap.get(bean.getLoadingAgent()).getDescription1());
            bean.setDestinationAgentContactDetails(bean.getCompanyAddress());
        }
        bean.setExpImp(param);

        return new ModelAndView("operation/JOB", "command", bean);
    }

    @RequestMapping("/jobRetrieve.fin")
    public ModelAndView soRetrieve(@ModelAttribute("jobBean") JobBean jobBean,
                                   @RequestParam("jobNumber") String jobNumber,
                                   @RequestParam("param") String param,
                                   HttpSession session) {
        setSessionData(jobBean, session);
        jobBean.setJobNumber(jobNumber);
        String expImp = jobBean.getExpImp();
        jobBean = jobService.retrieve(jobBean);
        if(!StringUtils.hasText(expImp)) {
            jobBean.setExpImp(param);
        }
        else{
            jobBean.setExpImp(expImp);
        }
        return new ModelAndView("operation/JOB", "command", jobBean);
    }

    @RequestMapping("/saveJob.fin")
    public ModelAndView saveJob(@ModelAttribute("jobBean") JobBean bean,
                               HttpSession session) throws SQLException {
        setSessionData(bean, session);
        String msg = validate(bean);
        if(msg != null){
            bean.setErrMsg(msg);
            return new ModelAndView("operation/JOB", "command", bean);
        }
        else {
            jobService.save(bean);
        }
        return new ModelAndView("operation/JobSearch", "command", bean);
    }

    private String validate(JobBean bean) {
        return null;
    }

    @RequestMapping("/soListInJob.fin")
    public ModelAndView soListInJob(@ModelAttribute("jobBean") JobBean bean,
                                HttpSession session,@RequestParam("jobNumber") String jobNumber,
                                    @RequestParam("param") String param) throws SQLException {
        setSessionData(bean, session);
        bean.setJobNumber(jobNumber);
        bean.setAction("JOBSO");
        bean.setJobNumber(jobNumber);
        bean.setExpImp(param);
        JobBean jb = jobService.retrieve(bean);
        bean.setVsl(jb.getVsl());
        bean.setVoy(jb.getVoy());
        bean.setEtd(jb.getEtd());
        bean.setEta(jb.getEta());
        bean.setPol(jb.getPol());
        bean.setPod(jb.getPod());
        bean.setLength(10);
        List<SOBean> soBeanList = soService.findSOByPage(bean);
        bean.setSoBeanList(soBeanList);
        return new ModelAndView("operation/JobSo", "command", bean);
    }

    @RequestMapping(value = "/soListInJobGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String soListInJobGson(@ModelAttribute("jobBean") JobBean bean,
                                    HttpSession session, HttpServletRequest request,
                           @RequestParam("jobNumber") String jobNumber) throws SQLException {
        setSessionData(bean, session);
        prePaginate(bean, request, 10);
        bean.setAction("JOBSO");
        bean.setJobNumber(jobNumber);
        JobBean jb = jobService.retrieve(bean);
        bean.setVsl(jb.getVsl());
        bean.setVoy(jb.getVoy());
        bean.setEtd(jb.getEtd());
        bean.setEta(jb.getEta());
        bean.setPol(jb.getPol());
        bean.setPod(jb.getPod());
        List<SOBean> soBeanList = soService.findSOByPage(bean);
        return postPaginate(soBeanList);
    }

    @RequestMapping("/jobSoSave.fin")
    public String jobSoSave(@ModelAttribute("jobBean") JobBean bean,
                                    HttpSession session,
                            @RequestParam("param") String param) throws SQLException {
        setSessionData(bean, session);
        jobService.updateJobNumber(bean);
        bean.setExpImp(param);
        return "forward:/jobRetrieve.fin";
    }

    @RequestMapping("/deleteJob.fin")
    public ModelAndView deleteBL(@ModelAttribute("jobBean") JobBean jobBean,
                                 @RequestParam("param") String param){
        jobService.deleteJob(jobBean);
        jobBean.setExpImp(param);
        return new ModelAndView("operation/JobSearch", "command", jobBean);
    }
}
