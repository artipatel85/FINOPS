package com.finops.admin.dao;

import com.finops.admin.model.FormBean;
import com.finops.dao.AbstractDAO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FormDAO extends AbstractDAO {

    public List<FormBean> findAll(){
        String findAll = ("SELECT FORM_ID,FORM_NAME name,STATUS "
                + "FROM forms_d ");

        return jdbcTemplate.query(
                findAll,
                new BeanPropertyRowMapper<>(FormBean.class));
    }

    public FormBean findById(int id){
        String findById = ("SELECT form_id, form_name name, url, parent_id  "
                + "FROM forms_d WHERE form_id =? ");

        return jdbcTemplate.queryForObject(
                findById,
                new BeanPropertyRowMapper<>(FormBean.class),
                id);
    }
}
