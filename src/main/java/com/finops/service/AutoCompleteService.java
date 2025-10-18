package com.finops.service;

import com.finops.dao.AutoCompleteDAO;
import com.finops.report.model.ReportBean;
import com.finops.util.AutoEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutoCompleteService {

    @Autowired
    private AutoCompleteDAO autoCompleteDAO;

    public List<ReportBean> autoComplete(String autoEnum, Object[] args) {
        return autoCompleteDAO.getAutoDataList(AutoEnum.valueOf(autoEnum).getQuery(), args);
    }

    public List<ReportBean> getRecons(ReportBean reportbean, String s, int parseInt) {
        return autoCompleteDAO.getRecons(reportbean, s, parseInt);
    }
}
