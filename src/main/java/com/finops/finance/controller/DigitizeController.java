package com.finops.finance.controller;

import com.finops.controller.AbstractController;
import com.finops.finance.bean.InvoiceBean;
import com.finops.finance.service.DigitizeService;
import com.finops.finance.service.InvoiceService;
import com.finops.freight.bean.BLBean;
import com.finops.freight.bean.SIBean;
import com.finops.freight.bean.SOBean;
import com.finops.freight.service.BLService;
import com.finops.freight.service.SIService;
import com.finops.freight.service.SOService;
import com.finops.report.model.ReportBean;
import com.finops.util.AzureBlobUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;

@Controller
public class DigitizeController extends AbstractController {

    private static String baseContainer = "dcopy";

    @Autowired
    private DigitizeService digitizeService;

    @Autowired
    private SOService soService;

    @Autowired
    private BLService blService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private SIService siService;

    @RequestMapping("/digitalCopy.fin")
        public ModelAndView digitalCopy(@ModelAttribute("reportBean") ReportBean reportBean,
                                              HttpServletResponse response,
                                              HttpSession session) throws IOException, Exception {
        setSessionData(reportBean, session);

        return new ModelAndView("finact/DigitalCopySearch", "command", reportBean);
    }

    @RequestMapping("/digitalCopyUpload.fin")
    public ModelAndView digitalCopyUpload(@ModelAttribute("reportBean") ReportBean reportBean,
                                              @RequestParam("param100") MultipartFile file,
                                              HttpServletResponse response,
                                              HttpSession session) throws IOException, Exception {
        setSessionData(reportBean, session);
        String loc = reportBean.getDigitalCopyPath();
        if("N".equalsIgnoreCase((String)session.getAttribute("isPrimary"))){
            loc = "\\\\LENOVO-LA0X1333\\DigitalCopy\\";
        }   
        String blobPath = "digital/"+reportBean.getAcctYear();
        String dirPath = loc + blobPath;
        if("SO".equalsIgnoreCase(reportBean.getParam1())){
            blobPath = blobPath + "/SO/"+reportBean.getParam2();
        }
        else if("BL".equalsIgnoreCase(reportBean.getParam1())
        || "INVOICE".equalsIgnoreCase(reportBean.getParam1())
                || "EXPENSE".equalsIgnoreCase(reportBean.getParam1())){
            blobPath = blobPath + "/BL/"+reportBean.getParam3();
        }

        File theDir = new File(dirPath);
        if (!theDir.exists()) {
            theDir.mkdir();
        }

        String fileName = file.getOriginalFilename();

        byte[] bytes = new byte[0];
        try {
            bytes = file.getBytes();
            //String name = reportBean.getParam1()+ "_" +fileName;
            Path path = Paths.get(dirPath + "/" + fileName);
            Files.write(path, bytes);
            reportBean.setParam9("file:///"+path.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String blobUrl = AzureBlobUtil.uploadFile(fileName, new ByteArrayInputStream(bytes), bytes.length, blobPath, reportBean.getAzureEndPoint());
        reportBean.setParam10(blobUrl);
        digitizeService.saveDigitalCopyRecord(reportBean);

        return new ModelAndView("finact/DigitalCopySearch", "command", reportBean);
    }

    @RequestMapping("/digitalCopyNew.fin")
    public ModelAndView digitalCopyNew(@ModelAttribute("reportBean") ReportBean reportBean,
                                    HttpServletResponse response,
                                    HttpSession session) throws IOException, Exception {
        setSessionData(reportBean, session);

        return new ModelAndView("finact/DigitalCopy", "command", reportBean);
    }

    @RequestMapping(value = "/digitalCopyGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String digitalCopyGson(@ModelAttribute("reportBean") ReportBean reportBean, HttpSession session,
                     HttpServletRequest request) {
        setSessionData(reportBean, session);
        reportBean.setStart(Integer.parseInt(request.getParameter("start")));
        reportBean.setLength(Integer.parseInt(request.getParameter("length")));
        reportBean.setSearchFieldValueList(getSearchFieldValues(request, 11));
        List<ReportBean> list = digitizeService.viewDigitalCopyRecords(reportBean);
        reportBean.setAaData(list);
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(reportBean);
    }

    @RequestMapping("/fetchDigitalCopy.fin")
    public ModelAndView fetchDigitalCopy(@ModelAttribute("reportBean") ReportBean reportBean,
                                    HttpServletResponse response,
                                    HttpSession session) throws IOException, Exception {
        setSessionData(reportBean, session);
        if("SO".equalsIgnoreCase(reportBean.getParam1())){
            if("SEA".equalsIgnoreCase(reportBean.getParam11())) {
                seaSo(reportBean);
            }
            else{
                airSo(reportBean);
            }
        }
        else if("BL".equalsIgnoreCase(reportBean.getParam1())){
            BLBean blBean = new BLBean();
            blBean.setBlNumber(reportBean.getParam13());
            blBean.setLoadingAgent(reportBean.getLoadingAgent());
            blService.retrieve(blBean);
            reportBean.setParam3(blBean.getBlNumber());
            reportBean.setParam4(blBean.getShipperName());
            reportBean.setParam5(blBean.getShipper());
            reportBean.setParam6(blBean.getConsigneeName());
            reportBean.setParam7(blBean.getConsignee());
            reportBean.setParam2(blBean.getSoNumber());
            reportBean.setParam14(blBean.getJobNumber());
            if(StringUtils.hasText(blBean.getBlNumber())){
                reportBean.setIntparam1(1);
            }
        }
        else if("INVOICE".equalsIgnoreCase(reportBean.getParam1())){
            InvoiceBean billingBean = new InvoiceBean();
            billingBean.setBillNo(reportBean.getParam13());
            billingBean.setCompanyId(reportBean.getCompanyId());
            invoiceService.findInvoiceById(billingBean, true);
            reportBean.setParam3(billingBean.getBlNo());
            reportBean.setParam4(billingBean.getBilltoName());
            reportBean.setParam5(billingBean.getBillTo());
            //reportBean.setParam6(billingBean.getConsigneeName());
            reportBean.setParam14(billingBean.getJobNumber());
            if(StringUtils.hasText(billingBean.getBillTo())){
                reportBean.setIntparam1(1);
            }
        }
        return new ModelAndView("finact/DigitalCopy", "command", reportBean);
    }

    @RequestMapping(value = "/deleteDigitalCopy", method = RequestMethod.POST)
    public ModelAndView deleteVoucher(@ModelAttribute("reportBean") ReportBean reportBean,
                                      HttpSession session) throws ParseException {
        setSessionData(reportBean, session);
        digitizeService.deleteDigitalCopy(reportBean);
        return new ModelAndView("finact/DigitalCopySearch", "command", reportBean);
    }

    private void seaSo(ReportBean reportBean){
        SOBean soBean = new SOBean();
        soBean.setSoNumber(reportBean.getParam13());
        soBean.setLoadingAgent(reportBean.getLoadingAgent());
        SOBean resultBean = null;
        resultBean = soService.retrieveBySo(soBean, false);
        reportBean.setParam3(resultBean.getBlNumber());
        reportBean.setParam4(resultBean.getShipperName());
        reportBean.setParam5(resultBean.getShipper());
        reportBean.setParam6(resultBean.getConsigneeName());
        reportBean.setParam7(resultBean.getConsignee());
        reportBean.setParam2(resultBean.getSoNumber());
        reportBean.setParam14(resultBean.getJobNumber());
        if (StringUtils.hasText(resultBean.getSoNumber())) {
            reportBean.setIntparam1(1);
        }
    }

    private void airSo(ReportBean reportBean){
        SIBean soBean = new SIBean();
        soBean.setSoNumber(reportBean.getParam13());
        soBean.setLoadingAgent(reportBean.getLoadingAgent());
        SIBean resultBean = null;
        resultBean = siService.retrieveBySI(soBean, false);
        reportBean.setParam3(resultBean.getBlNumber());
        reportBean.setParam4(resultBean.getShipperName());
        reportBean.setParam5(resultBean.getShipper());
        reportBean.setParam6(resultBean.getConsigneeName());
        reportBean.setParam7(resultBean.getConsignee());
        reportBean.setParam2(resultBean.getSoNumber());
        reportBean.setParam14(resultBean.getJobNumber());
        if (StringUtils.hasText(resultBean.getSoNumber())) {
            reportBean.setIntparam1(1);
        }
    }

}
