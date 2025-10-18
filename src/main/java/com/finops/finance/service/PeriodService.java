package com.finops.finance.service;

import com.finops.finance.bean.PeriodBean;
import com.finops.finance.dao.PeriodDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PeriodService {

    @Autowired
    private PeriodDAO periodDAO;

    public List<PeriodBean> findAll(PeriodBean bean){
        return periodDAO.findAll(bean);
    }

    public void close(PeriodBean periodBean) {
        periodDAO.close(periodBean);
    }

    public void open(PeriodBean periodBean) {
        periodDAO.open(periodBean);
    }

    @Transactional
    public void save(PeriodBean periodBean) {
        periodDAO.save(periodBean);
    }
}
