package com.finops.freight.controller;

import com.finops.controller.AbstractController;
import com.finops.freight.bean.JobBean;
import com.finops.freight.bean.SIBean;
import com.finops.freight.bean.SOBean;
import com.finops.freight.service.AirJobService;
import com.finops.freight.service.JobService;
import com.finops.freight.service.SIService;
import com.finops.freight.service.SOService;
import com.finops.partner.model.PartnerBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Controller
public class AirJobController extends AbstractController {

    @Autowired
    private AirJobService jobService;

    @Autowired
    private SIService siService;

    @RequestMapping("/airJob.fin")
    public ModelAndView job(@ModelAttribute("jobBean") JobBean bean,
                           @RequestParam("param") String param,
                            HttpSession session) {
        bean.setExpImp(param);
        session.setAttribute("AIRJOBSEARCHBEAN", bean);
        return new ModelAndView("operation/AirJobSearch", "command", bean);
    }

    @RequestMapping(value = "/airJobGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String jobGson(@ModelAttribute("jobBean") JobBean bean,
                  HttpServletRequest request, HttpSession session,
                  @RequestParam("param") String param) {
        bean = (JobBean) session.getAttribute("AIRJOBSEARCHBEAN");
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

    @RequestMapping("/airJobCreate.fin")
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
        return new ModelAndView("operation/AirJOB", "command", bean);
    }

    @RequestMapping("/airJobRetrieve.fin")
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
        return new ModelAndView("operation/AirJOB", "command", jobBean);
    }

    @RequestMapping("/saveAirJob.fin")
    public ModelAndView saveJob(@ModelAttribute("jobBean") JobBean bean,
                               HttpSession session) throws SQLException {
        setSessionData(bean, session);
        String msg = validate(bean);
        if(msg != null){
            bean.setErrMsg(msg);
            return new ModelAndView("operation/AirJOB", "command", bean);
        }
        else {
            jobService.save(bean);
        }
        return new ModelAndView("operation/AirJobSearch", "command", bean);
    }

    private String validate(JobBean bean) {
        return null;
    }

    @RequestMapping("/siListInJob.fin")
    public ModelAndView soListInJob(@ModelAttribute("jobBean") JobBean bean,
                                HttpSession session,@RequestParam("jobNumber") String jobNumber,
                                    @RequestParam("param") String param) throws SQLException {
        setSessionData(bean, session);
        bean.setJobNumber(jobNumber);
        bean.setAction("JOBSI");
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
        List<SIBean> siBeanList = siService.findSIByPage(bean);
        bean.setSiBeanList(siBeanList);
        return new ModelAndView("operation/JobSI", "command", bean);
    }

    @RequestMapping(value = "/siListInJobGson.fin", method = RequestMethod.GET, produces = "application/json")
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
        List<SIBean> siBeanList = siService.findSIByPage(bean);
        return postPaginate(siBeanList);
    }

    @RequestMapping("/jobSISave.fin")
    public String jobSoSave(@ModelAttribute("jobBean") JobBean bean,
                                    HttpSession session) throws SQLException {
        setSessionData(bean, session);
        jobService.updateJobNumber(bean);
        return "forward:/airJobRetrieve.fin";
    }
}
