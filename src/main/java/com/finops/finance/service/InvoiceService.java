package com.finops.finance.service;

import com.finact.Inv;
import com.finops.admin.dao.AdminDAO;
import com.finops.aop.MeasureTime;
import com.finops.finance.bean.BillRow;
import com.finops.finance.bean.InvoiceBean;
import com.finops.finance.bean.VoucherBean;
import com.finops.finance.dao.InvoiceDAO;
import com.finops.finance.dao.VoucherDAO;
import com.finops.freight.bean.BLBean;
import com.finops.freight.service.BLService;
import com.finops.freight.service.HawbService;
import com.finops.partner.model.PartnerBean;
import com.finops.partner.service.PartnerService;
import com.finops.report.model.ReportBean;
import com.finops.util.ApplicationUtil;
import com.finops.util.Constants;
import com.finops.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceDAO invoiceDAO;

    @Autowired
    private VoucherDAO voucherDAO;

    @Autowired
    private AdminDAO adminDAO;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private BLService blService;

    @Autowired
    private HawbService hawbService;

    private static final String LINE_QUERY = "INSERT INTO ar_customer_trx_lines_f "
            + "(customer_trx_line_id,customer_trx_id,line_no,rev_exp,sea_air,exp_imp,"
            + "local_foreign,inv_dn_cn_misc,rev_exp_tax_disc_oc,"
            + "code_combination_id,code_desc,taxable_1,taxable_2,taxable_3,taxable_4,taxable_5,non_taxable,"
            + "per_on_tot_val,per_on_val,discount_per,discount_amt,oth_chrgs_per,oth_chrgs_amt,status,"
            + "created_by,creation_date,template_line_id,trx_number,"
            + "entered_amount,accounted_amount,remark,template_id,dr_cr,"
            + "TAX_1_PER,TAX_2_PER,TAX_3_PER,TAX_4_PER,TAX_5_PER,SAC_CODE,"
            + "TAX_1_VAL,TAX_2_VAL,TAX_3_VAL,TAX_4_VAL,TAX_5_VAL,INV_NO,INV_DATE,SO_NO, BL_NO,TRX_DATE,LOADING_AGNT) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE(),?,?,?,?,?,?,?,"
            + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    @MeasureTime
    public List<InvoiceBean> findInvoicesByPage(InvoiceBean bean){
        return invoiceDAO.findAllByPage(bean);
    }

    @MeasureTime
    public InvoiceBean findInvoiceById(InvoiceBean bean, boolean skipEmpty){
        List<BillRow> invoiceBeans = invoiceDAO.findAllById(bean);
        BillRow sourceBean = invoiceBeans.get(0);
        BeanUtils.copyProperties(sourceBean, bean);
        bean.setBillId(bean.getBillId());
        bean.setPreparedBy(sourceBean.getCreatedBy());
        bean.setBlNo(sourceBean.getHeaderBlNO());

        if(StringUtils.hasText(bean.getBillTo())) {
            PartnerBean pb = partnerService.getPartnerData(bean.getBillTo(),
                    bean.getLoadingAgent(), partnerService);
            if("REVENUE".equalsIgnoreCase(bean.getRevExp())) {
                bean.setPartyGstin(pb.getGstinNo());
                bean.setStateCode(pb.getStateCode());
            }
            PartnerBean shipper = partnerService.getPartnerData(bean.getShpr(),
                    bean.getLoadingAgent(), partnerService);
            PartnerBean cnee = partnerService.getPartnerData(bean.getCnee(),
                    bean.getLoadingAgent(), partnerService);

            bean.setShprName(shipper.getDescription1());
            bean.setCneeName(cnee.getDescription1());

        }

        for(BillRow br : invoiceBeans){
            if("R".equals(br.getCategory())){
                if(br.getAmount() == 0 && skipEmpty) {

                }
                else{
                    bean.getBillTemplateRows().add(br);
                }
            }
            else if("T".equals(br.getCategory())){
                bean.getBillTemplateTaxs().add(br);
            }
            else if("S".equals(br.getCategory())){
                bean.getBillTemplateTDS().add(br);
            }
            else if("X".equals(br.getCategory())){
                bean.setPartyTDSAcctCode(br.getCodeCombinationId());
                bean.setPartyTDSAcctName(br.getAcctName());
                bean.setPartyTDSDesc(br.getCodeDesc());
                bean.setPartyTDSValue(br.getAmount());
            }
        }
        return bean;
    }

    @MeasureTime
    @Transactional
    public void save(InvoiceBean bean) {

        if (bean.isGenerateInvoice()) {
            voucherDAO.deleteBySourceHdrId(bean.getBillId(), bean.getAcctYear());
            invoiceDAO.delete(bean);
            bean.setBillId(0);
            bean.setBillDate(DateUtil.getSystemDate());
            bean.setBillNo("AUTO");
            bean.setProforma(false);
        }
        int billId = bean.getBillId();
        if(billId > 0) {
            voucherDAO.deleteBySourceHdrId(bean.getBillId(), bean.getAcctYear());
            invoiceDAO.delete(bean);
        }
        else{
            int customerTrxId = invoiceDAO.generateAutoNumber("SELECT MAX(customer_trx_id)+1 COUNT FROM ar_customer_trx_f", 1001);
            bean.setBillId(customerTrxId);
            if ("AUTO".equals(bean.getBillNo())) {
                String serialNoColumn = ApplicationUtil.getBranchSrNoField(bean.getLoadingAgent());
                String type = getBillType(bean.getBillType(), bean.isProforma());
                String query = "SELECT "+serialNoColumn+" FROM gl_doc_serial_number_d WHERE doc_short_name = '" + type + "' and acct_year = '"+bean.getAcctYear()+"'";

                int srNo = invoiceDAO.generateAutoNumber(query, 1001);
                adminDAO.updateDocSerialNo(type, bean.getAcctYear(), serialNoColumn);

                // Generate BillNo
                String billNo = generateBillNO(bean, srNo);
                bean.setBillNo(billNo);
            }
        }

        setPartyTotal(bean);
        invoiceDAO.createHeader(bean);

        List<Object[]> batchArgsList = new ArrayList<>();
        List<Object[]> batchArgsList2 = new ArrayList<>();
        if(!bean.isProforma()) {
            invoiceDAO.createJEHeader(bean, batchArgsList2);
        }

        invoiceDAO.createHeads(bean, batchArgsList, batchArgsList2, bean.getBillTemplateRows(),"R");
        invoiceDAO.createHeads(bean, batchArgsList, batchArgsList2, bean.getBillTemplateTaxs(),"T");
        if("EXPENSE".equalsIgnoreCase(bean.getRevExp())) {
            invoiceDAO.createHeads(bean, batchArgsList, batchArgsList2, bean.getBillTemplateTDS(), "S");
            invoiceDAO.createTDSParty(bean, batchArgsList, batchArgsList2, "X");
        }
        invoiceDAO.insertHeads(bean, batchArgsList, LINE_QUERY);
        if(!bean.isProforma()) {
            invoiceDAO.insertHeads(bean, batchArgsList2, Constants.JE_INSERT);
        }

    }

    private String getBillType(String type, boolean isProforma) {
        if(Constants.MISC.equals(type)){
            type = Constants.INVOICE;
        }
        if(Constants.MISCEXP.equals(type)){
            type = Constants.EXPENSE;
        }
        if(isProforma){
            type = Constants.PROFORMA;
        }
        return  type;
    }

    private String generateBillNO(InvoiceBean bean, int srno){
        String acctYearString = String.valueOf(bean.getAcctYear());
        String foundation =  bean.getLoadingAgent() + getBillTypeChar(bean.getBillType(), bean.isProforma());
        if(Constants.BILL_OF_SUPPLY.equals(bean.getBillType())){
            return foundation + DateUtil.getSystemDateMMyy()+String.format("%05d", srno);
        }
        else{
            return foundation + acctYearString.substring(2, 4) + acctYearString.substring(6) + srno;
        }
    }

    private String getBillTypeChar(String btype, boolean isProforma) {
        if (isProforma) {
            return "P";
        }

        if ("INVOICE".equalsIgnoreCase(btype) || "MISC".equalsIgnoreCase(btype)) {
            return "B";
        } else if ("SELF".equalsIgnoreCase(btype)) {
            return "F";
        } else if ("EXPENSE".equalsIgnoreCase(btype) || "MISCEXP".equalsIgnoreCase(btype)) {
            return "E";
        } else if ("BOS".equalsIgnoreCase(btype)){
            return "S";
        }
        else {
            return btype.substring(0, 1);
        }
    }

    private void setPartyTotal(InvoiceBean bean) {
        String voucherType = bean.getBillType();
        double nonPartyCR = 0, nonPartyDR = 0, accountedCR, accountedDR;
        boolean condition = Constants.INVOICE.equals(voucherType) || Constants.MISC.equals(voucherType)
                || Constants.DEBITNOTE.equals(voucherType) || Constants.BILL_OF_SUPPLY.equals(voucherType);

        for (BillRow row : bean.getBillTemplateRows()) {
            if (row.getAcctName().isEmpty()) {
                continue;
            }
            row.setCashBank(bean.getPartyAcctCode());
            if (condition) {
                accountedCR = Math.round(row.getAmount() * bean.getExchangeRate());
                row.setAccountedCR(accountedCR);
                row.setAmountCR(row.getAmount());
                nonPartyCR += accountedCR;
            }
            else{
                accountedDR = row.getAmount();
                if (!"INR".equals(bean.getCurrencyCode())) {
                    accountedDR = Math.round(row.getAmount() * bean.getExchangeRate());
                }

                row.setAccountedDR(accountedDR);
                row.setAmountDR(row.getAmount());
                nonPartyDR += accountedDR;
            }
        }

        for (BillRow row : bean.getBillTemplateTaxs()) {
            if (!StringUtils.hasText(row.getAcctName())) {
                continue;
            }
            row.setCashBank(bean.getPartyAcctCode());
            if ("CR".equals(row.getTaxDrCrArr())) {
                accountedCR = Math.round(row.getAmount() * bean.getExchangeRate());
                row.setAccountedCR(accountedCR);
                row.setAmountCR(row.getAmount());
                nonPartyCR += accountedCR;
            }
            else{
                accountedDR = row.getAmount();
                if (!"INR".equals(bean.getCurrencyCode())) {
                    accountedDR = Math.round(row.getAmount() * bean.getExchangeRate());
                }
                row.setAccountedDR(accountedDR);
                row.setAmountDR(row.getAmount());
                nonPartyDR += accountedDR;
            }
        }

        for (BillRow row : bean.getBillTemplateTDS()) {
            if (!StringUtils.hasText(row.getAcctName())) {
                continue;
            }
            row.setCashBank(bean.getPartyAcctCode());
            if ("CR".equals(row.getTaxDrCrArr())) {
                accountedCR = Math.round(row.getAmount() * bean.getExchangeRate());
                row.setAccountedCR(accountedCR);
                row.setAmountCR(row.getAmount());

            }
            else{
                accountedDR = Math.round(row.getAmount() * bean.getExchangeRate());
                row.setAccountedDR(accountedDR);
                row.setAmountDR(row.getAmount());

            }
        }

        double grandTotal = nonPartyDR - nonPartyCR;

        if (condition) {
            bean.setAccountedDR(grandTotal*(-1));
            bean.setAmountDR(bean.getTrxTotal());
        }
        else{
            bean.setAccountedCR(grandTotal);
            bean.setAmountCR(bean.getTrxTotal());
        }
        bean.setCashBank(bean.getBillTemplateRows().get(0).getCodeCombinationId());
        bean.setCodeCombinationId(bean.getPartyAcctCode());
    }

    public JdbcTemplate getJDBCTemplate() {
        return voucherDAO.getJDBCTemplate();
    }

    public List<ReportBean> pendingBL(ReportBean vo) {
        return invoiceDAO.pendingBL(vo);
    }

    public boolean isDuplicateInvNo(InvoiceBean billingBean) {
        return invoiceDAO.isDuplicateInvNo(billingBean);
    }

    public BLBean getBlData(InvoiceBean bean) {
        BLBean blVo = null;
        if ("SEA".equals(bean.getSeaAir())) {
            blVo = blService.getBlForBilling(bean);
//            if ("EXPORT".equals(bean.getExpImp())) {
//
//            } else if ("IMPORT".equals(bean.getExpImp())) {
//                //blVo = new ImpBLService().getBlForBilling(bean.getBlNo(), bean.getLoadingAgent());
//            }
        } else if ("AIR".equals(bean.getSeaAir())) {
            blVo = hawbService.getBlForBilling(bean);
//            if ("EXPORT".equals(bean.getExpImp())) {
//                //blVo = new HawbService().getBlForBilling(bean.getBlNo(), bean.getLoadingAgent());
//            } else if ("IMPORT".equals(bean.getExpImp())) {
//                //blVo = new ImpHawbService().getBlForBilling(bean.getBlNo(), bean.getLoadingAgent());
//            }
        }
        return blVo;
    }

    @Transactional
    public void deleteInvoice(InvoiceBean invoiceBean){
        for(String record : invoiceBean.getTrxIds()){
            VoucherBean vb = voucherDAO.findByJeSourceHdrId(invoiceBean, record);
            int jeHdrId = vb.getJeHdrId();
            invoiceDAO.deleteInvoice(invoiceBean, record, jeHdrId);
        }
    }
}
