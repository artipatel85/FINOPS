package com.finops.util;




public class Constants {


    //Logging constants
    public static final String ID = "id";
    public static final String MODULE = "module";
    public static final String USERID = "userId";
    public static final String NUMBER = "number";
    public static final String INVOICE = "INVOICE";
    public static final String MISC = "MISC";
    public static final String DEBITNOTE = "DEBITNOTE";
    public static final String BILL_OF_SUPPLY = "BOS";

    public static final String JE_INSERT = "INSERT INTO gl_je_f (je_hdr_id,je_line_id,je_voucher_no,je_date,hdr_line_flag,period_id,acct_year,je_source,je_source_hdr_id,je_source_line_id,code_combination_id,currency_id,exchange_rate," +
            "entered_dr,entered_cr,accounted_dr,accounted_cr,je_line_remarks,company_id,status,created_by,creation_date,chq_no,chq_date,CASH_BANK,reference1,bl_no,attribute6,attribute7,attribute2,je_seq,so_number," +
            "inv_no,sea_air,exp_imp,sac_code,inv_date) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
            "?,?,?,SYSDATE(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

    public static final String PARTNER_KYC_MODULE = "partnerKYC";
    public static final String PROFORMA = "PROFORMA";
    public static final String REVENUE = "REVENUE";
    public static final String EXPENSE = "EXPENSE";
    public static final String LOCAL = "LOCAL";
    public static final String YES = "Y";
    public static final String SEZ = "SEZ";
    public static final String NO = "N";
    public static final String EXEMPTED = "EXEMPTED";
    public static final String SGST = "SGST";
    public static final String IGST = "IGST";
    public static final String EXPORT = "EXPORT";
    public static final String MISCEXP = "MISCEXP";
    public static final String CONTRA = "CONTRA";
}
