package com.finops.report;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public abstract class AbstractReport {
    protected String reportTitle;
    protected String reportFormat;
    protected HttpServletResponse response;
    protected Map parameters;
     
    public void generateReport() throws Exception{
        createData();
        preReport();
        createReport();
        postReport();
        setContentType();
        flushReport();
    }
    
    public void generateReportStream() throws Exception{
        createData();
        preReport();
        createReport();
        postReport();
        setContentType();
        flushReportStream();
    }
    
    public void flushReportStream() throws Exception{}
    public ByteArrayOutputStream getByteArrayOutputStream(){
        return null;
    }
    
    public byte[] getBytes() {
        return null;
    }
    
    public abstract void createData();
    public abstract void setContentType();
    public abstract void createReport() throws Exception;
    public abstract void flushReport() throws Exception;
    public void preReport(){}
    public void postReport(){}
}
