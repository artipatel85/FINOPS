package com.finops.admin.dao;

import com.finops.admin.model.RoleBean;
import com.finops.dao.AbstractDAO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;

@Component
public class RoleDAO extends AbstractDAO {
    public RoleBean retrieveRole(RoleBean roleBean) {
        String query = "SELECT * FROM role_d WHERE role_name = ?";

       return jdbcTemplate.queryForObject(query,
                new BeanPropertyRowMapper<>(RoleBean.class), roleBean.getRoleName());
    }
}
