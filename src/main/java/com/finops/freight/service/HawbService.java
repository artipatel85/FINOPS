package com.finops.freight.service;

import com.finops.aop.MeasureTime;
import com.finops.finance.bean.InvoiceBean;
import com.finops.freight.bean.BLBean;
import com.finops.freight.bean.HawbBean;
import com.finops.freight.bean.ContainerBean;
import com.finops.freight.dao.BLDAO;
import com.finops.freight.dao.HawbDAO;
import com.finops.freight.dao.SIDAO;
import com.finops.freight.dao.SODAO;
import com.finops.partner.model.PartnerBean;
import com.finops.partner.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class HawbService {

    @Autowired
    private HawbDAO hawbdao;

    @Autowired
    private SIDAO sidao;

    @Autowired
    private PartnerService partnerService;

    public List<HawbBean> findHawbByPage(HawbBean hawbBean, Map<String, PartnerBean> partnerBeanMap){
        List<HawbBean> beanList = hawbdao.findHawbByPage(hawbBean, partnerBeanMap);
        for(HawbBean bl : beanList){
            PartnerBean pb = partnerBeanMap.get(bl.getShipper());
            if(StringUtils.hasText(bl.getShipper())) {
                bl.setShipperName(pb.getDescription1());
            }
            if(StringUtils.hasText(bl.getConsignee())) {
                pb = partnerBeanMap.get(bl.getConsignee());
                bl.setConsigneeName(pb.getDescription1());
            }
        }
        return beanList;
    }

    @Transactional
    public void save(HawbBean bean){
        if("UPDATE".equalsIgnoreCase(bean.getAction())){
            hawbdao.update(bean);
        }
        else {
            HawbBean hawbBean = hawbdao.generateBLNo(bean);
            bean.setBlAutoSequence(hawbBean.getBlAutoSequence());
            if(!StringUtils.hasText(bean.getBlNumber())) {
                bean.setBlNumber(hawbBean.getBlNumber().trim());
            }
            bean.setBlNumber(bean.getBlNumber().trim().toUpperCase());
            hawbdao.save(bean);
            int counter = 1;
            for(ContainerBean cb: bean.getCbList()) {
                hawbdao.saveContainers(bean, cb, counter);
                counter++;
            }
            sidao.updateSI(bean);
        }
    }

    public HawbBean retrieve(HawbBean bean){
        return hawbdao.retrieve(bean);
    }


    public List<ContainerBean> getContainerDetails(HawbBean bean) {
        return hawbdao.getContainerDetails(bean, 0);
    }

    public void blPDF(HawbBean hawbBean) {
        hawbdao.blPDF(hawbBean);
    }

    @MeasureTime
    public HawbBean getBlForBilling(InvoiceBean bean) {
        HawbBean blBean = hawbdao.getBlForBilling(bean);

        if("IMPORT".equalsIgnoreCase(bean.getExpImp())){
            blBean.setBillTo(blBean.getConsignee());
            blBean.setBillToName(blBean.getConsigneeName());
            blBean.setBillToAddress(blBean.getConsigneeContactDetails());
        }

//        PartnerBean pb = partnerService.getPartnerData(bean.getBillTo(),
//                bean.getLoadingAgent(), partnerService);
//        blBean.setPartyAcctCode(pb.getPartnerAcctCode());
        return blBean;
    }

    public void blPDFDraft(HawbBean hawbBean) {
        hawbdao.blPDFDraft(hawbBean);
    }

    public void approve(HawbBean hawbBean) {
        hawbdao.approve(hawbBean);
    }

    public ContainerBean getHawbContainer(HawbBean hawbBean, int lineNo) {
        List<ContainerBean> containerBeanList = hawbdao.getContainerDetails(hawbBean, lineNo);
        return containerBeanList.get(0);
    }

    public void updateContainer(ContainerBean containerBean) {
        hawbdao.updateContainer(containerBean);
    }

    @Transactional
    public void deletHAWB(HawbBean bean){
        hawbdao.deletHAWB(bean);
    }
}
