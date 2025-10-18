package com.finops.partner.dao;

import com.finops.aop.MeasureTime;
import com.finops.dao.AbstractDAO;
import com.finops.partner.model.PartnerBean;
import com.finops.util.AzureBlobUtil;
import com.finops.util.Constants;
import com.finops.util.DateUtil;
import com.finops.util.FileUtil;
import com.finops.util.sql.Condition;
import com.finops.util.sql.Query;
import com.finops.util.sql.QueryBuilder;
import com.finops.util.sql.QueryType;
import jakarta.servlet.http.HttpSession;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

@Component
public class PartnerDAO extends AbstractDAO {

    @MeasureTime
    public List<PartnerBean> findPartnersByPage(PartnerBean bean) {
        QueryBuilder findByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" PARTNER_ACCOUNT_CODE,PARTNER_CODE,DESCRIPTION1,pad.address1,pad.pan_no," +
                        "pad.state_code,pad.gstin_no,cd.DESCRIPTION country,cc.acct_name ")
                .FROM("partner_account_d", "pad")
                .LEFT_JOIN("country_d", "cd",
                        new Condition("pad.COUNTRY_CODE", "cd.COUNTRY_CODE", Query.EQUALS))
                .LEFT_JOIN("gl_code_combination_d", "cc",
                        new Condition("cc.code_combination_id", "pad.partner_acct_code", Query.EQUALS))
                .WHERE("pad.LOADNG_AGNT", "?");

        String[] fields = {"partner_account_code", "partner_code", "description1", "address1",
                "pan_no", "state_code", "gstin_no", "country", "acct_name"};

        addFilter(findByPageQuery, bean, fields);
        addLimit("description1", findByPageQuery, bean);

