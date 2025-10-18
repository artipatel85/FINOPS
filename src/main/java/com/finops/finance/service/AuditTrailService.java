package com.finops.finance.service;

import com.finops.admin.model.AuditTrail;
import com.finops.finance.bean.AuditBean;
import com.finops.finance.dao.AuditTrailDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditTrailService {

    @Autowired
    private AuditTrailDAO auditTrailDAO;

    public List<AuditBean> fetchAuditData(AuditBean auditBean) {
        return auditTrailDAO.fetchAuditData(auditBean);
    }

    public void insertAuditLog(AuditTrail auditTrail){
        auditTrailDAO.insertAuditLog(auditTrail);
    }
}
