package com.finops.report.legacy;

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

public class SIPDF {

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
            StringBuilder dispQuery = new StringBuilder();
            StringBuilder dispQueryTwo = new StringBuilder();
            StringBuilder dispQueryThree = new StringBuilder();
            connection = jdbcTemplate.getDataSource().getConnection();

            dispQuery.append("SELECT BKG_REF_NO,SI_NUMBER,CNEE_CONTCT_DTLS,COMM_INV_NUMBER,CURRENCY_CODE"
                    + ",SHPR_CONTCT_DTLS,SHPR.DESCRIPTION1 SHPRDESCRIPTION1,SI_NOTIFY,SI_NOTIFY_CONTCT_DTLS,CARRIER_CONTCT_DTLS,POL"
                    + ",POD,POL_NAME,POD_NAME,VAL_CARRAIGE"
                    + ",VAL_CUSTOM,NO_OF_COMM_INV,NO_OF_PACK_LIST,NO_OF_EXP_LIC,NO_OF_CO"
                    + ",NO_OF_FORM_A,NO_OF_OTHERS,PT_AIR_FREIGHT,PT_TERMNL_HANDLNG,PT_CARTG"
                    + ",PT_HNDLNG_DOCMNTN,PT_PACKNG,PT_OTHRS,LOADING_AGNT,SI_REMARK,FREIGHT_PAYABLE_AT,ORIGN_WH_CONTCT_DTLS "
                    + "FROM (SI_HDR_F SI "
                    + "LEFT OUTER JOIN (SELECT SHPR.PARTNER_CODE,SHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D SHPR WHERE SHPR.LOADNG_AGNT = '" +branch+ "' AND SHPR.STATUS = 'A') SHPR ON (SI.SHPR = SHPR.PARTNER_CODE ))"
                    + "WHERE BKG_REF_NO='" + bookingNum + "'");

            dispQueryTwo.append("SELECT BKG_QTY,BKG_UNIT,BKG_KGS_GROSS_WT,BKG_CBM,MARK_NO,MARK_DETAILS FROM SI_DTL_F "
                    + "WHERE BKG_REF_NO ='" + bookingNum + "'");

            dispQueryThree.append("select DESCRIPTION1,TEL_NO,WEB,FAX_NO,EMAIL from partner_account_d where PARTNER_CODE='" +branch+ "' and loadng_agnt='" +branch+ "'");

            try {
                rs = Database.query(dispQuery.toString(), connection);
                rstwo = Database.query(dispQueryTwo.toString(), connection);
                rsthree = Database.query(dispQueryThree.toString(), connection);
            } catch (Exception e) {
                e.printStackTrace();
            }

