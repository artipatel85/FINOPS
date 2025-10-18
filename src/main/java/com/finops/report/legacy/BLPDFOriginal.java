package com.finops.report.legacy;

import com.finops.freight.bean.BLBean;
import com.finops.util.ApplicationUtil;
import com.finops.util.Database;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BLPDFOriginal {

    static String path = "D:/";
    static String testSystemID;
    static boolean isTest;
    static String contents = "Taken in charge in apparently good condition heriein at the"
            + "place of receipt for transport and delivery as mentioned above, unless otherwise"
            + "stated. The MTO in accordance with the provisions contained in th MTD undertakes to perform"
            + "or to procure the performance of the multimodal transport from the place at which the goods"
            + "are taken in change, to the place designated for delivery and assumes responsibility for such transport."
            + "\nOne of the MTD(s) must be surredered duly endorsed in exchange for the goods. In witness"
            + "where of the original MTD all of this tenor and date have been signed in the number"
            + "inidcated below one of which being accomplished the others(s) to be void";

    /*
     * Method 	: 	This method generates PDF report documents from UCM database.
     * The generated PDF documents are stored in the directory specified in Prop file.
     * Input 	: 	None.
     * Output	:	None.
     */
    public static String writeUCMpdf(String blNo, BLBean blBean, JdbcTemplate jdbcTemplate) {
        ResultSet rs = null;
        ResultSet rstwo = null;
        ResultSet rsfour = null;
        ResultSet rsfive = null;
        ResultSet rssix = null;
        String billNo = null;
        int rand = new Random().nextInt();
        Connection connection = null;
        String pdfFolderPath = blBean.getLegacyReportPath();

        try {
            boolean flag_data = false;
            connection = jdbcTemplate.getDataSource().getConnection();
            StringBuilder dispQuery = new StringBuilder();
            StringBuilder dispQueryTwo = new StringBuilder();
            StringBuilder dispQueryThree = new StringBuilder();
            StringBuilder dispQueryFour = new StringBuilder();
            StringBuilder dispQueryFive = new StringBuilder();
            StringBuilder dispQuerySix = new StringBuilder();

            dispQuery.append("SELECT "
                    + " BL.BL_NO "
                    + ",BL.NO_OF_ORIGINAL"
                    + ",BL.BKG_REF_NO"
                    + ",STR_TO_DATE(BL.BL_ISSUE_DATE,'%Y-%m-%d') BL_ISSUE_DATE"
                    + ",BL.JOB_NUMBER"
                    + ",BL.CARGO_TYPE"
                    + ",BL.NO_OF_ORIGINAL"
                    + ",BL.ACT_SHPR"
                    + ",BL.ACT_CNEE"
                    + ",BL.BKG_TYPE"
                    + ",BL.SHPR"
                    + ",BL.SHPR_CONTCT_DTLS"
                    + ",BL.CNEE"
                    + ",BL.CNEE_CONTCT_DTLS"
                    + ",BL.BL_NOTIFY"
                    + ",NOTIFY.DESCRIPTION1 NOTIFYDESCRIPTION1"
                    + ",BL.BL_NOTIFY_CONTCT_DTLS"
                    + ",BL.ALSO_NOTIFY"
                    + ",ANOTIFY.DESCRIPTION1 ANOTIFYDESCRIPTION1"
                    + ",BL.ALSO_NOTIFY_CONTCT_DTLS"
                    + ",BL.POR"
                    + ",BL.POR_NAME"
                    + ",BL.POL"
                    + ",BL.POL_NAME"
                    + ",BL.FROM_FREIGHTAGE_CODE"
                    + ",BL.FROM_FREIGHTAGE_NAME"
                    + ",BL.POD"
                    + ",BL.POD_NAME"
                    + ",BL.DEST"
                    + ",BL.DEST_NAME"
                    + ",BL.UPTO_FREIGHTAGE_CODE"
                    + ",BL.UPTO_FREIGHTAGE_NAME"
                    + ",BL.PRE_CARRAIGE"
                    + ",BL.PRE_VOYAGE"
                    + ",BL.VSL"
                    + ",BL.VOY"
                    + ",BL.CARRIER_CODE"
                    + ",CARR.DESCRIPTION1"
                    + ",BL.FREIGHT_TYPE"
                    + ",BL.FREIGHT_PAYABLE_AT"
                    + ",STR_TO_DATE(BL.CARGO_RECEIVE_DATE,'%Y-%m-%d') CARGO_RECEIVE_DATE"
                    + ",BL.ETD"
                    + ",BL.ETA"
                    + ",BL.POI_NAME"
                    + ",BL.LOADING_AGNT"
                    + ",LAGNT.DESCRIPTION1"
                    + ",BL.LOADING_AGNT_CONTCT_DTLS"
                    + ",BL.DEST_NAME"
                    + ",BL.DEST_AGNT"
                    + ",DAGNT.DESCRIPTION1 DAGENTDESCRIPTION1"
                    + ",BL.DEST_AGNT_CONTCT_DTLS"
                    + ",BL.BL_REMARK"
                    + ",BL.QTY_IN_WORDS"
                    + ",ACNEE.DESCRIPTION1"
                    + ",ASHPR.DESCRIPTION1"
                    + ",CNEE.DESCRIPTION1 CNEEDESCRIPTION1"
                    + ",SHPR.DESCRIPTION1 SHPRDESCRIPTION1"
                    + " FROM (((((((((BL_HDR_F BL "
                    + "LEFT OUTER JOIN PARTNER_ACCOUNT_D NOTIFY ON (BL.BL_NOTIFY = NOTIFY.PARTNER_CODE ))"
                    + "LEFT OUTER JOIN PARTNER_ACCOUNT_D ACNEE ON (BL.ACT_CNEE = ACNEE.PARTNER_CODE ))"
                    + "LEFT OUTER JOIN PARTNER_ACCOUNT_D ANOTIFY"
                    + " ON (BL.ALSO_NOTIFY = ANOTIFY.PARTNER_CODE ))"
                    + "LEFT OUTER JOIN PARTNER_ACCOUNT_D ASHPR ON (BL.ACT_SHPR = ASHPR.PARTNER_CODE ))"
                    + "LEFT OUTER JOIN PARTNER_ACCOUNT_D CNEE ON (BL.CNEE = CNEE.PARTNER_CODE))"
                    + "LEFT OUTER JOIN PARTNER_ACCOUNT_D SHPR ON (BL.SHPR = SHPR.PARTNER_CODE ))"
                    + "LEFT OUTER JOIN PARTNER_ACCOUNT_D LAGNT"
                    + " ON (BL.LOADING_AGNT = LAGNT.PARTNER_CODE)"
                    + "LEFT OUTER JOIN PARTNER_ACCOUNT_D DAGNT"
                    + " ON (BL.DEST_AGNT = DAGNT.PARTNER_CODE ))"
                    + "LEFT OUTER JOIN PARTNER_ACCOUNT_D CARR"
                    + " ON (BL.CARRIER_CODE = CARR.PARTNER_CODE)))"
                    + " where BL.BL_NO ='" + blNo + "'");

            dispQueryTwo.append("SELECT sfl.CONTNR_NO,sfl.SERVICE_TERM_CODE,s.DESCRIPTION,sfl.SIZE_CODE,sfl.LINE_SEAL_NO,sum(sfl.ACT_QTY) ACT_QTY,sfl.ACT_UNIT,sum(sfl.ACT_NET_KGS) ACT_NET_KGS," +
                    "sum(sfl.ACT_GROSS_WT) ACT_GROSS_WT,sum(sfl.ACT_CBM) ACT_CBM FROM bl_fcl_lcl_f sfl INNER JOIN bl_hdr_f sh ON (sfl.BL_NO = sh.BL_NO) LEFT OUTER JOIN size_d s ON (sfl.SIZE_CODE=s.SIZE_CODE) "
                    + "WHERE sfl.BL_NO = '" + blNo + "' group by sfl.SERVICE_TERM_CODE,sfl.LINE_SEAL_NO,sfl.ACT_UNIT,sfl.CONTNR_NO,sfl.SIZE_CODE,s.DESCRIPTION");

            dispQueryFive.append("SELECT sfl.MARK_NO,sfl.MARK_DETAILS FROM bl_fcl_lcl_f sfl WHERE sfl.BL_NO = '" + blNo + "'");

            dispQueryThree.append("SELECT distinct (sh.LOADING_AGNT+'/'+sh.POD+'/'+cast(sfl.SO_NUMBER as VARCHAR)) SO_NUMBER FROM bl_fcl_lcl_f sfl INNER JOIN bl_hdr_f sh ON (sfl.BL_NO = sh.BL_NO) WHERE sfl.BL_NO = '" + blNo + "'");

            dispQueryFour.append("SELECT BL_NO,sum(ACT_QTY)ACTUAL_QUANTITY,ACT_UNIT From bl_fcl_lcl_f WHERE BL_NO='" + blNo + "' Group By BL_NO,ACT_UNIT ");

            dispQuerySix.append("SELECT BL_NO,sum(ACT_GROSS_WT)ACTUAL_GROSS,sum(ACT_NET_KGS)ACTUAL_KGS,sum(ACT_CBM)MEASURMENT From bl_fcl_lcl_f WHERE BL_NO='" + blNo + "' Group By BL_NO");

            rs = (ResultSet) Database.query(dispQuery.toString(), connection);

            rstwo = (ResultSet) Database.query(dispQueryTwo.toString(), connection);

            rsfour = (ResultSet) Database.query(dispQueryFour.toString(), connection);
            rsfive = (ResultSet) Database.query(dispQueryFive.toString(), connection);

            rssix = (ResultSet) Database.query(dispQuerySix.toString(), connection);

            flag_data = true;
            if (flag_data) {
                billNo = blNo.substring(blNo.lastIndexOf("/") + 1);
                String address = blBean.getCompanyAddress();
                String addressFormated = "";
                StringTokenizer addressTokens = new StringTokenizer(address, "|");
                while (addressTokens.hasMoreElements()) {
                    addressFormated = addressFormated + addressTokens.nextElement() + "\n";

                }
                Document document = new Document(PageSize.A4, 25, 30, 35, 130);
                //PdfWriter.fitsPage(1);
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFolderPath + "/" + billNo + ".pdf"));

                final Rectangle page = document.getPageSize();

                final PdfPTable headtab = new PdfPTable(6);
                final float[] tabwidths = {14, 16, 34, 12, 12, 12};

                float[] containwidths = {20, 10, 15, 15, 15, 25};
                PdfPCell hdatacell = null;
                PdfPTable headdatatab = new PdfPTable(6);
                PdfPTable containerdatatab = new PdfPTable(6);

                rs.next();

                final String billLadiniNo = ApplicationUtil.checkForNull(rs.getString("BL_NO"));
                String shprDetails = ApplicationUtil.checkForNull(rs.getString("SHPR_CONTCT_DTLS"));
                String cneeDetails = ApplicationUtil.checkForNull(rs.getString("CNEE_CONTCT_DTLS"));
                String notifyAddress = ApplicationUtil.checkForNull(rs.getString("BL_NOTIFY_CONTCT_DTLS"));
                String por = ApplicationUtil.checkForNull(rs.getString("POR_NAME"));
                String pol = ApplicationUtil.checkForNull(rs.getString("POL_NAME"));
                String pod = ApplicationUtil.checkForNull(rs.getString("POD_NAME"));
                String vessel = ApplicationUtil.checkForNull(rs.getString("VSL"));
                String voyage = ApplicationUtil.checkForNull(rs.getString("VOY"));
                String bkgtype = ApplicationUtil.checkForNull(rs.getString("BKG_TYPE"));
                String payableAt = ApplicationUtil.checkForNull(rs.getString("FREIGHT_PAYABLE_AT"));

                String bkgstr = "";
                if (bkgtype.equals("F")) {
                    bkgstr = "SHIPPERS LOAD, WEIGH STOW AND COUNT";
                }
                final String bkgstring = bkgstr;

                String issuePlace = ApplicationUtil.checkForNull(rs.getString("POI_NAME"));
                String issueDate = ApplicationUtil.checkForNull(rs.getString("BL_ISSUE_DATE"));
                String destAddress = ApplicationUtil.checkForNull(rs.getString("DEST_AGNT_CONTCT_DTLS"));
                String destname = ApplicationUtil.checkForNull(rs.getString("DEST_NAME"));
                String precar = ApplicationUtil.checkForNull(rs.getString("PRE_CARRAIGE"));
                String original = ApplicationUtil.checkForNull(rs.getString("NO_OF_ORIGINAL"));
                if (original.equals("1")) {
                    original = "ONE";
                } else if (original.equals("2")) {
                    original = "TWO";
                } else if (original.equals("3")) {
                    original = "THREE";
                } else {
                    original = "SEAWAYBILL";
                }

                String gross = "";
                String cbmw = "";
                int totcount = 0;
                String saidto = "";

                while (rssix.next()) {
                    gross = ApplicationUtil.checkForNull(rssix.getString("ACTUAL_GROSS")) + "\n(KGS)";
                    cbmw = ApplicationUtil.checkForNull(rssix.getString("MEASURMENT")) + "\n(CBM)";
                    saidto = "SAID TO CONTAIN\n" + bkgstring;
                }
                while (rsfour.next()) {
                    String packages = ApplicationUtil.checkForNull(rsfour.getString("ACTUAL_QUANTITY"));
                    String unit = ApplicationUtil.checkForNull(rsfour.getString("ACT_UNIT"));

                    if (totcount > 0) {
                        gross = "";
                        cbmw = "";
                        saidto = "";
                    }

                    hdatacell = new PdfPCell(new Phrase("", new Font(Font.COURIER, 10, Font.NORMAL)));
                    hdatacell.setHorizontalAlignment(0);
                    hdatacell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headdatatab.addCell(hdatacell);

                    hdatacell = new PdfPCell(new Phrase(packages + "\n" + unit, new Font(Font.COURIER, 10, Font.NORMAL)));
                    hdatacell.setHorizontalAlignment(0);
                    hdatacell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headdatatab.addCell(hdatacell);

                    hdatacell = new PdfPCell(new Phrase(saidto, new Font(Font.HELVETICA, 9, Font.NORMAL)));
                    hdatacell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    hdatacell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headdatatab.addCell(hdatacell);

                    hdatacell = new PdfPCell(new Phrase(gross, new Font(Font.COURIER, 10, Font.NORMAL)));
                    hdatacell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    hdatacell.setColspan(2);
                    hdatacell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headdatatab.addCell(hdatacell);

                    hdatacell = new PdfPCell(new Phrase(cbmw, new Font(Font.COURIER, 10, Font.NORMAL)));
                    hdatacell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    hdatacell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headdatatab.addCell(hdatacell);

                    totcount++;
                }

                PdfPCell hcell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.TOP);
                hcell.setHorizontalAlignment(0);
                hcell.setFixedHeight(25);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.TOP);
                hcell.setHorizontalAlignment(0);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.TOP);
                hcell.setHorizontalAlignment(0);
                hcell.setColspan(1);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.TOP);
                hcell.setHorizontalAlignment(2);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.TOP);
                hcell.setHorizontalAlignment(2);
                hcell.setColspan(1);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.TOP);
                hcell.setHorizontalAlignment(2);
                hcell.setColspan(1);
                headtab.addCell(hcell);

                headtab.setWidthPercentage(100);
                headtab.setWidths(tabwidths);
                final int h1widths[] = {12, 10, 40, 20};
                class PageEvents extends PdfPageEventHelper {

                    @Override
                    public void onStartPage(PdfWriter writer, Document document) {

                        document.setMargins(30, 30, 62.5f, 60);
                        if (document.getPageNumber() > 1) {
                            PdfPTable headtab1 = new PdfPTable(4);

                            PdfPCell hcell = new PdfPCell(new Phrase("Attachment No.", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                            hcell.setHorizontalAlignment(0);
                            hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                            headtab1.addCell(hcell);

                            hcell = new PdfPCell(new Phrase(document.getPageNumber() - 1 + "", new Font(Font.COURIER, 10, Font.NORMAL)));
                            hcell.setHorizontalAlignment(Rectangle.LEFT);
                            hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                            headtab1.addCell(hcell);

                            hcell = new PdfPCell(new Phrase("BL/MTD Number", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                            hcell.setHorizontalAlignment(2);
                            hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                            headtab1.addCell(hcell);

                            hcell = new PdfPCell(new Phrase(billLadiniNo + "\n", new Font(Font.COURIER, 10, Font.NORMAL)));
                            hcell.setHorizontalAlignment(1);
                            hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                            headtab1.addCell(hcell);

                            headtab1.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                            headtab.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                            try {
                                headtab1.setWidths(h1widths);
                                headtab.setWidths(tabwidths);
                            } catch (Exception e) {
                            }
                            headtab1.writeSelectedRows(0, -1, document.leftMargin(), 815, writer.getDirectContent());
                            headtab.writeSelectedRows(0, -1, document.leftMargin(), 800, writer.getDirectContent());
                        }
                    }
                }

                writer.setPageEvent(new PageEvents());

                document.open();

                Table totalTable = new Table(1);
                totalTable.setBorderColor(Color.white);

                PdfPTable shipper = new PdfPTable(4);

                PdfPTable shipperhead = new PdfPTable(2);

                PdfPCell shipcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));

                shipcell.disableBorderSide(Rectangle.BOTTOM);
                shipcell.setBorderColor(Color.WHITE);
                shipcell.setFixedHeight(16);
                shipcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                shipcell.setColspan(2);
                shipperhead.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase(shprDetails, new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setBorderColor(Color.WHITE);
                shipcell.setFixedHeight(69);
                shipcell.setColspan(2);
                shipperhead.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.setBorderColor(Color.WHITE);
                shipcell.setColspan(2);
                shipcell.setFixedHeight(16);
                shipcell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM);
                shipcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                shipperhead.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase(cneeDetails, new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setBorderColor(Color.WHITE);
                shipcell.setColspan(2);
                shipcell.setFixedHeight(69);
                shipperhead.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.setColspan(2);
                shipcell.setBorderColor(Color.WHITE);
                shipcell.setFixedHeight(16);
                shipcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                shipcell.disableBorderSide(Rectangle.BOTTOM);
                shipperhead.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase(notifyAddress, new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setColspan(2);
                shipcell.setBorderColor(Color.WHITE);
                shipcell.setFixedHeight(68);
                shipperhead.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setBorderColor(Color.WHITE);
                shipcell.setFixedHeight(23);
                shipperhead.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setBorderColor(Color.WHITE);
                shipperhead.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase(por, new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);

                shipcell.setBorderColor(Color.WHITE);
                shipperhead.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase(pol, new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);

                shipcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                shipcell.setFixedHeight(22);
                shipcell.setBorderColor(Color.WHITE);
                shipperhead.addCell(shipcell);

                PdfPCell shipheadcell = new PdfPCell(shipperhead);
                shipheadcell.setColspan(2);
                shipper.addCell(shipheadcell);

                PdfPTable sfpvtltd = new PdfPTable(2);

                PdfPCell sfCell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));

                sfCell.disableBorderSide(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                sfCell.disableBorderSide(Rectangle.BOTTOM);
                sfCell.setBorderColor(Color.WHITE);
                sfpvtltd.addCell(sfCell);

                sfCell = new PdfPCell(new Phrase("\n " + billLadiniNo, new Font(Font.HELVETICA, 8, Font.NORMAL)));
                sfCell.setVerticalAlignment(Element.ALIGN_TOP);
                sfCell.setBorderColor(Color.WHITE);
                sfCell.disableBorderSide(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                sfpvtltd.addCell(sfCell);

                sfCell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));

                sfCell.disableBorderSide(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                sfCell.disableBorderSide(Rectangle.BOTTOM);
                sfCell.setBorderColor(Color.WHITE);
                sfpvtltd.addCell(sfCell);

                sfCell = new PdfPCell(new Phrase("\n ", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                sfCell.setVerticalAlignment(Element.ALIGN_TOP);
                sfCell.setBorderColor(Color.WHITE);
                sfCell.disableBorderSide(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                sfpvtltd.addCell(sfCell);

                PdfPCell shippvtcell = new PdfPCell(sfpvtltd);
                shippvtcell.setColspan(2);
                shippvtcell.setBorderColor(Color.white);
                shipper.addCell(shippvtcell);

                shipcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.BOTTOM);
                shipcell.setBorderColor(Color.WHITE);
                shipcell.setFixedHeight(19);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.BOTTOM);
                shipcell.setBorderColor(Color.WHITE);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("    \n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.BOTTOM);
                shipcell.setBorderColor(Color.WHITE);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("    \n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.BOTTOM);
                shipcell.setBorderColor(Color.WHITE);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase(pod, new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setBorderColor(Color.WHITE);
                shipcell.setFixedHeight(18);

                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase(destname, new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                shipcell.setBorderColor(Color.WHITE);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase(precar, new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setBorderColor(Color.WHITE);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("", new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setBorderColor(Color.WHITE);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.BOTTOM);
                shipcell.setBorderColor(Color.WHITE);
                shipcell.setFixedHeight(17);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.BOTTOM);
                shipcell.setBorderColor(Color.WHITE);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.BOTTOM);
                shipcell.setBorderColor(Color.WHITE);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.BOTTOM);
                shipcell.setBorderColor(Color.WHITE);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase(vessel + "	" + voyage, new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setBorderColor(Color.WHITE);
                shipcell.setFixedHeight(16);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("", new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setBorderColor(Color.WHITE);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("", new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setBorderColor(Color.WHITE);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("", new Font(Font.COURIER, 10, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.TOP);
                shipcell.setBorderColor(Color.WHITE);
                shipper.addCell(shipcell);

                while (rsfive.next()) {
                    String marknostr = (String) ApplicationUtil.checkForNull(rsfive.getString("MARK_NO"));
                    String markdetstr = (String) ApplicationUtil.checkForNull(rsfive.getString("MARK_DETAILS"));

                    PdfPCell markandno = new PdfPCell(new Phrase("\n" + marknostr + "\n", new Font(Font.COURIER, 10, Font.NORMAL)));
                    markandno.setBorderColor(Color.white);
                    markandno.setHorizontalAlignment(0);
                    markandno.setColspan(2);
                    containerdatatab.addCell(markandno);

                    PdfPCell goodsdescription = new PdfPCell(new Phrase("\n" + markdetstr + "\n", new Font(Font.COURIER, 10, Font.NORMAL)));
                    goodsdescription.setBorderColor(Color.white);
                    goodsdescription.setHorizontalAlignment(0);
                    goodsdescription.setColspan(3);
                    containerdatatab.addCell(goodsdescription);

                    PdfPCell netweight = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 10, Font.NORMAL)));
                    netweight.setBorderColor(Color.white);
                    netweight.setHorizontalAlignment(2);

                    containerdatatab.addCell(netweight);
                }

                PdfPCell containerNo = new PdfPCell(new Phrase("\nCONTAINER NO." + "/SIZE", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                containerNo.setBorderColor(Color.white);
                containerNo.setHorizontalAlignment(0);
                containerdatatab.addCell(containerNo);

                PdfPCell sealNo = new PdfPCell(new Phrase("\nSEAL NO.", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                sealNo.setBorderColor(Color.white);
                sealNo.setHorizontalAlignment(0);
                containerdatatab.addCell(sealNo);

                PdfPCell qty = new PdfPCell(new Phrase("\nQTY" + "/UNIT", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                qty.setBorderColor(Color.white);
                qty.setHorizontalAlignment(2);
                containerdatatab.addCell(qty);

                PdfPCell wtg = new PdfPCell(new Phrase("\nGROSS WT ", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                wtg.setBorderColor(Color.white);
                wtg.setHorizontalAlignment(2);
                containerdatatab.addCell(wtg);

                PdfPCell cbm = new PdfPCell(new Phrase("\nMEASUREMENT", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                cbm.setBorderColor(Color.white);
                cbm.setHorizontalAlignment(2);
                containerdatatab.addCell(cbm);

                PdfPCell terms = new PdfPCell(new Phrase("\nSERVICE TERM", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                terms.setBorderColor(Color.white);
                terms.setHorizontalAlignment(1);
                containerdatatab.addCell(terms);

                while (rstwo.next()) {

                    String weightstr = (String) ApplicationUtil.checkForNull(rstwo.getString("ACT_GROSS_WT"));
                    String measurementstr = (String) ApplicationUtil.checkForNull(rstwo.getString("ACT_CBM"));

                    String bkgqty = (String) ApplicationUtil.checkForNull(rstwo.getString("ACT_QTY"));
                    String bkgunit = (String) ApplicationUtil.checkForNull(rstwo.getString("ACT_UNIT"));
                    String container = (String) ApplicationUtil.checkForNull(rstwo.getString("CONTNR_NO"));
                    String seal = (String) ApplicationUtil.checkForNull(rstwo.getString("LINE_SEAL_NO"));
                    String term = (String) ApplicationUtil.checkForNull(rstwo.getString("SERVICE_TERM_CODE"));
                    String containerSize = (String) ApplicationUtil.checkForNull(rstwo.getString("DESCRIPTION"));

                    containerNo = new PdfPCell(new Phrase(container + "/" + containerSize + "\n", new Font(Font.COURIER, 10, Font.NORMAL)));
                    containerNo.setBorderColor(Color.white);
                    containerNo.setHorizontalAlignment(0);
                    containerdatatab.addCell(containerNo);

                    sealNo = new PdfPCell(new Phrase(seal + "\n", new Font(Font.COURIER, 10, Font.NORMAL)));
                    sealNo.setBorderColor(Color.white);
                    sealNo.setHorizontalAlignment(0);
                    containerdatatab.addCell(sealNo);

                    qty = new PdfPCell(new Phrase(bkgqty + "\n" + bkgunit + "\n", new Font(Font.COURIER, 10, Font.NORMAL)));
                    qty.setBorderColor(Color.white);
                    qty.setHorizontalAlignment(2);
                    containerdatatab.addCell(qty);

                    wtg = new PdfPCell(new Phrase(weightstr + "\n(KGS)\n", new Font(Font.COURIER, 10, Font.NORMAL)));
                    wtg.setBorderColor(Color.white);
                    wtg.setHorizontalAlignment(2);
                    containerdatatab.addCell(wtg);

                    cbm = new PdfPCell(new Phrase(measurementstr + "\n(CBM)\n", new Font(Font.COURIER, 10, Font.NORMAL)));
                    cbm.setBorderColor(Color.white);
                    cbm.setHorizontalAlignment(2);
                    containerdatatab.addCell(cbm);

                    terms = new PdfPCell(new Phrase(term + "\n", new Font(Font.COURIER, 10, Font.NORMAL)));
                    terms.setBorderColor(Color.white);
                    terms.setHorizontalAlignment(1);
                    containerdatatab.addCell(terms);

                }

                PdfPTable pageFooter = new PdfPTable(4);
                float[] pfwidths = {35, 20, 25, 20};

                PdfPTable attachedTab = new PdfPTable(6);

                String attchSht = "*** FULL PARTICULARS AS PER ATTACHED SHEET ***";

                PdfPCell attchedCell = new PdfPCell(new Phrase("\n" + attchSht, new Font(Font.COURIER, 10, Font.NORMAL)));
                attchedCell.setHorizontalAlignment(1);
                attchedCell.setBorderColor(Color.white);
                attchedCell.setColspan(6);
                attchedCell.setVerticalAlignment(0);
                attchedCell.setMinimumHeight(260);
                attachedTab.addCell(attchedCell);

                shipper.setWidthPercentage(100);
                document.add(shipper);
                boolean ifFits = true;
                try {
                    ifFits = (containerdatatab.getRows().size() < 5 && ((PdfPRow) containerdatatab.getRows().get(0)).getCells()[2].getPhrase().toString().length() < 550);
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }
                if (!ifFits) {
                    headtab.setWidths(tabwidths);
                    headtab.setWidthPercentage(100);
                    document.add(headtab);

                    attachedTab.setWidthPercentage(100);
                    attachedTab.setWidths(tabwidths);

                    document.add(attachedTab);

                } else {

                    headtab.setWidths(tabwidths);
                    headtab.setWidthPercentage(100);
                    document.add(headtab);

                }

                PdfPCell pfcell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                pfcell.setColspan(4);
                pfcell.setHorizontalAlignment(1);
                pfcell.setBorderColor(Color.WHITE);
                pfcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.RIGHT);
                pageFooter.addCell(pfcell);

                PdfPCell pIcell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.BOLD)));
                pIcell.disableBorderSide(Rectangle.BOTTOM);
                pIcell.setFixedHeight(15);
                pIcell.setBorderColor(Color.WHITE);
                pIcell.setColspan(1);
                pageFooter.addCell(pIcell);

                pIcell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.BOLD)));
                pIcell.disableBorderSide(Rectangle.BOTTOM);
                pIcell.setBorderColor(Color.WHITE);
                pIcell.setColspan(1);
                pageFooter.addCell(pIcell);

                pIcell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.BOLD)));
                pIcell.disableBorderSide(Rectangle.BOTTOM);
                pIcell.setBorderColor(Color.WHITE);
                pIcell.setColspan(1);
                pageFooter.addCell(pIcell);

                pIcell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.BOLD)));
                pIcell.disableBorderSide(Rectangle.BOTTOM);
                pIcell.setBorderColor(Color.WHITE);
                pIcell.setColspan(1);
                pageFooter.addCell(pIcell);

                pIcell = new PdfPCell(new Phrase("", new Font(Font.COURIER, 10, Font.NORMAL)));
                pIcell.disableBorderSide(Rectangle.TOP);
                pIcell.setBorderColor(Color.WHITE);
                pIcell.setColspan(1);
                pageFooter.addCell(pIcell);

                pIcell = new PdfPCell(new Phrase(payableAt, new Font(Font.COURIER, 10, Font.NORMAL)));
                pIcell.disableBorderSide(Rectangle.TOP);
                pIcell.setColspan(1);
                pIcell.setBorderColor(Color.WHITE);
                pIcell.setFixedHeight(10);
                pageFooter.addCell(pIcell);

                pIcell = new PdfPCell(new Phrase(original, new Font(Font.COURIER, 10, Font.NORMAL)));
                pIcell.disableBorderSide(Rectangle.TOP);
                pIcell.setBorderColor(Color.WHITE);
                pIcell.setColspan(1);
                pageFooter.addCell(pIcell);

                pIcell = new PdfPCell(new Phrase(issuePlace + "\n" + issueDate, new Font(Font.COURIER, 10, Font.NORMAL)));
                pIcell.disableBorderSide(Rectangle.TOP);
                pIcell.setColspan(1);
                pIcell.setBorderColor(Color.WHITE);
                pageFooter.addCell(pIcell);

                pIcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                pIcell.disableBorderSide(Rectangle.BOTTOM);
                pIcell.setColspan(2);
                pIcell.setFixedHeight(15);
                pIcell.setBorderColor(Color.WHITE);
                pageFooter.addCell(pIcell);

                pIcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.BOLD)));
                pIcell.disableBorderSide(Rectangle.BOTTOM);
                pIcell.setBorderColor(Color.WHITE);
                pIcell.setColspan(2);
                pageFooter.addCell(pIcell);

                pIcell = new PdfPCell(new Phrase(destAddress, new Font(Font.COURIER, 10, Font.NORMAL)));
                pIcell.disableBorderSide(Rectangle.TOP);
                pIcell.setBorderColor(Color.WHITE);
                pIcell.setColspan(2);
                pIcell.setMinimumHeight(60);
                pIcell.setFixedHeight(60);
                pageFooter.addCell(pIcell);

                pIcell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.BOLD)));
                pIcell.disableBorderSide(Rectangle.TOP);
                pIcell.setBorderColor(Color.WHITE);
                pIcell.setColspan(2);
                pIcell.setFixedHeight(55);
                pIcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                pIcell.setHorizontalAlignment(1);
                pageFooter.addCell(pIcell);

                pageFooter.setWidths(pfwidths);
                pageFooter.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                pageFooter.writeSelectedRows(0, -1, document.leftMargin(), 130, writer.getDirectContent());

                headdatatab.setWidthPercentage(100);
                headdatatab.setWidths(tabwidths);
                document.add(headdatatab);
                containerdatatab.setWidthPercentage(100);
                containerdatatab.setWidths(containwidths);

                document.add(containerdatatab);

                document.close();  // end of dispVec for loop

            }
            // end of flag_data  if
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
                if (rsfour != null) {
                    rsfour.close();
                }
                if (rsfive != null) {
                    rsfive.close();
                }
                if (rssix != null) {
                    rssix.close();
                }
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return billNo+rand;
    }//end writeUCMpdf


}

