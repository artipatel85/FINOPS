package com.finops.freight.service;

import com.finops.freight.dao.FreightCronDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FreightCronService {

    @Autowired
    private FreightCronDAO freightCronDAO;

    public void freightCron(String trxDate){
        freightCronDAO.freightCron(trxDate);
    }
}