            flag_data = true;
            if (flag_data) {

                Document document = new Document(PageSize.A4, 30, 30, 30, 100);

                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFolderPath + "/" + bookingNum + ".pdf"));

                final Rectangle page = document.getPageSize();

                String Desc1 = null;
                String tel = null;
                String fax = null;
                String url = null;
                String email = null;

                while (rsthree.next()) {
                    Desc1 = rsthree.getString("DESCRIPTION1");
                    tel = "Tel. " + rsthree.getString("TEL_NO");;
                    fax = "Fax. " + rsthree.getString("FAX_NO");
                    url = "URL. " + rsthree.getString("WEB");
                    email = "Email. " + ApplicationUtil.checkForNull(rsthree.getString("EMAIL"));
                }

                String contfield = tel + "\n" + fax + "\n" + url + "\n" + email + "\n";

                rs.next();

                final PdfPTable headtab = new PdfPTable(7);
                final PdfPTable headtab1 = new PdfPTable(4);
                PdfPCell hcell = new PdfPCell(new Phrase("S.I.No : ", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.setHorizontalAlignment(0);
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                headtab1.addCell(hcell);
                String siNumber = (String) rs.getString("SI_NUMBER");
                if ("0".equals(siNumber)) {
                    hcell = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    hcell.setHorizontalAlignment(0);
                    hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab1.addCell(hcell);
                } else {
                    hcell = new PdfPCell(new Phrase((String) rs.getString("LOADING_AGNT") + "/" + (String) rs.getString("POD") + "/" + (String) rs.getString("SI_NUMBER") + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    hcell.setHorizontalAlignment(0);
                    hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab1.addCell(hcell);

                }

                hcell = new PdfPCell(new Phrase("Shipper : ", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                hcell.setHorizontalAlignment(2);
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                headtab1.addCell(hcell);

                hcell = new PdfPCell(new Phrase((String) rs.getString("SHPRDESCRIPTION1"), new Font(Font.COURIER, 9, Font.NORMAL)));
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

                    @Override
                    public void onEndPage(PdfWriter writer, Document document) {
                        if (document.getPageNumber() > 1) {
                            document.getPageSize().setBorder(0);
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

                            } catch (DocumentException e) {
                            }

                        }

                    }
                }

                writer.setPageEvent(new PageEvents());

                document.open();
                PdfPTable table = new PdfPTable(7);

                PdfPTable table1 = new PdfPTable(3);
                PdfPTable table3 = new PdfPTable(2);
                PdfPTable table4 = new PdfPTable(2);
                PdfPTable table44 = new PdfPTable(4);

                String airfright = (String) rs.getString("PT_AIR_FREIGHT");

                String terminal = (String) rs.getString("PT_TERMNL_HANDLNG");

                String cartage = (String) rs.getString("PT_CARTG");

                String hdocument = (String) rs.getString("PT_HNDLNG_DOCMNTN");

                String packing = (String) rs.getString("PT_PACKNG");

                String others = (String) rs.getString("PT_OTHRS");

                PdfPTable shipper = new PdfPTable(1);

                PdfPCell shipcell = new PdfPCell(new Phrase("Shipper", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.setBorderColor(Color.white);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase((String) rs.getString("SHPR_CONTCT_DTLS") + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                shipcell.setMinimumHeight(60);
                shipcell.setFixedHeight(60);
                shipcell.setBorderColor(Color.white);
                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase("Consignee\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                shipcell.disableBorderSide(Rectangle.LEFT);
                shipcell.disableBorderSide(Rectangle.RIGHT);

                shipper.addCell(shipcell);

                shipcell = new PdfPCell(new Phrase((String) rs.getString("CNEE_CONTCT_DTLS") + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
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
                sfCell = new PdfPCell(new Phrase(contfield, new Font(Font.COURIER, 8, Font.NORMAL)));
                sfCell.setBorderColor(Color.white);
                sfCell.setFixedHeight(60);
                sfpvtltd.addCell(sfCell);

                sfCell = new PdfPCell(new Phrase("", new Font(Font.COURIER, 10, Font.NORMAL)));
                sfCell.setBorderColor(Color.white);
                sfpvtltd.addCell(sfCell);

                sfCell = new PdfPCell(new Phrase("SHIPPING INSTRUCTIONS", new Font(Font.TIMES_ROMAN, 12, Font.NORMAL)));
                sfCell.setBorderColor(Color.white);
                sfCell.setHorizontalAlignment(1);
                sfCell.setMinimumHeight(60);
                sfCell.setFixedHeight(60);
                sfCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                sfpvtltd.addCell(sfCell);

                sfCell = new PdfPCell(new Phrase("Customer satisfaction & Quality distribution are our Targets", new Font(Font.HELVETICA, 6, Font.ITALIC)));
                sfCell.setHorizontalAlignment(1);
                sfCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                sfCell.setBorderColor(Color.white);
                sfpvtltd.addCell(sfCell);
                table3.addCell(sfpvtltd);

                PdfPTable notify = new PdfPTable(1);
                PdfPCell notifycell = new PdfPCell(new Phrase("Notify \n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                notifycell.setBorderColor(Color.white);
                notify.addCell(notifycell);

                notifycell = new PdfPCell(new Phrase((String) rs.getString("SI_NOTIFY_CONTCT_DTLS"), new Font(Font.COURIER, 9, Font.NORMAL)));
                notifycell.setMinimumHeight(60);
                notifycell.setFixedHeight(60);
                notifycell.setBorderColor(Color.white);
                notify.addCell(notifycell);
                table3.addCell(notify);

                PdfPTable manufacturer = new PdfPTable(1);
                PdfPCell manucell = new PdfPCell(new Phrase("Carrier\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                manucell.setBorderColor(Color.white);
                manufacturer.addCell(manucell);
                manucell = new PdfPCell(new Phrase((String) rs.getString("CARRIER_CONTCT_DTLS"), new Font(Font.COURIER, 9, Font.NORMAL)));
                manucell.setBorderColor(Color.white);
                manucell.setMinimumHeight(60);
                manucell.setFixedHeight(60);
                manufacturer.addCell(manucell);

                table3.addCell(manufacturer);

                PdfPTable explicence = new PdfPTable(1);
                PdfPCell expliccell = new PdfPCell(new Phrase("Invoice No.\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                expliccell.setBorderColor(Color.white);
                explicence.addCell(expliccell);
                expliccell = new PdfPCell(new Phrase((String) rs.getString("COMM_INV_NUMBER") + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                expliccell.setBorderColor(Color.white);
                explicence.addCell(expliccell);
                table1.addCell(explicence);

                PdfPTable sono = new PdfPTable(1);
                String siNum = (String) rs.getString("SI_NUMBER");
                if ("0".equals(siNum)) {
                    PdfPCell sonocell = new PdfPCell(new Phrase("Shipping Instruction  No.\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    sonocell.setBorderColor(Color.white);
                    sono.addCell(sonocell);
                    sonocell = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    sonocell.setBorderColor(Color.white);
                    sono.addCell(sonocell);
                    table1.addCell(sono);

                } else {
                    PdfPCell sonocell = new PdfPCell(new Phrase("Shipping Instruction  No.\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    sonocell.setBorderColor(Color.white);
                    sono.addCell(sonocell);
                    sonocell = new PdfPCell(new Phrase((String) rs.getString("LOADING_AGNT") + "/" + (String) rs.getString("POD") + "/" + (String) rs.getString("SI_NUMBER") + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                    sonocell.setBorderColor(Color.white);
                    sono.addCell(sonocell);
                    table1.addCell(sono);
                }

                PdfPTable nooforiginal = new PdfPTable(1);
                PdfPCell nooforigcell = new PdfPCell(new Phrase("For Carriage\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                nooforigcell.setBorderColor(Color.white);
                nooforiginal.addCell(nooforigcell);

                String nor = (String) rs.getString("VAL_CARRAIGE");

                nooforigcell = new PdfPCell(new Phrase(nor + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                nooforigcell.setBorderColor(Color.white);
                nooforiginal.addCell(nooforigcell);
                table1.addCell(nooforiginal);

                PdfPTable intendedvessel = new PdfPTable(1);
                PdfPCell intvesscell = new PdfPCell(new Phrase("From (Airport of Depature)\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                intvesscell.setBorderColor(Color.white);
                intendedvessel.addCell(intvesscell);
                intvesscell = new PdfPCell(new Phrase((String) rs.getString("POL_NAME") + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                intvesscell.setBorderColor(Color.white);
                intendedvessel.addCell(intvesscell);
                table1.addCell(intendedvessel);

                PdfPTable placeofrec = new PdfPTable(1);
                PdfPCell placeofreccell = new PdfPCell(new Phrase("To (Airport of Destination)\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                placeofreccell.setBorderColor(Color.white);
                placeofrec.addCell(placeofreccell);
                placeofreccell = new PdfPCell(new Phrase((String) rs.getString("POD_NAME") + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                placeofreccell.setBorderColor(Color.white);
                placeofrec.addCell(placeofreccell);
                table1.addCell(placeofrec);

                PdfPTable portofload = new PdfPTable(1);
                PdfPCell portofloadcell = new PdfPCell(new Phrase("Declared Value for Customs\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                portofloadcell.setBorderColor(Color.white);
                portofload.addCell(portofloadcell);
                portofloadcell = new PdfPCell(new Phrase((String) rs.getString("VAL_CUSTOM") + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                portofloadcell.setBorderColor(Color.white);
                portofload.addCell(portofloadcell);
                table1.addCell(portofload);

                PdfPTable charges = new PdfPTable(3);

                PdfPCell chargescell = new PdfPCell(new Phrase("Charges(Please mark 'X')\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("Preapid\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("Collect\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("AirFreight\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase(airfright.equals("P") ? "X" : "" + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase(airfright.equals("C") ? "X" : "" + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("Terminal Handling\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase(terminal.equals("P") ? "X" : "" + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase(terminal.equals("C") ? "X" : "" + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("Crtage\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase(cartage.equals("P") ? "X" : "" + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase(cartage.equals("C") ? "X" : "" + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("Handling Document\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase(hdocument.equals("P") ? "X" : "" + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase(hdocument.equals("C") ? "X" : "" + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("Packing\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase(packing.equals("P") ? "X" : "" + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase(packing.equals("C") ? "X" : "" + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("Others\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase(others.equals("P") ? "X" : "" + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase(others.equals("C") ? "X" : "" + "\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                table4.addCell(charges);

                PdfPTable documentab = new PdfPTable(4);

                PdfPCell documentcell = new PdfPCell(new Phrase("DOCUMENT ACCOMPANYING AIR WAY BILL\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentcell.setHorizontalAlignment(1);
                documentcell.setColspan(4);
                documentab.addCell(documentcell);

                documentcell = new PdfPCell(new Phrase("\n" + "Commercial Invoice\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentab.addCell(documentcell);

                documentcell = new PdfPCell(new Phrase("\n" + (String) rs.getString("NO_OF_COMM_INV") + "\n", new Font(Font.COURIER, 7, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentab.addCell(documentcell);

                documentcell = new PdfPCell(new Phrase("\n" + "Packing list\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentab.addCell(documentcell);

                documentcell = new PdfPCell(new Phrase("\n" + (String) rs.getString("NO_OF_PACK_LIST") + "\n", new Font(Font.COURIER, 7, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentab.addCell(documentcell);

                documentcell = new PdfPCell(new Phrase("\n" + "Export licence\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentab.addCell(documentcell);

                documentcell = new PdfPCell(new Phrase("\n" + (String) rs.getString("NO_OF_EXP_LIC") + "\n", new Font(Font.COURIER, 7, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentab.addCell(documentcell);

                documentcell = new PdfPCell(new Phrase("\n" + "Certificate of origin\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentab.addCell(documentcell);

                documentcell = new PdfPCell(new Phrase("\n" + (String) rs.getString("NO_OF_CO") + "\n", new Font(Font.COURIER, 7, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentab.addCell(documentcell);

                documentcell = new PdfPCell(new Phrase("\n" + "Form a\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentab.addCell(documentcell);

                documentcell = new PdfPCell(new Phrase("\n" + (String) rs.getString("NO_OF_FORM_A") + "\n", new Font(Font.COURIER, 7, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentab.addCell(documentcell);

                documentcell = new PdfPCell(new Phrase("\n" + "Others\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentab.addCell(documentcell);

                documentcell = new PdfPCell(new Phrase("\n" + (String) rs.getString("NO_OF_OTHERS") + "\n", new Font(Font.COURIER, 7, Font.NORMAL)));
                documentcell.setBorderColor(Color.white);
                documentab.addCell(documentcell);

                table4.addCell(documentab);

                PdfPTable currency = new PdfPTable(1);
                PdfPCell currencycell = new PdfPCell(new Phrase("Currency \n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                currencycell.setBorderColor(Color.white);
                currency.addCell(currencycell);

                currencycell = new PdfPCell(new Phrase((String) rs.getString("CURRENCY_CODE"), new Font(Font.COURIER, 8, Font.NORMAL)));
                currencycell.setMinimumHeight(10);
                currencycell.setFixedHeight(10);
                currencycell.setBorderColor(Color.white);
                currency.addCell(currencycell);
                table4.addCell(currency);

                PdfPTable payable = new PdfPTable(1);
                PdfPCell payablecell = new PdfPCell(new Phrase("Insurence claim payable at\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                payablecell.setBorderColor(Color.white);
                payable.addCell(payablecell);

                payablecell = new PdfPCell(new Phrase((String) rs.getString("FREIGHT_PAYABLE_AT"), new Font(Font.COURIER, 8, Font.NORMAL)));
                payablecell.setBorderColor(Color.white);
                payablecell.setMinimumHeight(10);
                payable.addCell(payablecell);
                table4.addCell(payable);

                table3.setWidthPercentage(100);
                table1.setWidthPercentage(100);
                table4.setWidthPercentage(100);
                document.add(table3);
                document.add(table1);
                document.add(table4);

                while (rstwo.next()) {
                    String marknostr = (String) rstwo.getString("MARK_NO");
                    String bkgunit = (String) rstwo.getString("BKG_UNIT");
                    String markdetstr = (String) rstwo.getString("MARK_DETAILS");
                    String weightstr = (String) rstwo.getString("BKG_KGS_GROSS_WT");
                    String measurementstr = (String) rstwo.getString("BKG_CBM");
                    String bkgqty = (String) rstwo.getString("BKG_QTY");

                    PdfPCell bcell1 = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    table.addCell(bcell1);

                    PdfPCell marksandnocell = new PdfPCell(new Phrase(marknostr, new Font(Font.COURIER, 9, Font.NORMAL)));
                    marksandnocell.setHorizontalAlignment(0);
                    marksandnocell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    table.addCell(marksandnocell);

                    PdfPCell noofpackcell = new PdfPCell(new Phrase(bkgqty + "\n" + bkgunit, new Font(Font.COURIER, 8, Font.NORMAL)));
                    noofpackcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    noofpackcell.setHorizontalAlignment(0);
                    table.addCell(noofpackcell);

                    PdfPCell descofgdcell = new PdfPCell(new Phrase(markdetstr, new Font(Font.COURIER, 9, Font.NORMAL)));
                    descofgdcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    descofgdcell.setHorizontalAlignment(0);

                    table.addCell(descofgdcell);

                    PdfPCell weightcell = new PdfPCell(new Phrase(weightstr + "\n(KGS)", new Font(Font.COURIER, 9, Font.NORMAL)));
                    weightcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    weightcell.setHorizontalAlignment(2);
                    table.addCell(weightcell);

                    PdfPCell measurecell = new PdfPCell(new Phrase(measurementstr + "\n(CBM)", new Font(Font.COURIER, 9, Font.NORMAL)));
                    measurecell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    measurecell.setHorizontalAlignment(2);
                    table.addCell(measurecell);

                    PdfPCell bcell2 = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    bcell2.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    table.addCell(bcell2);

                }

                PdfPTable pageFooter = new PdfPTable(2);

                PdfPTable pfooter = new PdfPTable(2);
                PdfPTable warehouseadd = new PdfPTable(1);
                PdfPCell wareaddcell = new PdfPCell(new Phrase("Warehouse Address\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                wareaddcell.setBorderColor(Color.white);
                warehouseadd.addCell(wareaddcell);

                wareaddcell = new PdfPCell(new Phrase((String) rs.getString("ORIGN_WH_CONTCT_DTLS"), new Font(Font.COURIER, 9, Font.NORMAL)));
                wareaddcell.setBorderColor(Color.white);
                wareaddcell.setMinimumHeight(60);
                wareaddcell.setFixedHeight(60);
                warehouseadd.addCell(wareaddcell);

                pfooter.addCell(warehouseadd);

                PdfPTable closedt = new PdfPTable(1);
                PdfPCell closedtcell = new PdfPCell(new Phrase("Reserved AWB No.\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                closedtcell.setBorderColor(Color.white);
                closedt.addCell(closedtcell);
                closedtcell = new PdfPCell(new Phrase("Booking Ref : " + (String) rs.getString("LOADING_AGNT") + "/" + (String) rs.getString("BKG_REF_NO"), new Font(Font.COURIER, 9, Font.NORMAL)));
                closedtcell.setBorderColor(Color.white);
                closedtcell.setMinimumHeight(60);
                closedtcell.setFixedHeight(60);
                closedt.addCell(closedtcell);

                pfooter.addCell(closedt);
                PdfPCell closeinner = new PdfPCell(pfooter);
                closeinner.setColspan(2);
                pageFooter.addCell(closeinner);

                PdfPCell printdatacell = new PdfPCell(new Phrase("Print by :", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                printdatacell.disableBorderSide(Rectangle.LEFT);
                printdatacell.disableBorderSide(Rectangle.RIGHT);
                printdatacell.disableBorderSide(Rectangle.BOTTOM);
                table44.addCell(printdatacell);

                printdatacell = new PdfPCell(new Phrase(bean.getUserId(), new Font(Font.COURIER, 9, Font.NORMAL)));
                printdatacell.disableBorderSide(Rectangle.LEFT);
                printdatacell.disableBorderSide(Rectangle.RIGHT);
                printdatacell.disableBorderSide(Rectangle.BOTTOM);
                table44.addCell(printdatacell);

                printdatacell = new PdfPCell(new Phrase("Print date :", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                printdatacell.disableBorderSide(Rectangle.LEFT);
                printdatacell.disableBorderSide(Rectangle.RIGHT);
                printdatacell.disableBorderSide(Rectangle.BOTTOM);
                table44.addCell(printdatacell);

                printdatacell = new PdfPCell(new Phrase(DateUtil.getSystemDate(), new Font(Font.COURIER, 9, Font.NORMAL)));
                printdatacell.disableBorderSide(Rectangle.LEFT);
                printdatacell.disableBorderSide(Rectangle.RIGHT);
                printdatacell.disableBorderSide(Rectangle.BOTTOM);
                table44.addCell(printdatacell);
                float[] tab44widths = {10, 40, 10, 40};
                table44.setWidths(tab44widths);
                PdfPCell tab44inner = new PdfPCell(table44);
                tab44inner.setColspan(4);
                tab44inner.disableBorderSide(Rectangle.LEFT);
                tab44inner.disableBorderSide(Rectangle.RIGHT);
                tab44inner.disableBorderSide(Rectangle.BOTTOM);
                pageFooter.addCell(tab44inner);

                pageFooter.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                pageFooter.writeSelectedRows(0, -1, document.leftMargin(), 100, writer.getDirectContent());

                int[] midtabwidths = {2, 15, 13, 40, 15, 13, 2};

                PdfPTable attachedTab = new PdfPTable(7);

                String attchSht = "*** FULL PARTICULARS AS PER ATTACHED SHEET ***";

                PdfPCell attchedCell = new PdfPCell(new Phrase("\n" + attchSht, new Font(Font.COURIER, 9, Font.NORMAL)));
                attchedCell.setHorizontalAlignment(1);

                attchedCell.setColspan(7);
                attchedCell.setVerticalAlignment(0);
                attchedCell.setMinimumHeight(292);
                attchedCell.disableBorderSide(Rectangle.TOP);
                attachedTab.addCell(attchedCell);

                headtab.setWidths(hwidths);
                headtab.setWidthPercentage(100);
                document.add(headtab);

                table.setWidthPercentage(100);
                table.setWidths(midtabwidths);

                document.add(table);
                document.close();
                // end of dispVec for loop

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
