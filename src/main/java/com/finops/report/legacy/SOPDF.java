package com.finops.report.legacy;

import com.finops.admin.model.LoginBean;
import com.finops.freight.bean.SOBean;
import com.finops.util.ApplicationUtil;
import com.finops.util.Database;
import com.finops.util.DateUtil;
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
import java.util.Iterator;

public class SOPDF {

    /*
     * Method 	: 	This method generates PDF report documents from UCM database.
     * The generated PDF documents are stored in the directory specified in Prop file.
     * Input 	: 	None.
     * Output	:	None.
     */
    public static int writeUCMpdf(int bookingNum, SOBean bean, JdbcTemplate jdbcTemplate) {
        ResultSet rs = null;
        ResultSet rstwo = null;
        ResultSet rsthree = null;
        ResultSet rsfour = null;
        ResultSet rsfive = null;
        String branch = bean.getLoadingAgent();
        Connection connection = null;
        String pdfFolderPath = bean.getLegacyReportPath();

        try {
            boolean flag_data = false;
            connection = jdbcTemplate.getDataSource().getConnection();
            StringBuilder dispQuery = new StringBuilder();
            StringBuilder dispQueryTwo = new StringBuilder();
            StringBuilder dispQueryThree = new StringBuilder();
            StringBuilder dispQueryFour = new StringBuilder();
            StringBuilder dispQueryFive = new StringBuilder();

            dispQuery.append("SELECT SO.SO_NUMBER,SO.BKG_REF_NO,SO.SHPR,SHPR.DESCRIPTION1 SHPRDESC"
                    + ",SO.SHPR_CONTCT_DTLS,SO.CNEE,CNEE.DESCRIPTION1 CNEEDESC,SO.COMM_INV_NUMBER,SO.VSL,SO.VOY, "
                    + "SO.CNEE_CONTCT_DTLS,SO.LOADING_AGNT,LOADING.DESCRIPTION1,SO.NO_OF_ORIGINAL "
                    + ",SO.LOADING_AGNT_CONTCT_DTLS,SO.SO_NOTIFY,NOTIFY.DESCRIPTION1 NOTIFYDESC,SO.POR_NAME ,SO.POL_NAME"
                    + ",SO.SO_NOTIFY_CONTCT_DTLS,SO.ALSO_NOTIFY_CONTCT_DTLS,SO.SO_REMARK,SO.ORIGN_WH,OWH.DESCRIPTION1,SO.POD_NAME,SO.DEST_NAME "
                    + ",SO.ORIGN_WH_CONTCT_DTLS,SO.POR,SO.POL,SO.POD,So.FREIGHT_PAYABLE_AT,SO.SO_REMARK "
                    + "FROM (((((SO_HDR_F SO "
                    + "LEFT OUTER JOIN (SELECT NOTIFY.PARTNER_CODE,NOTIFY.DESCRIPTION1 FROM PARTNER_ACCOUNT_D NOTIFY WHERE NOTIFY.LOADNG_AGNT = '" + branch + "' AND NOTIFY.STATUS = 'A') NOTIFY ON (SO.SO_NOTIFY = NOTIFY.PARTNER_CODE ))"
                    + "LEFT OUTER JOIN (SELECT LOADING.PARTNER_CODE,LOADING.DESCRIPTION1 FROM PARTNER_ACCOUNT_D LOADING WHERE LOADING.LOADNG_AGNT = '" + branch + "' AND LOADING.STATUS = 'A') LOADING ON (SO.LOADING_AGNT = LOADING.PARTNER_CODE ))"
                    + "LEFT OUTER JOIN (SELECT CNEE.PARTNER_CODE,CNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CNEE WHERE CNEE.LOADNG_AGNT = '" + branch + "' AND CNEE.STATUS = 'A') CNEE ON (SO.CNEE = CNEE.PARTNER_CODE ))"
                    + "LEFT OUTER JOIN (SELECT SHPR.PARTNER_CODE,SHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D SHPR WHERE SHPR.LOADNG_AGNT = '" + branch + "' AND SHPR.STATUS = 'A') SHPR ON (SO.SHPR = SHPR.PARTNER_CODE ))"
                    + "LEFT OUTER JOIN (SELECT OWH.PARTNER_CODE,OWH.DESCRIPTION1 FROM PARTNER_ACCOUNT_D OWH WHERE OWH.LOADNG_AGNT = '" + branch + "' AND OWH.STATUS = 'A') OWH ON (SO.ORIGN_WH = OWH.PARTNER_CODE ))"
                    + "WHERE SO.BKG_REF_NO='" + bookingNum + "'");

            dispQueryTwo.append("SELECT sofl.CONTNR_NO"
                    + ",s.DESCRIPTION sDESCRIPTION"
                    + ",sofl.LINE_SEAL_NO"
                    + ",sofl.BKG_QTY"
                    + ",sofl.BKG_UNIT"
                    + ",sofl.MARK_NO"
                    + ",sofl.MARK_DETAILS"
                    + ",sofl.BKG_CBM"
                    + ",sofl.BKG_KGS_GROSS_WT"
                    + ",sofl.ACT_QTY"
                    + ",sofl.ACT_UNIT"
                    + ",sofl.ACT_NET_KGS"
                    + ",sofl.ACT_CBM "
                    + "FROM so_fcl_lcl_f sofl "
                    + "LEFT OUTER JOIN size_d s "
                    + "ON (sofl.SIZE_CODE = s.SIZE_CODE)"
                    + "WHERE BKG_REF_NO ='" + bookingNum + "'");

            dispQueryThree.append("select DESCRIPTION1,TEL_NO,WEB,FAX_NO,EMAIL from partner_account_d where PARTNER_CODE='" + branch + "' and loadng_agnt='" + branch + "'");

            dispQueryFour.append("SELECT SO_NUMBER,BKG_REF_NO,BKG_UNIT,sum(BKG_QTY)BKG_QTY From so_fcl_lcl_f WHERE BKG_REF_NO='" + bookingNum + "' Group By SO_NUMBER,BKG_REF_NO,BKG_UNIT");

            dispQueryFive.append("SELECT SO_NUMBER,BKG_REF_NO,sum(BKG_NET_WT)BKG_NET_WT,sum(BKG_KGS_GROSS_WT)BKG_KGS_GROSS_WT,sum(BKG_CBM)MEASURMENT From so_fcl_lcl_f WHERE BKG_REF_NO='" + bookingNum + "' Group By SO_NUMBER,BKG_REF_NO");

            Iterator dispItr = null;

            try {
                rs = Database.query(dispQuery.toString(), connection);
                rstwo = Database.query(dispQueryTwo.toString(), connection);
                rsthree = Database.query(dispQueryThree.toString(), connection);
                rsfour = Database.query(dispQueryFour.toString(), connection);
                rsfive = Database.query(dispQueryFive.toString(), connection);
            } catch (Exception e) {
                e.printStackTrace();
            }

            flag_data = true;
            if (flag_data) {
                String so_number = "";


                //PDF Start
                Document document = new Document(PageSize.A4, 30, 30, 30, 140);

                //PdfWriter.fitsPage(1); 
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFolderPath + "/" + bookingNum + ".pdf"));

                final Rectangle page = document.getPageSize();

                String Desc1 = null;
                String tel = null;
                String fax = null;
                String url = null;
                String email = null;

                while (rsthree.next()) {
                    Desc1 = ApplicationUtil.checkForNull(rsthree.getString("DESCRIPTION1"));
                    tel = "Tel. " + ApplicationUtil.checkForNull(rsthree.getString("TEL_NO"));;
                    fax = "Fax. " + ApplicationUtil.checkForNull(rsthree.getString("FAX_NO"));
                    url = "URL. " + ApplicationUtil.checkForNull(rsthree.getString("WEB"));
                    email = "Email. " + ApplicationUtil.checkForNull(rsthree.getString("EMAIL"));
                }

                String contfield = tel + "\n" + fax + "\n" + url + "\n" + email + "\n";

                rs.next();
                final PdfPTable headtab = new PdfPTable(7);
                final PdfPTable headtab1 = new PdfPTable(4);
                PdfPCell hcell = new PdfPCell(new Phrase("S.O.No : ", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.setHorizontalAlignment(0);
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                headtab1.addCell(hcell);
                String soNumber = (String) ApplicationUtil.checkForNull(rs.getString("SO_NUMBER"));
                if ("0".equals(soNumber)) {
                    hcell = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    hcell.setHorizontalAlignment(0);
                    hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab1.addCell(hcell);
                } else {
                    hcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("LOADING_AGNT")) + "/" + (String) rs.getString("POD") + "/" + (String) ApplicationUtil.checkForNull(rs.getString("SO_NUMBER")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));

                    hcell.setHorizontalAlignment(0);
                    hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab1.addCell(hcell);

                }

                hcell = new PdfPCell(new Phrase("Shipper : ", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.setHorizontalAlignment(2);
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                headtab1.addCell(hcell);

                hcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("SHPRDESC")), new Font(Font.COURIER, 9, Font.NORMAL)));
                hcell.setHorizontalAlignment(1);
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                headtab1.addCell(hcell);

                hcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.RIGHT);
                hcell.setBackgroundColor(Color.LIGHT_GRAY);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Marks & nos.\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT);
                hcell.setHorizontalAlignment(0);
                hcell.setBackgroundColor(Color.LIGHT_GRAY);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("No. of \nPackages\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT);
                hcell.setHorizontalAlignment(0);
                hcell.setBackgroundColor(Color.LIGHT_GRAY);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Description of goods\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT);
                hcell.setHorizontalAlignment(1);
                hcell.setBackgroundColor(Color.LIGHT_GRAY);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Weight\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT);
                hcell.setHorizontalAlignment(2);
                hcell.setBackgroundColor(Color.LIGHT_GRAY);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Measurement\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT);
                hcell.setHorizontalAlignment(2);
                hcell.setBackgroundColor(Color.LIGHT_GRAY);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT);
                hcell.setBackgroundColor(Color.LIGHT_GRAY);
                headtab.addCell(hcell);

                final PdfPTable endline = new PdfPTable(1);
                PdfPCell endlinecell = new PdfPCell(new Phrase("\n", new Font(Font.BOLDITALIC)));
                endline.addCell(endlinecell);
                final int hwidths[] = {2, 18, 15, 38, 12, 13, 2};
                final int h1widths[] = {10, 40, 20, 30};

                final float[] printwidths = {8, 22, 15, 25, 27, 3};
                class PageEvents extends PdfPageEventHelper {

                    @Override
                    public void onStartPage(PdfWriter writer, Document document) {

                        document.setMargins(30, 30, 62.5f, 40);
                        if (document.getPageNumber() > 1) {
                            //document.setMargins(30,30,60,40);
                            headtab1.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                            headtab.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                            try {
                                headtab1.setWidths(h1widths);
                                headtab.setWidths(hwidths);
                            } catch (DocumentException e) {

                            }
                            headtab1.writeSelectedRows(0, -1, document.leftMargin(), 815, writer.getDirectContent());
                            headtab.writeSelectedRows(0, -1, document.leftMargin(), 800, writer.getDirectContent());
                        }
                    }

                    public void onEndPage(PdfWriter writer, Document document) {

                        if (document.getPageNumber() > 1) {
                            document.getPageSize().setBorder(0);
                            //endline.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                            PdfPTable pageno = new PdfPTable(6);

                            PdfPCell pagenocell = new PdfPCell(new Phrase("Print by :", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                            pagenocell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                            pagenocell.setHorizontalAlignment(0);
                            pageno.addCell(pagenocell);

                            pagenocell = new PdfPCell(new Phrase(bean.getUserId(), new Font(Font.HELVETICA, 8, Font.NORMAL)));
                            pagenocell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                            pagenocell.setHorizontalAlignment(0);
                            pageno.addCell(pagenocell);

                            pagenocell = new PdfPCell(new Phrase("Print date :", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                            pagenocell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                            pagenocell.setHorizontalAlignment(2);
                            pageno.addCell(pagenocell);

                            pagenocell = new PdfPCell(new Phrase(DateUtil.getSystemDate(), new Font(Font.HELVETICA, 8, Font.NORMAL)));
                            pagenocell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                            pagenocell.setHorizontalAlignment(0);
                            pageno.addCell(pagenocell);

                            pagenocell = new PdfPCell(new Phrase("Page : ", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                            pagenocell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                            pagenocell.setHorizontalAlignment(2);
                            pageno.addCell(pagenocell);

                            pagenocell = new PdfPCell(new Phrase(document.getPageNumber() + "", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                            pagenocell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                            pagenocell.setHorizontalAlignment(2);
                            pageno.addCell(pagenocell);

                            try {

                                pageno.setWidths(printwidths);
                                pageno.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                                pageno.writeSelectedRows(0, -1, document.leftMargin(), document.bottom(), writer.getDirectContent());

                            } catch (Exception e) {

                            }
                        }
                    }
                }

                writer.setPageEvent(new PageEvents());

                document.open();
                PdfPTable table = new PdfPTable(7);
                PdfPTable table1 = new PdfPTable(3);
                PdfPTable table3 = new PdfPTable(2);
                PdfPTable table4 = new PdfPTable(6);

                PdfPTable shipper = new PdfPTable(1);

                PdfPCell shipcell = new PdfPCell(new Phrase("Shipper", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.setBorderColor(Color.white);
                shipcell.setBackgroundColor(Color.LIGHT_GRAY);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase(ApplicationUtil.checkForNull(rs.getString("SHPR_CONTCT_DTLS")), new Font(Font.COURIER, 9, Font.NORMAL)));
                shipcell.setMinimumHeight(60);
                shipcell.setFixedHeight(60);
                shipcell.setBorderColor(Color.white);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("Consignee\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.LEFT);
                shipcell.disableBorderSide(Rectangle.RIGHT);
                shipcell.setBackgroundColor(Color.LIGHT_GRAY);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase(ApplicationUtil.checkForNull(rs.getString("CNEE_CONTCT_DTLS")), new Font(Font.COURIER, 9, Font.NORMAL)));
                shipcell.setBorderColor(Color.white);
                shipcell.setMinimumHeight(60);
                shipcell.setFixedHeight(60);
                shipcell.setVerticalAlignment(Rectangle.TOP);
                shipper.addCell(shipcell);
                table3.addCell(shipper);

                PdfPTable sfpvtltd = new PdfPTable(1);
                PdfPCell sfCell = new PdfPCell(new Phrase(Desc1 + "\n", FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, new Color(0x00, 0x00, 0xFF))));
                sfCell.setBorderColor(Color.white);

                sfpvtltd.addCell(sfCell);
                sfCell = new PdfPCell(new Phrase(contfield, new Font(Font.COURIER, 9, Font.NORMAL)));
                sfCell.setBorderColor(Color.white);
                sfCell.setMinimumHeight(50);
                shipcell.setFixedHeight(50);
                sfpvtltd.addCell(sfCell);

                sfCell = new PdfPCell(new Phrase("", new Font(Font.COURIER, 10, Font.NORMAL)));
                sfCell.setBorderColor(Color.white);
                sfpvtltd.addCell(sfCell);

                sfCell = new PdfPCell(new Phrase("SHIPPING ORDER", new Font(Font.HELVETICA, 12, Font.NORMAL)));
                sfCell.setBorderColor(Color.white);
                sfCell.setHorizontalAlignment(1);
                sfCell.setMinimumHeight(60);
                shipcell.setFixedHeight(60);
                sfCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                sfpvtltd.addCell(sfCell);

                sfCell = new PdfPCell(new Phrase("Customer satisfaction & Quality distribution are our Targets", new Font(Font.HELVETICA, 6, Font.ITALIC)));
                sfCell.setHorizontalAlignment(1);
                sfCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                sfCell.setBorderColor(Color.white);
                sfpvtltd.addCell(sfCell);
                table3.addCell(sfpvtltd);

                PdfPTable notify = new PdfPTable(1);
                PdfPCell notifycell = new PdfPCell(new Phrase("Notify Address\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                notifycell.setBorderColor(Color.white);
                notifycell.setBackgroundColor(Color.LIGHT_GRAY);
                notify.addCell(notifycell);

                notifycell = new PdfPCell(new Phrase(ApplicationUtil.checkForNull(rs.getString("SO_NOTIFY_CONTCT_DTLS")), new Font(Font.COURIER, 9, Font.NORMAL)));
                notifycell.setMinimumHeight(60);
                notifycell.setFixedHeight(60);
                notifycell.setBorderColor(Color.white);
                notify.addCell(notifycell);
                table3.addCell(notify);

                PdfPTable manufacturer = new PdfPTable(1);
                PdfPCell manucell = new PdfPCell(new Phrase("Also Notify Address\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                manucell.setBorderColor(Color.white);
                manucell.setBackgroundColor(Color.LIGHT_GRAY);
                manufacturer.addCell(manucell);
                manucell = new PdfPCell(new Phrase(ApplicationUtil.checkForNull(rs.getString("ALSO_NOTIFY_CONTCT_DTLS")), new Font(Font.COURIER, 9, Font.NORMAL)));
                manucell.setBorderColor(Color.white);
                manucell.setMinimumHeight(60);
                manucell.setFixedHeight(60);

                manufacturer.addCell(manucell);

                table3.addCell(manufacturer);
                PdfPTable explicence = new PdfPTable(1);
                PdfPCell expliccell = new PdfPCell(new Phrase("Invoice no.\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                expliccell.setBorderColor(Color.white);
                explicence.addCell(expliccell);
                expliccell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("COMM_INV_NUMBER")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                expliccell.setBorderColor(Color.white);
                explicence.addCell(expliccell);
                table1.addCell(explicence);

                PdfPTable sono = new PdfPTable(1);
                String soNum = (String) rs.getString("So_NUMBER");
                if ("0".equals(soNum)) {
                    PdfPCell sonocell = new PdfPCell(new Phrase("Shipping Order No.\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    sonocell.setBorderColor(Color.white);
                    sono.addCell(sonocell);
                    sonocell = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    sonocell.setBorderColor(Color.white);
                    sono.addCell(sonocell);
                    table1.addCell(sono);
                } else {
                    PdfPCell sonocell = new PdfPCell(new Phrase("Shipping Order No.\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    sonocell.setBorderColor(Color.white);
                    sono.addCell(sonocell);
                    sonocell = new PdfPCell(new Phrase((String) rs.getString("LOADING_AGNT") + "/" + (String) ApplicationUtil.checkForNull(rs.getString("POD")) + "/" + (String) rs.getString("SO_NUMBER") + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    sonocell.setBorderColor(Color.white);
                    sono.addCell(sonocell);
                    table1.addCell(sono);

                }

                PdfPTable nooforiginal = new PdfPTable(1);
                PdfPCell nooforigcell = new PdfPCell(new Phrase("No. of Original\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                nooforigcell.setBorderColor(Color.white);
                nooforiginal.addCell(nooforigcell);

                String nor = (String) rs.getString("NO_OF_ORIGINAL");
                if (nor.equals("1")) {
                    nor = "ONE";
                } else if (nor.equals("2")) {
                    nor = "TWO";
                } else if (nor.equals("3")) {
                    nor = "THREE";
                } else if (nor.equals("4")) {
                    nor = "FOUR";
                } else {
                    nor = "ZERO";
                }

                nooforigcell = new PdfPCell(new Phrase(nor + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                nooforigcell.setBorderColor(Color.white);
                nooforiginal.addCell(nooforigcell);
                table1.addCell(nooforiginal);

                PdfPTable intendedvessel = new PdfPTable(1);
                PdfPCell intvesscell = new PdfPCell(new Phrase("Vessel\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                intvesscell.setBorderColor(Color.white);
                intendedvessel.addCell(intvesscell);
                intvesscell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("VSL")) + "/" + (String) ApplicationUtil.checkForNull(rs.getString("VOY")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                intvesscell.setBorderColor(Color.white);
                intendedvessel.addCell(intvesscell);
                table1.addCell(intendedvessel);

                PdfPTable placeofrec = new PdfPTable(1);
                PdfPCell placeofreccell = new PdfPCell(new Phrase("Place of receipt\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                placeofreccell.setBorderColor(Color.white);
                placeofrec.addCell(placeofreccell);
                placeofreccell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("POR_NAME")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                placeofreccell.setBorderColor(Color.white);
                placeofrec.addCell(placeofreccell);
                table1.addCell(placeofrec);

                PdfPTable portofload = new PdfPTable(1);
                PdfPCell portofloadcell = new PdfPCell(new Phrase("Port of Loading\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                portofloadcell.setBorderColor(Color.white);
                portofload.addCell(portofloadcell);
                portofloadcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("POL_NAME")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                portofloadcell.setBorderColor(Color.white);
                portofload.addCell(portofloadcell);
                table1.addCell(portofload);

                PdfPTable portofdischarge = new PdfPTable(1);
                PdfPCell portofdiscell = new PdfPCell(new Phrase("Port of discharge\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                portofdiscell.setBorderColor(Color.white);
                portofdischarge.addCell(portofdiscell);
                portofdiscell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("POD_NAME")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                portofdiscell.setBorderColor(Color.white);
                portofdischarge.addCell(portofdiscell);
                table1.addCell(portofdischarge);

                PdfPTable finaldest = new PdfPTable(1);
                PdfPCell finaldestcell = new PdfPCell(new Phrase("Destination\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                finaldestcell.setBorderColor(Color.white);
                finaldest.addCell(finaldestcell);
                finaldestcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("DEST_NAME")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                finaldestcell.setBorderColor(Color.white);
                finaldest.addCell(finaldestcell);
                table1.addCell(finaldest);

                PdfPTable freightpayable = new PdfPTable(1);
                PdfPCell frepaycell = new PdfPCell(new Phrase("Freight payable at\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                frepaycell.setBorderColor(Color.white);
                freightpayable.addCell(frepaycell);
                frepaycell = new PdfPCell(new Phrase((String) rs.getString("FREIGHT_PAYABLE_AT") + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                frepaycell.setBorderColor(Color.white);
                freightpayable.addCell(frepaycell);
                table1.addCell(freightpayable);
                PdfPCell cellin = new PdfPCell(table1);
                cellin.setColspan(2);
                table3.addCell(cellin);

                table3.setWidthPercentage(100);
                document.add(table3);

                PdfPCell maintabcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));

                String gross1 = "";
                String cbm1 = "";
                int totcount = 0;

                while (rsfive.next()) {
                    gross1 = rsfive.getString("BKG_KGS_GROSS_WT") + "\n(KGS)";
                    cbm1 = rsfive.getString("MEASURMENT") + "\n(CBM)";
                }

                while (rsfour.next()) {
                    String qty = rsfour.getString("BKG_QTY");
                    String unit = rsfour.getString("BKG_UNIT");

                    if (totcount > 0) {
                        gross1 = "";
                        cbm1 = "";
                    }
                    maintabcell = new PdfPCell(new Phrase("", new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setHorizontalAlignment(0);
                    maintabcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    table.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase("", new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setHorizontalAlignment(0);
                    maintabcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    table.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase(qty + "\n" + unit, new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setHorizontalAlignment(0);
                    maintabcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    table.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase("", new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setHorizontalAlignment(0);
                    maintabcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    table.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase(gross1, new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setHorizontalAlignment(0);
                    maintabcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    maintabcell.setHorizontalAlignment(2);
                    table.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase(cbm1, new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setHorizontalAlignment(0);
                    maintabcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    maintabcell.setHorizontalAlignment(2);
                    table.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase("", new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setHorizontalAlignment(0);
                    maintabcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    table.addCell(maintabcell);

                    totcount++;
                }

                while (rstwo.next()) {
                    String marknostr = (String) rstwo.getString("MARK_NO");
                    String markdetstr = (String) rstwo.getString("MARK_DETAILS");

                    maintabcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    maintabcell.disableBorderSide(Rectangle.RIGHT | Rectangle.BOTTOM | Rectangle.TOP | Rectangle.LEFT);
                    table.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase(marknostr, new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setBorderColor(Color.white);
                    maintabcell.setHorizontalAlignment(0);

                    table.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setBorderColor(Color.white);
                    maintabcell.setHorizontalAlignment(0);
                    table.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase(markdetstr, new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setBorderColor(Color.white);
                    maintabcell.setHorizontalAlignment(0);

                    table.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setBorderColor(Color.white);
                    maintabcell.setHorizontalAlignment(2);
                    table.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setBorderColor(Color.white);
                    maintabcell.setHorizontalAlignment(2);
                    table.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                    maintabcell.disableBorderSide(Rectangle.LEFT | Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
                    table.addCell(maintabcell);

                }
                PdfPCell terms = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                terms.setBorderColor(Color.white);
                terms.setHorizontalAlignment(1);
                table.addCell(terms);

                PdfPCell containerNo = new PdfPCell(new Phrase("\nCONTAINER NO." + "/SIZE", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                containerNo.setBorderColor(Color.white);
                containerNo.setHorizontalAlignment(0);
                table.addCell(containerNo);

                PdfPCell sealNo = new PdfPCell(new Phrase("\nL.SEAL NO.", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                sealNo.setBorderColor(Color.white);
                sealNo.setHorizontalAlignment(0);
                table.addCell(sealNo);

                PdfPCell qty = new PdfPCell(new Phrase("\nQTY" + "/UNIT", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                qty.setBorderColor(Color.white);
                qty.setHorizontalAlignment(0);
                table.addCell(qty);

                PdfPCell wtg = new PdfPCell(new Phrase("\nGROSS WT ", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                wtg.setBorderColor(Color.white);
                wtg.setHorizontalAlignment(2);
                table.addCell(wtg);

                PdfPCell cbm = new PdfPCell(new Phrase("\nMEASUREMENT", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                cbm.setBorderColor(Color.white);
                table.addCell(cbm);

                PdfPCell termslast = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                termslast.setBorderColor(Color.white);
                termslast.setHorizontalAlignment(1);
                table.addCell(termslast);

                rstwo.beforeFirst();
                while (rstwo.next()) {

                    String container = (String) ApplicationUtil.checkForNull(rstwo.getString("CONTNR_NO"));
                    String containerSize = (String) ApplicationUtil.checkForNull(rstwo.getString("sDESCRIPTION"));
                    String seal = (String) ApplicationUtil.checkForNull(rstwo.getString("LINE_SEAL_NO"));
                    String bkgqty = (String) ApplicationUtil.checkForNull(rstwo.getString("BKG_QTY"));
                    String bkgunit = (String) ApplicationUtil.checkForNull(rstwo.getString("BKG_UNIT"));
                    String weightstr = (String) ApplicationUtil.checkForNull(rstwo.getString("BKG_KGS_GROSS_WT"));
                    String measurementstr = (String) ApplicationUtil.checkForNull(rstwo.getString("BKG_CBM"));

                    terms = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    terms.setBorderColor(Color.white);
                    terms.setHorizontalAlignment(1);
                    table.addCell(terms);

                    if ((null == container || "".equals(container)) && (null == containerSize || "".equals(containerSize))) {
                        containerNo = new PdfPCell();
                        containerNo.setBorderColor(Color.white);
                        containerNo.setHorizontalAlignment(0);
                        table.addCell(containerNo);

                    } else {
                        containerNo = new PdfPCell(new Phrase(container + "/" + containerSize + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                        containerNo.setBorderColor(Color.white);
                        containerNo.setHorizontalAlignment(0);
                        table.addCell(containerNo);
                    }

                    sealNo = new PdfPCell(new Phrase(seal + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    sealNo.setBorderColor(Color.white);
                    sealNo.setHorizontalAlignment(0);
                    table.addCell(sealNo);

                    qty = new PdfPCell(new Phrase(bkgqty + "/" + bkgunit + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    qty.setBorderColor(Color.white);
                    qty.setHorizontalAlignment(0);
                    table.addCell(qty);

                    wtg = new PdfPCell(new Phrase(weightstr + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    wtg.setBorderColor(Color.white);
                    wtg.setHorizontalAlignment(2);
                    table.addCell(wtg);

                    cbm = new PdfPCell(new Phrase(measurementstr + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    cbm.setBorderColor(Color.white);
                    cbm.setHorizontalAlignment(2);
                    table.addCell(cbm);

                    terms = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    terms.setBorderColor(Color.white);
                    terms.setHorizontalAlignment(1);
                    table.addCell(terms);

                }

                PdfPTable pageFooter = new PdfPTable(2);

                Cell blankcell = new Cell(new Phrase("\n" + "ABOVE PARTICULARS ARE DECLARED BY SHIPPER, NOT"
                        + "RESPONSIBLE BY THE CARRIER\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                blankcell.setBorderColor(Color.white);
                blankcell.setHorizontalAlignment(1);
                blankcell.setVerticalAlignment(1);
                blankcell.setColspan(5);

                PdfPTable remark = new PdfPTable(1);

                PdfPCell reminner = new PdfPCell(remark);
                reminner.setColspan(2);
                pageFooter.addCell(reminner);

                PdfPTable pfooter = new PdfPTable(2);
                PdfPTable warehouseadd = new PdfPTable(1);
                PdfPCell wareaddcell = new PdfPCell(new Phrase("Warehouse Address\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                wareaddcell.setBorderColor(Color.white);
                warehouseadd.addCell(wareaddcell);
                wareaddcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("ORIGN_WH_CONTCT_DTLS")), new Font(Font.COURIER, 9, Font.NORMAL)));
                wareaddcell.setBorderColor(Color.white);
                wareaddcell.setMinimumHeight(60);
                wareaddcell.setFixedHeight(60);
                warehouseadd.addCell(wareaddcell);

                pfooter.addCell(warehouseadd);

                PdfPTable closedt = new PdfPTable(1);
                PdfPCell closedtcell = new PdfPCell(new Phrase("Closing date & time\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                closedtcell.setBorderColor(Color.white);
                closedt.addCell(closedtcell);
                closedtcell = new PdfPCell(new Phrase("Booking Ref : " + (String) ApplicationUtil.checkForNull(rs.getString("LOADING_AGNT")) + "/" + (String) ApplicationUtil.checkForNull(rs.getString("BKG_REF_NO")), new Font(Font.COURIER, 9, Font.NORMAL)));
                closedtcell.setBorderColor(Color.white);
                closedtcell.setMinimumHeight(45);
                closedtcell.setFixedHeight(45);
                closedt.addCell(closedtcell);

                pfooter.addCell(closedt);
                PdfPCell closeinner = new PdfPCell(pfooter);

                closeinner.setColspan(2);
                pageFooter.addCell(closeinner);

                PdfPCell printdatacell = new PdfPCell(new Phrase("Print by :", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                printdatacell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                printdatacell.setHorizontalAlignment(0);
                table4.addCell(printdatacell);

                printdatacell = new PdfPCell(new Phrase(bean.getUserId(), new Font(Font.HELVETICA, 8, Font.NORMAL)));
                printdatacell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                printdatacell.setHorizontalAlignment(0);
                table4.addCell(printdatacell);

                printdatacell = new PdfPCell(new Phrase("Print date :", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                printdatacell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                printdatacell.setHorizontalAlignment(2);
                table4.addCell(printdatacell);

                printdatacell = new PdfPCell(new Phrase(DateUtil.getSystemDate(), new Font(Font.HELVETICA, 8, Font.NORMAL)));
                printdatacell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                printdatacell.setHorizontalAlignment(0);
                table4.addCell(printdatacell);

                printdatacell = new PdfPCell(new Phrase("Page : ", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                printdatacell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                printdatacell.setHorizontalAlignment(2);
                table4.addCell(printdatacell);

                printdatacell = new PdfPCell(new Phrase("1", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                printdatacell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                printdatacell.setHorizontalAlignment(2);
                table4.addCell(printdatacell);

                table4.setWidths(printwidths);
                table4.setWidthPercentage(100);
                PdfPCell tab4inner = new PdfPCell(table4);
                tab4inner.setColspan(2);
                tab4inner.disableBorderSide(Rectangle.LEFT);
                tab4inner.disableBorderSide(Rectangle.RIGHT);
                tab4inner.disableBorderSide(Rectangle.BOTTOM);
                pageFooter.addCell(tab4inner);

                int[] widths = {2, 18, 15, 38, 12, 13, 2};

                int i = 0;

                PdfPTable attachedTab = new PdfPTable(7);

                PdfPCell attchcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                attchcell.disableBorderSide(Rectangle.RIGHT);
                attchcell.setBackgroundColor(Color.LIGHT_GRAY);
                attachedTab.addCell(attchcell);

                attchcell = new PdfPCell(new Phrase("Marks & nos.\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                attchcell.setHorizontalAlignment(1);
                attchcell.disableBorderSide(Rectangle.RIGHT | Rectangle.LEFT);
                attchcell.setBackgroundColor(Color.LIGHT_GRAY);
                attachedTab.addCell(attchcell);

                attchcell = new PdfPCell(new Phrase("No. of \nPackages\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                attchcell.setHorizontalAlignment(0);
                attchcell.disableBorderSide(Rectangle.RIGHT | Rectangle.LEFT);
                attchcell.setBackgroundColor(Color.LIGHT_GRAY);
                attachedTab.addCell(attchcell);

                attchcell = new PdfPCell(new Phrase("Description of goods\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                attchcell.setHorizontalAlignment(1);
                attchcell.disableBorderSide(Rectangle.RIGHT | Rectangle.LEFT);
                attchcell.setBackgroundColor(Color.LIGHT_GRAY);
                attachedTab.addCell(attchcell);

                attchcell = new PdfPCell(new Phrase("Weight\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                attchcell.setHorizontalAlignment(1);
                attchcell.disableBorderSide(Rectangle.RIGHT | Rectangle.LEFT);
                attchcell.setBackgroundColor(Color.LIGHT_GRAY);
                attachedTab.addCell(attchcell);

                attchcell = new PdfPCell(new Phrase("Measurement\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                attchcell.setHorizontalAlignment(1);
                attchcell.disableBorderSide(Rectangle.RIGHT | Rectangle.LEFT);
                attchcell.setBackgroundColor(Color.LIGHT_GRAY);
                attachedTab.addCell(attchcell);

                attchcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                attchcell.disableBorderSide(Rectangle.LEFT);
                attchcell.setBackgroundColor(Color.LIGHT_GRAY);
                attachedTab.addCell(attchcell);

                String attchSht = "*** FULL PARTICULARS AS PER ATTACHED SHEET ***";

                PdfPCell attchedCell = new PdfPCell(new Phrase("\n" + attchSht, new Font(Font.COURIER, 9, Font.NORMAL)));
                attchedCell.setHorizontalAlignment(1);

                attchedCell.setColspan(7);
                attchedCell.setVerticalAlignment(0);
                attchedCell.setMinimumHeight(336);
                attchedCell.setBorderColor(Color.white);
                attachedTab.addCell(attchedCell);

                pageFooter.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                pageFooter.writeSelectedRows(0, -1, document.leftMargin(), 140, writer.getDirectContent());

                headtab.setWidths(hwidths);
                headtab.setWidthPercentage(100);
                document.add(headtab);

                table.setWidthPercentage(100);
                table.setWidths(widths);
                document.add(table);

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
                if (rsthree != null) {
                    rsthree.close();
                }   
                if (rsfour != null) {
                    rsfour.close();
                }
                if (rsfive != null) {
                    rsfive.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return bookingNum;
    }//end writeUCMpdf

}
