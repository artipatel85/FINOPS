package com.finops.finance.dao;

import com.finops.dao.AbstractDAO;
import com.finops.finance.bean.BillRow;
import com.finops.finance.bean.BillTemplateBean;
import com.finops.finance.bean.BillTemplateRow;
import com.finops.finance.bean.InvoiceBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BillTemplateDAO extends AbstractDAO {

    public List<BillTemplateBean> billTempView(BillTemplateBean billTemplateBean) {
        String query = "SELECT DISTINCT TEMPLATE_NAME,SEA_AIR,EXP_IMP,LOCAL_FOREIGN,REV_EXP,"
                + "INV_DN_CN_MISC billType,TAX_TYPE templateType FROM template_d";

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(BillTemplateBean.class));
    }

    public BillTemplateBean fetchBillTemplate(BillTemplateBean bean) {
        String query = "SELECT template_name,rev_exp,sea_air,exp_imp,local_foreign,inv_dn_cn_misc,tax_type,"
                + "temp.template_id,temp.code_combination_id,temp.code_desc,temp.taxable_1,"
                + "temp.taxable_2,temp.taxable_3,temp.taxable_4,temp.taxable_5,temp.non_taxable,"
                + "temp.per_on_tot_val,temp.per_on_val,temp.discount_per,temp.discount_amt,"
                + "temp.oth_chrgs_per,temp.oth_chrgs_amt,temp.status,cc.acct_name,temp.template_id,dr_cr,"
                + "temp.tax_1_per,temp.tax_2_per,temp.tax_3_per,temp.tax_4_per,temp.tax_5_per,"
                + "temp.sac_code,rev_exp_tax_disc_oc "
                + "FROM template_d temp "
                + "LEFT OUTER JOIN gl_code_combination_d cc ON (cc.code_combination_id = temp.code_combination_id "
                + "AND cc.company_id = " + bean.getCompanyId() + ") "
                + "WHERE template_name = '" + bean.getTemplateName() + "' "
                + "ORDER BY line_no ";

        List<BillTemplateRow> revenueList = new ArrayList<>();
        List<BillTemplateRow> taxList = new ArrayList<>();

        return jdbcTemplate.query(query,
                new ResultSetExtractor<BillTemplateBean>() {
                    @Override
                    public BillTemplateBean extractData(ResultSet rs) throws SQLException, DataAccessException {
                        while (rs.next()) {
                            bean.setTemplateId(rs.getInt("template_id"));
                            bean.setTemplateName(rs.getString("template_name"));
                            bean.setRevExp(rs.getString("rev_exp"));
                            bean.setSeaAir(rs.getString("sea_air"));
                            bean.setExpImp(rs.getString("exp_imp"));
                            bean.setLocalForeign(rs.getString("local_foreign"));
                            bean.setBillType(rs.getString("inv_dn_cn_misc"));
                            bean.setTemplateType(rs.getString("tax_type"));

                            String rev_exp_tax_disc_oc = rs.getString("rev_exp_tax_disc_oc");
                            if ("R".equalsIgnoreCase(rev_exp_tax_disc_oc)) {
                                BillTemplateRow row = new BillTemplateRow();
                                row.setAccountcode(rs.getString("code_combination_id"));
                                row.setDes(rs.getString("code_desc"));
                                row.setSac(rs.getString("sac_code"));
                                row.setAccountname(rs.getString("acct_name"));
                                row.setTax1PerArr(rs.getDouble("tax_1_per"));
                                row.setTax2PerArr(rs.getDouble("tax_2_per"));
                                row.setTax3PerArr(rs.getDouble("tax_3_per"));
                                row.setTax4PerArr(rs.getDouble("tax_4_per"));
                                row.setTax5PerArr(rs.getDouble("tax_5_per"));
                                row.setChkBox6(rs.getString("non_taxable"));
                                revenueList.add(row);
                            } else if ("T".equalsIgnoreCase(rev_exp_tax_disc_oc)) {
                                BillTemplateRow row = new BillTemplateRow();
                                row.setTaxAcctCodeArr(rs.getInt("code_combination_id"));
                                row.setTaxAcctDescArr(rs.getString("code_desc"));
                                row.setTaxAcctNameArr(rs.getString("acct_name"));
                                row.setTaxPercentageArr(rs.getDouble("per_on_tot_val"));
                                row.setTaxOnValueArr(rs.getDouble("per_on_val"));
                                row.setTaxDrCrArr(rs.getString("dr_cr"));
                                taxList.add(row);

                            } else if ("D".equalsIgnoreCase(rev_exp_tax_disc_oc)) {
                                bean.setDiscountAcctCode(rs.getInt("code_combination_id"));
                                bean.setDiscountDesc(rs.getString("code_desc"));
                                bean.setDiscountAcctName(rs.getString("acct_name"));
                                bean.setDiscountPercentage(rs.getDouble("discount_per"));
                                bean.setDiscountOnValue(rs.getDouble("discount_amt"));
                                //discList.add(bean);

                            } else {
                                bean.setOtherChargesAcctCode(rs.getInt("code_combination_id"));
                                bean.setOtherChargesDesc(rs.getString("code_desc"));
                                bean.setOtherChargesAcctName(rs.getString("acct_name"));
                                bean.setOtherChargesPercentage(rs.getDouble("oth_chrgs_per"));
                                bean.setOtherChargesOnValue(rs.getDouble("oth_chrgs_amt"));
                                //otherChargesList.add(bean);
                            }
                        }
                        bean.setBillTemplateRows(revenueList);
                        bean.setBillTemplateTaxs(taxList);
                        return bean;
                    }
                });
    }

    public void deleteBillTemplate(BillTemplateBean btb){
        String deleteQuery = "DELETE FROM template_d WHERE template_id = ? ";
        jdbcTemplate.update(deleteQuery, btb.getTemplateId());
    }

    public void saveBillTemplate(BillTemplateBean btb, int autoNumber ){
        String insertQuery = "INSERT INTO template_d (template_id,template_line_id,"
                + "line_no,rev_exp,sea_air,exp_imp,local_foreign,inv_dn_cn_misc,rev_exp_tax_disc_oc,"
                + "code_combination_id,code_desc,tax_1_per,tax_2_per,tax_3_per,tax_4_per,tax_5_per,non_taxable,"
                + "per_on_tot_val,per_on_val,discount_per,discount_amt,oth_chrgs_per,oth_chrgs_amt,status,"
                + "created_by,creation_date,template_name,dr_cr,sac_code,tax_type) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE(),?,?,?,?)";

        int revLineNo = 1;
        int commonLine = 1;
        List<Object[]> batchRows = new ArrayList<>();
        for (BillTemplateRow row : btb.getBillTemplateRows()) {
            if (row.getDes() == null || row.getDes().equals("")) {
                continue;
            }
            Object[] objArr = {autoNumber, commonLine++, revLineNo++, btb.getRevExp(), btb.getSeaAir(), btb.getExpImp(),btb.getLocalForeign(),
            btb.getBillType(), "R", row.getAccountcode(), row.getDes(), row.getTax1PerArr(), row.getTax2PerArr(), row.getTax3PerArr(),
                    row.getTax4PerArr(), row.getTax5PerArr(), StringUtils.hasText(row.getChkBox6())? row.getChkBox6() : "0", 0.00,
                    0.00,0.00,0.00,0.00,0.00, "A", btb.getUserId(), btb.getTemplateName(), "DR", row.getSac(), btb.getTemplateType()};
            batchRows.add(objArr);
        }
        // TAX HEAD
        int taxLineNo = 1;
        for (BillTemplateRow row : btb.getBillTemplateTaxs()) {

            Object[] objArr = {autoNumber, commonLine++, taxLineNo++, btb.getRevExp(), btb.getSeaAir(), btb.getExpImp(),btb.getLocalForeign(),
                    btb.getBillType(), "T", row.getTaxAcctCodeArr(), row.getTaxAcctDescArr(), "0","0","0","0","0","0", row.getTaxPercentageArr(),
                    row.getTaxOnValueArr(),0.00,0.00,0.00,0.00, "A", btb.getUserId(), btb.getTemplateName(), row.getTaxDrCrArr(), null, btb.getTemplateType()};
            batchRows.add(objArr);
        }
        // DISCOUNT
        if (btb.getDiscountDesc() == null || btb.getDiscountDesc().equals("")) {
        } else {
            Object[] objArr = {autoNumber, commonLine++, revLineNo++, btb.getRevExp(), btb.getSeaAir(), btb.getExpImp(),btb.getLocalForeign(),
                    btb.getBillType(), "D", btb.getDiscountAcctCode(), btb.getDiscountDesc(), "0","0","0","0","0","0",0.00,0.00,
                    btb.getDiscountPercentage(), btb.getDiscountOnValue(),0.00,0.00, "A", btb.getUserId(), btb.getTemplateName(),
                    "DR", null, btb.getTemplateType()};
            batchRows.add(objArr);

        }
        // OTHER CHARGES
        if (btb.getOtherChargesDesc() == null || btb.getOtherChargesDesc().equals("")) {
        } else {
            Object[] objArr = {autoNumber, commonLine++, revLineNo++, btb.getRevExp(), btb.getSeaAir(), btb.getExpImp(),btb.getLocalForeign(),
                    btb.getBillType(), "O", btb.getOtherChargesAcctCode(), btb.getOtherChargesDesc(), "0","0","0","0","0","0",0.00,0.00,0.00,0.00,
                    btb.getOtherChargesPercentage(), btb.getOtherChargesOnValue(), "A", btb.getUserId(), btb.getTemplateName(),
                    "DR", null, btb.getTemplateType()};
            batchRows.add(objArr);
        }

        jdbcTemplate.batchUpdate(insertQuery, batchRows);
    }

    public void fetchBillTemplateByParams(InvoiceBean bean) {
        String query = "SELECT template_name,rev_exp,sea_air,exp_imp,local_foreign,inv_dn_cn_misc,tax_type,"
                + "temp.template_id,temp.code_combination_id,temp.code_desc,temp.taxable_1,"
                + "temp.taxable_2,temp.taxable_3,temp.taxable_4,temp.taxable_5,temp.non_taxable,"
                + "temp.per_on_tot_val,temp.per_on_val,temp.discount_per,temp.discount_amt,"
                + "temp.oth_chrgs_per,temp.oth_chrgs_amt,temp.status,cc.acct_name,temp.template_id,dr_cr,"
                + "temp.tax_1_per,temp.tax_2_per,temp.tax_3_per,temp.tax_4_per,temp.tax_5_per,"
                + "temp.sac_code,rev_exp_tax_disc_oc "
                + "FROM template_d temp "
                + "LEFT OUTER JOIN gl_code_combination_d cc ON (cc.code_combination_id = temp.code_combination_id "
                + "AND cc.company_id = " + bean.getCompanyId() + ") "
                + "WHERE sea_air = '"+bean.getSeaAir()+"' AND exp_imp = '"+bean.getExpImp()+"' "
                + "AND local_foreign = '"+bean.getLocalForeign()+"' AND inv_dn_cn_misc='"+bean.getBillType()+"' "
                + "AND tax_type = '"+bean.getTemplateType()+"' "
                + "ORDER BY line_no ";

        List<BillRow> revenueList = new ArrayList<>();
        List<BillRow> taxList = new ArrayList<>();

        jdbcTemplate.query(query,
                new ResultSetExtractor<InvoiceBean>() {
                    @Override
                    public InvoiceBean extractData(ResultSet rs) throws SQLException, DataAccessException {
                        while (rs.next()) {
                            bean.setTemplateId(rs.getInt("template_id"));
                            bean.setTemplateName(rs.getString("template_name"));
                            bean.setRevExp(rs.getString("rev_exp"));
                            bean.setSeaAir(rs.getString("sea_air"));
                            bean.setExpImp(rs.getString("exp_imp"));
                            bean.setLocalForeign(rs.getString("local_foreign"));
                            bean.setBillType(rs.getString("inv_dn_cn_misc"));
                            bean.setTemplateType(rs.getString("tax_type"));

                            String rev_exp_tax_disc_oc = rs.getString("rev_exp_tax_disc_oc");
                            if ("R".equalsIgnoreCase(rev_exp_tax_disc_oc)) {
                                BillRow row = new BillRow();
                                row.setCodeCombinationId(rs.getInt("code_combination_id"));
                                row.setCodeDesc(rs.getString("code_desc"));
                                row.setSacCode(rs.getString("sac_code"));
                                row.setAcctName(rs.getString("acct_name"));
                                row.setTax1Per(rs.getDouble("tax_1_per"));
                                row.setTax2Per(rs.getDouble("tax_2_per"));
                                row.setTax3Per(rs.getDouble("tax_3_per"));
                                row.setTax4Per(rs.getDouble("tax_4_per"));
                                row.setTax5Per(rs.getDouble("tax_5_per"));
                                row.setChkBox6(rs.getString("non_taxable"));
                                revenueList.add(row);
                            } else if ("T".equalsIgnoreCase(rev_exp_tax_disc_oc)) {
                                BillRow row = new BillRow();
                                row.setCodeCombinationId(rs.getInt("code_combination_id"));
                                row.setCodeDesc(rs.getString("code_desc"));
                                row.setAcctName(rs.getString("acct_name"));
                                row.setTaxPercentageArr(rs.getDouble("per_on_tot_val"));
                                row.setTaxOnValueArr(rs.getDouble("per_on_val"));
                                row.setTaxDrCrArr(rs.getString("dr_cr"));
                                taxList.add(row);

                            } else if ("D".equalsIgnoreCase(rev_exp_tax_disc_oc)) {
                                bean.setDiscountAcctCode(rs.getInt("code_combination_id"));
                                bean.setDiscountDesc(rs.getString("code_desc"));
                                bean.setDiscountAcctName(rs.getString("acct_name"));
                                bean.setDiscountPercentage(rs.getDouble("discount_per"));
                                bean.setDiscountOnValue(rs.getDouble("discount_amt"));
                                //discList.add(bean);

                            } else {
                                bean.setOtherChargesAcctCode(rs.getInt("code_combination_id"));
                                bean.setOtherChargesDesc(rs.getString("code_desc"));
                                bean.setOtherChargesAcctName(rs.getString("acct_name"));
                                bean.setOtherChargesPercentage(rs.getDouble("oth_chrgs_per"));
                                bean.setOtherChargesOnValue(rs.getDouble("oth_chrgs_amt"));
                                //otherChargesList.add(bean);
                            }
                        }
                        bean.setBillTemplateRows(revenueList);
                        bean.setBillTemplateTaxs(taxList);
                        return bean;
                    }
                });
    }
}
