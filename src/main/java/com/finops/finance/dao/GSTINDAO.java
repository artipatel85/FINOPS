package com.finops.finance.dao;

import com.finact.gstin.credit.*;
import com.finops.dao.AbstractDAO;
import com.finops.report.model.ReportBean;
import com.finops.util.DateUtil;
import com.google.gson.Gson;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.List;

@Component
public class GSTINDAO extends AbstractDAO {

    public List<Data> listOfPeriods(String branch) {
        String query = "SELECT distinct period rtnprd,branch_gstin gstin,branch version FROM gstin_credit_details WHERE branch=?";

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(Data.class),
                branch);
    }

    public int saveCreditDetails(String branch, String file) {
        Gson gson = new Gson();
        GstinCredit gc = null;
        try {
            gc = gson.fromJson(new FileReader(file),
                    GstinCredit.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String query = "INSERT INTO gstin_credit_details (id, period, branch_gstin, version, checksum, doctype, trade_name, branch, invoice_number, invoice_date, invoice_value, invoice_type, "
                + "igst, cgst, sgst, total_tax_value, place_of_supply, itc_available, irn, irn_date, supplier_gstin, gstin_filling_period, gstin_filling_date) "
                + "VALUES "
                + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

        Data data = gc.getData();
        Docdata docdata = data.getDocdata();

        for (B2b b2b : docdata.getB2b()) {
            for (Inv inv : b2b.getInv()) {
                Object[] args = {generateAutoNumber("SELECT MAX(id)+1 FROM gstin_credit_details", 1),
                        data.getRtnprd(), data.getGstin(), data.getVersion(), gc.getChksum(), "B2B",
                        b2b.getTrdnm(), branch, inv.getInum(), DateUtil.convertDateFormat(inv.getDt()), inv.getVal(), inv.getTyp(),
                        default0(inv.getIgst()), default0(inv.getCgst()), default0(inv.getSgst()),
                        inv.getTxval(), inv.getPos(), inv.getItcavl(), inv.getIrn(), DateUtil.convertDateFormat(inv.getIrngendate()),
                        b2b.getCtin(), b2b.getSupprd(), DateUtil.convertDateFormat(b2b.getSupfildt())};

                if (inv.getIrn() != null) {
                    jdbcTemplate.update(query, args);
                }
            }
        }

        if (docdata.getB2ba() != null) {
            for (B2ba b2b : docdata.getB2ba()) {
                for (Inv inv : b2b.getInv()) {
                    Object[] args = {generateAutoNumber("SELECT MAX(id)+1 FROM gstin_credit_details", 1),
                            data.getRtnprd(), data.getGstin(), data.getVersion(), gc.getChksum(), "B2BA",
                            b2b.getTrdnm(), branch, inv.getInum(), DateUtil.convertDateFormat(inv.getDt()), inv.getVal(), inv.getTyp(),
                            default0(inv.getIgst()), default0(inv.getCgst()), default0(inv.getSgst()),
                            inv.getTxval(), inv.getPos(), inv.getItcavl(), inv.getIrn(), DateUtil.convertDateFormat(inv.getIrngendate()),
                            b2b.getCtin(), b2b.getSupprd(), DateUtil.convertDateFormat(b2b.getSupfildt())};

                    if (inv.getIrn() != null) {
                        jdbcTemplate.update(query, args);
                    }
                }
            }
        }

        if (docdata.getCdnr() != null) {
            for (Cdnr b2b : docdata.getCdnr()) {
                for (Nt inv : b2b.getNt()) {
                    Object[] args = {generateAutoNumber("SELECT MAX(id)+1 FROM gstin_credit_details", 1),
                            data.getRtnprd(), data.getGstin(), data.getVersion(), gc.getChksum(), "CDNR",
                            b2b.getTrdnm(), branch, inv.getNtnum(), DateUtil.convertDateFormat(inv.getDt()), inv.getVal(), inv.getTyp(),
                            default0(inv.getIgst()), default0(inv.getCgst()), default0(inv.getSgst()),
                            inv.getTxval(), inv.getPos(), inv.getItcavl(), inv.getIrn(), DateUtil.convertDateFormat(inv.getIrngendate()),
                            b2b.getCtin(), b2b.getSupprd(), DateUtil.convertDateFormat(b2b.getSupfildt())};

                    if (inv.getIrn() != null) {
                        jdbcTemplate.update(query, args);
                    }
                }
            }

        }

        return 0;
    }

    public List<ReportBean> retrieveMappedRecords(String gstin, ReportBean reportBean) {
        String startDate = reportBean.getParam4();
        String endDate = reportBean.getParam5();
        String branch = reportBean.getLoadingAgent();
        String type = reportBean.getParam6();

        String matchedQuery = "SELECT customer_trx_id,trx_number, CONVERT(trx_date,DATE) trx_date, inv_sb_no, CONVERT(inv_date,DATE) inv_date,  " +
                "                total_taxable, total_tax, '1-MATCHED' category, billto_name party, gstin_gstin_no gstinno," +
                "                gstin_igst,gstin_cgst,gstin_sgst  " +
                "                FROM ar_customer_trx_f trx   " +
                "                WHERE trx_date BETWEEN '" + startDate + "' AND '" + endDate + "' AND gstin_period IS NOT NULL" +
                "                AND LOADING_AGNT = '" + branch + "' AND rev_exp = 'EXPENSE' ";

        String gstinQuery = "SELECT 0,'','',invoice_number, invoice_date, SUM(total_tax_value),(SUM(igst)+SUM(cgst)+SUM(sgst)) total_tax,  " +
                "                '2-GSTIN' category, trade_name party, supplier_gstin gstinno," +
                "                SUM(igst) IGST, SUM(cgst) CGST, SUM(sgst) SGST  " +
                "                FROM gstin_credit_details WHERE invoice_number NOT IN (SELECT gstin_inv_no FROM ar_customer_trx_f where GSTIN_INV_NO is not null)  " +
                "                AND invoice_date BETWEEN '" + startDate + "' AND '" + endDate + "' AND branch = '" + branch + "'  " +
                "                GROUP BY invoice_number,invoice_date, trade_name, supplier_gstin ";

        String shikharQuery = "SELECT customer_trx_id,trx_number, CONVERT(trx_date,DATE) trx_date, inv_sb_no, CONVERT(inv_date,DATE) inv_date, total_taxable, total_tax, " +
                "                '3-SHIKHAR' category,billto_name party, gstin_gstin_no gstinno, 0 igst,0 cgst,0 sgst " +
                "                FROM ar_customer_trx_f  " +
                "                WHERE inv_sb_no NOT IN (SELECT invoice_number FROM gstin_credit_details WHERE invoice_number IS NOT NULL)  " +
                "                AND trx_date BETWEEN '" + startDate + "' AND '" + endDate + "' AND inv_sb_no IS NOT NULL AND inv_sb_no <> ''  " +
                "                AND LOADING_AGNT = '" + branch + "' AND rev_exp = 'EXPENSE' AND local_foreign='LOCAL' AND  INV_DN_CN_MISC <> 'CREDITNOTE' " +
                "                AND TOTAL_TAXABLE <> 0 ";

        StringBuilder query = new StringBuilder();
        if (type == null || type.equals("")) {
            query.append(matchedQuery).append(" UNION ").append(gstinQuery).append(" UNION ").append(shikharQuery);
        } else if (type.equalsIgnoreCase("1")) {
            query.append(matchedQuery);
        } else if (type.equalsIgnoreCase("2")) {
            query.append(gstinQuery);
        } else if (type.equalsIgnoreCase("3")) {
            query.append(shikharQuery);
        }
        query.append(" ORDER BY category LIMIT " + reportBean.getStart() + ", 1000");

        String[] fields = {"TRX_NUMBER", "TRX_DATE", "INV_SB_NO", "INV_DATE", "TOTAL_TAXABLE", "TOTAL_TAX",
                "CATEGORY", "PARTY", "GSTINNO"};

        List<ReportBean> reportBeanList = jdbcTemplate.query(query.toString(),
                (rs, rowNum) -> {
                    ReportBean rb = new ReportBean();
                    String category = rs.getString("category");
                    double difference = rs.getDouble("total_tax") -
                            (rs.getDouble("gstin_igst") + rs.getDouble("gstin_cgst") + rs.getDouble("gstin_sgst"));
                    if ("1-MATCHED".equalsIgnoreCase(category) && difference != 0) {
                        category = "0-VERIFY";
                    }
                    rb.setParam1(rs.getString("trx_number"));
                    rb.setParam2(rs.getString("trx_date"));
                    rb.setParam3(rs.getString("inv_sb_no"));
                    rb.setParam4(rs.getString("inv_date"));
                    rb.setParam5(rs.getString("total_taxable"));
                    rb.setParam6(rs.getString("total_tax"));
                    rb.setParam7(category);
                    rb.setParam8(rs.getString("party"));
                    rb.setParam9(rs.getString("gstinno"));
                    rb.setParam11(rs.getString("customer_trx_id"));
                    rb.setDoubleParam1(rs.getDouble("gstin_igst"));
                    rb.setDoubleParam2(rs.getDouble("gstin_cgst"));
                    rb.setDoubleParam3(rs.getDouble("gstin_sgst"));
                    return rb;
                });
        return reportBeanList;
    }

    public void match(String branch, String period, String gstin) {

        String updateQuery = "UPDATE ar_customer_trx_f TRX,prt_contact_details_d PA,(SELECT period,invoice_number,\n" +
                "invoice_date,supplier_gstin,SUM(total_tax_value) total_taxable,(SUM(igst)+SUM(cgst)+SUM(sgst)) total_tax,SUM(igst) IGST, SUM(cgst) CGST, SUM(sgst) SGST\n" +
                " FROM gstin_credit_details WHERE period = ? AND branch_gstin = ? GROUP BY period,invoice_number,invoice_date,supplier_gstin) GSTIN \n" +
                "SET TRX.gstin_period = gstin.period , gstin_inv_no = invoice_number, gstin_inv_date=invoice_date,gstin_total_taxable = gstin.total_taxable,gstin_total_tax = gstin.total_tax, \n" +
                "gstin_gstin_no = supplier_gstin,gstin_igst=igst, gstin_cgst=cgst, gstin_sgst=sgst  " +
                "WHERE TRX.INV_SB_NO = GSTIN.invoice_number " +
                "AND pa.CODE_COMBINATION_ID=trx.PARTY_ACCT_CODE " +
                "AND loading_agnt = ? " +
                "AND   TRX.INV_DATE = GSTIN.invoice_date " +
                "AND (TRX.TOTAL_TAX - GSTIN.total_tax) > -1 AND (TRX.TOTAL_TAX - GSTIN.total_tax) < 1 " +
                "AND local_foreign='LOCAL' AND  INV_DN_CN_MISC <> 'CREDITNOTE' AND TRX.TOTAL_TAXABLE <> 0 ";

        jdbcTemplate.update(updateQuery,
                period, gstin, branch);
    }
}
