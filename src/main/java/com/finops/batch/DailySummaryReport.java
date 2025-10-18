package com.finops.batch;

import com.finops.util.DateUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@Configuration
@EnableScheduling
public class DailySummaryReport {

    protected static int companyId;
    protected static int acctYear;
    protected static String startDate;
    protected static String endDate;
    protected static Properties prop = new Properties();
    protected static Properties prop2 = new Properties();
    protected static String host;
    protected static String from;
    protected static String to;
    protected static String newword;
    protected static String userName;
    protected static String database;

    protected static void setup() throws Exception {
        prop2.load(new FileInputStream("C:/Bhaumik/db.properties"));
        //prop.load(new FileInputStream("D:/Bhaumik/Projects/batch.properties"));
        prop.load(DailySummaryReport.class.getClassLoader().getResourceAsStream("batch.properties"));
        companyId = Integer.parseInt(prop.getProperty("companyId"));
        acctYear = Integer.parseInt(prop.getProperty("acctYear"));
        startDate = prop.getProperty("startDate");
        endDate = prop.getProperty("endDate");
        host = prop.getProperty("email.host");
        from = prop.getProperty("email.from");
        to = prop.getProperty("email.to");
        newword = "p@ssw0rd";
        userName = "root";
        database = "SHIK2023";
    }

    @Scheduled(cron = "0 0 */8 * * *")
    public void schedule() throws Exception {
        int currentHour = DateUtil.getCurrentHour();
        if (currentHour > 0) {
            setup();
            if ("Y".equals(prop2.getProperty("isPrimary"))) {

                String mp = prop.getProperty("newword");
//                if (mp.equals(newword)) {
//                    // RUN DAILY BILLING JOB
//                    boolean billingJobFlag = runBillingJob();
//                    Database db = new Database(true);
//                    // DAILY TRIAL SUMMARY
//                    TrialBalanceDAO dao = new TrialBalanceDAOImpl();
//                    SummaryPrimaryVO vo = new SummaryPrimaryVO();
//                    UtilDAO utilDAO = new UtilDAOImpl();
//                    vo.setSession(createSession());
//                    vo.setType("TRIAL_PRIMARY");
//                    vo.setLineAcctId(0);
//                    vo.setLineAcctName(null);
//                    dao.matchTrialBalance(vo, db);
//
//                    StringBuilder sb = new StringBuilder();
//
//                    sb.append("*************** DAILY SUMMARY REPORT - START - "+acctYear+" ************************\n\n");
//                    BigDecimal debit = vo.getDebitTotal().setScale(0,BigDecimal.ROUND_FLOOR);
//                    BigDecimal credit = vo.getCreditTotal().setScale(0,BigDecimal.ROUND_FLOOR);
//                    BigDecimal diff = debit.subtract(credit);
//                    if (vo.getDebitTotal().equals(vo.getCreditTotal())) {
//                        sb.append("--> Trial balance matched :::::\n");
//                    } else {
//                        sb.append("--> Trial Balance did not match ::::: Difference amount is == "+diff.toString()+"\n");
//                    }
//                    sb.append("--> DEBIT =  " + vo.getDebitTotal() + "\n--> CREDIT = " + vo.getCreditTotal() + "\n");
//                    sb.append("\n*************** DAILY SUMMARY REPORT - END ************************\n\n");
//
//                    sb.append("*************** DAILY JOB - START ************************\n\n");
//                    if (billingJobFlag) {
//                        sb.append("-->  Daily billing job completed successfully  :::::\n");
//                    } else {
//                        sb.append("-->  Daily billing job failed  :::::\n");
//                    }
//
//                    boolean backupJobFlag = runBackupJob();
//
//                    if (backupJobFlag) {
//                        sb.append("-->  Daily backup job completed successfully  :::::\n");
//                    } else {
//                        sb.append("-->  Daily backup job failed  :::::\n");
//                    }
//                    sb.append("\n*************** DAILY JOB - END ************************\n\n");
//
//                    try {
//                        List<VoucherVO> dirtyList = utilDAO.getDirtyVouchers(db);
//
//                        if (dirtyList.isEmpty()) {
//                            sb.append("-->  No Dirty Vouchers Found!  :::::\n");
//                        } else {
//                            sb.append("-->  Dirty Vouchers Found as below!  :::::\n");
//                            sb.append("ACCOUNTING YEAR ----> ACCOUNTED_DR ----> ACCOUNTED_CR ----> JE_VOUCHER_NO ----> ATTRIBUTE2 \n");
//                            for (VoucherVO dvo : dirtyList) {
//                                sb.append(dvo.getAcctYear()).append(" ----> ");
//                                sb.append(dvo.getAccountedDr()).append(" ----> ");
//                                sb.append(dvo.getAccountedCr()).append(" ----> ");
//                                sb.append(dvo.getPaymentNo()).append(" ----> ");
//                                sb.append(dvo.getAttribute2()).append(" ----> ");
//                                sb.append(dvo.getRemarks()).append("\n");
//                            }
//                        }
//                        sb.append("-->  Dirty Check job completed successfully  :::::\n");
//                    } catch (SQLException sqe) {
//                        sb.append("-->  Dirty Check job failed!!!  :::::\n");
//                        sqe.printStackTrace();
//                    }
//                    sb.append("*************** DIRTY CHECK - END ************************\n\n");
//                    db.close();
//                    //sendEmail(sb.toString());
//                    //BatchUtil.sendEmail(sb.toString(), host, from, to, newword, DateUtil.getSystemDate() + " :: DAILY SUMMARY REPORT ::");
//                }
            }
        }
    }
}
