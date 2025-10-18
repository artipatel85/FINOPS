package com.finops.freight.service;

import com.finops.bean.FileUploadBean;
import com.finops.freight.bean.ContainerBean;
import com.finops.freight.bean.HawbBean;
import com.finops.freight.bean.SIBean;
import com.finops.freight.bean.SOBean;
import com.finops.freight.dao.SIDAO;
import com.finops.freight.dao.SODAO;
import com.finops.partner.model.PartnerBean;
import com.finops.partner.service.PartnerService;
import com.finops.report.model.ReportBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SIService {

    @Autowired
    private SIDAO sidao;

    @Autowired
    private PartnerService partnerService;

    public List<SIBean> findSIByPage(SOBean bean){
    List<SIBean> beanList = sidao.findSIByPage(bean);
        for(SIBean so : beanList){
            so.setUuid(so.getJobNumber());
        }
        return beanList;
    }


    public SIBean retrieve(SIBean bean, boolean isFetchCntnrRequired){
        List<SIBean> siBeanList = sidao.retrieve(bean);
        Set<String> duplicateCheckSet = new HashSet<>();
        int i = 0;
        if(siBeanList.size() > 0){
            for(SIBean sb : siBeanList){
                String value = sb.getShipper()+sb.getConsignee();
                boolean flag = duplicateCheckSet.add(value);
                if(flag == true && i>0){
                    return null;
                }
                i++;
            }

            SIBean sb = siBeanList.get(0);
            if(bean.getIds() != null && bean.getIds().length > 0){
                String remarkList = siBeanList.stream().map(o -> o.getRemarks()).collect(Collectors.joining("\n"));
                sb.setRemarks(remarkList);
            }
            int[] ids = bean.getIds();
            BeanUtils.copyProperties(sb, bean);
            bean.setIds(ids);

            if(isFetchCntnrRequired) {
                List<ContainerBean> containerBeanList = sidao.getContainerDetails(bean);
                bean.setCbList(containerBeanList);
            }
            return sb;
        }
        return null;
    }

    public List<ContainerBean> getContainerDetails(SIBean bean){
        return sidao.getContainerDetails(bean);
    }

    public List<ContainerBean> getContainerDetailsForJob(SIBean bean){
        return sidao.getContainerDetailsForJob(bean);
    }

    @Transactional
    public void saveSI(SIBean bean, boolean copyAction) {
        if(bean.getBkgRefNo() == 0 || copyAction) {
            int bkgRefNo = sidao.generateAutoNumber("SELECT MAX(BKG_REF_NO)+1 FROM si_hdr_f", 1001);
            bean.setBkgRefNo(bkgRefNo);

            int soNumber = sidao.generateAutoNumber("SELECT MAX(si_number)+1 FROM si_hdr_f", 1001);
            bean.setSoNumber(Integer.toString(soNumber));

            sidao.saveSI(bean);
        }
        else{
            sidao.updateSI(bean);
        }

    }

    public void siPDF(SOBean soBean) {
        sidao.siPDF(soBean);
    }

    public void saveLclFcl(ContainerBean containerBean) {
        if(!"SAVE".equalsIgnoreCase(containerBean.getAction())){
            sidao.updateLclFcl(containerBean);
        }
        else {
            sidao.saveLclFcl(containerBean);
        }
    }

    public ContainerBean getContainer(SIBean soBean) {
        List<ContainerBean> containerBeanList = sidao.getContainerDetails(soBean);
        if(containerBeanList == null || containerBeanList.size() == 0){
            return  null;
        }
        return containerBeanList.get(0);
    }

    public List<ReportBean> retrieve(String soNo, String loadingAgent, String yrStartDate, String seaAir, String expImp) {
        return sidao.retrieve(soNo, loadingAgent, yrStartDate, seaAir, expImp);
    }

    public SIBean retrieveBySI(SIBean bean, boolean isFetchCntnrRequired) {
        List<SIBean> siBeanList = sidao.retrieveBySI(bean);
        Set<String> duplicateCheckSet = new HashSet<>();
        int i = 0;
        if(siBeanList.size() > 0){
            for(SIBean sb : siBeanList){
                String value = sb.getShipper()+sb.getConsignee();
                boolean flag = duplicateCheckSet.add(value);
                if(flag == true && i>0){
                    return null;
                }
                i++;
            }

            SIBean sb = siBeanList.get(0);
            if(bean.getIds() != null && bean.getIds().length > 0){
                String remarkList = siBeanList.stream().map(o -> o.getRemarks()).collect(Collectors.joining("\n"));
                sb.setRemarks(remarkList);
            }
            int[] ids = bean.getIds();
            BeanUtils.copyProperties(sb, bean);
            bean.setIds(ids);

            if(isFetchCntnrRequired) {
                List<ContainerBean> containerBeanList = sidao.getContainerDetails(bean);
                bean.setCbList(containerBeanList);
            }
            return sb;
        }
        return null;
    }
//
//    public void uploadFile(FileUploadBean fileUploadBean) {
//        sodao.uploadFile(fileUploadBean);
//    }
}
