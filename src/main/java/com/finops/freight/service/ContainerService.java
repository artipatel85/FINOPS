package com.finops.freight.service;

import com.finops.admin.service.AdminService;
import com.finops.freight.bean.ContainerBean;
import com.finops.freight.dao.ContainerDAO;
import com.finops.partner.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ContainerService {

    @Autowired
    private ContainerDAO containerDAO;

    public List<ContainerBean> findCLPByPage(ContainerBean bean){
        return containerDAO.findCLPByPage(bean, false);
    }

        public ContainerBean findCLPByPlanNo(ContainerBean bean, PartnerService partnerService,
                                             AdminService adminService){
            List<ContainerBean> clpList = containerDAO.findCLPByPage(bean, true );
            if(clpList != null && clpList.size() > 0){
                ContainerBean cb = clpList.get(0);
                cb.setWarehouseName(partnerService.getPartnerData(cb.getWarehouse(),
                        bean.getLoadingAgent(), partnerService).getDescription1());
                cb.setCarrierName(partnerService.getPartnerData(cb.getCarrierCode(),
                        bean.getLoadingAgent(), partnerService).getDescription1());
                cb.setPolName(adminService.getPortName(cb.getPol(), adminService));
                cb.setPodName(adminService.getPortName(cb.getPod(), adminService));
                return cb;
            }
            return null;
    }

    @Transactional
    public void saveCLP(ContainerBean cb, SOService soService){
        if("AUTO".equalsIgnoreCase(cb.getNumber())){
            int lpNumber = containerDAO.generateAutoNumber("SELECT MAX(LOAD_PLAN_NO)+1 FROM load_plan_hdr_f", 1);
            cb.setNumber(lpNumber+"");
            containerDAO.saveCLP(cb);
            soService.updateCLP(cb);
        }
        else {
            containerDAO.updateCLP(cb);
            soService.updateCLP(cb);
        }
    }


    public void clpPDFReport(ContainerBean containerBean) {
        containerDAO.clpPDFReport(containerBean);
    }
}
