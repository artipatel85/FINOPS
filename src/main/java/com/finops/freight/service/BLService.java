package com.finops.freight.service;

import com.finops.admin.dao.AdminDAO;
import com.finops.aop.MeasureTime;
import com.finops.finance.bean.InvoiceBean;
import com.finops.freight.bean.BLBean;
import com.finops.freight.bean.ContainerBean;
import com.finops.freight.bean.SOBean;
import com.finops.freight.dao.BLDAO;
import com.finops.freight.dao.SODAO;
import com.finops.partner.model.PartnerBean;
import com.finops.partner.service.PartnerService;
import com.finops.util.ApplicationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class BLService {

    @Autowired
    private BLDAO bldao;

    @Autowired
    private SODAO sodao;

    @Autowired
    private AdminDAO adminDAO;

    @Autowired
    private PartnerService partnerService;

    public List<BLBean> findBLByPage(BLBean blBean, Map<String, PartnerBean> partnerBeanMap){
        List<BLBean> beanList = bldao.findBLByPage(blBean, partnerBeanMap);
        for(BLBean bl : beanList){
            PartnerBean pb = partnerBeanMap.get(bl.getShipper());
            if(StringUtils.hasText(bl.getShipper())) {
                //bl.setShipperName(pb.getDescription1());
            }
            if(StringUtils.hasText(bl.getConsignee())) {
                pb = partnerBeanMap.get(bl.getConsignee());
                //bl.setConsigneeName(pb.getDescription1());
            }
        }
        return beanList;
    }

    @Transactional
    public void save(BLBean bean){
        if("UPDATE".equalsIgnoreCase(bean.getAction())){
            if("IMPORT".equalsIgnoreCase(bean.getExpImp()) && !StringUtils.hasText(bean.getDoNumber())){
                String type = "DOSEAIMP";
                String serialNoColumn = ApplicationUtil.getBranchSrNoField(bean.getLoadingAgent());
                String query = "SELECT "+serialNoColumn+" FROM gl_doc_serial_number_d WHERE doc_short_name = '" + type + "' and acct_year = 20222023 ";
                int srNo = bldao.generateAutoNumber(query, 1);
                adminDAO.updateDocSerialNo(type, 20222023, serialNoColumn);
                bean.setDoNumber(srNo+"");
            }
            bldao.update(bean);
        }
        else {
            BLBean blBean = bldao.generateBLNo(bean);
            bean.setBlAutoSequence(blBean.getBlAutoSequence());
            if(!StringUtils.hasText(bean.getBlNumber())) {
                bean.setBlNumber(blBean.getBlNumber());
            }
            bean.setBlNumber(bean.getBlNumber().trim().toUpperCase());
            bldao.save(bean);
            int counter = 1;
            for(ContainerBean cb: bean.getCbList()) {
                bldao.saveContainers(bean, cb, counter);
                counter++;
            }
            sodao.updateSO(bean);
        }
    }

    public BLBean retrieve(BLBean bean){
        return bldao.retrieve(bean);
    }


    public List<ContainerBean> getContainerDetails(BLBean bean) {
        return bldao.getContainerDetails(bean, 0);
    }

    public void blPDF(BLBean blBean) {
        bldao.blPDF(blBean);
    }

    @MeasureTime
    public BLBean getBlForBilling(InvoiceBean bean) {
        BLBean blBean = bldao.getBlForBilling(bean);

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

    public void blPDFDraft(BLBean blBean) {
        bldao.blPDFDraft(blBean);
    }

    public void approve(BLBean blBean) {
        bldao.approve(blBean);
    }

    public ContainerBean getBlContainer(BLBean blBean, int lineNo) {
        List<ContainerBean> containerBeanList = bldao.getContainerDetails(blBean, lineNo);
        return containerBeanList.get(0);
    }

    public void updateContainer(ContainerBean containerBean) {
        bldao.updateContainer(containerBean);
    }

    @Transactional
    public void deleteBL(BLBean blBean){
        bldao.deletBL(blBean);
    }
}
