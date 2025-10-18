package com.finops.report.legacy;

import com.finops.freight.bean.ContainerBean;
import com.finops.util.Database;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Author : Ranjitha Description : This class is used to generate PDF document
 * for Daily Records that are to be sent to users who are registered for the DSR
 * .
 *
 *
 */
public class CLPPDF {

    static String path = "D:/";
    static String testSystemID;
    static boolean isTest;

    /*
     * Method 	: 	This method generates PDF report documents from UCM database.
     * The generated PDF documents are stored in the directory specified in Prop file.
     * Input 	: 	None.
     * Output	:	None.
     */
    public static String writeUCMpdf(String loadPlanNo, ContainerBean containerBean,
                                     final String pdfFolderPath , JdbcTemplate jdbcTemplate) {
        ResultSet rs = null;
        ResultSet rstwo = null;
        ResultSet rsthree = null;
        ResultSet rsfour = null;
        String lAgent = containerBean.getLoadingAgent();
        String userName = containerBean.getUserId();
        Connection connection = null;

        try {
            boolean flag_data = false;
            StringBuilder dispQuery = new StringBuilder();
            StringBuilder dispQueryTwo = new StringBuilder();
            StringBuilder dispQueryThree = new StringBuilder();
            StringBuilder dispQueryFour = new StringBuilder();
            connection = jdbcTemplate.getDataSource().getConnection();

            dispQuery.append("SELECT lp.VSL, lp.VOY, lp.CARRIER_CODE,PA.DESCRIPTION1"
                    + ",CONVERT(LOAD_PLAN_DT,DATE)LOAD_PLAN_DT "
                    + ",lp.POL,PORT.DESCRIPTION1 PDES"
                    + ",CONVERT(ACT_LOADING_DT,DATE)ACT_LOADING_DT"
                    + ",lp.LOAD_METHOD"
                    + ",lp.LOAD_PLAN_NO,lp.LOADING_AGNT"
                    + ", lp.POD,PORT1.DESCRIPTION1 PDESC"
                    + ",lp.WAREHSE,PA1.DESCRIPTION1 WHD"
                    + ",lp.CONTNR_NO,lp.SIZE_CODE,lp.LINE_SEAL_NUM"
                    + ",s.DESCRIPTION sDESCRIPTION"
                    + ",lp.REMARKS,lp.CUSTM_SEAL_NO,lp.SERVICE_TERM_CODE"
                    + ",CONVERT(CFS_DATE,DATE)CFS_DATE "
                    + ",CONVERT(CY_DATE,DATE)CY_DATE "
                    + ",lp.CREATED_BY"
                    + ",CONVERT(lp.CREATION_DATE,DATE)CREATION_DATE "
                    + ",lp.AMENDED_BY"
                    + ",CONVERT(lp.AMENDED_DATE,DATE)AMENDED_DATE"
                    + " FROM ((((LOAD_PLAN_HDR_F  lp LEFT OUTER JOIN PORT_D PORT  ON (lp.POL = PORT.PORT_CODE ))"
                    + " LEFT OUTER JOIN PORT_D PORT1  ON (lp.POD = PORT1.PORT_CODE ))"
                    + " LEFT OUTER JOIN PARTNER_ACCOUNT_D PA  ON (lp.CARRIER_CODE = PA.PARTNER_CODE ))"
                    + " LEFT OUTER JOIN PARTNER_ACCOUNT_D PA1  ON (lp.WAREHSE = PA1.PARTNER_CODE ))"
                    + " LEFT OUTER JOIN SIZE_D S  "
                    + " ON (lp.SIZE_CODE = s.SIZE_CODE )"
                    + " WHERE LOAD_PLAN_NO =" + loadPlanNo + "");

            dispQueryTwo.append("SELECT so.SO_NUMBER, so.load_plan_no"
                    + ",so.BKG_REF_NO,so.LOAD_PLAN_SEQ_NO"
                    + ",so.POL,so.POD,so.CARRIER_CODE"
                    + ",so.CARGO_RECEIVE_DATE,so.ORIGN_WH"
                    + ",so.dest,so.VSL,so.VOY,so.LOADING_AGNT"
                    + ",so.SHPR,SO.DEST,so.CNEE,cnee.DESCRIPTION1 CD"
                    + ",SHPR.DESCRIPTION1 SD,PORT.DESCRIPTION1,lcl.ACT_QTY"
                    + ",lcl.ACT_UNIT,lcl.ACT_GROSS_WT,lcl.ACT_NET_KGS,lcl.ACT_CBM"
                    + " FROM ((((SO_HDR_F SO INNER JOIN so_fcl_lcl_f lcl ON (SO.BKG_REF_NO = lcl.BKG_REF_NO))"
                    + " LEFT OUTER JOIN(SELECT CNEE.PARTNER_CODE,CNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CNEE WHERE CNEE.LOADNG_AGNT = '" + lAgent + "' AND CNEE.STATUS = 'A') CNEE  ON (SO.CNEE = CNEE.PARTNER_CODE))"
                    + " LEFT OUTER JOIN (SELECT SHPR.PARTNER_CODE,SHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D SHPR WHERE SHPR.LOADNG_AGNT = '" + lAgent + "' AND SHPR.STATUS = 'A') SHPR ON (SO.SHPR = SHPR.PARTNER_CODE ))"
                    + " LEFT OUTER JOIN PORT_D PORT  ON (SO.DEST = PORT.PORT_CODE ))"
                    + " WHERE so.so_number<>0 AND so.load_plan_no =" + loadPlanNo + ""
                    + " AND ACT_QTY>0 AND ACT_GROSS_WT>0 AND ACT_NET_KGS>0"
                    + " AND ACT_CBM>0 AND CARGO_RECEIVE_DATE is not null");

            dispQueryThree.append("select DESCRIPTION1 from partner_account_d where PARTNER_CODE='" + lAgent + "' ");

            dispQueryFour.append("SELECT DESCRIPTION FROM user_d WHERE USER_ID = '" + userName + "' ");

            try {
                rs = Database.query(dispQuery.toString(), connection);

                rstwo = Database.query(dispQueryTwo.toString(), connection);

                rsthree = Database.query(dispQueryThree.toString(), connection);
                rsfour = Database.query(dispQueryFour.toString(), connection);

            } catch (Exception e) {
                e.printStackTrace();
            }

            flag_data = true;
            if (flag_data) {
                Document document = new Document(PageSize.A4, 30, 30, 60, 70);

                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFolderPath + "/" + loadPlanNo + ".pdf"));

                final Rectangle page = document.getPageSize();

                final PdfPTable headtab = new PdfPTable(7);

                PdfPCell hcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
                hcell.setHorizontalAlignment(10);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("S/O NO\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
                hcell.setHorizontalAlignment(0);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("SHIPPER\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
                hcell.setHorizontalAlignment(10);
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("A.QTY\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
                hcell.setHorizontalAlignment(2);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("PKS\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
                hcell.setHorizontalAlignment(2);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("KGS\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
                hcell.setHorizontalAlignment(2);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("CBM\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
                hcell.setHorizontalAlignment(2);

                headtab.addCell(hcell);

                final int hwidths[] = {5, 25, 40, 15, 12, 13, 10};

                headtab.setWidths(hwidths);

                String Desc1 = null;
                String lp = "CONTAINER LOAD PLAN";
                while (rsthree.next()) {
                    Desc1 = rsthree.getString("DESCRIPTION1");

                }

                String user = null;
                while (rsfour.next()) {
                    user = rsfour.getString("DESCRIPTION");

                }

                final PdfPTable sfpvtltd = new PdfPTable(1);

                PdfPCell sfCell = new PdfPCell(new Phrase(Desc1, FontFactory.getFont(FontFactory.HELVETICA, 20, Font.BOLD, new Color(0x00, 0x00, 0x00))));
                sfCell.setBorderColor(Color.white);
                sfCell.setHorizontalAlignment(1);

                sfpvtltd.addCell(sfCell);
                sfCell = new PdfPCell(new Phrase(lp, new Font(Font.HELVETICA, 15, Font.NORMAL)));
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

                Table table = new Table(7);
                PdfPTable table1 = new PdfPTable(2);

                sfpvtltd.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                try {
                    sfpvtltd.setWidths(hwidths);
                } catch (DocumentException e) {
                }
                sfpvtltd.writeSelectedRows(0, -1, document.leftMargin(), 830, writer.getDirectContent());

                rs.next();

                PdfPTable loadplan = new PdfPTable(2);//new pdf table
                PdfPCell tab2cell = null;

                PdfPCell loadplancell = new PdfPCell(new Phrase("LOAD PLAN NO :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                loadplancell.setBorderColor(Color.white);

                loadplan.addCell(loadplancell);
                loadplancell = new PdfPCell(new Phrase((String) rs.getString("LOADING_AGNT") + "/" + (String) rs.getString("LOAD_PLAN_NO"), new Font(Font.HELVETICA, 9, Font.NORMAL)));

                loadplancell.setBorderColor(Color.white);

                loadplan.addCell(loadplancell);

                loadplancell = new PdfPCell(new Phrase("VESSEL/VOY :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                loadplancell.setBorderColor(Color.white);
                loadplan.addCell(loadplancell);

                loadplancell = new PdfPCell(new Phrase((String) rs.getString("VSL") + "/" + (String) rs.getString("VOY"), new Font(Font.HELVETICA, 9, Font.NORMAL)));
                loadplancell.setBorderColor(Color.white);
                loadplan.addCell(loadplancell);
                loadplancell = new PdfPCell(new Phrase("CARRIER :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                loadplancell.setBorderColor(Color.white);
                loadplan.addCell(loadplancell);

                loadplancell = new PdfPCell(new Phrase((String) rs.getString("DESCRIPTION1"), new Font(Font.HELVETICA, 9, Font.NORMAL)));
                loadplancell.setBorderColor(Color.white);
                loadplan.addCell(loadplancell);

                loadplancell = new PdfPCell(new Phrase("WAREHOUSE :\n\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                loadplancell.setBorderColor(Color.white);
                loadplan.addCell(loadplancell);

                loadplancell = new PdfPCell(new Phrase((String) rs.getString("WHD"), new Font(Font.HELVETICA, 9, Font.NORMAL)));
                loadplancell.setBorderColor(Color.white);
                loadplan.addCell(loadplancell);

                float[] loadWidths = {30, 70};
                loadplan.setWidths(loadWidths);

                tab2cell = new PdfPCell(loadplan);
                tab2cell.setHorizontalAlignment(0);
                table1.addCell(tab2cell);

                PdfPTable actualdate = new PdfPTable(2);//new pdf table
                PdfPCell tab3cell = null;

                PdfPCell actualdatecell = new PdfPCell(new Phrase("LOAD PLAN DATE :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                actualdatecell.setBorderColor(Color.white);
                actualdate.addCell(actualdatecell);
                actualdatecell = new PdfPCell(new Phrase((String) rs.getString("LOAD_PLAN_DT"), new Font(Font.HELVETICA, 9, Font.NORMAL)));

                actualdatecell.setBorderColor(Color.white);
                //actualdatecell.setHorizontalAlignment(1);
                actualdate.addCell(actualdatecell);

                actualdatecell = new PdfPCell(new Phrase("ACT STUFFING DATE :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                actualdatecell.setBorderColor(Color.white);
                actualdate.addCell(actualdatecell);
                //actualdatecell.setHorizontalAlignment(2);
                actualdatecell = new PdfPCell(new Phrase((String) rs.getString("ACT_LOADING_DT"), new Font(Font.HELVETICA, 9, Font.NORMAL)));

                actualdatecell.setBorderColor(Color.white);
                //actualdatecell.setHorizontalAlignment(1);
                actualdate.addCell(actualdatecell);

                actualdatecell = new PdfPCell(new Phrase("POL :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                actualdatecell.setBorderColor(Color.white);
                actualdate.addCell(actualdatecell);

                actualdatecell = new PdfPCell(new Phrase((String) rs.getString("PDES") + "\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                actualdatecell.setBorderColor(Color.white);
                actualdate.addCell(actualdatecell);

                actualdatecell = new PdfPCell(new Phrase("POD :\n\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                actualdatecell.setBorderColor(Color.white);
                actualdate.addCell(actualdatecell);

                actualdatecell = new PdfPCell(new Phrase((String) rs.getString("PDESC") + "\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                actualdatecell.setBorderColor(Color.white);
                actualdate.addCell(actualdatecell);
                actualdatecell = new PdfPCell(new Phrase("PRINTED BY :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                actualdatecell.setBorderColor(Color.white);
                actualdate.addCell(actualdatecell);
                float[] actualDTWidths = {58, 42};
                actualdate.setWidths(actualDTWidths);

                tab3cell = new PdfPCell(actualdate);
                tab3cell.setHorizontalAlignment(0);
                table1.addCell(tab3cell);

                PdfPTable cydate = new PdfPTable(2);//new pdf table
                PdfPCell tab4cell = null;

                PdfPCell cydatecell = new PdfPCell(new Phrase("ETD DATE :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                cydatecell.setBorderColor(Color.white);
                cydate.addCell(cydatecell);
                cydatecell = new PdfPCell(new Phrase((String) rs.getString("CFS_DATE"), new Font(Font.HELVETICA, 9, Font.NORMAL)));

                cydatecell.setBorderColor(Color.white);
                cydate.addCell(cydatecell);
                cydatecell = new PdfPCell(new Phrase("ETA DATE :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                cydatecell.setBorderColor(Color.white);
                cydate.addCell(cydatecell);

                cydatecell = new PdfPCell(new Phrase((String) rs.getString("CY_DATE"), new Font(Font.HELVETICA, 9, Font.NORMAL)));
                cydatecell.setBorderColor(Color.white);
                cydate.addCell(cydatecell);
                cydatecell = new PdfPCell(new Phrase("LOAD METHOD :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                cydatecell.setBorderColor(Color.white);
                cydate.addCell(cydatecell);

                String lm = (String) rs.getString("LOAD_METHOD");
                if (lm.equals("H")) {
                    lm = "HEAD LOAD";
                } else if (lm.equals("F")) {
                    lm = "FULL LOAD";
                } else if (lm.equals("T")) {
                    lm = "TAIL LOAD";
                } else if (lm.equals("M")) {
                    lm = "MIDDLE LOAD";
                } else {
                    lm = "NIL";
                }

                cydatecell = new PdfPCell(new Phrase(lm, new Font(Font.HELVETICA, 9, Font.NORMAL)));
                cydatecell.setBorderColor(Color.white);
                cydate.addCell(cydatecell);

                cydatecell = new PdfPCell(new Phrase("LINE SEAL NO :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                cydatecell.setBorderColor(Color.white);
                cydate.addCell(cydatecell);
                cydatecell = new PdfPCell(new Phrase((String) rs.getString("LINE_SEAL_NUM"), new Font(Font.HELVETICA, 9, Font.NORMAL)));

                cydatecell.setBorderColor(Color.white);
                cydate.addCell(cydatecell);
                cydate.setWidths(loadWidths);
                tab4cell = new PdfPCell(cydate);
                table1.addCell(tab4cell);

                PdfPTable containerno = new PdfPTable(2);//new pdf table
                PdfPCell tab5 = null;

                PdfPCell containernocell = new PdfPCell(new Phrase("CONTAINER NO :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                containernocell.setBorderColor(Color.white);
                containerno.addCell(containernocell);
                containernocell = new PdfPCell(new Phrase((String) rs.getString("CONTNR_NO"), new Font(Font.HELVETICA, 9, Font.NORMAL)));

                containernocell.setBorderColor(Color.white);
                containerno.addCell(containernocell);
                containernocell = new PdfPCell(new Phrase("SIZE :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                containernocell.setBorderColor(Color.white);
                containerno.addCell(containernocell);

                containernocell = new PdfPCell(new Phrase((String) rs.getString("sDESCRIPTION"), new Font(Font.HELVETICA, 9, Font.NORMAL)));
                containernocell.setBorderColor(Color.white);
                containerno.addCell(containernocell);
                containernocell = new PdfPCell(new Phrase("CUSTUM SEAL NO :", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                containernocell.setBorderColor(Color.white);
                containerno.addCell(containernocell);

                containernocell = new PdfPCell(new Phrase((String) rs.getString("CUSTM_SEAL_NO"), new Font(Font.HELVETICA, 9, Font.NORMAL)));
                containernocell.setBorderColor(Color.white);
                containerno.addCell(containernocell);
                containernocell = new PdfPCell(new Phrase("PRINTED BY:", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                containernocell.setBorderColor(Color.white);
                containerno.addCell(containernocell);

                containernocell = new PdfPCell(new Phrase(user, new Font(Font.HELVETICA, 9, Font.NORMAL)));
                containernocell.setBorderColor(Color.white);
                containerno.addCell(containernocell);

                containerno.setWidths(actualDTWidths);
                tab5 = new PdfPCell(containerno);
                table1.addCell(tab5);

                float[] t1widths = {60, 40};
                table1.setWidths(t1widths);

                table1.setWidthPercentage(100);//This is used to add the table1 to document
                document.add(table1);

                PdfPTable remark = new PdfPTable(2);

                PdfPCell remarkcell = new PdfPCell(new Phrase("REMARKS :", new Font(Font.HELVETICA, 9, Font.BOLD)));
                remarkcell.setBorderColor(Color.white);
                remark.addCell(remarkcell);

                remarkcell = new PdfPCell(new Phrase((String) rs.getString("REMARKS"), new Font(Font.COURIER, 8, Font.NORMAL)));
                remarkcell.setBorderColor(Color.white);
                remark.addCell(remarkcell);

                float[] rmwidths = {15, 85};
                remark.setWidths(rmwidths);
                remark.setWidthPercentage(100);
                document.add(remark);

                Cell coutcell = new Cell(new Phrase("\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                coutcell.setBorderColor(Color.white);
                coutcell.setHorizontalAlignment(1);
                table.addCell(coutcell);

                Cell sonumbercell = new Cell(new Phrase("S/O NO.\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                sonumbercell.setBorderColor(Color.white);

                sonumbercell.setHorizontalAlignment(0);

                table.addCell(sonumbercell);

                Cell shippercell = new Cell(new Phrase("SHIPPER\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                shippercell.setBorderColor(Color.white);
                shippercell.setHorizontalAlignment(10);

                table.addCell(shippercell);

                Cell actualqtycell = new Cell(new Phrase("A.QTY\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                actualqtycell.setBorderColor(Color.white);
                actualqtycell.setHorizontalAlignment(2);

                table.addCell(actualqtycell);

                Cell actualunitcell = new Cell(new Phrase("PKS\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                actualunitcell.setBorderColor(Color.white);
                actualunitcell.setHorizontalAlignment(2);
                table.addCell(actualunitcell);

                Cell actualGwtcell = new Cell(new Phrase("KGS\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                actualGwtcell.setBorderColor(Color.white);
                actualGwtcell.setHorizontalAlignment(2);
                table.addCell(actualGwtcell);

                Cell actualcbmcell = new Cell(new Phrase("CBM\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                actualcbmcell.setBorderColor(Color.white);
                actualcbmcell.setHorizontalAlignment(2);
                table.addCell(actualcbmcell);

                String line = "_________________________________________________________________________________________________________________________________________";
                
                Cell linescell = new Cell(new Phrase(line, new Font(Font.HELVETICA, 7, Font.NORMAL)));
                linescell.setBorderColor(Color.white);
                linescell.setColspan(7);

                table.addCell(linescell);

                int count = 0;
                int totalctn = 0;

                double totalGwt = 0;
                double totalCbm = 0;
                while (rstwo.next()) {

                    count++;
                    String loadingAgent = (String) rstwo.getString("LOADING_AGNT");
                    String pod = (String) rstwo.getString("POD");
                    String soNumber = (String) rstwo.getString("SO_NUMBER");

                    String shpr = (String) rstwo.getString("SD");
                    String cnee = (String) rstwo.getString("CD");
                    String actualQty = (String) rstwo.getString("ACT_QTY");
                    String actualUnit = (String) rstwo.getString("ACT_UNIT");
                    String actualGWT = (String) rstwo.getString("ACT_GROSS_WT");
                    String actualCbm = (String) rstwo.getString("ACT_CBM");

                    totalctn = totalctn + Integer.parseInt(actualQty);

                    totalGwt = totalGwt + Double.parseDouble(actualGWT);

                    totalCbm = totalCbm + Double.parseDouble(actualCbm);

                    coutcell = new Cell(new Phrase(count + ".", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                    coutcell.setBorderColor(Color.white);
                    table.addCell(coutcell);

                    sonumbercell = new Cell(new Phrase(loadingAgent + "/" + pod + "/" + soNumber, new Font(Font.HELVETICA, 9, Font.NORMAL)));
                    sonumbercell.setBorderColor(Color.white);
                    sonumbercell.setHorizontalAlignment(0);

                    table.addCell(sonumbercell);

                    shippercell = new Cell(new Phrase("S/" + shpr + "\n" + "C/" + cnee, new Font(Font.HELVETICA, 9, Font.NORMAL)));
                    shippercell.setBorderColor(Color.white);

                    table.addCell(shippercell);

                    actualqtycell = new Cell(new Phrase(actualQty, new Font(Font.HELVETICA, 9, Font.NORMAL)));
                    actualqtycell.setBorderColor(Color.white);

                    actualqtycell.setHorizontalAlignment(2);

                    table.addCell(actualqtycell);

                    actualunitcell = new Cell(new Phrase(actualUnit, new Font(Font.HELVETICA, 9, Font.NORMAL)));
                    actualunitcell.setBorderColor(Color.white);
                    actualunitcell.setHorizontalAlignment(2);

                    table.addCell(actualunitcell);

                    actualGwtcell = new Cell(new Phrase(actualGWT, new Font(Font.HELVETICA, 9, Font.NORMAL)));
                    actualGwtcell.setBorderColor(Color.white);

                    actualGwtcell.setHorizontalAlignment(2);
                    table.addCell(actualGwtcell);

                    actualcbmcell = new Cell(new Phrase(actualCbm, new Font(Font.HELVETICA, 9, Font.NORMAL)));
                    actualcbmcell.setBorderColor(Color.white);
                    actualcbmcell.setHorizontalAlignment(2);
                    table.addCell(actualcbmcell);

                    Cell linecell = new Cell(new Phrase(line, new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    linecell.setBorderColor(Color.white);
                    linecell.setColspan(7);
                    table.addCell(linecell);

                }

                Double tgwt = totalGwt;
                Double tcbm = totalCbm;

                Cell totalbk1 = new Cell(new Phrase("\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                totalbk1.setBorderColor(Color.white);
                table.addCell(totalbk1);

                Cell totalbk2 = new Cell(new Phrase("\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                totalbk2.setBorderColor(Color.white);
                table.addCell(totalbk2);

                Cell totalSocell = new Cell(new Phrase("   TOTAL   " + count + "  S/O  " + "\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                totalSocell.setBorderColor(Color.white);
                table.addCell(totalSocell);

                Cell totalcell = new Cell(new Phrase(totalctn + "", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                totalcell.setBorderColor(Color.white);
                totalcell.setHorizontalAlignment(2);
                table.addCell(totalcell);

                Cell totalunitcell = new Cell(new Phrase("PKGS", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                totalunitcell.setBorderColor(Color.white);
                totalunitcell.setHorizontalAlignment(2);
                table.addCell(totalunitcell);

                Cell totalGwtcell = new Cell(new Phrase(tgwt.floatValue() + "", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                totalGwtcell.setBorderColor(Color.white);
                totalGwtcell.setHorizontalAlignment(2);
                table.addCell(totalGwtcell);

                Cell totalCbmcell = new Cell(new Phrase(tcbm.floatValue() + "", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                totalCbmcell.setBorderColor(Color.white);
                totalCbmcell.setHorizontalAlignment(2);
                table.addCell(totalCbmcell);

                int[] widths = {5, 25, 40, 15, 12, 13, 10};
                
                table.setBorderColor(Color.white);
                table.setWidth(100);
                table.setWidths(widths);

                document.add(table);

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
                if (rsthree != null) {
                    rsthree.close();
                } 
                if (rsfour != null) {
                    rsfour.close();
                }
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
        return loadPlanNo;
    }
}
