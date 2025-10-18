package com.finops.report.legacy;

import com.finops.freight.bean.HawbBean;
import com.finops.util.ApplicationUtil;
import com.finops.util.Database;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AirwayBillPDF {

    static String path = "D:/";
    static String testSystemID;
    static boolean isTest;
    static String contents = "It is agreed that the goods described herein are accepted in apparent good order and condition"
            + "condition(except as noted) for carriage subject to the conditions of contract on the reverse hereof."
            + "all goods may be carried by any other means including road or any other carrier unless specific contrary"
            + "instructions are givedn hereon by the shipper,and shipper agrees that the shipment may be carried via intermediate "
            + "stopping places which the carrier deems appropriate.the shipper's attemtion is drawn to the notice considering carrier's "
            + "limitation of liability.shipper may increase such limitation of liability by declaring a higher value of carriage "
            + "and paying a supplement charge if required.";


    /*
     * Method 	: 	This method generates PDF report documents from UCM database.
     * The generated PDF documents are stored in the directory specified in Prop file.
     * Input 	: 	None.
     * Output	:	None.
     */
    public static String writeUCMpdf(String hawbNum, final HawbBean bean, JdbcTemplate jdbcTemplate) {
        ResultSet rs = null;
        ResultSet rstwo = null;
        ResultSet rsthree = null;
        ResultSet rsfour = null;
        ResultSet rsfive = null;
        String awbillNo = null;
        Connection connection = null;
        String pdfFolderPath = bean.getLegacyReportPath();
        String loadingAgent = bean.getLoadingAgent();

        try {
            boolean flag_data = false;
            StringBuilder dispQuery = new StringBuilder();
            StringBuilder dispQueryTwo = new StringBuilder();
            StringBuilder dispQueryThree = new StringBuilder();
            StringBuilder dispQueryFour = new StringBuilder();
            StringBuilder dispQueryFive = new StringBuilder();
            connection = jdbcTemplate.getDataSource().getConnection();

            dispQuery.append("SELECT aw.POL_NAME,aw.DEST_NAME,aw.POD_NAME,aw.FREIGHT_PAYABLE_AT, aw.DEST_AGNT_CONTCT_DTLS,AW.DEST, aw.m_hawb_no,AW.HAWB_NUMBER,AW.SHPR,AW.SHPR_CONTCT_DTLS,SHPR.DESCRIPTION1 shprDescription1," +
                    "AW.CNEE,CNEE.DESCRIPTION1 cneeDescription1"
                    + ",AW.POL,POL.DESCRIPTION1 POLDESCRIPTION1,AW.POD,POD.DESCRIPTION1 PODDESCRIPTION1,AW.CURRENCY_CODE,CURR.currency_code currency,AW.CNEE_CONTCT_DTLS"
                    + ",AW.VAL_CARRAIGE,AW.VAL_CUSTOM,AW.AMT_INSURANCE,AW.FLIGHT_NO,convert(AW.FLIGHT_DATE,DATE)FLIGHT_DATE,AW.CARRIER_CODE,CARR.DESCRIPTION1,AW.PT_OTHRS,AW.PT_PACKNG"
                    + ",AW.FLIGHT_NO,convert(AW.FLIGHT_DATE,DATE)FLIGHT_DATE,AW.POD_NAME,AW.POI_NAME,convert(AW.DATE_OF_ISSUE,DATE)DATE_OF_ISSUE"
                    + ",AW.HAWB_NOTIFY,NOTIFY.DESCRIPTION1 NOTIFYDESCRIPTION1,AW.HAWB_REMARK "
                    + ",AW.PC_AIR_FREIGHT,AW.PC_TAX,AW.PC_DUE_AGENT,AW.PC_DUE_CARRIER "
                    + ",AW.TOTAL_PREPAID,AW.TOTAL_COLLECT,AW.OTHER_CHARGES,AW.NO_OF_COMM_INV "
                    + ",AW.NO_OF_PACK_LIST,AW.NO_OF_EXP_LIC,AW.NO_OF_CO "
                    + " FROM (((((((hawb_hdr_f AW "
                    + " LEFT OUTER JOIN (SELECT SHPR.PARTNER_CODE,SHPR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D SHPR WHERE SHPR.LOADNG_AGNT = '" + loadingAgent + "' AND SHPR.STATUS = 'A') SHPR ON (AW.SHPR = SHPR.PARTNER_CODE ))"
                    + " LEFT OUTER JOIN (SELECT CNEE.PARTNER_CODE,CNEE.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CNEE WHERE CNEE.LOADNG_AGNT = '" + loadingAgent + "' AND CNEE.STATUS = 'A') CNEE ON (AW.CNEE = CNEE.PARTNER_CODE ))"
                    + " LEFT OUTER JOIN (SELECT CARR.PARTNER_CODE,CARR.DESCRIPTION1 FROM PARTNER_ACCOUNT_D CARR WHERE CARR.LOADNG_AGNT = '" + loadingAgent + "' AND CARR.STATUS = 'A') CARR ON (AW.CARRIER_CODE = CARR.PARTNER_CODE ))"
                    + " LEFT OUTER JOIN (SELECT NOTIFY.PARTNER_CODE,NOTIFY.DESCRIPTION1 FROM PARTNER_ACCOUNT_D NOTIFY WHERE NOTIFY.LOADNG_AGNT = '" + loadingAgent + "' AND NOTIFY.STATUS = 'A') NOTIFY ON (AW.HAWB_NOTIFY = NOTIFY.PARTNER_CODE ))"
                    + " LEFT OUTER JOIN currency_d CURR ON (AW.CURRENCY_CODE = CURR.currency_id )) "
                    + " LEFT OUTER JOIN PORT_D POL ON (AW.POL = POL.PORT_CODE)) "
                    + " LEFT OUTER JOIN PORT_D POD ON (AW.POD = POD.PORT_CODE)) "
                    + " WHERE AW.HAWB_NUMBER='" + hawbNum + "'");

            dispQueryTwo.append("SELECT sum(ACT_QTY)ACT_QTY,ACT_UNIT,sum(CHARGBL_WT)CHARGBL_WT,sum(ACT_GROSS_WT)ACT_GROSS_WT,sum(ACT_CBM)ACT_CBM from hawb_dtl_f WHERE HAWB_NUMBER ='" + hawbNum + "' Group By HAWB_NUMBER,ACT_UNIT");

            dispQueryThree.append("SELECT MK.MARK_NO,MK.MARK_DETAILS,AW.HAWB_NOTIFY,AW.HAWB_NOTIFY_CONTCT_DTLS"
                    + " from (hawb_dtl_f MK LEFT OUTER JOIN hawb_hdr_f AW  ON (AW.HAWB_NUMBER = MK.HAWB_NUMBER ))  "
                    + "WHERE MK.HAWB_NUMBER ='" + hawbNum + "'");

            dispQueryFour.append("select DESCRIPTION1,TEL_NO,WEB,FAX_NO,EMAIL from partner_account_d where PARTNER_CODE='" + loadingAgent + "' ");

            dispQueryFive.append(" select sum(ACT_QTY)ACT_QTY,ACT_UNIT from hawb_dtl_f  WHERE HAWB_NUMBER ='" + hawbNum + "' Group By HAWB_NUMBER,ACT_UNIT");

            try {
                rs = (ResultSet) Database.query(dispQuery.toString(), connection);
                rstwo = (ResultSet) Database.query(dispQueryTwo.toString(), connection);
                rsthree = (ResultSet) Database.query(dispQueryThree.toString(), connection);
                rsfour = (ResultSet) Database.query(dispQueryFour.toString(), connection);
                rsfive = (ResultSet) Database.query(dispQueryFive.toString(), connection);

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            flag_data = true;
            if (flag_data) {
                awbillNo = hawbNum.substring(hawbNum.lastIndexOf("/") + 1);

                Document document = new Document(PageSize.A4, 30, 30, 30, 185);

                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFolderPath + "/" + awbillNo + ".pdf"));

                final Rectangle page = document.getPageSize();

                PdfPTable headtab = new PdfPTable(13);

                PdfPCell hcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell = new PdfPCell(new Phrase("No of pcs\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.setHorizontalAlignment(0);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Gross weight\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.setHorizontalAlignment(1);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Kg.\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.setHorizontalAlignment(1);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.BOTTOM);
                hcell.setHorizontalAlignment(0);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Commodity Item No.\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.setHorizontalAlignment(1);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.BOTTOM);
                hcell.setHorizontalAlignment(0);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Chargeable Weight\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.setHorizontalAlignment(1);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.BOTTOM);
                hcell.setHorizontalAlignment(0);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Rate / Change\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT);
                hcell.setHorizontalAlignment(1);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.BOTTOM);
                hcell.setHorizontalAlignment(0);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Total\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.setHorizontalAlignment(1);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.BOTTOM);
                hcell.setHorizontalAlignment(0);
                headtab.addCell(hcell);

                hcell = new PdfPCell(new Phrase("Nature and quality of goods(Incl.Dimensions or volume)", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                hcell.disableBorderSide(Rectangle.LEFT);
                hcell.setHorizontalAlignment(0);
                headtab.addCell(hcell);

                int[] midheadwidths = {10, 12, 4, 1, 12, 1, 14, 1, 8, 1, 12, 1, 26};
                headtab.setWidthPercentage(100);
                headtab.setWidths(midheadwidths);

                String Desc1 = null;

                while (rsfour.next()) {
                    Desc1 = ApplicationUtil.checkForNull(rsfour.getString("DESCRIPTION1"));

                }

                rs.next();

                document.open();

                PdfPTable maintable = new PdfPTable(2);
                PdfPTable table1 = new PdfPTable(2);

                PdfPTable head = new PdfPTable(4);

                float[] pagehds = {10, 40, 10, 40};
                PdfPCell headcell = new PdfPCell(new Phrase("MAWB NO:", new Font(Font.HELVETICA, 8, Font.BOLD)));
                headcell.setBorderColor(Color.white);

                headcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
                head.addCell(headcell);

                headcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("m_hawb_no")), new Font(Font.HELVETICA, 8, Font.BOLD)));
                headcell.setBorderColor(Color.white);
                headcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
                head.addCell(headcell);

                headcell = new PdfPCell(new Phrase("HAWB NO:", new Font(Font.HELVETICA, 8, Font.BOLD)));
                headcell.setBorderColor(Color.white);
                headcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
                head.addCell(headcell);

                headcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("HAWB_NUMBER")), new Font(Font.HELVETICA, 8, Font.BOLD)));
                headcell.setBorderColor(Color.white);
                headcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);

                head.addCell(headcell);
                head.setWidths(pagehds);
                head.setWidthPercentage(100);
                document.add(head);

                PdfPTable notify = new PdfPTable(1);
                PdfPCell notifycell = new PdfPCell(new Phrase("Shipper's Name and Address \n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                notifycell.setBorderColor(Color.white);
                notify.addCell(notifycell);

                notifycell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("SHPR_CONTCT_DTLS")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                notifycell.setMinimumHeight(45);
                notifycell.setFixedHeight(45);
                notifycell.setBorderColor(Color.white);
                notify.addCell(notifycell);
                maintable.addCell(notify);

                PdfPTable manufacturer = new PdfPTable(2);

                PdfPCell manusecondcell = new PdfPCell(new Phrase("NON NEGOTIABLE\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                manusecondcell.setBorderColor(Color.white);
                manusecondcell.setColspan(2);
                manufacturer.addCell(manusecondcell);

                manusecondcell = new PdfPCell(new Phrase("\n" + "AIR WAY BILL\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                manusecondcell.setBorderColor(Color.white);
                manufacturer.addCell(manusecondcell);

                manusecondcell = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 7, Font.NORMAL)));
                manusecondcell.setBorderColor(Color.white);
                manufacturer.addCell(manusecondcell);

                manusecondcell = new PdfPCell(new Phrase("Air Consigment Note" + "\n" + "Issued By :" + "\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));

                manusecondcell.setBorderColor(Color.white);
                manufacturer.addCell(manusecondcell);

                manusecondcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 9, Font.BOLD)));
                manusecondcell.setBorderColor(Color.white);
                manufacturer.addCell(manusecondcell);

                manusecondcell = new PdfPCell(new Phrase(Desc1 + "\n", new Font(Font.HELVETICA, 9, Font.BOLD)));
                manusecondcell.setBorderColor(Color.white);
                manusecondcell.setColspan(2);
                manusecondcell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                manufacturer.addCell(manusecondcell);

                manusecondcell = new PdfPCell(new Phrase("Copies 1,2 and 3 of this Air WayBill are Original and have same validity\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                manusecondcell.setBorderColor(Color.white);
                manusecondcell.setColspan(2);
                manusecondcell.setVerticalAlignment(Element.ALIGN_TOP);
                manufacturer.addCell(manusecondcell);

                maintable.addCell(manufacturer);

                PdfPTable consignee = new PdfPTable(1);
                PdfPCell consigneecell = new PdfPCell(new Phrase("Consignee's Name and Address\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                consigneecell.setBorderColor(Color.white);
                consignee.addCell(consigneecell);

                consigneecell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("CNEE_CONTCT_DTLS")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                consigneecell.setMinimumHeight(70);
                consigneecell.setFixedHeight(70);
                consigneecell.setBorderColor(Color.white);
                consignee.addCell(consigneecell);
                maintable.addCell(consignee);

                PdfPCell datacell = new PdfPCell();
                datacell = new PdfPCell(new Phrase(contents, new Font(Font.ITALIC, 7, Font.NORMAL)));

                datacell.setMinimumHeight(70);
                datacell.setFixedHeight(70);

                maintable.addCell(datacell);

                PdfPTable issueing = new PdfPTable(2);
                PdfPCell issueingcell = new PdfPCell(new Phrase("Issuing Carrier's Agent Name and City \n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                issueingcell.disableBorderSide(Rectangle.BOTTOM);
                issueingcell.setColspan(2);
                issueing.addCell(issueingcell);

                issueingcell = new PdfPCell(new Phrase(Desc1 + "\n" + (String) ApplicationUtil.checkForNull(rs.getString("POL_NAME")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                issueingcell.setMinimumHeight(45);
                issueingcell.setFixedHeight(45);
                issueingcell.disableBorderSide(Rectangle.TOP);
                issueingcell.setColspan(2);
                issueing.addCell(issueingcell);

                issueingcell = new PdfPCell(new Phrase("Agent's IATA Code", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                issueingcell.setMinimumHeight(23);
                issueingcell.setFixedHeight(23);
                issueingcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT);
                issueing.addCell(issueingcell);

                issueingcell = new PdfPCell(new Phrase("Account No.", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                issueingcell.setMinimumHeight(23);
                issueingcell.setFixedHeight(23);
                issueingcell.disableBorderSide(Rectangle.RIGHT);

                issueing.addCell(issueingcell);

                issueingcell = new PdfPCell(new Phrase("Airport of Departure(address of first Carrier) and requested Routing.\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                issueingcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                issueingcell.setColspan(2);
                issueing.addCell(issueingcell);

                issueingcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("POL_NAME")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                issueingcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                issueingcell.setColspan(2);
                issueing.addCell(issueingcell);
                PdfPCell addcell = new PdfPCell(issueing);

                maintable.addCell(addcell);

                PdfPTable accountinginfo = new PdfPTable(1);
                PdfPCell accountinginfocell = new PdfPCell(new Phrase("Accouning Information\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                accountinginfocell.setBorderColor(Color.white);
                accountinginfo.addCell(accountinginfocell);

                accountinginfocell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("DEST_AGNT_CONTCT_DTLS")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                accountinginfocell.setBorderColor(Color.white);
                accountinginfocell.setMinimumHeight(45);
                accountinginfocell.setFixedHeight(45);
                accountinginfo.addCell(accountinginfocell);
                maintable.addCell(accountinginfo);

                float[] byfirstCarrier = {10, 50, 10, 10, 10, 10};
                PdfPTable charges = new PdfPTable(6);
                PdfPCell chargescell = new PdfPCell(new Phrase("to", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("By first Carrier", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("to", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("by", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                chargescell.setHorizontalAlignment(1);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("to", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("by", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.disableBorderSide(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("DEST")) + "\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                chargescell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("CARRIER_CODE")) + "\n", new Font(Font.HELVETICA, 9, Font.NORMAL)));
                chargescell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                charges.addCell(chargescell);

                chargescell = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargescell.disableBorderSide(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                charges.addCell(chargescell);
                charges.setWidths(byfirstCarrier);
                addcell = new PdfPCell(charges);

                maintable.addCell(addcell);

                float[] chargestwotable = {13, 8, 12, 9, 28, 28};
                PdfPTable chargestwo = new PdfPTable(6);
                PdfPCell chargestwoscell = new PdfPCell(new Phrase("Currency", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargestwoscell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                chargestwoscell.setVerticalAlignment(1);
                chargestwo.addCell(chargestwoscell);

                chargestwoscell = new PdfPCell(new Phrase("CHGS code", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                chargestwoscell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                chargestwoscell.setVerticalAlignment(1);
                chargestwo.addCell(chargestwoscell);

                chargestwoscell = new PdfPCell(new Phrase("WT/VAL", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargestwoscell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                chargestwoscell.setVerticalAlignment(Element.ALIGN_TOP);
                chargestwo.addCell(chargestwoscell);

                chargestwoscell = new PdfPCell(new Phrase("Other", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                chargestwoscell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                chargestwoscell.setVerticalAlignment(Element.ALIGN_TOP);
                chargestwo.addCell(chargestwoscell);

                chargestwoscell = new PdfPCell(new Phrase("Declared value of carriage", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                chargestwoscell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                chargestwoscell.setVerticalAlignment(Element.ALIGN_TOP);
                chargestwo.addCell(chargestwoscell);

                chargestwoscell = new PdfPCell(new Phrase("Declared value of customs", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                chargestwoscell.disableBorderSide(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                chargestwo.addCell(chargestwoscell);

                chargestwoscell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("currency")) + "\n", new Font(Font.COURIER, 8, Font.NORMAL)));
                chargestwoscell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                chargestwoscell.setVerticalAlignment(Element.ALIGN_TOP | Element.ALIGN_LEFT);
                chargestwo.addCell(chargestwoscell);

                chargestwoscell = new PdfPCell();
                chargestwoscell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                chargestwoscell.setVerticalAlignment(Element.ALIGN_TOP);
                chargestwo.addCell(chargestwoscell);

                chargestwoscell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("PT_PACKNG")) + "\n", new Font(Font.COURIER, 8, Font.NORMAL)));
                chargestwoscell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                chargestwoscell.setVerticalAlignment(1);
                chargestwo.addCell(chargestwoscell);

                chargestwoscell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("PT_OTHRS")) + "\n", new Font(Font.COURIER, 8, Font.NORMAL)));
                chargestwoscell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                chargestwoscell.setVerticalAlignment(Element.ALIGN_TOP);
                chargestwo.addCell(chargestwoscell);

                chargestwoscell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("VAL_CARRAIGE")) + "\n", new Font(Font.COURIER, 8, Font.NORMAL)));
                chargestwoscell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                chargestwoscell.setVerticalAlignment(Element.ALIGN_TOP);
                chargestwo.addCell(chargestwoscell);

                chargestwoscell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("VAL_CUSTOM")) + "\n", new Font(Font.COURIER, 8, Font.NORMAL)));
                chargestwoscell.disableBorderSide(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                chargestwoscell.setVerticalAlignment(Element.ALIGN_TOP);
                chargestwo.addCell(chargestwoscell);
                chargestwo.setWidths(chargestwotable);
                addcell = new PdfPCell(chargestwo);

                maintable.addCell(addcell);

                float[] airportdesttable = {43, 28, 28};
                PdfPTable airportdest = new PdfPTable(3);
                PdfPCell airportdestcell = new PdfPCell(new Phrase("Airport of Destination", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                airportdestcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                airportdest.addCell(airportdestcell);

                airportdestcell = new PdfPCell(new Phrase("Flight No", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                airportdestcell.setHorizontalAlignment(0);
                airportdestcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                airportdest.addCell(airportdestcell);

                airportdestcell = new PdfPCell(new Phrase("Flight/Date", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                airportdestcell.setHorizontalAlignment(0);
                airportdestcell.disableBorderSide(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                airportdest.addCell(airportdestcell);

                airportdestcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("POD_NAME")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                airportdestcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                airportdestcell.setVerticalAlignment(Element.ALIGN_TOP);
                airportdest.addCell(airportdestcell);

                airportdestcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("FLIGHT_NO")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                airportdestcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                airportdestcell.setVerticalAlignment(Element.ALIGN_TOP);
                airportdest.addCell(airportdestcell);

                airportdestcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("FLIGHT_DATE")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                airportdestcell.disableBorderSide(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                airportdestcell.setVerticalAlignment(Element.ALIGN_TOP);
                airportdest.addCell(airportdestcell);

                airportdest.setWidths(airportdesttable);
                addcell = new PdfPCell(airportdest);

                maintable.addCell(addcell);

                float[] amtinstable = {36, 64};

                PdfPTable amount = new PdfPTable(1);
                PdfPCell amountcell = new PdfPCell(new Phrase("Amount of Insurance\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                amountcell.setVerticalAlignment(0);
                amountcell.setBorderColor(Color.white);
                amountcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
                amount.addCell(amountcell);

                amountcell = new PdfPCell(new Phrase(ApplicationUtil.checkForNull((String) rs.getString("AMT_INSURANCE")) + "\n", new Font(Font.COURIER, 9, Font.NORMAL)));
                amountcell.setBorderColor(Color.white);
                amountcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                amount.addCell(amountcell);
                table1.addCell(amount);

                PdfPCell insurencecell = new PdfPCell(new Phrase("INSURENCE-if carrier orrers insurence and such insurence is requested in accordance with conditions on reverse hereof,indicate amount to be insured in figres in box marked Amount of Insurence", new Font(Font.HELVETICA, 5, Font.NORMAL)));

                amountcell.disableBorderSide(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);

                table1.addCell(insurencecell);
                table1.setWidths(amtinstable);
                addcell = new PdfPCell(table1);

                maintable.addCell(addcell);

                PdfPTable handling = new PdfPTable(1);
                PdfPCell handlingcell = new PdfPCell(new Phrase("Handling Information \n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                handlingcell.disableBorderSide(Rectangle.BOTTOM);
                handling.addCell(handlingcell);
                handlingcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("HAWB_REMARK")), new Font(Font.COURIER, 9, Font.NORMAL)));
                handlingcell.disableBorderSide(Rectangle.TOP);
                handlingcell.setMinimumHeight(35);
                handlingcell.setFixedHeight(35);
                handling.addCell(handlingcell);

                PdfPCell handlingextracell = new PdfPCell(handling);
                handlingextracell.setBorderColor(Color.white);
                handlingextracell.setColspan(2);
                maintable.addCell(handlingextracell);

                PdfPCell bcell1 = new PdfPCell();

                String total = "";
                String kgs = "";
                String kgUnit = "KGS";
                String chargeblewt = "";
                String rate = "";
                int totcount = 0;
                double dchawt = 0;
                double drate = 0;
                String dtotal = "";

                while (rstwo.next()) {
                    kgs = ApplicationUtil.checkForNull(rstwo.getString("ACT_GROSS_WT"));
                    chargeblewt = ApplicationUtil.checkForNull(rstwo.getString("CHARGBL_WT"));
                    total = ApplicationUtil.checkForNull(rstwo.getString("ACT_CBM"));
                }

                while (rsfive.next()) {
                    String qty = ApplicationUtil.checkForNull(rsfive.getString("ACT_QTY"));
                    String unit = ApplicationUtil.checkForNull(rsfive.getString("ACT_UNIT"));

                    if (totcount > 0) {
                        kgs = "";
                        chargeblewt = "";
                        total = "";
                        kgUnit = "";
                    }

                    bcell1 = new PdfPCell(new Phrase(qty + "\n" + unit, new Font(Font.COURIER, 9, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab.addCell(bcell1);

                    bcell1 = new PdfPCell(new Phrase(kgs, new Font(Font.COURIER, 9, Font.NORMAL)));
                    bcell1.setHorizontalAlignment(2);
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab.addCell(bcell1);

                    bcell1 = new PdfPCell(new Phrase(kgUnit, new Font(Font.COURIER, 8, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    bcell1.setHorizontalAlignment(0);
                    headtab.addCell(bcell1);

                    bcell1 = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab.addCell(bcell1);

                    bcell1 = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab.addCell(bcell1);

                    bcell1 = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab.addCell(bcell1);

                    bcell1 = new PdfPCell(new Phrase(chargeblewt, new Font(Font.COURIER, 8, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    bcell1.setHorizontalAlignment(2);
                    headtab.addCell(bcell1);

                    bcell1 = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab.addCell(bcell1);

                    rate = rs.getString("NO_OF_COMM_INV");

                    bcell1 = new PdfPCell(new Phrase(rate, new Font(Font.COURIER, 8, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab.addCell(bcell1);

                    bcell1 = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab.addCell(bcell1);

                    if (chargeblewt != null && !chargeblewt.equals("")) {
                        dchawt = Double.valueOf(chargeblewt);
                    }
                    if (rate != null && !rate.equals("")) {
                        drate = Double.valueOf(rate);
                    }

                    if (dchawt > 0 && drate > 0) {
                        dtotal = Double.toString(dchawt * drate);
                    }

                    bcell1 = new PdfPCell(new Phrase(dtotal, new Font(Font.COURIER, 8, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    bcell1.setHorizontalAlignment(2);
                    headtab.addCell(bcell1);

                    bcell1 = new PdfPCell(new Phrase("", new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab.addCell(bcell1);

                    bcell1 = new PdfPCell(new Phrase(total, new Font(Font.HELVETICA, 8, Font.NORMAL)));
                    bcell1.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                    headtab.addCell(bcell1);
                    totcount++;
                }

                boolean flag = false;
                String notifyadd = null;
                while (rsthree.next()) {
                    String marknostr = (String) rsthree.getString("MARK_NO");
                    String markdetstr = (String) rsthree.getString("MARK_DETAILS");
                    String notifyContact = rsthree.getString("HAWB_NOTIFY_CONTCT_DTLS");
                    if (flag == false && StringUtils.hasText(notifyContact)) {
                        notifyadd = "NOTIFY :\n" + notifyContact;
                    }
                    else {
                        notifyadd = "\n";
                    }

                    PdfPCell maintabcell = new PdfPCell(new Phrase(marknostr, new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setBorderColor(Color.white);
                    maintabcell.setColspan(4);
                    headtab.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase(notifyadd, new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setBorderColor(Color.white);
                    maintabcell.setHorizontalAlignment(0);
                    maintabcell.setColspan(4);
                    headtab.addCell(maintabcell);

                    maintabcell = new PdfPCell(new Phrase(markdetstr, new Font(Font.COURIER, 9, Font.NORMAL)));
                    maintabcell.setBorderColor(Color.white);
                    maintabcell.setHorizontalAlignment(0);
                    maintabcell.setColspan(5);
                    headtab.addCell(maintabcell);

                    flag = true;
                }
                PdfPTable pageFooter = new PdfPTable(4);

                PdfPTable notifysecond = new PdfPTable(3);
                PdfPCell notifysecondcell = new PdfPCell(new Phrase("Prepaid \n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                notifysecondcell.setHorizontalAlignment(1);
                notifysecondcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                notifysecond.addCell(notifysecondcell);

                notifysecondcell = new PdfPCell(new Phrase("Weight Charge\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                notifysecondcell.setHorizontalAlignment(1);
                notifysecondcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                notifysecond.addCell(notifysecondcell);

                notifysecondcell = new PdfPCell(new Phrase("Collect\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                notifysecondcell.setHorizontalAlignment(1);
                notifysecondcell.disableBorderSide(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                notifysecond.addCell(notifysecondcell);

                PdfPCell incell = new PdfPCell(notifysecond);
                pageFooter.addCell(incell);

                PdfPCell manuseccell = new PdfPCell(new Phrase("Other Charges\n", new Font(Font.HELVETICA, 7, Font.NORMAL)));
                manuseccell.disableBorderSide(Rectangle.BOTTOM);
                manuseccell.setColspan(3);
                pageFooter.addCell(manuseccell);

                String pdata = "";
                String cdata = "";
                double tpdata = 0;
                double tcdata = 0;

                if ("P".equals(rs.getString("PC_AIR_FREIGHT"))) {
                    pdata = dtotal;
                    if (!dtotal.equals("")) {
                        tpdata = tpdata + Double.valueOf(dtotal);
                    }
                    if (pdata.equals("")) {
                        pdata = "PREPAID";
                    }
                }
                else if ("C".equals(rs.getString("PC_AIR_FREIGHT"))) {
                    cdata = dtotal;
                    if (!dtotal.equals("")) {
                        tcdata = tcdata + Double.valueOf(dtotal);
                    }
                    if (cdata.equals("")) {
                        cdata = "COLLECT";
                    }
                }

                PdfPTable footerrthird = new PdfPTable(2);
                PdfPCell footerthirdcell = new PdfPCell(new Phrase(pdata, new Font(Font.COURIER, 7, Font.NORMAL)));
                footerthirdcell.setHorizontalAlignment(1);
                footerthirdcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                footerrthird.addCell(footerthirdcell);

                footerthirdcell = new PdfPCell(new Phrase(cdata, new Font(Font.COURIER, 7, Font.NORMAL)));
                footerthirdcell.setHorizontalAlignment(1);
                footerthirdcell.disableBorderSide(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                footerrthird.addCell(footerthirdcell);

                incell = new PdfPCell(footerrthird);
                pageFooter.addCell(incell);

                PdfPCell manufacturerthirdcell = new PdfPCell(new Phrase(rs.getString("OTHER_CHARGES"), new Font(Font.COURIER, 8, Font.NORMAL)));
                manufacturerthirdcell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM);
                manufacturerthirdcell.setColspan(3);

                pageFooter.addCell(manufacturerthirdcell);

                PdfPTable second = new PdfPTable(3);
                PdfPCell secondcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                secondcell.setHorizontalAlignment(1);
                secondcell.disableBorderSide(Rectangle.BOTTOM);
                second.addCell(secondcell);

                secondcell = new PdfPCell(new Phrase("Valuation Charge\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                secondcell.setHorizontalAlignment(1);
                secondcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                second.addCell(secondcell);

                secondcell = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 6, Font.NORMAL)));
                secondcell.setHorizontalAlignment(1);
                secondcell.disableBorderSide(Rectangle.BOTTOM);
                second.addCell(secondcell);

                incell = new PdfPCell(second);

                pageFooter.addCell(incell);

                PdfPCell secondrightcell = new PdfPCell();
                secondrightcell.disableBorderSide(Rectangle.BOTTOM);
                secondrightcell.setColspan(3);
                pageFooter.addCell(secondrightcell);

                PdfPTable third = new PdfPTable(2);
                PdfPCell thirdcell = new PdfPCell();
                thirdcell.disableBorderSide(Rectangle.TOP);
                thirdcell.setHorizontalAlignment(1);
                thirdcell.setFixedHeight(10);
                third.addCell(thirdcell);

                thirdcell = new PdfPCell();
                thirdcell.setHorizontalAlignment(1);
                thirdcell.disableBorderSide(Rectangle.TOP);
                thirdcell.setFixedHeight(10);
                third.addCell(thirdcell);
                incell = new PdfPCell(third);

                pageFooter.addCell(incell);

                PdfPCell thirdsecondcell = new PdfPCell();
                thirdsecondcell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM);
                thirdsecondcell.setColspan(3);
                pageFooter.addCell(thirdsecondcell);

                PdfPTable thirdright = new PdfPTable(3);
                PdfPCell thirdrightcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                thirdrightcell.setHorizontalAlignment(1);
                thirdrightcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                thirdright.addCell(thirdrightcell);

                thirdrightcell = new PdfPCell(new Phrase("Tax\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                thirdrightcell.setHorizontalAlignment(1);
                thirdrightcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                thirdright.addCell(thirdrightcell);

                thirdrightcell = new PdfPCell(new Phrase("\n", new Font(Font.COURIER, 6, Font.NORMAL)));
                thirdrightcell.setHorizontalAlignment(1);
                thirdrightcell.disableBorderSide(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                thirdright.addCell(thirdrightcell);

                incell = new PdfPCell(thirdright);

                pageFooter.addCell(incell);

                PdfPCell secondritcell = new PdfPCell();
                secondritcell.disableBorderSide(Rectangle.BOTTOM);
                secondritcell.setColspan(3);
                pageFooter.addCell(secondritcell);

                pdata = "";
                cdata = "";
                if ("P".equals(rs.getString("PC_TAX"))) {
                    pdata = rs.getString("NO_OF_PACK_LIST");
                    if (!pdata.equals("")) {
                        tpdata = tpdata + Double.valueOf(pdata);
                    }
                    if (pdata.equals("")) {
                        pdata = "PREPAID";
                    }
                }
                else if ("C".equals(rs.getString("PC_TAX"))) {
                    cdata = rs.getString("NO_OF_PACK_LIST");
                    if (!cdata.equals("")) {
                        tcdata = tcdata + Double.valueOf(cdata);
                    }
                    if (cdata.equals("")) {
                        cdata = "COLLECT";
                    }
                }

                PdfPTable thirdthird = new PdfPTable(2);
                PdfPCell thirdthirdcell = new PdfPCell(new Phrase(pdata, new Font(Font.COURIER, 6, Font.NORMAL)));
                thirdthirdcell.setHorizontalAlignment(1);
                thirdthirdcell.setFixedHeight(10);
                thirdthirdcell.disableBorderSide(Rectangle.TOP);
                thirdthird.addCell(thirdthirdcell);

                thirdthirdcell = new PdfPCell(new Phrase(cdata, new Font(Font.COURIER, 6, Font.NORMAL)));
                thirdthirdcell.setHorizontalAlignment(1);
                thirdthirdcell.setFixedHeight(10);
                thirdthirdcell.disableBorderSide(Rectangle.TOP);
                thirdthird.addCell(thirdthirdcell);
                incell = new PdfPCell(thirdthird);

                pageFooter.addCell(incell);

                PdfPCell thirdsecdcell = new PdfPCell();
                thirdsecdcell.disableBorderSide(Rectangle.TOP | Rectangle.BOTTOM);
                thirdsecdcell.setColspan(3);
                pageFooter.addCell(thirdsecdcell);

                PdfPTable footerfive = new PdfPTable(2);
                PdfPCell footerfivecell = new PdfPCell(new Phrase("Total other Charges Due Agent\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));

                footerfivecell.setFixedHeight(10);
                footerfivecell.setHorizontalAlignment(1);
                footerfivecell.setColspan(2);
                footerfive.addCell(footerfivecell);

                pdata = "";
                cdata = "";
                if ("P".equals(rs.getString("PC_DUE_AGENT"))) {
                    pdata = rs.getString("NO_OF_EXP_LIC");
                    if (!pdata.equals("")) {
                        tpdata = tpdata + Double.valueOf(pdata);
                    }
                }
                else if ("C".equals(rs.getString("PC_DUE_AGENT"))) {
                    cdata = rs.getString("NO_OF_EXP_LIC");
                    if (!cdata.equals("")) {
                        tcdata = tcdata + Double.valueOf(cdata);
                    }
                }

                footerfivecell = new PdfPCell(new Phrase(pdata, new Font(Font.COURIER, 6, Font.NORMAL)));
                footerfivecell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT);
                footerfivecell.setHorizontalAlignment(1);
                footerfivecell.setFixedHeight(10);
                footerfive.addCell(footerfivecell);

                footerfivecell = new PdfPCell(new Phrase(cdata, new Font(Font.COURIER, 6, Font.NORMAL)));
                footerfivecell.disableBorderSide(Rectangle.RIGHT);
                footerfivecell.setHorizontalAlignment(1);
                footerfivecell.setFixedHeight(10);
                footerfive.addCell(footerfivecell);

                footerfivecell = new PdfPCell(new Phrase("Total other Charges Due Carrier\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                footerfivecell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                footerfivecell.setFixedHeight(10);
                footerfivecell.setHorizontalAlignment(1);
                footerfivecell.setColspan(2);
                footerfive.addCell(footerfivecell);

                pdata = "";
                cdata = "";
                if ("P".equals(rs.getString("PC_DUE_CARRIER"))) {
                    pdata = rs.getString("NO_OF_CO");
                    if (!pdata.equals("")) {
                        tpdata = tpdata + Double.valueOf(pdata);
                    }
                }
                else if ("C".equals(rs.getString("PC_DUE_CARRIER"))) {
                    cdata = rs.getString("NO_OF_CO");
                    if (!cdata.equals("")) {
                        tcdata = tcdata + Double.valueOf(cdata);
                    }
                }

                footerfivecell = new PdfPCell(new Phrase(pdata, new Font(Font.COURIER, 6, Font.NORMAL)));
                footerfivecell.setFixedHeight(10);
                footerfivecell.setHorizontalAlignment(1);
                footerfivecell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                footerfive.addCell(footerfivecell);

                footerfivecell = new PdfPCell(new Phrase(cdata, new Font(Font.COURIER, 6, Font.NORMAL)));
                footerfivecell.disableBorderSide(Rectangle.RIGHT | Rectangle.BOTTOM);
                footerfivecell.setHorizontalAlignment(1);
                footerfive.addCell(footerfivecell);

                incell = new PdfPCell(footerfive);

                pageFooter.addCell(incell);

                String lineTwo = "                                                                 __________________________________________________________";
                PdfPCell manyfivecell = new PdfPCell(new Phrase("Shipper certifies that the particular on the face hereof are corredt and that insofar as any part of the consigment contains dangerous goods, such part is properly described by name and is in proper condition for carriage by air according to the applicable Dangerous Goods Regilations.\n\n\n\n\n" + lineTwo, new Font(Font.HELVETICA, 5, Font.NORMAL)));
                manyfivecell.setColspan(3);
                manyfivecell.disableBorderSide(Rectangle.BOTTOM);
                pageFooter.addCell(manyfivecell);

                pdata = "";
                cdata = "";

                if ((rate == null || rate.equals("") || rate.equals("0"))) {
                    if ("P".equals(rs.getString("PC_AIR_FREIGHT"))) {
                        pdata = "PREPAID";
                    }
                    else {
                        cdata = "COLLECT";
                    }
                }

                PdfPTable footerfiveextra = new PdfPTable(2);
                footerfivecell = new PdfPCell(new Phrase(pdata, new Font(Font.HELVETICA, 6, Font.NORMAL)));
                footerfivecell.setFixedHeight(10);
                footerfivecell.setHorizontalAlignment(1);
                footerfivecell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                footerfiveextra.addCell(footerfivecell);

                footerfivecell = new PdfPCell(new Phrase(cdata, new Font(Font.HELVETICA, 6, Font.NORMAL)));
                footerfivecell.disableBorderSide(Rectangle.RIGHT | Rectangle.BOTTOM);
                footerfivecell.setFixedHeight(10);
                footerfivecell.setHorizontalAlignment(1);
                footerfiveextra.addCell(footerfivecell);
                incell = new PdfPCell(footerfiveextra);
                pageFooter.addCell(incell);

                manyfivecell = new PdfPCell(new Phrase("Signature of Shipper or his Agent", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                manyfivecell.setHorizontalAlignment(1);
                manyfivecell.setColspan(3);
                manyfivecell.setFixedHeight(10);
                manyfivecell.disableBorderSide(Rectangle.TOP);
                pageFooter.addCell(manyfivecell);

                PdfPTable six = new PdfPTable(3);
                PdfPCell sixcell = new PdfPCell(new Phrase("Total prepaid \n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                sixcell.setHorizontalAlignment(1);
                sixcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                six.addCell(sixcell);

                sixcell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                sixcell.disableBorderSide(Rectangle.BOTTOM);
                six.addCell(sixcell);

                sixcell = new PdfPCell(new Phrase("Total collect\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                sixcell.setHorizontalAlignment(1);
                sixcell.disableBorderSide(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                six.addCell(sixcell);

                incell = new PdfPCell(six);
                pageFooter.addCell(incell);

                PdfPCell datecell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("DATE_OF_ISSUE")) + "\n", new Font(Font.COURIER, 7, Font.NORMAL)));
                datecell.setHorizontalAlignment(1);
                datecell.disableBorderSide(Rectangle.BOTTOM | Rectangle.RIGHT);
                pageFooter.addCell(datecell);

                datecell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("POI_NAME")) + "\n", new Font(Font.COURIER, 7, Font.NORMAL)));
                datecell.setHorizontalAlignment(1);
                datecell.disableBorderSide(Rectangle.BOTTOM | Rectangle.RIGHT | Rectangle.LEFT);
                pageFooter.addCell(datecell);

                datecell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                datecell.setHorizontalAlignment(1);
                datecell.disableBorderSide(Rectangle.BOTTOM | Rectangle.LEFT);
                pageFooter.addCell(datecell);

                PdfPTable seven = new PdfPTable(2);
                PdfPCell sevencell = new PdfPCell(new Phrase(String.valueOf(tpdata > 0 ? tpdata : ""), new Font(Font.HELVETICA, 6, Font.NORMAL)));
                sevencell.setHorizontalAlignment(1);
                sevencell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                seven.addCell(sevencell);

                sevencell = new PdfPCell(new Phrase(String.valueOf(tcdata > 0 ? tcdata : ""), new Font(Font.HELVETICA, 6, Font.NORMAL)));
                sevencell.disableBorderSide(Rectangle.BOTTOM);
                sevencell.setHorizontalAlignment(1);
                //sevencell.setMinimumHeight(10);
                seven.addCell(sevencell);

                incell = new PdfPCell(seven);
                pageFooter.addCell(incell);

                PdfPCell datesecondcell = new PdfPCell();
                datesecondcell.setHorizontalAlignment(1);
                datesecondcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                datesecondcell.setColspan(3);
                pageFooter.addCell(datesecondcell);

                PdfPTable eight = new PdfPTable(2);
                sevencell = new PdfPCell(new Phrase("Currency Conversion Rates\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                sevencell.setHorizontalAlignment(1);
                sevencell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                eight.addCell(sevencell);

                sevencell = new PdfPCell(new Phrase("cc charges in Dest. Currency\n", new Font(Font.HELVETICA, 5, Font.NORMAL)));
                sevencell.disableBorderSide(Rectangle.BOTTOM);
                eight.addCell(sevencell);

                incell = new PdfPCell(eight);
                pageFooter.addCell(incell);

                String lineThree = "__________________________________________________________________________________";

                PdfPCell manysixcell = new PdfPCell(new Phrase(lineThree, new Font(Font.HELVETICA, 7, Font.NORMAL)));

                manysixcell.setHorizontalAlignment(1);
                manysixcell.setColspan(3);
                manysixcell.disableBorderSide(Rectangle.BOTTOM | Rectangle.TOP);
                pageFooter.addCell(manysixcell);

                PdfPTable nine = new PdfPTable(2);
                sevencell = new PdfPCell();
                sevencell.setHorizontalAlignment(1);
                sevencell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                nine.addCell(sevencell);

                sevencell = new PdfPCell(new Phrase("\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                sevencell.disableBorderSide(Rectangle.BOTTOM);
                nine.addCell(sevencell);

                incell = new PdfPCell(nine);
                pageFooter.addCell(incell);

                manysixcell = new PdfPCell(new Phrase("Executed on    (Date)", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                manysixcell.setHorizontalAlignment(1);
                manysixcell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT);
                pageFooter.addCell(manysixcell);

                manysixcell = new PdfPCell(new Phrase("at     (Place)", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                manysixcell.setHorizontalAlignment(1);
                manysixcell.disableBorderSide(Rectangle.TOP | Rectangle.RIGHT | Rectangle.LEFT);
                pageFooter.addCell(manysixcell);

                manysixcell = new PdfPCell(new Phrase("Signature of Issuing or its agent", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                manysixcell.setHorizontalAlignment(0);
                manysixcell.disableBorderSide(Rectangle.TOP | Rectangle.LEFT);
                pageFooter.addCell(manysixcell);
                PdfPTable lastfirst = new PdfPTable(2);

                PdfPCell lastfirstcell = new PdfPCell(new Phrase("For Carrier's Use only at Destination \n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                lastfirstcell.setHorizontalAlignment(1);
                lastfirstcell.disableBorderSide(Rectangle.LEFT | Rectangle.TOP | Rectangle.BOTTOM);
                lastfirst.addCell(lastfirstcell);

                lastfirstcell = new PdfPCell(new Phrase("charges at Desstination\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                lastfirstcell.setHorizontalAlignment(1);
                lastfirstcell.disableBorderSide(Rectangle.RIGHT | Rectangle.TOP | Rectangle.BOTTOM);
                lastfirst.addCell(lastfirstcell);

                incell = new PdfPCell(lastfirst);
                pageFooter.addCell(incell);

                PdfPCell lastcell = new PdfPCell(new Phrase("Total collect charges\n", new Font(Font.HELVETICA, 6, Font.NORMAL)));
                lastcell.setHorizontalAlignment(1);
                pageFooter.addCell(lastcell);

                lastcell = new PdfPCell(new Phrase("HAWB NO:\n", new Font(Font.HELVETICA, 8, Font.BOLD)));
                lastcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                lastcell.setHorizontalAlignment(2);
                pageFooter.addCell(lastcell);

                lastcell = new PdfPCell(new Phrase((String) ApplicationUtil.checkForNull(rs.getString("HAWB_NUMBER")), new Font(Font.HELVETICA, 8, Font.BOLD)));
                lastcell.disableBorderSide(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
                lastcell.setHorizontalAlignment(0);

                pageFooter.addCell(lastcell);

                float[] pgfooterwidths = {30, 25, 25, 20};
                pageFooter.setWidths(pgfooterwidths);
                pageFooter.setTotalWidth(page.getWidth() - document.leftMargin() - document.rightMargin());
                pageFooter.writeSelectedRows(0, -1, document.leftMargin(), 185, writer.getDirectContent());

                maintable.setWidthPercentage(100);
                document.add(maintable);
                document.add(headtab);
                document.close();

            }
        }
        catch (DocumentException de) {
            de.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        finally {
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
            }
            catch (SQLException ex) {
                Logger.getLogger(AirwayBillPDF.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return awbillNo;
    }//end writeUCMpdf
}
