package com.finops.finance.dao;

import com.finops.dao.AbstractDAO;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

@Component
public class FinanceAccountCronDAO extends AbstractDAO {

    public void financeCron(String trxDate){
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("LEDGER2")
                .declareParameters(
                        new SqlParameter("p_acctname", Types.VARCHAR)
                );
        Map<String, Object> inParamMap = new HashMap<>();
        inParamMap.put("trxDate", trxDate);
        simpleJdbcCall.execute(inParamMap);

//
//        String q1 = "UPDATE ar_customer_trx_f ar INNER JOIN so_hdr_f so ON (CAST(so.so_number AS CHAR) = ar.so_number) " +
//                "SET ar.awb_bl_no = so.bl_no " +
//                "WHERE ar.so_number is not null AND (ar.awb_bl_no is null or ar.awb_bl_no = '')  " +
//                "AND ar.rev_exp = 'EXPENSE' AND ar.sea_air = 'SEA' " +
//                "and ar.trx_date > ? ";
//
//        int count = jdbcTemplate.update(q1, trxDate);
//        if(count > 0){
//            System.out.println("Query q1 had records to be updated");
//        }
//
//        String q2 = "UPDATE ar_customer_trx_f ar " +
//                "INNER JOIN si_hdr_f si ON (si.si_number = ar.so_number) " +
//                "SET ar.awb_bl_no = si.hawb_no " +
//                "WHERE ar.so_number is not null AND (ar.awb_bl_no is null or ar.awb_bl_no = '') " +
//                "AND ar.rev_exp = 'EXPENSE' AND ar.sea_air = 'AIR' " +
//                "and ar.trx_date > ? ";
//
//        count = jdbcTemplate.update(q1, trxDate);
//        if(count > 0){
//            System.out.println("Query q2 had records to be updated");
//        }
//
//        String q3 = "UPDATE ar_customer_trx_lines_f arl " +
//                "INNER JOIN so_hdr_f so ON (CAST(so.so_number AS CHAR) = arl.so_no) " +
//                "SET arl.bl_no = so.bl_no " +
//                "WHERE arl.so_no is not null AND (arl.bl_no is null or arl.bl_no = '') " +
//                "AND arl.rev_exp = 'EXPENSE' AND arl.sea_air = 'SEA' " +
//                "and arl.trx_date > ? ";
//
//        count = jdbcTemplate.update(q1, trxDate);
//        if(count > 0){
//            System.out.println("Query q3 had records to be updated");
//        }
//
//        String q4 = "UPDATE gl_je_f je " +
//                "INNER JOIN so_hdr_f so ON (so.so_number = je.SO_NUMBER) " +
//                "SET je.BL_NO = so.BL_NO " +
//                "WHERE (je.SO_NUMBER IS NOT NULL AND je.SO_NUMBER <> '') and (je.BL_NO IS NULL " +
//                "OR je.bl_no = '') " +
//                "AND sea_air = 'SEA' AND je.so_number = 1 " +
//                "AND je.JE_DATE > ? ";
//
//        count = jdbcTemplate.update(q1, trxDate);
//        if(count > 0){
//            System.out.println("Query q4 had records to be updated");
//        }
    }
}
