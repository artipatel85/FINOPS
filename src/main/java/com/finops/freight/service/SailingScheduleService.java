package com.finops.freight.service;

import com.finops.freight.bean.FreightRow;
import com.finops.freight.bean.SailingScheduleBean;
import com.finops.freight.dao.SailingScheduleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SailingScheduleService {

    @Autowired
    private SailingScheduleDAO sailingScheduleDAO;


    public void retrieveSS(SailingScheduleBean sailingScheduleBean) {
        sailingScheduleDAO.retrieveSS(sailingScheduleBean);
    }

    public void createSS(SailingScheduleBean bean) {
        List<FreightRow> polList = new ArrayList<>();
        List<FreightRow> podList = new ArrayList<>();
        fillFreightRows(polList, 10);
        bean.setPolList(polList);
        fillFreightRows(podList, 7);
        bean.setPodList(podList);
    }

    public void fillFreightRows(List<FreightRow> list, int size) {
        int listSize = list.size();
        for (int i = 0; i < size - listSize; i++) {
            list.add(new FreightRow());
        }
    }

    public void save(SailingScheduleBean sailingScheduleBean) {
        sailingScheduleDAO.save(sailingScheduleBean);
    }

    public List fetchList(SailingScheduleBean sailingScheduleBean) {
        return sailingScheduleDAO.fetchList(sailingScheduleBean);
    }
}
