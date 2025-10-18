package com.finops.admin.dao;

import com.finops.admin.model.PartnerAccount;
import com.finops.dao.AbstractDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PartnerAccountDAO extends AbstractDAO {

    public List<PartnerAccount> getBranchDetails(String branch){
        String branchQuery = "SELECT cd.company_id,cd.pan_no,cd.tan_no,gstin_no,state_code,pa.bank_ac_no,banker_name,banker_address," +
                "pa.ifsc_code,pa.swift_code,zip_code,partner_code,description1, pa.address1 " +
                "FROM partner_account_d pa,company_d cd where pa.company_id = cd.company_id " +
                "and partner_code IN ('SFP','SFPM','SFPC','SFPK','SFPG') ";

        return jdbcTemplate.query(
                branchQuery,
                new BeanPropertyRowMapper<>(PartnerAccount.class));
    }
}
