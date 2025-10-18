package com.finops.freight.service;

import com.finops.freight.bean.JobBean;
import com.finops.freight.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AirJobService {

    @Autowired
    private AirJobDAO jobDAO;

    @Autowired
    private SIDAO sidao;

    @Autowired
    private HawbDAO bldao;

    public List<JobBean> findJobByPage(JobBean jobBean){
        return jobDAO.findJobByPage(jobBean);
    }

    public JobBean retrieve(JobBean jobBean) {
        return jobDAO.retrieve(jobBean);
    }

    public void jobPdfReport(JobBean bean) {
        jobDAO.shippingAdvice(bean);
    }

    @Transactional
    public void save(JobBean bean) {
        if(StringUtils.hasText(bean.getJobNumber())){
            jobDAO.delete(bean);
            //sidao.updateSIWithJob(bean);
            //bldao.updateBlWithJob(bean);
        }
        else{
            int jobSeqNo = jobDAO.generateAutoNumber("SELECT MAX(JOB_SEQ_NO)+1 JOB_SEQ_NO FROM job_air_hdr_f ", 1);
            String jobNumber = "J"+bean.getLoadingAgent()+jobSeqNo;
            bean.setJobAutoSequence(jobSeqNo);
            bean.setJobNumber(jobNumber);
        }
        jobDAO.save(bean);
    }

    @Transactional
    public void updateJobNumber(JobBean bean) {
        sidao.updateSI(bean);
        bldao.updateJobNumber(bean);
    }
}
