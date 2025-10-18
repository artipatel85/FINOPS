package com.finops.admin.dao;

import com.finops.admin.model.AdminBean;
import com.finops.admin.model.FormBean;
import com.finops.admin.model.GeneralBean;
import com.finops.dao.AbstractDAO;
import com.finops.report.model.ReportBean;
import com.finops.util.ApplicationUtil;
import com.finops.util.sql.Condition;
import com.finops.util.sql.Query;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdminDAO extends AbstractDAO {


    public List<GeneralBean> getSignatures(){
        String query = "SELECT branch param1, type param2, name param3, signature_file param4 FROM signature_master ";

        return jdbcTemplate.query(
                query,
                new BeanPropertyRowMapper<>(GeneralBean.class));
    }

    public List<AdminBean> findAllUsers(AdminBean bean) {
        QueryBuilder findByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" u.USER_ID userId ,u.description,u.PARTNER_CODE ,u.CITY_NAME" +
                        ",c.DESCRIPTION cityName,u.TEL_CC,u.TEL_AC,u.TEL_NO" +
                        ",u.FAX_CC,u.FAX_AC,u.FAX_NO,u.EMAIL" +
                        ",u.ROLL role,u.USER_PW,u.REMARKS UR,u.STATUS,u.CREATED_BY" +
                        ",STR_TO_DATE(u.CREATION_DATE,'%Y-%m-%d') UD,u.AMENDED_BY" +
                        ",STR_TO_DATE(u.AMENDED_DATE,'%Y-%m-%d') CREATED_BY ")
                .FROM("user_d", "u")
                .LEFT_JOIN("country_d", "c"
                        , new Condition("u.country_code", "c.country_code", Query.EQUALS))
                .WHERE("1", "1").build();

        return jdbcTemplate.query(findByPageQuery.toString(),
                new BeanPropertyRowMapper<>(AdminBean.class));
    }

    public List<AdminBean> findAllRoles(AdminBean bean) {
        return jdbcTemplate.query("SELECT role_name role, description FROM role_d",
                new BeanPropertyRowMapper<>(AdminBean.class));
    }

    public List<AdminBean> findAllCurrency(AdminBean bean) {
        return jdbcTemplate.query("SELECT currency_code, currency_id, description, status FROM currency_d ",
                new BeanPropertyRowMapper<>(AdminBean.class));
    }

    public List<AdminBean> findAllCountries(AdminBean bean) {
        return jdbcTemplate.query("SELECT country_code, description countryName, description2, status FROM country_d ",
                new BeanPropertyRowMapper<>(AdminBean.class));
    }

    public List<AdminBean> findAllPorts(AdminBean bean) {
        return jdbcTemplate.query("SELECT PORT.PORT_CODE,PORT.DESCRIPTION1 description ,PORT.DESCRIPTION2,COUNTRY.DESCRIPTION countryName," +
                        "PORT.DISPLAY,PORT.STATUS,PORT.CREATED_BY,PORT.CREATION_DATE,PORT.AMENDED_BY,PORT.AMENDED_DATE FROM PORT_D PORT " +
                        "LEFT OUTER JOIN (SELECT COUNTRY.COUNTRY_CODE,COUNTRY.DESCRIPTION FROM COUNTRY_D COUNTRY WHERE COUNTRY.STATUS = 'A') COUNTRY " +
                        "ON (PORT.COUNTRY_CODE=COUNTRY.COUNTRY_CODE) ",
                new BeanPropertyRowMapper<>(AdminBean.class));
    }

    public AdminBean retrievePort(AdminBean adminBean) {
        String query = "SELECT a.PORT_CODE,a.COUNTRY_CODE,a.DESCRIPTION1 description,a.DESCRIPTION2, " +
                "a.STATUS,a.DISPLAY,b.DESCRIPTION countryName  FROM port_d a LEFT OUTER JOIN country_d b ON ( a.COUNTRY_CODE=b.COUNTRY_CODE) WHERE PORT_CODE= ? ";

        return jdbcTemplate.queryForObject(query,
                new BeanPropertyRowMapper<>(AdminBean.class),
                adminBean.getPortCode());
    }

    public List<ReportBean> getDocSerialNo(AdminBean adminBean) {
        String sql = "SELECT doc_short_name param1, max_serial_no_01 param2, max_serial_no_02 param3, max_serial_no_03 param4, "
                + "max_serial_no_04 param5, max_serial_no_05 param6 "
                + "FROM gl_doc_serial_number_d WHERE acct_year = ? AND doc_short_name IN ('CREDITNOTE','DEBITNOTE','INVOICE','EXPENSE','BOS')";

        return jdbcTemplate.query(sql,
                new BeanPropertyRowMapper<>(ReportBean.class),
                adminBean.getAcctYear());
    }

    public void saveDocSerialNo(AdminBean adminBean) {
        String sql = "UPDATE gl_doc_serial_number_d SET max_serial_no_01=?, max_serial_no_02=?, max_serial_no_03=?, "
                + "max_serial_no_04=?,max_serial_no_05=? WHERE doc_short_name=? AND acct_year=? ";
        List<ReportBean> list = adminBean.getReconList();

        List<Object[]> objects = new ArrayList<>();
        for(ReportBean rb : list){
            Object[] arr = {rb.getParam2(), rb.getParam3(), rb.getParam4(), rb.getParam5(), rb.getParam6(), rb.getParam1(), adminBean.getAcctYear()};
            objects.add(arr);
        }

        jdbcTemplate.batchUpdate(sql,
                objects);
    }

    public void updateDocSerialNo(String type, int acctYear, String serialNoColumn) {

        String updateQuery = "UPDATE gl_doc_serial_number_d SET "+serialNoColumn+" = "+serialNoColumn+" + 1 " +
                "WHERE doc_short_name = ? and acct_year = ? ";

        jdbcTemplate.update(updateQuery, type, acctYear);

    }

    public List<AdminBean> findAllSizes(AdminBean adminBean) {
        return jdbcTemplate.query("select SIZE_CODE,STATUS,DESCRIPTION sizeName FROM size_d",
                new BeanPropertyRowMapper<>(AdminBean.class));
    }

    public List<AdminBean> findAllUnits(AdminBean adminBean) {
        return jdbcTemplate.query("select UNIT_CODE,DESCRIPTION unitName,STATUS FROM unit_d",
                new BeanPropertyRowMapper<>(AdminBean.class));
    }

    public List<ReportBean> getFinactProperties() {
        return jdbcTemplate.query("SELECT CACHE_KEY param1, CACHE_VALUE param2 FROM finact_cache",
                new BeanPropertyRowMapper<>(ReportBean.class));
    }

    public AdminBean retrieveCurrency(AdminBean adminBean) {
        return jdbcTemplate.queryForObject("select CURRENCY_CODE,DESCRIPTION ,DESCRIPTION2,STATUS,EXCHANGE_RATE  " +
                        "FROM currency_d WHERE CURRENCY_CODE=?",
                new BeanPropertyRowMapper<>(AdminBean.class), adminBean.getCurrencyCode());
    }

    public void createCurrency(AdminBean ab) {
        int currencyId = generateAutoNumber("select MAX(CURRENCY_ID)+1 CURRENCY_ID FROM currency_d", 1001);

        String insertQuery = "insert into currency_d (CURRENCY_CODE,DESCRIPTION,DESCRIPTION2,STATUS,CREATED_BY,CREATION_DATE,EXCHANGE_RATE,CURRENCY_ID) " +
                    "values (?,?,?,?,?,SYSDATE(),?,?)";

        jdbcTemplate.update(insertQuery, ab.getCurrencyCode(), ab.getDescription(), ab.getDescription2(),
                ab.getStatus(), ab.getUserId(), ab.getExchangeRate(), currencyId);
    }

    public void updateCurrency(AdminBean ab) {
        String updateQuery = "UPDATE currency_d " +
                "SET DESCRIPTION=?, DESCRIPTION2=?, STATUS=?,AMENDED_BY=?,AMENDED_DATE=SYSDATE(),EXCHANGE_RATE=? " +
                "WHERE CURRENCY_CODE=?";

        jdbcTemplate.update(updateQuery, ab.getDescription(), ab.getDescription2(),
                ab.getStatus(),ab.getUserId(), ab.getExchangeRate(), ab.getCurrencyCode());
    }

    public List<AdminBean> findAllStates(AdminBean adminBean) {
        return jdbcTemplate.query("select state_code unitCode,state_name unitName FROM state_d",
                new BeanPropertyRowMapper<>(AdminBean.class));
    }

    public List<AdminBean> findAllSalesman(AdminBean adminBean) {
        return jdbcTemplate.query("select code,name, designation, email_id,STATUS FROM salesman_f",
                new BeanPropertyRowMapper<>(AdminBean.class));
    }

    public AdminBean retrieveSalesman(AdminBean adminBean) {
        return jdbcTemplate.queryForObject("select code,name, designation, email_id,STATUS FROM salesman_f WHERE code=?",
                new BeanPropertyRowMapper<>(AdminBean.class), adminBean.getCode());
    }

    public void saveSalesman(AdminBean ab) {
        String code = ApplicationUtil.autoString("SM",null);
        String insertQuery = "insert into salesman_f (CODE,NAME,DESIGNATION,STATUS,CREATED_BY,CREATION_DATE,EMAIL_ID) " +
                "values (?,?,?,'A',?,SYSDATE(),?)";

        jdbcTemplate.update(insertQuery, code, ab.getName(), ab.getDesignation(),
                ab.getUserId(), ab.getEmailId());
    }

    public void updateSalesman(AdminBean ab) {
        String updateQuery = "UPDATE salesman_f " +
                "SET NAME=?, DESIGNATION=?, STATUS=?,AMENDED_BY=?,AMENDED_DATE=SYSDATE(),EMAIL_ID=? " +
                "WHERE CODE=?";

        jdbcTemplate.update(updateQuery, ab.getName(), ab.getDesignation(),
                ab.getStatus(),ab.getUserId(), ab.getEmailId(), ab.getCode());
    }

    public List<AdminBean> findAllTerminals(AdminBean adminBean) {
        return jdbcTemplate.query("SELECT TERMINAL.TERMINAL_CODE code,TERMINAL.DESCRIPTION1 name," +
                        "TERMINAL.DESCRIPTION2 description2,TERMINAL.PORT_CODE portCode,TERMINAL.display," +
                        "TERMINAL.status,TERMINAL.created_by,TERMINAL.creation_date " +
                        "FROM TERMINAL_D TERMINAL LEFT OUTER JOIN (SELECT PORT.PORT_CODE,PORT.DESCRIPTION1 " +
                        "FROM PORT_D PORT WHERE PORT.STATUS = 'A') PORT ON (PORT.PORT_CODE=TERMINAL.TERMINAL_CODE)",
                new BeanPropertyRowMapper<>(AdminBean.class));
    }

    public AdminBean retrieveTerminal(AdminBean adminBean) {
        return jdbcTemplate.queryForObject("select a.TERMINAL_CODE code,a.PORT_CODE portCode,a.DESCRIPTION1 name,a.description2," +
                        "a.status,a.display PC FROM terminal_d a LEFT OUTER JOIN port_d b " +
                        "ON ( a.PORT_CODE=b.PORT_CODE) WHERE TERMINAL_CODE=?",
                new BeanPropertyRowMapper<>(AdminBean.class), adminBean.getCode());
    }

    public AdminBean retrieveCountry(AdminBean adminBean) {
        return jdbcTemplate.queryForObject("select country_code,description countryName, description2, zone_code zone,status FROM country_d WHERE COUNTRY_CODE=?",
                new BeanPropertyRowMapper<>(AdminBean.class), adminBean.getCode());
    }

    public void saveTerminal(AdminBean ab) {
        String code = ApplicationUtil.autoString("SM",null);
        String insertQuery = "insert into terminal_d " +
                "(TERMINAL_CODE,PORT_CODE,DESCRIPTION1,DESCRIPTION2,DISPLAY,STATUS,CREATED_BY,CREATION_DATE) " +
                "values (?,?,?,?,'Y','A',?,SYSDATE())";

        jdbcTemplate.update(insertQuery, ab.getCode(),ab.getPortCode(), ab.getName(), ab.getDescription2(),
                ab.getUserId());
    }

    public void saveCountry(AdminBean ab) {
        String code = ApplicationUtil.autoString("SM",null);
        String insertQuery = "insert into country_d " +
                "(COUNTRY_CODE,ZONE_CODE,DESCRIPTION,DESCRIPTION2,DISPLAY,STATUS,CREATED_BY,CREATION_DATE) " +
                "values (?,?,?,?,'Y','A',?,SYSDATE())";

        jdbcTemplate.update(insertQuery, ab.getCountryCode(),ab.getZone(), ab.getCountryName(), ab.getDescription2(),
                ab.getUserId());
    }

    public void updateTerminal(AdminBean ab) {
        String updateQuery = "UPDATE terminal_d SET PORT_CODE=?, DESCRIPTION1=?, " +
                "DESCRIPTION2=?, STATUS=?, DISPLAY=?,AMENDED_BY=?,AMENDED_DATE=SYSDATE() WHERE TERMINAL_CODE=?";

        jdbcTemplate.update(updateQuery, ab.getPortCode(), ab.getName(), ab.getDescription2(),
                ab.getStatus(), ab.getDisplay(),ab.getUserId(), ab.getCode());
    }

    public void updateCountry(AdminBean ab) {
        String updateQuery = "UPDATE country_d SET DESCRIPTION=?, " +
                "DESCRIPTION2=?, ZONE_CODE=?, STATUS=?, AMENDED_BY=?,AMENDED_DATE=SYSDATE() WHERE COUNTRY_CODE=?";

        jdbcTemplate.update(updateQuery, ab.getCountryName(), ab.getDescription2(), ab.getZone(),
                ab.getStatus(), ab.getUserId(), ab.getCountryCode());
    }

    public AdminBean retrieveUser(AdminBean adminBean) {
        StringBuilder query = new StringBuilder(
                "select a.USER_ID userProfileId,a.DESCRIPTION,a.PARTNER_CODE,b.DESCRIPTION1,a.CITY_NAME"
                        + ",a.COUNTRY_CODE,c.DESCRIPTION countryName,a.TEL_CC telCc,a.TEL_AC telAc,a.TEL_NO telNo,a.FAX_CC,a.FAX_AC,a.FAX_NO"
                        + ",a.EMAIL emailId,a.ROLL userRole,a.USER_PW password, a.USER_PW confirmPassword,a.REMARKS,a.STATUS,a.is_fin_user isFinanceUser "
                        + " FROM user_d a"
                        + " INNER JOIN partner_account_d b ON (a.PARTNER_CODE=b.PARTNER_CODE)"
                        + " INNER JOIN country_d c ON(a.COUNTRY_CODE=c.COUNTRY_CODE) WHERE a.USER_ID = ?");

        return jdbcTemplate.queryForObject(query.toString(), new BeanPropertyRowMapper<>(AdminBean.class), adminBean.getUserProfileId());
    }

    public void updateUser(AdminBean bean) {
        String update = "UPDATE user_d "
                + "SET description=?,city_name=?,country_code=?,tel_cc=?,tel_ac=?,tel_no=?,fax_cc=?,fax_ac=?,fax_no=?,"
                + "email=?,roll=?,user_pw=?,remarks=?, AMENDED_BY=?, AMENDED_DATE=SYSDATE(), is_fin_user=?, status=? "
                + "WHERE user_id = ?";

        jdbcTemplate.update(update, bean.getDescription(), bean.getCityName(), bean.getCountryCode(),
                bean.getTelCc(), bean.getTelAc(), bean.getTelNo(), bean.getFaxCc(), bean.getFaxAc(), bean.getFaxNo(),
                bean.getEmailId(), bean.getUserRole(), bean.getPassword(), bean.getRemarks(), bean.getUserId(),
                bean.getIsFinanceUser(), bean.getStatus(), bean.getUserProfileId());
    }

    public void saveUser(AdminBean bean) {

        String insert = "INSERT INTO user_d "
                + "( user_id, description,partner_code,city_name,country_code,tel_cc,tel_ac,tel_no,fax_cc,fax_ac,fax_no,"
                + "email,roll,user_pw,remarks, STATUS, CREATED_BY, CREATION_DATE, AMENDED_BY, AMENDED_DATE, is_fin_user) "
                + " VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,SYSDATE(),?,SYSDATE(), ?)";

        jdbcTemplate.update(insert,bean.getUserProfileId(), bean.getDescription(), bean.getLoadingAgent(), bean.getCityName(), bean.getCountryCode(),
                bean.getTelCc(), bean.getTelAc(), bean.getTelNo(), bean.getFaxCc(), bean.getFaxAc(), bean.getFaxNo(),
                bean.getEmailId(), bean.getUserRole(), bean.getPassword(), bean.getRemarks(), bean.getStatus(), bean.getUserId(),
                bean.getUserId(), bean.getIsFinanceUser());
    }

    public void savePort(AdminBean bean) {
        String insert = "INSERT INTO port_d "
                + "( port_code, country_code, description1, description2, status, display, created_by, creation_date) "
                + " VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE())";

        jdbcTemplate.update(insert, bean.getPortCode(), bean.getCountryCode(), bean.getDescription(), bean.getDescription2(),
                bean.getStatus(), bean.getDisplay(), bean.getUserId());
    }

    public void updatePort(AdminBean bean) {
        String update = "UPDATE port_d "
                + "SET country_code=?, description1=?, description2=?, status=?, display=?, AMENDED_BY=?, AMENDED_DATE=SYSDATE() "
                + "WHERE port_code = ?";

        jdbcTemplate.update(update, bean.getCountryCode(), bean.getDescription(), bean.getDescription2(),
                bean.getStatus(), bean.getDisplay(), bean.getUserId(), bean.getPortCode());
    }

    public void saveRole(AdminBean roleBean) {
        String insert = "INSERT INTO role_d "
                + "(role_code, description, created_by, creation_date) "
                + " VALUES (?, ?, ?, ?, SYSDATE())";

        jdbcTemplate.update(insert, roleBean.getName(), roleBean.getDescription(), roleBean.getUserId());

    String query2 = "INSERT INTO role_priviledge_d (role_name, form_id, _select, _create, _update, _delete, _upload, _download, _print, _approve,created_by,creation_date,"
                + "amended_by,amended_date) "
                + "SELECT ?, form_id, _select, _create, _update, _delete, _upload, _download, _print,_approve,?,SYSDATE(),?,SYSDATE() FROM forms_d ";

        jdbcTemplate.update(query2, roleBean.getName(), roleBean.getUserId(), roleBean.getUserId());


    }

    public void updateRole(AdminBean roleBean) {
        String update = "UPDATE role_d "
                + "SET description=?, AMENDED_BY=?, AMENDED_DATE=SYSDATE() "
                + "WHERE role_name = ?";

        jdbcTemplate.update(update, roleBean.getDescription(), roleBean.getUserId(), roleBean.getName());

        String query3 = "DELETE FROM role_priviledge_d WHERE role_name=?";
        jdbcTemplate.update(query3, roleBean.getName());

        String query2 = "INSERT INTO role_priviledge_d (role_name, form_id, _select, _create, _update, _delete,"
                + " _upload, _download, _print,created_by,creation_date,"
                + "amended_by,amended_date,_approve) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,SYSDATE(),?,SYSDATE(),?)  ";

        for (FormBean fb : roleBean.getPriviledgeList()) {
            jdbcTemplate.update(query2, roleBean.getName(), fb.getFormId(), defaultN(fb.getSelect()), defaultN(fb.getCreate()),
                    defaultN(fb.getUpdate()), defaultN(fb.getDelete()), defaultN(fb.getUpload()), defaultN(fb.getDownload()),
                            defaultN(fb.getPrint()), roleBean.getUserId(), roleBean.getUserId(), defaultN(fb.getApprove()));
        }
    }

    public AdminBean retrieveRole(AdminBean roleBean) {
        String query = "SELECT role_name name, description FROM role_d WHERE role_name = ?";
        return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(AdminBean.class), roleBean.getName());
    }
}
