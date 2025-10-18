package com.finops.freight.dao;

import com.finops.dao.AbstractDAO;
import org.springframework.stereotype.Component;

@Component
public class FreightCronDAO extends AbstractDAO {

    public void freightCron(String trxDate){
        String q1 = "UPDATE ar_customer_trx_f ar INNER JOIN so_hdr_f so ON (CAST(so.so_number AS CHAR) = ar.so_number) " +
                "SET ar.awb_bl_no = so.bl_no " +
                "WHERE ar.so_number is not null AND (ar.awb_bl_no is null or ar.awb_bl_no = '')  " +
                "AND ar.rev_exp = 'EXPENSE' AND ar.sea_air = 'SEA' " +
                "and ar.trx_date > ? ";

        jdbcTemplate.update(q1, trxDate);
    }
}