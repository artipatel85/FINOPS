package com.finops.admin.dao;

import com.finops.admin.entity.User;
import com.finops.admin.model.AdminBean;
import com.finops.admin.model.PartnerAccount;
import com.finops.admin.model.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class UserDAO extends AdminDAO {

    public UserBean getUser(UserBean bean){

        String userquery = ("SELECT USR.PARTNER_CODE branch,PA.PARTNER_ACCOUNT_CODE"
                + ",PA.DESCRIPTION1"
                + ",((PA.DESCRIPTION1)+(PA.ADDRESS1)+(PA.CITY_NAME)+'-'+(COALESCE(PA.ZIP_CODE,''))+(C.DESCRIPTION))ADDRESS"
                + ",((PA.ADDRESS1)+(PA.CITY_NAME)+'-'+(COALESCE(PA.ZIP_CODE,''))+(C.DESCRIPTION)) ADDRESS1"
                + ",('('+PA.TEL_AC+')'+PA.TEL_NO) TEL,('('+PA.FAX_AC+')'+PA.FAX_NO)FAX,PA.EMAIL,PA.WEB,PA.CLIENT"
                + ",PA.BILLING_PARTY,PA.SHPR,PA.CNEE,PA.AGNT,PA.CO_LOADER,PA.CARRIER,PA.SUB_CONTRCTR"
                + ",USR.USER_ID,USR.DESCRIPTION,USR.USER_PW,USR.ROLL ROLE,USR.CITY_NAME,USR.COUNTRY_CODE,"
                + "pa.COMPANY_ID,company.description companyName,"
                + "CONCAT(company.ADDRESS1,company.CITY_NAME) companyAddress,pa.salesman_code,sal.name salesman"
                + ",company.pan_no,company.tan_no,company.service_tax_no,company.note,usr.is_fin_user,"
                + "PA.state_code companyStateCode, PA.gstin_no,company.bank_name,company.bank_address,company.bank_ac_no,company.swift_code,pa.zip_code "
                + "FROM USER_D USR "
                + "INNER JOIN PARTNER_ACCOUNT_D PA ON (USR.PARTNER_CODE=PA.PARTNER_CODE) "
                + "LEFT OUTER JOIN COUNTRY_D C ON (PA.COUNTRY_CODE=C.COUNTRY_CODE) "
                + "LEFT OUTER JOIN COMPANY_D company ON (company.company_id = pa.company_id) "
                + "LEFT OUTER JOIN salesman_f sal ON (sal.code = pa.salesman_code) "
                + "WHERE USR.STATUS='A'  "
                + "AND usr.user_id=? "
                + "AND usr.user_pw=? ");

        return jdbcTemplate.queryForObject(
                userquery,
                new BeanPropertyRowMapper<>(UserBean.class),
                bean.getUserId(), bean.getPassword());
    }


    public Properties getFinactCache(){
        String query = "SELECT * FROM finact_cache";

        return jdbcTemplate.query(query,
                new ResultSetExtractor<Properties>() {
                    @Override
                    public Properties extractData(ResultSet rs) throws SQLException, DataAccessException {
                        Properties map = new Properties();
                        while (rs.next()) {
                            map.put(rs.getString("cache_key"), rs.getString("cache_value"));
                        }
                        return map;
                    }
                });
    }

}
