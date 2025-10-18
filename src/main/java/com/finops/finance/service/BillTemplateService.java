package com.finops.finance.service;

import com.finops.finance.bean.BillTemplateBean;
import com.finops.finance.bean.InvoiceBean;
import com.finops.finance.dao.BillTemplateDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BillTemplateService {

    @Autowired
    private BillTemplateDAO billTemplateDAO;

    public List<BillTemplateBean> billTempView(BillTemplateBean billTemplateBean) {
        return billTemplateDAO.billTempView(billTemplateBean);
    }

    @Transactional
    public void saveBillTemplate(BillTemplateBean billTemplateBean) {
        if (billTemplateBean.getTemplateId() != 0) {
            billTemplateDAO.deleteBillTemplate(billTemplateBean);
        }
        int autoNumber = billTemplateDAO.generateAutoNumber("SELECT MAX(template_id) FROM template_d", 1001);
        billTemplateDAO.saveBillTemplate(billTemplateBean, autoNumber);
    }

    public BillTemplateBean fetchBillTemplate(BillTemplateBean billTemplateBean) {
        return billTemplateDAO.fetchBillTemplate(billTemplateBean);
    }

    public void fetchBillTemplateByParams(InvoiceBean billingBean) {
        billTemplateDAO.fetchBillTemplateByParams(billingBean);
    }
}
