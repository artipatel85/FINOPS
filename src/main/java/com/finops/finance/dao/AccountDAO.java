package com.finops.finance.dao;

import com.finops.dao.AbstractDAO;
import com.finops.finance.bean.LedgerBean;
import com.finops.freight.bean.FinactPropertiesBean;
import com.finops.partner.model.PartnerBean;
import com.finops.report.model.ReportBean;
import com.finops.util.DateUtil;
import com.finops.util.sql.Condition;
import com.finops.util.sql.Query;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AccountDAO extends AbstractDAO {
    public List<LedgerBean> ledgerView(LedgerBean ledgerBean) {
        QueryBuilder queryBuilder = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" cc.code_combination_id,cc.acct_id,cc.acct_name,cc.parent_id,cc.acct_type_id,bt.acct_type_name,at1.acct_name parentName," +
                        "prt.address1 prtAddress,prt.gstin_no,cou.DESCRIPTION country ")
                .FROM("gl_code_combination_d", "cc")
                .INNER_JOIN("gl_acct_type_d", "bt", new Condition("cc.acct_type_id", "bt.acct_type_id", Query.EQUALS))
                .INNER_JOIN("gl_code_combination_d", "at1", new Condition("cc.parent_id", "at1.acct_id", Query.EQUALS))
                .LEFT_JOIN("prt_contact_details_d", "prt", new Condition("cc.code_combination_id", "prt.code_combination_id", Query.EQUALS))
                .LEFT_JOIN("country_d", "cou", new Condition("prt.country_code", "cou.country_code", Query.EQUALS))
                .WHERE("cc.company_id", "?")
                .AND("cc.acct_flag","'L'");

        String[] fields = {"cc.acct_name", "at1.acct_name", "acct_type_name", "address1", "gstin_no", "DESCRIPTION"};
        addFilter(queryBuilder, ledgerBean, fields);
        addLimit("cc.acct_name ASC", queryBuilder, ledgerBean);

        return jdbcTemplate.query(queryBuilder.build().toString(),
                new BeanPropertyRowMapper<>(LedgerBean.class)
                , ledgerBean.getCompanyId());
    }

    public LedgerBean retrieveLedger(LedgerBean ledgerBean) {
        String query = "SELECT prt.address1 prtAddress, prt.address2 prtAddress2, prt.contact_person prtContactPerson," +
                "prt.email prtEmail, prt.web prtWebsite, prt.city_name city,prt.zip_code zipcode, prt.currency_id," +
                "prt.tel_cc telCC, prt.tel_AC telAC, prt.tel_no, prt.fax_cc faxCC, prt.fax_ac faxAC, prt.fax_no, prt.country_code countrycode," +
                "cc.ACCT_NAME,cc.ACCT_ID,CC.PARENT_ID,CC.ACCT_TYPE_ID,at1.acct_name parentName,sta.STATE_NAME,cou.DESCRIPTION country,"
                + "cur.currency_code currencyName,cc.attribute1,COALESCE(BAL.OPN_BAL,0) openingBalance,prt.state_code,"
                + "partner.description,cc.code_combination_id,prt.gstin_No,prt.pan_no,prt.tan_no "
                + "FROM gl_code_combination_d cc "
                + "LEFT OUTER JOIN prt_contact_details_d prt ON (cc.code_combination_id = prt.code_combination_id) "
                + "LEFT OUTER JOIN currency_d cur ON (prt.currency_id = cur.currency_id) "
                + "LEFT OUTER JOIN STATE_D sta ON (prt.STATE_CODE = sta.STATE_CODE) "
                + "LEFT OUTER JOIN country_d cou ON(prt.country_code = cou.country_code) "
                + "INNER JOIN gl_code_combination_d at1 ON (cc.parent_id=at1.acct_id) "
                + "LEFT OUTER JOIN GL_BALANCES_F BAL ON(BAL.CODE_COMBINATION_ID=CC.CODE_COMBINATION_ID and BAL.acct_year=?) "
                + "LEFT OUTER JOIN partner_d partner ON (partner.partner_code = cc.attribute1) "
                + "WHERE cc.code_combination_id = ? ";
        return jdbcTemplate.queryForObject(query,
                new BeanPropertyRowMapper<>(LedgerBean.class),
                ledgerBean.getAcctYear(),ledgerBean.getCodeCombinationId());

//        return jdbcTemplate.queryForObject(query,
//                (rs, rowNum )-> {
//                    LedgerBean ledger = new LedgerBean();
//                    ledger.setAcctName(rs.getString("ACCT_NAME"));
//                    ledger.setAcctID(rs.getInt("ACCT_ID"));
//                    ledger.setParentId(rs.getInt("PARENT_ID"));
//                    ledger.setAcctTypeId(rs.getString("ACCT_TYPE_ID"));
//                    ledger.setParentName(rs.getString("parentName"));
////                    ledger.setAddress1(rs.getString("prtAddress"));
////                    ledger.setAddress2(rs.getString("prtAddress2"));
////                    ledger.setContactPerson(rs.getString("prtContactPerson"));
////                    ledger.setEmail(rs.getString("prtEmail"));
////                    ledger.setWebsite(rs.getString("prtWebsite"));
//                    ledger.setCity(rs.getString("city"));
//                    ledger.setZipcode(rs.getString("zipcode"));
//                    ledger.setCurrencyId(rs.getString("currency_id"));
//                    ledger.setTelCC(rs.getString("telCC"));
//                    ledger.setTelAC(rs.getString("telAC"));
//                    ledger.setTelNo(rs.getString("tel_no"));
//                    ledger.setFaxCC(rs.getString("faxCC"));
//                    ledger.setFaxAC(rs.getString("faxAC"));
//                    ledger.setFaxNo(rs.getString("fax_no"));
//                    //ledger.setCountryCode(rs.getString("countrycode"));
//                    ledger.setStateCode(rs.getString("state_code"));
//                    ledger.setCountry(rs.getString("country"));
//                    ledger.setCurrencyName(rs.getString("currencyName"));
//                    ledger.setAttribute1(rs.getString("attribute1"));
//                    ledger.setOpeningBalance(rs.getDouble("openingBalance"));
//                    ledger.setGstinNo(rs.getString("gstin_No"));
//                    //ledger.setPartnerDescription(rs.getString("description"));
//                    ledger.setCodeCombinationId(rs.getInt("code_combination_id"));
//                    return ledger;
//                },ledgerBean.getAcctYear(),ledgerBean.getCodeCombinationId());
    }

    public LedgerBean retrieveLedgerByName(LedgerBean ledgerBean) {
        String query = "SELECT prt.address1 prtAddress, prt.address2 prtAddress2, prt.contact_person prtContactPerson," +
                "prt.email prtEmail, prt.web prtWebsite, prt.city_name city,prt.zip_code zipcode, prt.currency_id," +
                "prt.tel_cc telCC, prt.tel_AC telAC, prt.tel_no, prt.fax_cc faxCC, prt.fax_ac faxAC, prt.fax_no, prt.country_code countrycode," +
                "cc.ACCT_NAME,cc.ACCT_ID,CC.PARENT_ID,CC.ACCT_TYPE_ID,at1.acct_name parentName,sta.STATE_NAME,cou.DESCRIPTION country,"
                + "cur.currency_code currencyName,cc.attribute1,COALESCE(BAL.OPN_BAL,0) OPN_BAL,prt.state_code,"
                + "partner.description,prt.code_combination_id,prt.gstin_No "
                + "FROM gl_code_combination_d cc "
                + "LEFT OUTER JOIN prt_contact_details_d prt ON (cc.code_combination_id = prt.code_combination_id) "
                + "LEFT OUTER JOIN currency_d cur ON (prt.currency_id = cur.currency_id) "
                + "LEFT OUTER JOIN STATE_D sta ON (prt.STATE_CODE = sta.STATE_CODE) "
                + "LEFT OUTER JOIN country_d cou ON(prt.country_code = cou.country_code) "
                + "INNER JOIN gl_code_combination_d at1 ON (cc.parent_id=at1.acct_id) "
                + "LEFT OUTER JOIN GL_BALANCES_F BAL ON(BAL.CODE_COMBINATION_ID=CC.CODE_COMBINATION_ID and BAL.acct_year=?) "
                + "LEFT OUTER JOIN partner_d partner ON (partner.partner_code = cc.attribute1) "
                + "WHERE cc.acct_name = ? ";
        try {
            return jdbcTemplate.queryForObject(query,
                    new BeanPropertyRowMapper<>(LedgerBean.class),
                    ledgerBean.getAcctYear(), ledgerBean.getAcctName());
        }
        catch (Exception e){
            return null;
        }
    }

    public List<LedgerBean> taxMasterView(LedgerBean ledgerBean) {
        QueryBuilder queryBuilder = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" tax.description,tax.percentage,tax.sub_1_per, tax.sub_2_per,tax.sub_3_per,parent.code_combination_id parentccid," +
                        "parent.acct_id,parent.acct_name,parent.acct_type_id,cc.acct_name taxhead ")
                .FROM("gl_tax_master_f", "tax")
                .INNER_JOIN("gl_code_combination_d", "cc", new Condition("cc.code_combination_id", "tax.tax_cc_id", Query.EQUALS))
                .LEFT_JOIN("gl_code_combination_d", "parent", new Condition("parent.code_combination_id", "parent.acct_id", Query.EQUALS));

        if(ledgerBean.getAcctID() != 0) {
                queryBuilder.WHERE("tax.id", ledgerBean.getAcctID()+"");
        }

        return jdbcTemplate.query(queryBuilder.build().toString(),
                new BeanPropertyRowMapper<>(LedgerBean.class));
    }


    public void importPartnerInToFinance(PartnerBean bean) {
        String acctTypeId = "1001";
        String parentId = "22";
        if("Y".equals(bean.getCreditorType())){
            acctTypeId = "1004";
            parentId = "29";
        }
        double finalbalance = 0;

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("LEDGER2");

        Map<String, Object> inParamMap = new HashMap<>();
        inParamMap.put("p_acctname", bean.getDescription1().toUpperCase());
        inParamMap.put("p_accttypeid", acctTypeId);
        inParamMap.put("p_parentid", parentId);
        inParamMap.put("p_ccflag", "Y");
        inParamMap.put("p_userid", bean.getUserId());
        inParamMap.put("p_acctyear", bean.getAcctYear());
        inParamMap.put("p_opnbalance", finalbalance);
        inParamMap.put("p_dr_cr", "DR");
        inParamMap.put("p_param", "insert");
        inParamMap.put("p_codeCombinationID", 0);
        inParamMap.put("p_billWiseFlag", "Y");
        inParamMap.put("p_companyid", bean.getCompanyId());
        inParamMap.put("p_diffInOpeningBal", finalbalance);
        inParamMap.put("p_rateOfDep", 0.00);
        inParamMap.put("p_attribute1", bean.getPartnerCode());
        SqlParameterSource in = new MapSqlParameterSource(inParamMap);


        Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);

        Integer codeCombinationId = (Integer) simpleJdbcCallResult.get("p_maxcodecombiid");
        bean.setPartnerAcctCode(codeCombinationId);

        LedgerBean ledgerBean = new LedgerBean();
        ledgerBean.setPrtContactPerson(bean.getContactPerson());
        ledgerBean.setPrtAddress(bean.getAddress1());
        ledgerBean.setPrtAddress2(bean.getAddress2());
        ledgerBean.setCity(bean.getCity());
        ledgerBean.setZipcode(bean.getZipCode());
        ledgerBean.setCountrycode(bean.getCountryCode());
        ledgerBean.setPrtEmail(bean.getEmail());
        ledgerBean.setPrtWebsite(bean.getWeb());
        ledgerBean.setTelCC(bean.getTelCc());
        ledgerBean.setTelAC(bean.getTelAc());
        ledgerBean.setFaxCC(bean.getFaxCc());
        ledgerBean.setFaxAC(bean.getFaxAc());
        ledgerBean.setFaxNo(bean.getFaxNo());
        ledgerBean.setBankerAddress(bean.getBankerName());
        ledgerBean.setCurrencyId(bean.getCurrencyId());
        ledgerBean.setCreditDays(Integer.toString(bean.getCreditPeriod()));
        ledgerBean.setPanNo(bean.getPanNo());
        ledgerBean.setTanNo(bean.getTanNo());
        ledgerBean.setCodeCombinationId(codeCombinationId);
        ledgerBean.setGstinNo(bean.getGstinNo());
        ledgerBean.setStateCode(bean.getStateCode());
        insertParty(ledgerBean);
    }

    private void insertParty(LedgerBean lb) {
        String insertQuery = "INSERT INTO prt_contact_details_d(contact_person,address1,address2,city_name,"
                + "zip_code,country_code,email,web,tel_cc,tel_ac,tel_no,fax_cc,fax_ac,fax_no,"
                + "banker_name,bank_address,currency_id,credit_period,pan_no,tan_no,"
                + "code_combination_id,gstin_no,STATE_CODE) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        jdbcTemplate.update(insertQuery, lb.getPrtContactPerson(), lb.getPrtAddress(), lb.getPrtAddress2(), lb.getCity(),
                 lb.getZipcode(), lb.getCountrycode(), lb.getPrtEmail(), lb.getPrtWebsite(), lb.getTelCC(), lb.getTelAC(), lb.getTelNo(),
                lb.getFaxCC(), lb.getFaxAC(), lb.getFaxNo(), lb.getBankerName(), lb.getBankerAddress(), lb.getCurrencyId(), lb.getCreditDays(),
                lb.getPanNo(), lb.getTanNo(), lb.getCodeCombinationId(), lb.getGstinNo(), lb.getStateCode());
    }

    public void updateParty(LedgerBean lb) {
        String updateQuery = "UPDATE prt_contact_details_d " +
                "SET " +
                "ADDRESS1 = ?, ADDRESS2 = ?, CITY_NAME = ?, ZIP_CODE = ?, COUNTRY_CODE = ?, EMAIL = ?, WEB = ?, " +
                "TEL_CC = ?, TEL_AC = ?, TEL_NO = ?, FAX_CC = ?, FAX_AC = ?, FAX_NO = ?, CONTACT_PERSON = ?, BANKER_NAME = ?, " +
                "BANK_ADDRESS = ?, CURRENCY_ID = ?, CREDIT_PERIOD = ?, PAN_NO = ?, " +
                "TAN_NO = ?, GSTIN_NO = ?, STATE_CODE = ? WHERE CODE_COMBINATION_ID = ? ";

        String updateBalanceQuery = "UPDATE GL_BALANCES_F SET OPN_BAL = ? WHERE CODE_COMBINATION_ID = ? AND ACCT_YEAR = ? ";
        String updateGlBalanceQuery = "UPDATE GL_DAY_BALANCES_F SET _0_0 = ? WHERE CODE_COMBINATION_ID = ? AND ACCT_YEAR = ? ";

        String updateLedgerQuery = "UPDATE gl_code_combination_d SET acct_name=?, parent_id=? WHERE CODE_COMBINATION_ID = ? ";


        jdbcTemplate.update(updateQuery, lb.getPrtAddress(), lb.getPrtAddress2(), lb.getCity(),
                lb.getZipcode(), lb.getCountrycode(), lb.getPrtEmail(), lb.getPrtWebsite(), lb.getTelCC(), lb.getTelAC(), lb.getTelNo(),
                lb.getFaxCC(), lb.getFaxAC(), lb.getFaxNo(), lb.getPrtContactPerson(), lb.getBankerName(), lb.getBankerAddress(),
                default0(lb.getCurrencyId()), default0(lb.getCreditDays()),
                lb.getPanNo(), lb.getTanNo(), lb.getGstinNo(), lb.getStateCode(), lb.getCodeCombinationId());

        double openingBalance = lb.getOpeningBalance();
        if("CR".equalsIgnoreCase(lb.getLineDtrCtr())){
            openingBalance = openingBalance * -1;
        }

        jdbcTemplate.update(updateBalanceQuery, openingBalance, lb.getCodeCombinationId(), lb.getAcctYear());
        jdbcTemplate.update(updateGlBalanceQuery, openingBalance, lb.getCodeCombinationId(), lb.getAcctYear());
        jdbcTemplate.update(updateLedgerQuery, lb.getAcctName(),lb.getParentId(), lb.getCodeCombinationId());

    }

    public ReportBean getCurrentBalance(String acctYear, String codeId, String dayMonth) {
        Map<String, Integer> params = new HashMap<>();
        params.put(acctYear, java.sql.Types.VARCHAR);
        params.put(codeId, java.sql.Types.VARCHAR);
        String query = "SELECT DBAL." + dayMonth + " param1,CC.acct_name param2 FROM gl_day_balances_f DBAL "
                + "LEFT OUTER JOIN gl_code_combination_d CC ON (DBAL.code_combination_id = CC.code_combination_id) "
                + "WHERE DBAL.acct_year = ? AND DBAL.code_combination_id = '" + codeId + "' ";

        String newQuery = "select (SUM(ACCOUNTED_DR) - SUM(ACCOUNTED_CR) + GLDAY._0_0) param1,CC.acct_name param2 from gl_je_f je " +
                "INNER JOIN gl_day_balances_f glday ON (je.code_combination_id=glday.code_combination_id AND je.ACCT_YEAR = glday.ACCT_YEAR) " +
                "INNER JOIN gl_code_combination_d CC ON (JE.code_combination_id = CC.code_combination_id) " +
                "where je.CODE_COMBINATION_ID = ? AND JE_DATE <= ? AND je.ACCT_YEAR=? " +
                "GROUP BY GLDAY._0_0 ";

        return jdbcTemplate.queryForObject(newQuery,
                new BeanPropertyRowMapper<>(ReportBean.class),
                codeId, dayMonth, acctYear);
    }


    public void saveLedger(LedgerBean bean) {
        String acctTypeId = "1001";
        String parentId = "22";
        if("Y".equals(bean.getCreditorType())){
            acctTypeId = "1004";
            parentId = "29";
        }
        double finalbalance = 0;


        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("LEDGER2");

        Map<String, Object> inParamMap = new HashMap<>();
        inParamMap.put("p_acctname", bean.getAcctName().toUpperCase());
        inParamMap.put("p_accttypeid", bean.getAcctTypeId());
        inParamMap.put("p_parentid", bean.getParentId());
        inParamMap.put("p_ccflag", "Y");
        inParamMap.put("p_userid", bean.getUserId());
        inParamMap.put("p_acctyear", bean.getAcctYear());
        inParamMap.put("p_opnbalance", finalbalance);
        inParamMap.put("p_dr_cr", "DR");
        inParamMap.put("p_param", "insert");
        inParamMap.put("p_codeCombinationID", 0);
        inParamMap.put("p_billWiseFlag", "Y");
        inParamMap.put("p_companyid", bean.getCompanyId());
        inParamMap.put("p_diffInOpeningBal", finalbalance);
        inParamMap.put("p_rateOfDep", 0.00);
        inParamMap.put("p_attribute1", "N");

        SqlParameterSource in = new MapSqlParameterSource(inParamMap);

        Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);

        Integer codeCombinationId = (Integer) simpleJdbcCallResult.get("p_maxcodecombiid");

        LedgerBean ledgerBean = new LedgerBean();
        ledgerBean.setPrtContactPerson(bean.getPrtContactPerson());
        ledgerBean.setPrtAddress(bean.getPrtAddress());
        ledgerBean.setPrtAddress2(bean.getPrtAddress2());
        ledgerBean.setCity(bean.getCity());
        ledgerBean.setZipcode(bean.getZipcode());
        ledgerBean.setCountrycode(bean.getCountrycode());
        ledgerBean.setPrtEmail(bean.getPrtEmail());
        ledgerBean.setPrtWebsite(bean.getPrtWebsite());
        ledgerBean.setTelCC(bean.getTelCC());
        ledgerBean.setTelAC(bean.getTelCC());
        ledgerBean.setFaxCC(bean.getFaxCC());
        ledgerBean.setFaxAC(bean.getFaxAC());
        ledgerBean.setFaxNo(bean.getFaxNo());
        ledgerBean.setBankerAddress(bean.getBankerName());
        ledgerBean.setCurrencyId(StringUtils.hasText(bean.getCurrencyId()) ? bean.getCurrencyId() : "0");
        ledgerBean.setCreditDays(StringUtils.hasText(bean.getCreditDays()) ? bean.getCreditDays() : "0");
        ledgerBean.setPanNo(bean.getPanNo());
        ledgerBean.setTanNo(bean.getTanNo());
        ledgerBean.setCodeCombinationId(codeCombinationId);
        ledgerBean.setGstinNo(bean.getGstinNo());
        ledgerBean.setStateCode(bean.getStateCode());
        insertParty(ledgerBean);
    }

    public void balanceMove(LedgerBean bean) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("GL_BALANCE_UPDATE_CRON");

        Map<String, Object> inParamMap = new HashMap<>();
        inParamMap.put("p_Root", 0);
        inParamMap.put("p_acctyear", bean.getAcctYear());

        SqlParameterSource in = new MapSqlParameterSource(inParamMap);

        Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);

        String dayBalanceMove = "update gl_day_balances_f dbal " +
                "INNER JOIN gl_balances_f bal " +
                "set dbal._0_0=bal.opn_bal " +
                "where bal.code_combination_id = dbal.code_combination_id " +
                "AND bal.acct_year = dbal.acct_year " +
                "AND dbal.acct_year = ? ";

        jdbcTemplate.update(dayBalanceMove, bean.getAcctYear());

    }

    public void balanceTransfer(FinactPropertiesBean bean) {
        String query = "UPDATE gl_balances_f bal1, gl_day_balances_f bal2 SET bal1.OPN_BAL = bal2._31_3 "
                + "WHERE bal1.ACCT_YEAR=" + bean.getAcctYear() + " AND bal2.ACCT_YEAR="+ DateUtil.getPrevAcctYear(bean.getAcctYear())
                + " AND bal1.CODE_COMBINATION_ID=bal2.CODE_COMBINATION_ID "
                + "AND bal1.CODE_COMBINATION_ID NOT IN ("
                + "SELECT CODE_COMBINATION_ID FROM GL_CODE_COMBINATION_D "
                + "WHERE PARENT_ID IN (6,7,8,9, 4852, 4855)  )";

        String query2 = "UPDATE gl_day_balances_f bal1, gl_day_balances_f bal2 SET bal1._0_0 = bal2._31_3 "
                + "WHERE bal1.ACCT_YEAR=" + bean.getAcctYear() + " AND bal2.ACCT_YEAR="+DateUtil.getPrevAcctYear(bean.getAcctYear())
                + " AND bal1.CODE_COMBINATION_ID=bal2.CODE_COMBINATION_ID "
                + "AND bal1.CODE_COMBINATION_ID NOT IN ("
                + "SELECT CODE_COMBINATION_ID FROM GL_CODE_COMBINATION_D "
                + "WHERE PARENT_ID IN (6,7,8,9,4852, 4855)  )";
        jdbcTemplate.update(query);
        jdbcTemplate.update(query2);
    }

    public void updateFinactProperties(FinactPropertiesBean finactPropertiesBean) {
        String query = "UPDATE finact_cache SET cache_value = ? WHERE cache_key=?";
        jdbcTemplate.update(query, finactPropertiesBean.getCutOffDate(), "CUT_OFF_DATE");
    }
}
