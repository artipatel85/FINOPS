package com.finops.freight.service;

import com.finops.bean.FileUploadBean;
import com.finops.freight.bean.ContainerBean;
import com.finops.freight.bean.JobBean;
import com.finops.freight.bean.SOBean;
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
public class SOService {

    @Autowired
    private SODAO sodao;

    @Autowired
    private PartnerService partnerService;

    public List<SOBean> findSOByPage(SOBean bean){
    List<SOBean> beanList = sodao.findSOByPage(bean);
        for(SOBean so : beanList){
            so.setShipperName(partnerService.getPartnerData(so.getShipper(),
                    bean.getLoadingAgent(), partnerService).getDescription1());
            so.setConsigneeName(partnerService.getPartnerData(so.getConsignee(),
                    bean.getLoadingAgent(), partnerService).getDescription1());
            so.setUuid(so.getJobNumber());
        }
        return beanList;
    }

    public List<SOBean> searchSo(SOBean bean){
        List<SOBean> beanList = sodao.searchSOByPage(bean);

        return beanList;
    }

    public SOBean retrieve(SOBean bean, boolean isFetchCntnrRequired){
        List<SOBean> soBeanList = sodao.retrieve(bean);
        Set<String> duplicateCheckSet = new HashSet<>();
        int i = 0;
        if(soBeanList.size() > 0){
            for(SOBean sb : soBeanList){
                String value = sb.getShipper()+sb.getConsignee();
                boolean flag = duplicateCheckSet.add(value);
                if(flag == true && i>0){
                    return null;
                }
                i++;
            }

            SOBean sb = soBeanList.get(0);
            if(bean.getIds() != null && bean.getIds().length > 0){
                String remarkList = soBeanList.stream().map(o -> o.getRemarks()).collect(Collectors.joining("\n"));
                sb.setRemarks(remarkList);
            }
            int[] ids = bean.getIds();
            BeanUtils.copyProperties(sb, bean);
            bean.setIds(ids);

            if(isFetchCntnrRequired) {
                List<ContainerBean> containerBeanList = sodao.getContainerDetails(bean, 0, 0);
                bean.setCbList(containerBeanList);
            }
            return sb;
        }
        return null;
    }

    public SOBean retrieveBySo(SOBean bean, boolean isFetchCntnrRequired){
        List<SOBean> soBeanList = sodao.retrieveBySoNumber(bean);
        Set<String> duplicateCheckSet = new HashSet<>();
        int i = 0;
        if(soBeanList.size() > 0){
            for(SOBean sb : soBeanList){
                String value = sb.getShipper()+sb.getConsignee();
                boolean flag = duplicateCheckSet.add(value);
                if(flag == true && i>0){
                    return null;
                }
                i++;
            }

            SOBean sb = soBeanList.get(0);
            if(bean.getIds() != null && bean.getIds().length > 0){
                String remarkList = soBeanList.stream().map(o -> o.getRemarks()).collect(Collectors.joining("\n"));
                sb.setRemarks(remarkList);
            }
            BeanUtils.copyProperties(sb, bean);

            if(isFetchCntnrRequired) {
                List<ContainerBean> containerBeanList = sodao.getContainerDetails(bean, 0, 0);
                bean.setCbList(containerBeanList);
            }
            return sb;
        }
        return null;
    }

    public List<ContainerBean> getContainerDetails(SOBean bean, int type){
        List<ContainerBean> containerBeanList =  sodao.getContainerDetails(bean, 0, type);
        if(type == 5 || type==6) {
            for (ContainerBean cb : containerBeanList) {
                cb.setShipperName(partnerService.getPartnerData(cb.getShipper(),
                        bean.getLoadingAgent(), partnerService).getDescription1());
                cb.setConsigneeName(partnerService.getPartnerData(cb.getConsignee(),
                        bean.getLoadingAgent(), partnerService).getDescription1());

            }
        }
        return containerBeanList;
    }

    @Transactional
    public void saveSO(SOBean bean, boolean copyAction) {
        if(bean.getBkgRefNo() == 0 || copyAction) {
            int bkgRefNo = sodao.generateAutoNumber("SELECT MAX(BKG_REF_NO)+1 FROM so_hdr_f", 1001);
            bean.setBkgRefNo(bkgRefNo);

            int soNumber = sodao.generateAutoNumber("SELECT MAX(so_number)+1 FROM so_hdr_f", 1001);
            bean.setSoNumber(Integer.toString(soNumber));

            sodao.saveSO(bean);
        }
        else{
            sodao.updateSO(bean);
        }

    }

    @Transactional
    public void updateCLP(ContainerBean cb){
        sodao.updateSO(cb);
    }

    @Transactional
    public void updateJobNumber(JobBean jobBean){
        sodao.updateSO(jobBean);
    }

    public void soPDF(SOBean soBean) {
        sodao.soPDF(soBean);
    }

    public void saveLclFcl(ContainerBean containerBean) {
        if(containerBean.getLineNumber() > 0){
            sodao.updateLclFcl(containerBean);
        }
        else {
            int lineNo = sodao.generateAutoNumber("SELECT MAX(LINE_NO)+1 LINE_NO  FROM SO_FCL_LCL_F where bkg_ref_no= " + containerBean.getBookingRefNumber(),
                    1);
            sodao.saveLclFcl(containerBean, lineNo + "");
        }
    }

    public ContainerBean getContainer(SOBean soBean, int lineNo, int type) {
        List<ContainerBean> containerBeanList = sodao.getContainerDetails(soBean, lineNo, type);
        for(ContainerBean cb : containerBeanList){
            if(lineNo == cb.getLineNumber()){
                return cb;
            }
        }
        return null;
    }

    public void uploadFile(FileUploadBean fileUploadBean) {
        sodao.uploadFile(fileUploadBean);
    }

    public void retrieveFileUpload(FileUploadBean fileUploadBean) {
        sodao.retrieveFileUpload(fileUploadBean);
    }

    public List<ReportBean> retrieve(String soNo, String loadingAgent, String yrStartDate, String seaAir, String expImp) {
        return sodao.retrieve(soNo, loadingAgent, yrStartDate, seaAir, expImp);
    }

    public void deleteFclLcl(String bkgRefNo, String lineNo) {
        sodao.deleteFclLcl(bkgRefNo, lineNo);
    }

    @Transactional
    public void deleteSO(SOBean bean) {
        sodao.deleteSO(bean);
    }
}
