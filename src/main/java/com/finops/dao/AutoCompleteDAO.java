package com.finops.dao;

import com.finops.report.model.ReportBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class AutoCompleteDAO extends AbstractDAO{
    private static Map<Integer, Method> methods;

    static {
        try {
            Class<ReportBean> clazz = ReportBean.class;
            methods = new HashMap<>();
            methods.put(1, clazz.getDeclaredMethod("setParam1", String.class));
            methods.put(2, clazz.getDeclaredMethod("setParam2", String.class));
            methods.put(3, clazz.getDeclaredMethod("setParam3", String.class));
            methods.put(4, clazz.getDeclaredMethod("setParam4", String.class));
            methods.put(5, clazz.getDeclaredMethod("setParam5", String.class));
            methods.put(6, clazz.getDeclaredMethod("setParam6", String.class));
            methods.put(7, clazz.getDeclaredMethod("setParam7", String.class));
            methods.put(8, clazz.getDeclaredMethod("setParam8", String.class));
            methods.put(9, clazz.getDeclaredMethod("setParam9", String.class));
            methods.put(10, clazz.getDeclaredMethod("setParam10", String.class));
            methods.put(11, clazz.getDeclaredMethod("setParam11", String.class));
            methods.put(12, clazz.getDeclaredMethod("setParam12", String.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ReportBean> getAutoDataList(String query, Object[] params) {
        if(params == null) {
            return jdbcTemplate.query(query,
                    new BeanPropertyRowMapper<>(ReportBean.class));
        }
        else{
            return jdbcTemplate.query(query,
                    new BeanPropertyRowMapper<>(ReportBean.class),
                    params);
        }
    }

    public List<ReportBean> getRecons(ReportBean reportbean, String query, int index) {
        String[] params = createParamArray(reportbean.getSearchFieldValueList());
        List<ReportBean> dataList = new ArrayList<>();

        return jdbcTemplate.query(query,
                new ResultSetExtractor<List<ReportBean>>() {
                    @Override
                    public List<ReportBean> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        while (rs.next()) {
                            ReportBean bean = null;
                            try {
                                bean = setValues(rs, index);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            dataList.add(bean);
                        }
                        return dataList;
                    }
                }, params);
    }

    private Map<String, Integer> createParams(List<String> list){
        Map<String, Integer> params = new LinkedHashMap<>();
        int i = 0;
        for(String s: list){
            String appender = "";
            for(int j=0; j <= i; j++){
                appender += "%";
            }
            params.put(s+appender, java.sql.Types.VARCHAR);
            i++;
        }
        return params;
    }

    private String[] createParamArray(List<String> list){
        String[] params = new String[list.size()];
        int i = 0;
        for(String s: list){
            String appender = "";
            for(int j=0; j <= i; j++){
                appender += "%";
            }
            params[i] = appender;
            i++;
        }
        return params;
    }

    private static ReportBean setValues(ResultSet rs, int index) throws SQLException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ReportBean bean = new ReportBean();
        for (int i = 1; i <= index+1; i++) {
            Method method = methods.get(i);
            method.invoke(bean, rs.getString(i));
        }
        return bean;
    }
}
