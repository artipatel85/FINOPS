package com.finops.report.legacy;

import com.finops.freight.bean.JobBean;
import com.finops.util.ApplicationUtil;
import com.finops.util.Database;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JOBPDF {


    public static String writeUCMpdf(String jobNum, JobBean jobBean, final String pdfFolderPath, JdbcTemplate jdbcTemplate) {

        ResultSet rs = null;
        ResultSet rstwo = null;
        ResultSet rsfour = null;
        ResultSet rsfive = null;
        String lAgent = "";
        String userName = "";
        lAgent = jobBean.getLoadingAgent();
        userName = jobBean.getUserId();
        Connection connection = null;

        try {
            boolean flag_data = false;
            connection = jdbcTemplate.getDataSource().getConnection();
            StringBuilder dispQuery = new StringBuilder();
            StringBuilder dispQueryTwo = new StringBuilder();
            StringBuilder dispQueryThree = new StringBuilder();
            StringBuilder dispQueryFour = new StringBuilder();
            StringBuilder dispQueryFive = new StringBuilder();
            StringBuilder dispQuerySix = new StringBuilder();

            dispQuery.append("select j.dest_agnt,DAGNT.DESCRIPTION1 DAGNTDESC,j.vsl,j.voy"
                    + ",CONVERT(j.etd,DATE) ETD,j.pol"
                    + ",POL.DESCRIPTION1 POLDESC"
                    + ",CONVERT(j.eta,DATE) ETA,j.pod,pod.description1 PODDESC"
                    + ",CONVERT(j.job_date,DATE) JOB_DATE,j.created_by "
                    + " from (((job_hdr_f j "
                    + " LEFT OUTER JOIN PORT_D POL  ON (j.POL = POL.PORT_CODE ))"
                    + " LEFT OUTER JOIN PORT_D POD  ON (j.POD = POD.PORT_CODE)) "
                    + " LEFT OUTER JOIN PARTNER_ACCOUNT_D DAGNT  ON (j.DEST_AGNT = DAGNT.PARTNER_CODE ))"
                    + " where j.job_number='" + jobNum + "' and j.LOADING_AGNT='" + lAgent + "' "
                    + " group by j.dest_agnt,DAGNT.DESCRIPTION1,j.vsl,j.voy"
                    + ",ETD,j.pol,J.POD,POL.DESCRIPTION1,pod.description1,ETA,"
                    + " JOB_DATE,j.created_by");

            dispQueryTwo.append("  SELECT SHPR.DESCRIPTION1 SHPRDESC,pol.description1 POLDESC"
                    + ",SOFL.CONTNR_NO SO_CONTNR,SOFL.SIZE_CODE SO_SIZE"
                    + ",SOFL.ACT_QTY SO_ACT_QTY,SOFL.ACT_UNIT SO_ACT_UNIT"
                    + ",SOFL.ACT_GROSS_WT SO_ACT_GROSS_WT"
                    + ",SOFL.ACT_NET_KGS SO_ACT_NET_KGS,SOFL.ACT_CBM SO_ACT_CBM"
                    + ",dest.description1 DESTDESC"
                    + ",blfl.service_term_code"
                    + ",SOH.bl_no,soh.cnee,cnee.description1 CNEEDESC FROM SO_HDR_F SOH INNER JOIN SO_FCL_LCL_F SOFL ON (SOH.BKG_REF_NO = SOFL.BKG_REF_NO)"
                    + " LEFT OUTER JOIN (SELECT CNEE.PARTNER_CODE,CNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CNEE WHERE CNEE.LOADNG_AGNT = '" + lAgent + "' AND CNEE.STATUS = 'A') "
                    + "CNEE ON(SOH.CNEE=CNEE.PARTNER_CODE)"
                    + " LEFT OUTER JOIN (SELECT SHPR.PARTNER_CODE,SHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D SHPR WHERE SHPR.LOADNG_AGNT = '" + lAgent + "' AND SHPR.STATUS = 'A')"
                    + " SHPR ON (SOH.SHPR=SHPR.PARTNER_CODE)"
                    + "LEFT OUTER JOIN port_d pol ON (SOH.pol=pol.port_code)"
                    + "LEFT OUTER JOIN port_d dest ON (SOH.dest=dest.port_code)"
                    + "LEFT OUTER JOIN BL_FCL_LCL_F BLFL ON (BLFL.BKG_REF_NO = SOFL.BKG_REF_NO)"
                    + "WHERE ( SOH.JOB_NUMBER='" + jobNum + "')" + " "
                    + "AND (SOFL.ACT_QTY IS NOT NULL ) "
                    + "AND (SOFL.ACT_GROSS_WT IS NOT NULL ) "
                    + "AND (SOFL.ACT_NET_KGS IS NOT NULL ) "
                    + "AND (SOFL.ACT_CBM IS NOT NULL ) "
                    + "  AND SOH.SO_STATUS='A' "
                    + " AND SOFL.CONTNR_NO=?"
                    + " and  SOH.BL_NO is not null "
                    + " and LOADING_AGNT='" + lAgent + "' and blfl.service_term_code is not null "
                    + " GROUP BY  SHPR.DESCRIPTION1 ,pol.description1 ,SOFL.CONTNR_NO,SOFL.SIZE_CODE "
                    + ",SOFL.ACT_QTY ,SOFL.ACT_UNIT ,SOFL.ACT_GROSS_WT ,SOFL.ACT_NET_KGS "
                    + ",SOFL.ACT_CBM ,dest.description1 ,blfl.service_term_code,SOH.bl_no,soh.cnee"
                    + ",cnee.description1 "
                    + "   order BY so_contnr  ");

            dispQueryThree.append("select DESCRIPTION1 from partner_account_d where PARTNER_CODE='" + lAgent + "' ");
            dispQueryFour.append("SELECT distinct BLFL.CONTNR_NO bl_CONTNR  ,BLFL.SIZE_CODE bl_SIZE"
                    + " ,sum(BLFL.ACT_QTY) bl_ACT_QTY"
                    + " ,sum(BLFL.ACT_GROSS_WT) bl_ACT_GROSS_WT"
                    + " ,sum(BLFL.ACT_NET_KGS) bl_ACT_NET_KGS"
                    + " ,sum(BLFL.ACT_CBM) bl_ACT_CBM"
                    + " FROM SO_HDR_F SOH INNER JOIN BL_FCL_LCL_F BLFL ON (SOH.BKG_REF_NO = BLFL.BKG_REF_NO) "
                    + " WHERE ( SOH.JOB_NUMBER='" + jobNum + "')" + " AND "
                    + " (BLFL.ACT_QTY IS NOT NULL ) AND (BLFL.ACT_GROSS_WT IS NOT NULL ) AND "
                    + " (BLFL.ACT_NET_KGS IS NOT NULL ) AND (BLFL.ACT_CBM IS NOT NULL )  AND"
                    + "  SOH.SO_STATUS='A'  and LOADING_AGNT='" + lAgent + "' and SOH.BL_NO is not null "
                    + " and blfl.contnr_no is not null and blfl.contnr_no<>''"
                    + " group by blfl.CONTNR_NO,blfl.SIZE_CODE"
                    + " order BY bl_contnr");

            dispQueryFive.append("select DISTINCT SOFL.size_code,count(SOFL.size_code) sizecount"
                    + ",count(DISTINCT sofl.CONTNR_NO) countcntnr,blfl.service_term_code "
                    + ",sum(SOFL.ACT_CBM) SO_ACT_CBM "
                    + "FROM SO_HDR_F SOH INNER JOIN SO_FCL_LCL_F SOFL ON "
                    + "(SOH.BKG_REF_NO = SOFL.BKG_REF_NO) "
                    + "LEFT OUTER JOIN BL_FCL_LCL_F BLFL ON "
                    + " (BLFL.BKG_REF_NO = SOFL.BKG_REF_NO) WHERE "
                    + " ( SOH.JOB_NUMBER='" + jobNum + "')" + " AND "
                    + " (SOFL.ACT_QTY IS NOT NULL ) AND (SOFL.ACT_GROSS_WT IS NOT NULL ) "
                    + " AND (SOFL.ACT_NET_KGS IS NOT NULL ) AND (SOFL.ACT_CBM IS NOT NULL )"
                    + " AND  SOH.SO_STATUS='A' and blfl.service_term_code is not null"
                    + " and LOADING_AGNT='" + lAgent + "' and "
                    + " SOH.BL_NO is not null  group by blfl.service_term_code ,"
                    + "  SOFL.size_code");

            dispQuerySix.append("select blfl.service_term_code "
                    + ",sum(SOFL.ACT_CBM) SO_ACT_CBM "
                    + " FROM SO_HDR_F SOH INNER JOIN SO_FCL_LCL_F SOFL ON "
                    + " (SOH.BKG_REF_NO = SOFL.BKG_REF_NO) "
                    + " LEFT OUTER JOIN BL_FCL_LCL_F BLFL ON "
                    + " (BLFL.BKG_REF_NO = SOFL.BKG_REF_NO) "
                    + " WHERE ( SOH.JOB_NUMBER='" + jobNum + "') "
                    + " AND SOH.SO_STATUS='A' "
                    + " and blfl.service_term_code is not null "
                    + " and LOADING_AGNT='" + lAgent + "' and soh.bl_no is not null "
                    + " group by blfl.service_term_code ");

            try {
                rs = Database.query(dispQuery.toString(), connection);

                rsfour = Database.query(dispQueryFour.toString(), connection);
                rsfive = Database.query(dispQueryFive.toString(), connection);

            } catch (Exception e) {
                e.printStackTrace();
            }

            flag_data = true;
            if (flag_data) {
                Document document = new Document(PageSize.A4, 30, 30, 60, 70);

                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFolderPath + "/" + jobNum + ".pdf"));

                final Rectangle page = document.getPageSize();

                final PdfPTable headtab = new PdfPTable(7);

                final int hwidths[] = {5, 25, 40, 15, 12, 13, 10};

                headtab.setWidths(hwidths);

                String Desc1 = null;
                String job = "Export Shipment Pre-Alert";

                Desc1 = jobBean.getCompanyName();

                final PdfPTable sfpvtltd = new PdfPTable(1);

                PdfPCell sfCell = new PdfPCell(new Phrase(Desc1, FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, new Color(0x00, 0x00, 0x00))));
                sfCell.setBorderColor(Color.white);
                sfCell.setHorizontalAlignment(1);

                sfpvtltd.addCell(sfCell);
                sfCell = new PdfPCell(new Phrase(job, new Font(Font.HELVETICA, 12, Font.NORMAL)));
                sfCell.setBorderColor(Color.white);
                sfCell.setHorizontalAlignment(1);
                sfpvtltd.addCell(sfCell);

                class PageEvents extends PdfPageEventHelper {

                    @Override
                    public void onStartPage(PdfWriter writer, Document document) {

                        if (document.getPageNumber() > 1) {
                            sfpvtltd.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                            headtab.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                            try {
                                sfpvtltd.setWidths(hwidths);
                                headtab.setWidths(hwidths);
                            } catch (DocumentException e) {
                            }
                            sfpvtltd.writeSelectedRows(0, -1, document.leftMargin(), 830, writer.getDirectContent());
                            headtab.writeSelectedRows(0, -1, document.leftMargin(), 785, writer.getDirectContent());

                        }
                    }
                }

                writer.setPageEvent(new PageEvents());

                document.open();

                Table table = new Table(8);

                PdfPTable table1 = new PdfPTable(2);
                PdfPTable table2 = new PdfPTable(2);
                PdfPTable table4 = new PdfPTable(2);

                sfpvtltd.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                try {
                    sfpvtltd.setWidths(hwidths);
                } catch (DocumentException e) {
                }
                sfpvtltd.writeSelectedRows(0, -1, document.leftMargin(), 830, writer.getDirectContent());

                while (rs.next()) {
                    PdfPTable loadplan = new PdfPTable(2);//new pdf table
                    PdfPCell tab2cell = null;

                    PdfPCell loadplancell = new PdfPCell(new Phrase("Destination Agent", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                    loadplancell.setBorderColor(Color.white);

                    loadplan.addCell(loadplancell);
                    loadplancell = new PdfPCell(new Phrase(": " + (String) ApplicationUtil.checkForNull(rs.getString("DAGNTDESC")), new Font(Font.HELVETICA, 7, Font.NORMAL)));

                    loadplancell.setBorderColor(Color.white);

                    loadplan.addCell(loadplancell);

                    loadplancell = new PdfPCell(new Phrase("VESSEL/VOY ", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    loadplancell.setBorderColor(Color.white);
                    loadplan.addCell(loadplancell);

                    loadplancell = new PdfPCell(new Phrase(": " + (String) ApplicationUtil.checkForNull(rs.getString("VSL")) + "/" + (String) ApplicationUtil.checkForNull(rs.getString("VOY")), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    loadplancell.setBorderColor(Color.white);
                    loadplan.addCell(loadplancell);
                    loadplancell = new PdfPCell(new Phrase("ETD " + " " + (String) ApplicationUtil.checkForNull(rs.getString("POLDESC")), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    loadplancell.setBorderColor(Color.white);
                    loadplan.addCell(loadplancell);

                    loadplancell = new PdfPCell(new Phrase(": " + (String) ApplicationUtil.checkForNull(rs.getString("etd")), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    loadplancell.setBorderColor(Color.white);
                    loadplan.addCell(loadplancell);

                    loadplancell = new PdfPCell(new Phrase("ETA " + " " + (String) ApplicationUtil.checkForNull(rs.getString("PODDESC")), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    loadplancell.setBorderColor(Color.white);
                    loadplan.addCell(loadplancell);

                    loadplancell = new PdfPCell(new Phrase(": " + (String) ApplicationUtil.checkForNull(rs.getString("eta")), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    loadplancell.setBorderColor(Color.white);
                    loadplan.addCell(loadplancell);

                    float[] loadWidths = {30, 70};
                    loadplan.setWidths(loadWidths);

                    tab2cell = new PdfPCell(loadplan);
                    tab2cell.setHorizontalAlignment(0);
                    table1.addCell(tab2cell);

                    PdfPTable actualdate = new PdfPTable(2);//new pdf table
                    PdfPCell tab3cell = null;

                    PdfPCell actualdatecell = new PdfPCell(new Phrase("Date ", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                    actualdatecell.setBorderColor(Color.white);
                    actualdate.addCell(actualdatecell);

                    actualdatecell = new PdfPCell(new Phrase(": " + (String) ApplicationUtil.checkForNull(rs.getString("job_date")) + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    actualdatecell.setBorderColor(Color.white);
                    actualdate.addCell(actualdatecell);

                    actualdatecell = new PdfPCell(new Phrase("Prepared By \n\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                    actualdatecell.setBorderColor(Color.white);
                    actualdate.addCell(actualdatecell);

                    actualdatecell = new PdfPCell(new Phrase(": " + userName + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    actualdatecell.setBorderColor(Color.white);
                    actualdate.addCell(actualdatecell);

                    float[] actualDTWidths = {30, 70};
                    actualdate.setWidths(actualDTWidths);
                    //actualdate.setWidths(loadWidths);

                    tab3cell = new PdfPCell(actualdate);
                    tab3cell.setHorizontalAlignment(0);
                    table1.addCell(tab3cell);

                    float[] t1widths = {60, 40};
                    table1.setWidths(t1widths);

                    float[] t2widths = {60, 40};
                    table2.setWidths(t2widths);

                    String lines = "________________________________________________________________________________________________________________________________________";

                    Cell lincell = new Cell(new Phrase(lines, new Font(Font.HELVETICA, 7, Font.BOLD, new Color(0x00, 0x00, 0x00))));
                    lincell.setBorderColor(Color.white);
                    lincell.setColspan(8);
                    table.addCell(lincell);

                    table1.setWidthPercentage(100);//This is used to add the table1 to document
                    document.add(table1);
                    String no_of_contnr = "";
                    String serviceTerm = "";
                    String cbmTot = "";

                    while (rsfive.next()) {
                        serviceTerm = (String) ApplicationUtil.checkForNull(rsfive.getString("service_term_code"));
                        cbmTot = (String) ApplicationUtil.checkForNull(rsfive.getString("SO_ACT_CBM"));

                        if ("LCL/LCL".equalsIgnoreCase(serviceTerm)) {
                            no_of_contnr = no_of_contnr + cbmTot + " x " + (String) rsfive.getString("size_code") + ",";

                        } else {
                            no_of_contnr = no_of_contnr + (String) rsfive.getString("countcntnr") + " x " + (String) rsfive.getString("size_code") + ",";
                        }
                    }
                    Cell sonumbercell = new Cell(new Phrase("Total No of Container:" + no_of_contnr, new Font(Font.HELVETICA, 9, Font.BOLD)));
                    sonumbercell.setBorderColor(Color.white);
                    sonumbercell.setColspan(8);
                    sonumbercell.setHorizontalAlignment(0);

                    table.addCell(sonumbercell);
                    table.addCell(lincell);

                    while (rsfour.next()) {

                        Cell containercell = new Cell(new Phrase("CONTAINER-->" + (String) rsfour.getString("bl_CONTNR"), new Font(Font.HELVETICA, 9, Font.NORMAL)));
                        containercell.setBorderColor(Color.white);
                        containercell.setColspan(4);
                        containercell.setHorizontalAlignment(0);

                        table.addCell(containercell);

                        Cell containercell1 = new Cell(new Phrase((String) ApplicationUtil.checkForNull(rsfour.getString("bl_SIZE")), new Font(Font.HELVETICA, 9, Font.NORMAL)));
                        containercell1.setBorderColor(Color.white);

                        containercell1.setHorizontalAlignment(0);

                        table.addCell(containercell1);

                        Cell jobNumbercell = new Cell(new Phrase("        " + "Job No.:" + jobNum + "\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                        jobNumbercell.setBorderColor(Color.white);
                        jobNumbercell.setColspan(3);
                        jobNumbercell.setHorizontalAlignment(1);

                        table.addCell(jobNumbercell);

                        Cell emptycell = new Cell(new Phrase("\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                        emptycell.setBorderColor(Color.white);
                        emptycell.setColspan(2);
                        emptycell.setHorizontalAlignment(1);

                        table.addCell(emptycell);

                        Cell empty1cell = new Cell(new Phrase("           " + "CONTAINER TOT:" + (String) ApplicationUtil.checkForNull(rsfour.getString("bl_ACT_QTY")) + "\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                        empty1cell.setBorderColor(Color.white);
                        empty1cell.setColspan(3);
                        empty1cell.setHorizontalAlignment(2);

                        table.addCell(empty1cell);

                        Cell cbm1 = new Cell(new Phrase("" + (String) ApplicationUtil.checkForNull(rsfour.getString("bl_ACT_CBM")) + "\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                        cbm1.setBorderColor(Color.white);
                        cbm1.setColspan(1);
                        cbm1.setHorizontalAlignment(1);

                        table.addCell(cbm1);

                        Cell kgcell = new Cell(new Phrase("          " + (String) ApplicationUtil.checkForNull(rsfour.getString("bl_ACT_GROSS_WT")) + "\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                        kgcell.setBorderColor(Color.white);
                        kgcell.setColspan(2);
                        kgcell.setHorizontalAlignment(0);

                        table.addCell(kgcell);

                        PreparedStatement selectStat = null;
                        selectStat = Database.preparedStatement(dispQueryTwo.toString(),connection);
                        selectStat.setString(1, (String) ApplicationUtil.checkForNull(rsfour.getString("BL_CONTNR")));
                        rstwo = selectStat.executeQuery();

                        while (rstwo.next()) {

                            Cell shippercell = new Cell(new Phrase("-B/L No-", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                            shippercell.setBorderColor(Color.white);
                            shippercell.setHorizontalAlignment(10);

                            table.addCell(shippercell);

                            Cell actualqtycell = new Cell(new Phrase("-Pre-carriage-", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                            actualqtycell.setBorderColor(Color.white);
                            actualqtycell.setHorizontalAlignment(2);

                            table.addCell(actualqtycell);

                            Cell actualunitcell = new Cell(new Phrase("-POL-", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                            actualunitcell.setBorderColor(Color.white);
                            actualunitcell.setHorizontalAlignment(1);
                            table.addCell(actualunitcell);

                            Cell actualGwtcell = new Cell(new Phrase("-DEST-", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                            actualGwtcell.setBorderColor(Color.white);
                            actualGwtcell.setHorizontalAlignment(1);
                            table.addCell(actualGwtcell);

                            Cell actualcbmcell = new Cell(new Phrase("-QTY-", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                            actualcbmcell.setBorderColor(Color.white);
                            actualcbmcell.setHorizontalAlignment(1);

                            table.addCell(actualcbmcell);

                            Cell cbmcell = new Cell(new Phrase("-CBM-", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                            cbmcell.setBorderColor(Color.white);
                            cbmcell.setHorizontalAlignment(1);
                            table.addCell(cbmcell);

                            Cell qtycell = new Cell(new Phrase("-KGS-", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                            qtycell.setBorderColor(Color.white);
                            qtycell.setHorizontalAlignment(1);
                            table.addCell(qtycell);

                            Cell pccell = new Cell(new Phrase("-P/C-", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                            pccell.setBorderColor(Color.white);
                            pccell.setHorizontalAlignment(1);
                            table.addCell(pccell);

                            Cell hbcell = new Cell(new Phrase((String) rstwo.getString("bl_no"), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            hbcell.setBorderColor(Color.white);
                            hbcell.setHorizontalAlignment(10);

                            table.addCell(hbcell);

                            Cell precell = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            precell.setBorderColor(Color.white);
                            precell.setHorizontalAlignment(2);

                            table.addCell(precell);

                            Cell porcell = new Cell(new Phrase((String) rstwo.getString("POLDESC"), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            porcell.setBorderColor(Color.white);
                            porcell.setHorizontalAlignment(1);
                            table.addCell(porcell);

                            Cell destcell = new Cell(new Phrase((String) rstwo.getString("DESTDESC"), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            destcell.setBorderColor(Color.white);
                            destcell.setHorizontalAlignment(1);
                            table.addCell(destcell);

                            Cell qty1cell = new Cell(new Phrase((String) rstwo.getString("SO_ACT_QTY"), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            qty1cell.setBorderColor(Color.white);
                            qty1cell.setHorizontalAlignment(1);

                            table.addCell(qty1cell);

                            Cell cbm1cell = new Cell(new Phrase((String) rstwo.getString("SO_ACT_CBM"), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            cbm1cell.setBorderColor(Color.white);
                            cbm1cell.setHorizontalAlignment(1);
                            table.addCell(cbm1cell);

                            Cell kg1cell = new Cell(new Phrase((String) rstwo.getString("SO_ACT_GROSS_WT"), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            kg1cell.setBorderColor(Color.white);
                            kg1cell.setHorizontalAlignment(1);
                            table.addCell(kg1cell);

                            Cell pc1cell = new Cell(new Phrase((String) rstwo.getString("SO_ACT_UNIT"), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            pc1cell.setBorderColor(Color.white);
                            pc1cell.setHorizontalAlignment(1);
                            table.addCell(pc1cell);

                            Cell bcell = new Cell(new Phrase("\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                            bcell.setBorderColor(Color.white);

                            bcell.setHorizontalAlignment(1);
                            bcell.setColspan(8);
                            table.addCell(bcell);

                            Cell shipprcell = new Cell(new Phrase("Shipper:" + (String) ApplicationUtil.checkForNull(rstwo.getString("SHPRDESC")), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            shipprcell.setBorderColor(Color.white);
                            shipprcell.setColspan(2);
                            shipprcell.setHorizontalAlignment(0);

                            table.addCell(shipprcell);

                            Cell consigneecell = new Cell(new Phrase("Consignee:" + rstwo.getString("CNEEDESC"), new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            consigneecell.setBorderColor(Color.white);
                            consigneecell.setColspan(5);
                            consigneecell.setHorizontalAlignment(1);

                            table.addCell(consigneecell);

                            Cell fclcell = new Cell(new Phrase((String) rstwo.getString("service_term_code") + "", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            fclcell.setBorderColor(Color.white);
                            fclcell.setHorizontalAlignment(1);
                            table.addCell(fclcell);

                            Cell sl1 = new Cell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            sl1.setBorderColor(Color.white);
                            sl1.setColspan(4);
                            table.addCell(sl1);

                            Cell sl = new Cell(new Phrase("\n" + "", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                            sl.setBorderColor(Color.white);
                            sl.setColspan(4);
                            sl.setHorizontalAlignment(0);
                            table.addCell(sl);
                        }
                        table.addCell(lincell);
                    }
                    PdfPTable remark = new PdfPTable(2);

                    PdfPCell remarkcell = new PdfPCell(new Phrase("REMARKS :", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    remarkcell.setBorderColor(Color.white);
                    remark.addCell(remarkcell);
                    remarkcell = new PdfPCell(new Phrase("vvk", new Font(Font.COURIER, 10, Font.NORMAL)));

                    remarkcell.setBorderColor(Color.white);
                    remark.addCell(remarkcell);

                    table4.addCell(remark);

                    table.setBorderColor(Color.white);
                    table.setWidth(100);

                    document.add(table);
                }
                document.close();  // end of dispVec for loop
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (rstwo != null) {
                    rstwo.close();
                }
                if (rsfive != null) {
                    rsfive.close();
                }
                if (rsfour != null) {
                    rsfour.close();
                }
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
        return jobNum;
    }//end writeUCMpdf
}
