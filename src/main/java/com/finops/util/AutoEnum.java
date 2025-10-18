
package com.finops.util;

public enum AutoEnum {
    SALESMAN_0("SELECT NAME param1,CODE param2 FROM salesman_f"),
    SALESMAN_1("SELECT NAME param1,CODE param2 FROM salesman_f WHERE name like ? LIMIT 0,10 "),
    DEBTORCREDITOR_0("SELECT code_combination_id param2,acct_name param1,parent_id param3 FROM gl_code_combination_d "
            + "WHERE acct_name like ? AND acct_flag='L' LIMIT 0,20 "),
    CURRENCY("SELECT CURRENCY_CODE param1,EXCHANGE_RATE param2,currency_id param3 FROM currency_d"),
    CURRENCY_0("SELECT CURRENCY_CODE param1,currency_id param2 FROM currency_d WHERE currency_code like ? limit 0,10"),
    COUNTRY_0("SELECT DESCRIPTION param1,COUNTRY_CODE param2 FROM country_d WHERE DESCRIPTION LIKE ? limit 0, 10"),
    STATE_0("SELECT STATE_NAME param1,STATE_CODE param2 FROM STATE_D WHERE STATE_NAME LIKE ? LIMIT 0, 10"),
    CASHBANK_0("SELECT code_combination_id param2,acct_name param1 FROM gl_code_combination_d "
            + "WHERE parent_id IN (18,20) AND acct_name LIKE ? "),
    CURBALANCE_0("SELECT DBAL._31_3 cur_bal,CC.acct_name FROM gl_day_balances_f DBAL "
                + "LEFT OUTER JOIN gl_code_combination_d CC ON (DBAL.code_combination_id = CC.code_combination_id) "
                + "WHERE DBAL.acct_year = ? AND DBAL.code_combination_id = ?"),
    ACCOUNTS_0("SELECT code_combination_id,acct_name FROM gl_code_combination_d "
            + "WHERE parent_id IN (7,9,16) "),
    ACCOUNTS_1("SELECT code_combination_id,acct_name FROM gl_code_combination_d "
            + "WHERE parent_id IN (6,8) "),
    ACCOUNTS_2("SELECT code_combination_id,acct_name FROM gl_code_combination_d "
            + "WHERE parent_id IN (25) "),
    ACCOUNTS_3("SELECT code_combination_id,acct_name FROM gl_code_combination_d "
                + "WHERE parent_id IN (6,7,8,9,16,4852,4855,31) "),
    DESCRIPTION_0("SELECT DESCRIPTION,PERCENTAGE FROM gl_tax_master_f"),
    UNDER_0("SELECT acct_name,acct_id,ACCT_TYPE_ID FROM gl_code_combination_d where acct_flag IN ('P','G')"),
    UNDER_1("SELECT acct_name param1,acct_id param2,ACCT_TYPE_ID param3 FROM gl_code_combination_d where acct_name like ? AND acct_flag IN ('P','G')"),
    DEBTORCREDITOR_1("SELECT code_combination_id,acct_name FROM gl_code_combination_d "
            + "WHERE acct_name like ?  AND acct_flag='L' LIMIT 0,20 "),
    TAXLEDGER_0("SELECT acct_name,code_combination_id FROM gl_code_combination_d where PARENT_ID=25"),
    SO_SEA_EXPORT("SELECT TOP 10 so_number,bl_no FROM so_hdr_f WHERE LOADING_AGNT=? AND so_number LIKE ? AND BKG_DATE > CAST(? AS DATETIME)- 400 "),
    SO_SEA_IMPORT("SELECT TOP 10 so_number,bl_no FROM so_hdr_f WHERE DEST_AGNT=? AND so_number LIKE ? AND BKG_DATE > CAST(? AS DATETIME)- 400 "),
    SO_AIR_EXPORT("SELECT TOP 10 si_number,hawb_no FROM si_hdr_f WHERE LOADING_AGNT=? AND si_number LIKE ? AND BKG_DATE > CAST(? AS DATETIME)- 400 "),
    SO_AIR_IMPORT("SELECT TOP 10 si_number,hawb_no FROM si_hdr_f WHERE DEST_AGNT=? AND si_number LIKE ? AND BKG_DATE > CAST(? AS DATETIME)- 400 "),
    INVOICE_BILLTO("SELECT DESCRIPTION1 param1,PARTNER_CODE param2,ADDRESS1 param3,PARTNER_ACCT_CODE param4,PARTNER_ACCOUNT_CODE param5,"
            + "STATE_CODE param6,IS_SEZ param7,GSTIN_NO param8 from partner_account_d WHERE DESCRIPTION1 LIKE ? "
            + "AND PARTNER_ACCT_CODE IS NOT NULL AND PARTNER_ACCT_CODE <> '' AND PARTNER_ACCT_CODE <> 0 AND LOADNG_AGNT = ? AND STATUS = 'A' "),
    TAXMASTER_0("SELECT txcc.acct_name param1,tx.description param2,tx.percentage param3,tx.tax_cc_id param4 FROM gl_tax_master_f tx "
                + " INNER JOIN gl_code_combination_d txcc ON (txcc.code_combination_id = tx.tax_cc_id)"),
    SO_EXPENSE("select so.SO_NUMBER param1,so.POL param2,so.POD param3,so.job_number param4,"
                + "so.BL_NO param5,SO.CARRIER_CODE param6,STR_TO_DATE(bl.etd,'%Y-%m-%d') param7 "
                + "FROM so_hdr_f so "
                + "LEFT OUTER JOIN bl_hdr_f bl ON (bl.bl_no = so.bl_no) "
                + "WHERE so.so_number = ? and (so.bl_no IS NULL OR so.bl_no LIKE ?) and so.loading_agnt LIKE ? and so.dest_agnt LIKE ? limit 0,10"),
    SI_EXPENSE("select so.SI_NUMBER param1,so.POL param2,so.POD param3,so.job_number param4," +
"                so.HAWB_NO param5,SO.CARRIER_CODE param6,STR_TO_DATE(bl.etd,'%Y-%m-%d') param7 " +
"                FROM si_hdr_f so " +
"                LEFT OUTER JOIN hawb_hdr_f bl ON (bl.HAWB_NUMBER = so.HAWB_NO) " +
"                WHERE so.si_number = ? and (so.HAWB_NO IS NULL OR so.HAWB_NO LIKE ?) and so.loading_agnt LIKE ? and so.dest_agnt LIKE ? limit 0,10"),
    EXPENSE_BILLTO("SELECT cc.acct_name param1,prt.address1 param2,cc.code_combination_id param3,prt.gstin_no param4,prt.state_code param5 "
            + "FROM gl_code_combination_d cc "
            + "LEFT OUTER JOIN gl_code_combination_d dd ON (cc.parent_id=dd.acct_id) "
            + "LEFT OUTER JOIN  prt_contact_details_d prt "
            + "ON (cc.code_combination_id = prt.code_combination_id) "
            + "WHERE cc.acct_name like ? AND cc.PARENT_ID IN (29, 22, 19)"),
    PARTNER("SELECT TOP 10 DESCRIPTION1,PARTNER_CODE,ADDRESS1,PARTNER_ACCT_CODE,PARTNER_ACCOUNT_CODE,"
            + "STATE_CODE,IS_SEZ,GSTIN_NO from partner_account_d WHERE DESCRIPTION1 LIKE ? "
            + "AND LOADNG_AGNT = ? AND STATUS = 'A' "),
    PARTNER_CODE("SELECT DESCRIPTION1 param2,PARTNER_CODE param1,CONCAT(description1,'\\n',replace(ADDRESS1,'|','\\n')) param3,PARTNER_ACCT_CODE param4,PARTNER_ACCOUNT_CODE param5,"
            + "STATE_CODE param6,IS_SEZ param7,GSTIN_NO param8 from partner_account_d WHERE PARTNER_CODE LIKE ? "
            + "AND LOADNG_AGNT = ? AND STATUS = 'A' LIMIT 0,20"),
    PARTNER_NAME("SELECT DESCRIPTION1 param1,PARTNER_CODE param2,CONCAT(description1,'\\n',replace(ADDRESS1,'|','\\n')) param3,PARTNER_ACCT_CODE param4,PARTNER_ACCOUNT_CODE param5,"
            + "STATE_CODE param6,IS_SEZ param7,GSTIN_NO param8 from partner_account_d WHERE DESCRIPTION1 LIKE ? "
            + "AND LOADNG_AGNT = ? " +
            "" +
            "AND STATUS = 'A' LIMIT 0,20"),
    CARRIER_0("SELECT TOP 50 partner_code, description1, address1 FROM partner_account_d WHERE carrier = 'Y' "
            + "AND partner_code like ? AND description1 like ? AND address1 like ? "),
    ROLE_0("SELECT role_name param1,description param2 FROM role_d WHERE role_name like ? "),
    PORT_0("SELECT port_code param1, description1 param2 FROM port_d WHERE port_code LIKE ? limit 0, 10"),
    PORT_1("SELECT port_code param2, description1 param1 FROM port_d WHERE description1 LIKE ? limit 0, 10"),
    PARTNER_ACCOUNT_0("SELECT pa.partner_code CODE,pa.description1 PARTNERNAME,"
            + "((pa.description1)+(coalesce(case when Ltrim(pa.address1) <> '' then +('|'+ltrim(pa.address1)) end,''))+c.description) ADDRESS "
            + "FROM partner_account_d pa LEFT OUTER JOIN country_d c ON (pa.country_code = c.country_code) "
            + "WHERE pa.status = 'A' AND pa.display='Y' and pa.SHPR='Y' AND pa.partner_code LIKE ? AND pa.description1 = ? ");
    
    
    private String query;
    private AutoEnum(String query) {
        this.query = query;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }   
}
