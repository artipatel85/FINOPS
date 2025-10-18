/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.finops.finance.report;


import com.finops.report.AbstractReport;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PDFReport extends AbstractReport {

    protected Object data = null;
    protected JasperPrint jasperPrint;
    protected ServletContext context;
    protected ByteArrayOutputStream out;
    protected byte[] bytes;

    public PDFReport(String reportTitle, Map params, HttpServletResponse response, ServletContext context) {
        this.reportFormat = "PDF";
        this.reportTitle = reportTitle;
        this.parameters = params;
        this.response = response;
        this.context = context;
        if (this.parameters == null) {
            this.parameters = new HashMap();
        }
    }

    @Override
    public void setContentType() {
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "filename=DetailReport.pdf");
    }

    @Override
    public void createReport() throws JRException {
        JasperReport jasperReport = null;
        jasperReport = JasperCompileManager.compileReport(context.getRealPath(reportTitle));
        JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource((List) data);
        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, ds);
    }

    @Override
    public void flushReport() throws Exception {
        ServletOutputStream ouputStream = response.getOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, ouputStream);
        exporter.exportReport();
        ouputStream.flush();
        ouputStream.close();
    }

    @Override
    public void flushReportStream() throws Exception {
//        out = new ByteArrayOutputStream();
//        JRPdfExporter exporter = new JRPdfExporter();
//        exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING.JASPER_PRINT, jasperPrint);
//        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
//        exporter.exportReport();
        bytes = JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public ByteArrayOutputStream getByteArrayOutputStream() {
        return out;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