        return jdbcTemplate.query(
                findByPageQuery.build().toString(),
                new BeanPropertyRowMapper<>(PartnerBean.class),
                bean.getLoadingAgent());
    }

    @MeasureTime
    public List<PartnerBean> findKYCByPage(PartnerBean bean) {
        QueryBuilder findByPageQuery = new QueryBuilder(QueryType.SELECT)
                .COLUMNS(" a.PARTNER_CODE, a.DESCRIPTION description1, a.STATUS,b.DESCRIPTION country,a.KYC_ID id,a.loading_agent ")
                .FROM("partner_kyc_d", "a")
                .LEFT_JOIN("country_d", "b",
                        new Condition("a.COUNTRY_CODE", "b.COUNTRY_CODE", Query.EQUALS))
                .LEFT_JOIN("partner_account_d", "pa",
                        new Condition("pa.partner_code", "a.partner_code", Query.EQUALS),
                        new Condition("pa.loadng_agnt", "?", Query.EQUALS))
                .WHERE("(1", "1")
                .OR_WITH_SEPARATOR("a.partner_code", "NULL", Query.IS)
                .AND("a.LOADING_AGENT", "?");

        String[] fields = {"PARTNER_CODE", "a.description", "STATUS", "b.description"};

        addFilter(findByPageQuery, bean, fields);
        addLimit("a.PARTNER_CODE", findByPageQuery, bean);
        findByPageQuery.ORDER_BY(" a.kyc_id DESC ");

        return jdbcTemplate.query(
                findByPageQuery.build().toString(),
                new BeanPropertyRowMapper<>(PartnerBean.class),
                bean.getLoadingAgent(), bean.getLoadingAgent());
    }

    @MeasureTime
    public List<PartnerBean> fetchAllPartners(PartnerBean bean) {
        String query = "SELECT partner_account_code, partner_code, description1, address1, IFNULL(partner_acct_code,0) partnerAcctCode," +
                "GSTIN_NO,state_code,SALESMAN_CODE salesManCode " +
                "FROM partner_account_d WHERE loadng_agnt = ? ";

        return jdbcTemplate.query(query,
                new BeanPropertyRowMapper<>(PartnerBean.class),
                bean.getLoadingAgent());
    }

    public PartnerBean retrieveKYC(PartnerBean partnerBean) {
        String RETRIEVE_KYC = "SELECT k.kyc_id id, k.partner_code, k.description description1, k.address1, k.address2, k.city, k.state_code, k.country_code,"
                + "k.zip_code, k.tel_cc , k.tel_ac, k.tel_no, k.fax_cc, k.fax_ac, k.fax_no, k.credit_period, k.contact_person, k.attachment_1_name attachment1FileName,"
                + "k.attachment_2_name attachment2FileName, k.attachment_3_name attachment3FileName, k.attachment_4_name attachment4FileName,c.description country_name,st.state_name,"
                + "k.banker_name, k.banker_tel_cc bankerTelCC, k.banker_tel_ac bankerTelAC, k.banker_tel_no bankerTelNo, k.banker_address bankAddress," +
                "k.banker_city bankCity, k.banker_state_code bankStateCode,k.banker_zip_code bankZipCode, k.bank_ac_no bankAccountNo, k.ifsc_code, " +
                "k.swift_code, k.iec_no, IFNULL(k.credit_limit,0) credit_limit, k.PAN_NO,k.TAN_NO,k.SERVICE_TAX,k.NOTE kycNote,k.EMAIL,"
                + "k.CURRENCY_ID,k.PARTNER_ACCT_CODE,k.SALESMAN_CODE salesManCode,k.GSTIN_NO,k.CLIENT clientType,k.BILLING_PARTY bpType,k.SHPR shprType,"
                + "k.CNEE cneeType,k.AGNT agentType,k.CO_LOADER coloadrType,k.CARRIER carrierType,k.SUB_CONTRCTR cntrctrType,cur.currency_code," +
                "(CASE WHEN pa.partner_acct_code IS NULL THEN 0 ELSE pa.partner_acct_code END) partner_acct_code, " +
                "bankst.state_name bankStateName,s.name salesManName,k.loading_agent loadingAgentCode "
                + "FROM partner_kyc_d k "
                + "LEFT OUTER JOIN country_d c ON (k.country_code = c.country_code) "
                + "LEFT OUTER JOIN state_d st ON (st.state_code=k.state_code) "
                + "LEFT OUTER JOIN currency_d cur ON (cur.currency_id=k.currency_id) "
                + "LEFT OUTER JOIN state_d bankst ON (bankst.state_code=k.banker_state_code) "
                + "LEFT OUTER JOIN salesman_f s ON (s.code = k.salesman_code) "
                + "LEFT OUTER JOIN partner_account_d pa ON(pa.partner_code = k.partner_code AND pa.loadng_agnt = ?) "
                + "WHERE k.kyc_id = ? ";

        return jdbcTemplate.queryForObject(RETRIEVE_KYC,
                new BeanPropertyRowMapper<>(PartnerBean.class), partnerBean.getLoadingAgent(),
                partnerBean.getId());
    }

    public PartnerBean retrievePartnerAccount(PartnerBean partnerBean) {
        String query = ("select pa.PARTNER_ACCOUNT_CODE,pa.COMPANY_ID,pa.PARTNER_CODE,pa.LOADNG_AGNT,pa.DEST_AGNT,pa.DESCRIPTION1 ,pa.DESCRIPTION2 ,pa.ADDRESS1,pa.ADDRESS2"
                + ",pa.CITY_NAME,pa.COUNTRY_CODE,pa.EMAIL,pa.WEB,pa.TEL_CC,pa.TEL_AC,pa.TEL_NO,pa.FAX_CC,pa.FAX_AC,pa.FAX_NO,pa.CLIENT clientType,pa.BILLING_PARTY bpType,pa.SHPR shprType,"
                + "pa.CNEE cneeType,pa.AGNT agentType,pa.CO_LOADER coloadrType,pa.CARRIER carrierType,pa.SUB_CONTRCTR cntrctrType,IFNULL(pa.PARTNER_ACCT_CODE,0) partnerAcctCode,pa.DISPLAY,pa.STATUS,pa.ZIP_CODE,"
                + "pa.PAN_NO,pa.TAN_NO,pa.SERVICE_TAX,pa.NOTE,pa.CURRENCY_ID,IFNULL(pa.CREDIT_PERIOD,0) creditPeriod,"
                + "pa.SALESMAN_CODE salesManCode,s.NAME salesManName,pa.STATE_CODE,pa.GSTIN_NO,c.description country_name, cur.currency_code, st.state_name,"
                + "pa.banker_name, pa.banker_tel_cc bankerTelCC, pa.banker_tel_ac bankerTelAC, pa.banker_tel_no bankerTelNo, " +
                "pa.banker_address bankAddress,pa.banker_city bankCity, pa.banker_state_code bankStateCode," +
                "pa.banker_zip_code bankZipCode, pa.bank_ac_no bankAccountNo, pa.ifsc_code, pa.swift_code, pa.iec_no, IFNULL(pa.credit_limit,0) credit_limit, bankst.state_name bank_state_name, pa.creditor, pa.debtor,pa.disable_print," +
                "k.attachment_1_name attachment1FileName,k.attachment_2_name attachment2FileName, k.attachment_3_name attachment3FileName, k.attachment_4_name attachment4FileName "
                + "FROM partner_account_d pa  " +
                "LEFT OUTER JOIN partner_kyc_d k ON (k.partner_code = pa.partner_code and pa.loadng_agnt = ?) "
                + "LEFT OUTER JOIN salesman_f s ON (s.code = pa.salesman_code) "
                + "LEFT OUTER JOIN country_d c ON (pa.country_code = c.country_code) "
                + "LEFT OUTER JOIN state_d st ON (st.state_code=pa.state_code) "
                + "LEFT OUTER JOIN currency_d cur ON (cur.currency_id=pa.currency_id) "
                + "LEFT OUTER JOIN state_d bankst ON (bankst.state_code=pa.banker_state_code) "
                + "WHERE pa.PARTNER_CODE= ? "
                + "AND pa.LOADNG_AGNT = ? ");

        return jdbcTemplate.queryForObject(query,
                new BeanPropertyRowMapper<>(PartnerBean.class),
                partnerBean.getLoadingAgent(),partnerBean.getPartnerCode(), partnerBean.getLoadingAgent());
    }

    public boolean isDuplicatePartnerKyc(PartnerBean partnerBean) {
        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM partner_kyc_d WHERE description=?", Integer.class, partnerBean.getDescription1().trim());
        return count > 0;
    }

    public boolean isDuplicatePartner(PartnerBean partnerBean) {
        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM partner_account_d WHERE description1=?", Integer.class, partnerBean.getDescription1().trim());
        return count > 0;
    }

    public boolean isDuplicatePartnerCode(PartnerBean partnerBean) {
        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM partner_account_d WHERE partner_code=? AND loadng_agnt=? AND GSTIN_NO=? ",
                Integer.class, partnerBean.getPartnerCode(), partnerBean.getBranch(), partnerBean.getGstinNo());
        return count > 0;
    }

    public boolean isDuplicateGSTIN(PartnerBean partnerBean) {
        int count = jdbcTemplate.queryForObject("SELECT count(*) FROM partner_account_d WHERE gstin_no=?", Integer.class, partnerBean.getGstinNo());
        return count > 0;
    }

    public void updatePartnerKYC(PartnerBean pb) {
        pb.setDescription1(pb.getDescription1().trim());
        String updateQuery = "UPDATE partner_kyc_d SET description=?, address1=?, city=?, state_code=?, country_code=?, zip_code=?,"
                + "tel_cc=?, tel_ac=?, tel_no=?, fax_cc=?, fax_ac=?, fax_no=?, credit_period=?, contact_person=?, amended_by=?, amended_date=SYSDATE(),"
                + "partner_code=?,CLIENT=?,BILLING_PARTY=?,SHPR=?,CNEE=?,AGNT=?,"//17+4
                + "CO_LOADER=?,CARRIER=?,SUB_CONTRCTR=?,PAN_NO=?,TAN_NO=?,CURRENCY_ID=?,"//26
                + "SALESMAN_CODE=?,GSTIN_NO=?,banker_name=?,banker_tel_cc=?,banker_tel_ac=?, "//33
                + "banker_tel_no=?,banker_address=?,banker_city=?,banker_state_code=?,banker_zip_code=?, "
                + "bank_ac_no=?, ifsc_code=?, swift_code=?, iec_no=?, credit_limit=?,NOTE=?, partner_acct_code=?, "
                + "email=? " +
                "WHERE kyc_id=?";

        Object[] args = new Object[]{pb.getDescription1(), pb.getAddress1(), pb.getCity(), pb.getStateCode(), pb.getCountryCode(), pb.getZipCode(),
                pb.getTelCc(), pb.getTelAc(), pb.getTelNo(), pb.getFaxCc(), pb.getFaxAc(), pb.getFaxNo(), pb.getCreditPeriod(), pb.getContactPerson(), pb.getUserId(),
                pb.getPartnerCode(), pb.getClientType(), pb.getBpType(), pb.getShprType(), pb.getCneeType(), pb.getAgntType(), pb.getColoadrType(), pb.getCarrierType(), pb.getCntrctrType(),
                pb.getPanNo(), pb.getTanNo(), default0(pb.getCurrencyId()), pb.getSalesManCode(), pb.getGstinNo(), pb.getBankerName(), pb.getBankerTelCC(), pb.getBankerTelAC(), pb.getBankerTelNo(),
                pb.getBankAddress(), pb.getBankCity(), pb.getBankStateCode(), pb.getBankZipCode(), pb.getBankAccountNo(), pb.getIfscCode(), pb.getSwiftCode(), pb.getIecNo(), pb.getCreditLimit(),
                pb.getKycNote(), pb.getPartnerAcctCode(), pb.getEmail(),pb.getId()};

    jdbcTemplate.update(updateQuery, args);

        try {
            Map<String, byte[]> fileMap = FileUtil.getKycAttachmentsDetailFromStream(pb.getAttachment1File(), pb.getAttachment2File(),
                    pb.getAttachment3File(), pb.getAttachment4File());
            //FileUtil.moveFilesToFileSystem(fileMap, pb.getFilterValue(), Constants.PARTNER_KYC_MODULE, pb.getDescription1());
            int i=1;
            for (String fileName : fileMap.keySet()) {
                byte[] bytes = fileMap.get(fileName);
                if(bytes != null && bytes.length > 0) {
                    AzureBlobUtil.uploadFile(fileName, new ByteArrayInputStream(bytes), bytes.length, "partner/" + pb.getId(), pb.getAzureEndPoint());
                    String updateAttachmentQuery = "UPDATE partner_kyc_d SET attachment_"+i+"_name = ? WHERE kyc_id=?";
                    jdbcTemplate.update(updateAttachmentQuery, fileName, pb.getId());
                }
                i++;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createPartnerKYC(PartnerBean pb) {
        int maxNumber = generateAutoNumber("SELECT MAX(KYC_ID) + 1 FROM partner_kyc_d", 1001);
        pb.setDescription1(pb.getDescription1().trim());
        String query = "INSERT INTO partner_kyc_d VALUES (?,?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?,?,?, ?, ?, ?, " //19
                + "?,?, ?, ?, ?, ?,'A', ?, SYSDATE(), ?, SYSDATE(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + //50
                "?,?,?,?,?,?,?,?,?,?,?)";

        jdbcTemplate.update(query, maxNumber, null,pb.getDescription1(), pb.getAddress1(), pb.getAddress2(), pb.getCity(), pb.getStateCode(), pb.getCountryCode(), pb.getZipCode(),
                pb.getTelCc(), pb.getTelAc(), pb.getTelNo(), pb.getFaxCc(), pb.getFaxAc(), pb.getFaxNo(), pb.getCreditPeriod(), pb.getContactPerson(), //17
                pb.getAttachment1File() != null ? pb.getAttachment1File().getOriginalFilename() : null, pb.getAttachment2File() != null ? pb.getAttachment2File().getOriginalFilename() : null,
                pb.getAttachment3File() != null ? pb.getAttachment3File().getOriginalFilename() : null, pb.getAttachment4File() != null ? pb.getAttachment4File().getOriginalFilename() : null, //21
                null, null, null, null, pb.getUserId(), pb.getUserId(), pb.getClientType(), pb.getBpType(), pb.getShprType(), pb.getCneeType(), pb.getAgntType(), pb.getColoadrType(), pb.getCarrierType(),
                pb.getCntrctrType(), 0, pb.getPanNo(), pb.getTanNo(), pb.getServiceTax(), StringUtils.hasText(pb.getCurrencyId())? pb.getCurrencyId() : null, pb.getKycNote(), pb.getSalesManCode(), pb.getGstinNo(), "N", pb.getCreditorType(), pb.getDebtorType(),//46
                pb.getBankerName(), pb.getBankerTelCC(), pb.getBankerTelAC(), pb.getBankerTelNo(), pb.getBankAddress(), pb.getBankCity(), pb.getBankStateCode(), pb.getZipCode(), pb.getBankAccountNo(), //55
                pb.getIfscCode(), pb.getSwiftCode(), pb.getIecNo(), pb.getCreditLimit(), pb.getLoadingAgent(), pb.getEmail());

        try {
            Map<String, byte[]> fileMap = FileUtil.getKycAttachmentsDetailFromStream(pb.getAttachment1File(), pb.getAttachment2File(),
                    pb.getAttachment3File(), pb.getAttachment4File());
            FileUtil.moveFilesToFileSystem(fileMap, pb.getFilterValue(), Constants.PARTNER_KYC_MODULE, pb.getDescription1().trim());
            for (String fileName : fileMap.keySet()) {
                byte[] bytes = fileMap.get(fileName);
                if(bytes != null) {
                    AzureBlobUtil.uploadFile(fileName, new ByteArrayInputStream(bytes), bytes.length,
                            "partner/" + maxNumber, pb.getAzureEndPoint());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createPartner(PartnerBean pb) {
        pb.setDescription1(pb.getDescription1().trim());
        createPartnerCode(pb);

        String query = "insert into partner_d (PARTNER_CODE, DESCRIPTION,ADDRESS1,ADDRESS2,COUNTRY_CODE,TEL_CC,"
                + "TEL_AC,TEL_NO,FAX_CC,FAX_AC,FAX_NO,DISPLAY,STATUS,CREATED_BY,CREATION_DATE,AMENDED_BY,AMENDED_DATE,STATE_CODE) "
                + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE(),?,SYSDATE(),?)";

        jdbcTemplate.update(query, pb.getPartnerCode(), pb.getDescription1(), pb.getAddress1(), pb.getAddress2(), pb.getCountryCode(),
                pb.getTelCc(), pb.getTelAc(), pb.getTelNo(), pb.getFaxCc(), pb.getFaxAc(), pb.getFaxNo(), "Y" , "A", pb.getUserId(),
                pb.getUserId(), pb.getStateCode());
    }

    public void createPartnerCode(PartnerBean bean) {

        ResultSet resultSet1 = null;
        String s = bean.getDescription1();
        String str = ".,!,@,#,$,%,^,&,*,(,),-,_,+,=,/, ";
        StringTokenizer st = new StringTokenizer(s, str);

        StringBuilder codeNo = new StringBuilder();

        while (st.hasMoreTokens()) {
            codeNo.append(st.nextToken().charAt(0));
        }
        if (codeNo.length() == 1 && s.length() >= 3) {
            codeNo = new StringBuilder(s.substring(0, 3));
        }
        if (codeNo.length() > 3) {
            codeNo = new StringBuilder(codeNo.substring(0, 3));
        }

        int srNo = generateAutoNumber("select IFNULL(MAX(serial_no)+1,1) from partner_account_d where code_no = '"+codeNo+"' ",
                1);

        String serialNo = "";

        if(srNo < 10) {
            serialNo = "00" + srNo;
        }
        else{
            serialNo = "0" + srNo;
        }

        if (!StringUtils.hasText(bean.getPartnerCode())) {
            bean.setPartnerCode(codeNo + serialNo);
        }
        bean.setCodeNo(codeNo.toString());
        bean.setSerialNo(serialNo);

    }

    public void createPartnerDetails(PartnerBean pb) {

        int paCode = generateAutoNumber("SELECT max(PARTNER_ACCOUNT_CODE)+1 from PARTNER_ACCOUNT_D", 1);
        String sysDate = DateUtil.getSystemDate();
        pb.setDescription1(pb.getDescription1().trim());
        String query = "insert into partner_account_d (PARTNER_ACCOUNT_CODE,COMPANY_ID," +
                "CODE_NO,SERIAL_NO,PARTNER_CODE,LOADNG_AGNT,DEST_AGNT, DESCRIPTION1,DESCRIPTION2, ADDRESS1,ADDRESS2,CITY_NAME,"+
                "COUNTRY_CODE,EMAIL,WEB,TEL_CC,TEL_AC,TEL_NO,FAX_CC,FAX_AC,FAX_NO,CLIENT,BILLING_PARTY,SHPR,CNEE,AGNT,CO_LOADER," +
                "CARRIER,SUB_CONTRCTR,PARTNER_ACCT_CODE,DISPLAY,STATUS,CREATED_BY,CREATION_DATE,AMENDED_BY,AMENDED_DATE," +
                "ZIP_CODE,STATE_CODE,PAN_NO,TAN_NO,SERVICE_TAX,CURRENCY_ID,NOTE,CREDIT_PERIOD, SALESMAN_CODE,GSTIN_NO,IS_SEZ," +
                "BANKER_NAME, BANKER_TEL_CC, BANKER_TEL_AC, BANKER_TEL_NO,BANKER_ADDRESS, BANKER_CITY, BANKER_STATE_CODE," +
                "BANKER_ZIP_CODE,BANK_AC_NO, IFSC_CODE, SWIFT_CODE, IEC_NO, CREDIT_LIMIT) " +
                "values (?,?,?,?,?,?,?,?,   ?,?,?,?,?,?,?,?,   ?,?,?,?,?,?,?,?,    ?,?,?,?,?,?,?,?,   ?,?,?,?,?,?,?,?,   ?,?,?,?,?,?,?,?,   ?,?,?,?,?,?,?,?,  ?,?,?,?)";

        jdbcTemplate.update(query, paCode, 1001, pb.getCodeNo(), pb.getSerialNo(), pb.getPartnerCode(), pb.getLoadingAgent(), pb.getDestAgentCode(), pb.getDescription1(),
                pb.getDescription2(), pb.getAddress1(), pb.getAddress2(), pb.getCity(), pb.getCountryCode(), pb.getEmail(),pb.getWeb(),pb.getTelCc(), pb.getTelAc(),pb.getTelNo(),
                pb.getFaxAc(), pb.getFaxCc(), pb.getFaxNo(), pb.getClientType(), pb.getBpType(), pb.getShprType(), pb.getCneeType(), pb.getAgntType(), pb.getColoadrType(), pb.getCarrierType(),
                pb.getCntrctrType(), pb.getPartnerAcctCode(),"Y","A", pb.getUserId(), sysDate, pb.getUserId(), sysDate, pb.getZipCode(), pb.getStateCode(), pb.getPanNo(),
                pb.getTanNo(), pb.getServiceTax(), default0(pb.getCurrencyId()), pb.getNote(), pb.getCreditPeriod(), pb.getSalesManCode(), pb.getGstinNo(), "N", pb.getBankerName(), pb.getBankerTelCC(),
                pb.getBankerTelCC(), pb.getBankerTelNo(), pb.getBankAddress(), pb.getBankCity(),pb.getBankStateCode(), pb.getBankZipCode(),pb.getBankAccountNo(), pb.getIfscCode(), pb.getSwiftCode(),
                pb.getIecNo(), pb.getCreditLimit());
    }

    public void replicatePartnerDetails(PartnerBean pb) {

        int paCode = generateAutoNumber("SELECT max(PARTNER_ACCOUNT_CODE)+1 from PARTNER_ACCOUNT_D", 1);
        String sysDate = DateUtil.getSystemDate();
        String branch = pb.getLoadingAgent();
        if(StringUtils.hasText(branch)){
            branch = pb.getBranch();
        }

        String query = "INSERT INTO partner_account_d " +
                "SELECT "+paCode+",1001,\n" +
                "    CODE_NO,\n" +
                "    SERIAL_NO,\n" +
                "    PARTNER_CODE,'"+branch+"','"+branch+"'," +
                "    DESCRIPTION1,\n" +
                "    DESCRIPTION2,\n" +
                "    ADDRESS1,\n" +
                "    ADDRESS2,\n" +
                "    CITY_NAME,\n" +
                "    ZIP_CODE,\n" +
                "    COUNTRY_CODE,\n" +
                "    EMAIL,\n" +
                "    WEB,\n" +
                "    TEL_CC,\n" +
                "    TEL_AC,\n" +
                "    TEL_NO,\n" +
                "    FAX_CC,\n" +
                "    FAX_AC,\n" +
                "    FAX_NO,\n" +
                "    CLIENT,\n" +
                "    BILLING_PARTY,\n" +
                "    SHPR,\n" +
                "    CNEE,\n" +
                "    AGNT,\n" +
                "    CO_LOADER,\n" +
                "    CARRIER,\n" +
                "    SUB_CONTRCTR,\n" +
                "    PARTNER_ACCT_CODE,\n" +
                "    DISPLAY,\n" +
                "    PAN_NO,\n" +
                "    TAN_NO,\n" +
                "    SERVICE_TAX,\n" +
                "    STATUS,'"+pb.getUserId()+"',SYSDATE(),'"+pb.getUserId()+"',SYSDATE(),\n" +
                "    CURRENCY_ID,\n" +
                "    NOTE,\n" +
                "    CREDIT_PERIOD,\n" +
                "    SALESMAN_CODE,'"+pb.getGstinNo()+"',"+
                "    STATE_CODE,\n" +
                "    IS_SEZ,\n" +
                "    CREDITOR,\n" +
                "    DEBTOR,\n" +
                "    BANKER_NAME,\n" +
                "    BANKER_TEL_CC,\n" +
                "    BANKER_TEL_AC,\n" +
                "    BANKER_TEL_NO,\n" +
                "    BANKER_ADDRESS,\n" +
                "    BANKER_CITY,\n" +
                "    BANKER_STATE_CODE,\n" +
                "    BANKER_ZIP_CODE,\n" +
                "    BANK_AC_NO,\n" +
                "    IFSC_CODE,\n" +
                "    SWIFT_CODE,\n" +
                "    IEC_NO,\n" +
                "    CREDIT_LIMIT,\n" +
                "    DISABLE_PRINT\n" +
                "FROM partner_account_d WHERE partner_code = ? AND loadng_agnt = ?";

        jdbcTemplate.update(query, pb.getPartnerCode(), pb.getLoadingAgent());
    }

    public void updatePartnerAccount(PartnerBean bean) {
        if(bean.getStatus() == null){
            bean.setStatus("S");
        }
        bean.setDescription1(bean.getDescription1().trim());
        String updateQuery = "UPDATE partner_account_d SET "
                + "DEST_AGNT=?,ADDRESS1=?,CITY_NAME=?,COUNTRY_CODE=?,EMAIL=?,WEB=?,"
                + "TEL_CC=?,TEL_AC=?,TEL_NO=?,FAX_CC=?,FAX_AC=?,FAX_NO=?,CLIENT=?,BILLING_PARTY=?,SHPR=?,CNEE=?,AGNT=?,"//17
                + "CO_LOADER=?,CARRIER=?,SUB_CONTRCTR=?,DISPLAY=?,ZIP_CODE=?,PAN_NO=?,TAN_NO=?,NOTE=?,CURRENCY_ID=?,"//26
                + "CREDIT_PERIOD=?,SALESMAN_CODE=?,STATE_CODE=?,GSTIN_NO=?,banker_name=?,banker_tel_cc=?,banker_tel_ac=?, "//33
                + "banker_tel_no=?,banker_address=?,banker_city=?,banker_state_code=?,banker_zip_code=?, "
                + "bank_ac_no=?, ifsc_code=?, swift_code=?, iec_no=?, credit_limit=?, CREDITOR=?, DEBTOR=?, disable_print = ?, description1=? , status = ?"
                + "WHERE partner_code = ? and loadng_agnt = ? ";//44

        Object[] args = new Object[]{bean.getDestAgentCode(), bean.getAddress1(), bean.getCity(), bean.getCountryCode(), bean.getEmail(),
                bean.getWeb(), bean.getTelCc(), bean.getTelAc(), bean.getTelNo(), bean.getFaxCc(), bean.getFaxAc(),//11
                bean.getFaxNo(), bean.getClientType(), bean.getBpType(), bean.getShprType(), bean.getCneeType(),
                bean.getAgntType(), bean.getColoadrType(), bean.getCarrierType(), bean.getCntrctrType(), "Y",//21
                bean.getZipCode(), bean.getPanNo(), bean.getTanNo(), bean.getNote(), bean.getCurrencyId(),
                bean.getCreditPeriod(), bean.getSalesManCode(), bean.getStateCode(), bean.getGstinNo(),//30
                bean.getBankerName(), bean.getBankerTelCC(), bean.getBankerTelAC(), bean.getBankerTelNo(),
                bean.getBankAddress(), bean.getBankCity(), bean.getBankStateCode(), bean.getZipCode(),//38
                bean.getBankAccountNo(), bean.getIfscCode(), bean.getSwiftCode(), bean.getIecNo(),
                bean.getCreditLimit(), bean.getCreditorType(), bean.getDebtorType(),bean.getDisablePrint(),
                bean.getDescription1(),bean.getStatus(), bean.getPartnerCode(), bean.getLoadingAgent()};

        jdbcTemplate.update(updateQuery, args);

        try {
            Map<String, byte[]> fileMap = FileUtil.getKycAttachmentsDetailFromStream(bean.getAttachment1File(), bean.getAttachment2File(),
                    bean.getAttachment3File(), bean.getAttachment4File());
            FileUtil.moveFilesToFileSystem(fileMap, bean.getFilterValue(), Constants.PARTNER_KYC_MODULE, bean.getDescription1());
            for (String fileName : fileMap.keySet()) {
                byte[] bytes = fileMap.get(fileName);
                if(bytes != null) {
                    AzureBlobUtil.uploadFile(fileName, new ByteArrayInputStream(bytes), bytes.length, "partner/" + bean.getId(), bean.getAzureEndPoint());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void delete(int id) {
            jdbcTemplate.update(
                    "DELETE FROM partner_kyc_d WHERE kyc_id = ? AND partner_code IS NULL ",
                    id
            );
    }

    public void linkPartner(PartnerBean partnerBean, int codeCombinationId){
            String updateQuery = "UPDATE partner_account_d SET partner_acct_code = ? "
                    + "WHERE partner_code = ? and (partner_acct_code = '' OR partner_acct_code=0) ";
            jdbcTemplate.update(updateQuery, codeCombinationId, partnerBean.getPartnerCode());
    }
}
