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
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PDFJDBCConnectionReport extends PDFReport {

    protected JdbcTemplate jdbcTemplate = null;

    public PDFJDBCConnectionReport(String reportTitle, Map params, HttpServletResponse response, ServletContext context) {
        super(reportTitle, params, response, context);
    }
    
    @Override
    public void createReport() throws JRException {
        JasperReport jasperReport = null;
        jasperReport = JasperCompileManager.compileReport(context.getRealPath(reportTitle));
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
