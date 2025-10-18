package com.finops.partner.controller;

import com.finops.admin.model.AdminBean;
import com.finops.controller.AbstractController;
import com.finops.partner.model.PartnerBean;
import com.finops.partner.service.PartnerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Controller
public class PartnerController extends AbstractController {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    CacheManager cacheManager;

    Logger logger = LoggerFactory.getLogger(PartnerService.class);

    @RequestMapping("/partnerSearch.fin")
    public ModelAndView partnerSearch(@ModelAttribute("adminBean") AdminBean adminBean,
                                   @RequestParam("param") String param) {
        switch (param) {
            case "PartnerAccount" -> {
                return new ModelAndView("partner/PartnerAccountSearch", "command", adminBean);
            }
            case "KYC" -> {
                return new ModelAndView("partner/PartnerSearchKyc", "command", adminBean);
            }
        }
        return null;
    }


    @RequestMapping(value = "/partnerGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String adminGson(@ModelAttribute("partnerBean") PartnerBean partnerBean, HttpServletRequest request,
                                          @RequestParam("param") String param) throws SQLException {
        setSessionData(partnerBean, request.getSession());
        partnerBean.setStart(Integer.parseInt(request.getParameter("start")));
        partnerBean.setLength(Integer.parseInt(request.getParameter("length")));
        partnerBean.setSearchFieldValueList(getSearchFieldValues(request, 11));
        List<PartnerBean> list = null;
        switch (param) {
            case "PartnerAccount" -> list = partnerService.findPartnersByPage(partnerBean);
            case "KYC" -> list = partnerService.findKYCByPage(partnerBean);
        }
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping("/partnerKyc.fin")
    public ModelAndView partner(@ModelAttribute("partnerBean") PartnerBean partnerBean,
                                @RequestParam("kycId") int kycId, HttpSession session) throws SQLException {
        setSessionData(partnerBean, session);
        partnerBean.setId(kycId);

        if (kycId > 0) {
            partnerService.retrieveKYC(partnerBean);
            String note = partnerBean.getNote();
            partnerBean.setAction("UPDATE");
            setSessionData(partnerBean, session);
            partnerBean.setNote(note);
        } else {
            partnerBean.setNote("");
            partnerBean.setCreditLimit(0.00);
            partnerBean.setCreditPeriod(0);
            partnerBean.setAction("SAVE");
        }

        return new ModelAndView("partner/PartnerKyc", "command", partnerBean);
    }

    @RequestMapping("/partnerAccount.fin")
    public ModelAndView partnerAccount(@ModelAttribute("partnerBean") PartnerBean partnerBean,
                                       @RequestParam("partnerCode") String partnerCode, HttpSession session) {
        setSessionData(partnerBean, session);
        partnerBean.setPartnerCode(partnerCode);
        partnerBean.setAction("UPDATE");
        partnerBean = partnerService.retrievePartnerAccount(partnerBean);
        return new ModelAndView("partner/PartnerAccount", "command", partnerBean);
    }

    @RequestMapping("/createPartnerKYC.fin")
    public ModelAndView createPartnerKYC(@ModelAttribute("partnerBean") PartnerBean partnerBean,
                                         @RequestParam("param100") MultipartFile file1,
                                         @RequestParam("param101") MultipartFile file2,
                                         @RequestParam("param102") MultipartFile file3,
                                         @RequestParam("param103") MultipartFile file4,
                                         HttpSession session) throws IOException, Exception {
        String note = partnerBean.getNote();
        setSessionData(partnerBean, session);
        partnerBean.setNote(note);
        partnerBean.setAttachment1File(file1);
        partnerBean.setAttachment2File(file2);
        partnerBean.setAttachment3File(file3);
        partnerBean.setAttachment4File(file4);

        partnerBean.setFilterValue(partnerBean.getFileserverRootPath());
        if ("SAVE".equalsIgnoreCase(partnerBean.getAction())) {
            if (partnerService.isDuplicatePartnerKyc(partnerBean)) {
                partnerBean.setErrorMsg("Company already created for KYC");
                return new ModelAndView("partner/PartnerKyc", "command", partnerBean);
            }
            if (partnerService.isDuplicatePartner(partnerBean)) {
                partnerBean.setErrorMsg("Duplicate Company Name.");
                return new ModelAndView("partner/PartnerKyc", "command", partnerBean);
            }
            if (!partnerBean.isAdministrator() && StringUtils.hasText(partnerBean.getGstinNo()) && partnerService.isDuplicateGSTIN(partnerBean)) {
                partnerBean.setErrorMsg("Duplicate GSTIN Number.");
                return new ModelAndView("partner/PartnerKyc", "command", partnerBean);
            }
            partnerService.createPartnerKYC(partnerBean);
        } else {
            partnerService.updatePartnerKYC(partnerBean, session);
        }
        cacheManager.getCache("partners").clear();
        if (StringUtils.hasText(partnerBean.getPartnerCode())) {
            return new ModelAndView("partner/PartnerAccountSearch", "command", partnerBean);
        }

        return new ModelAndView("partner/PartnerSearchKyc", "command", partnerBean);
    }

    @RequestMapping("/replicatePartnerKYC.fin")
    public ModelAndView replicatePartnerKYC(@ModelAttribute("partnerBean") PartnerBean partnerBean,
                                            @RequestParam("param100") MultipartFile file1,
                                            @RequestParam("param101") MultipartFile file2,
                                            @RequestParam("param102") MultipartFile file3,
                                            @RequestParam("param103") MultipartFile file4,
                                            HttpSession session) throws IOException, Exception {
        setSessionData(partnerBean, session);

        partnerBean.setAttachment1File(file1);
        partnerBean.setAttachment2File(file2);
        partnerBean.setAttachment3File(file3);
        partnerBean.setAttachment4File(file4);

        partnerBean.setLoadingAgentCode(partnerBean.getLoadingAgent());
        partnerBean.setReplicate(true);
        partnerService.updatePartnerKYC(partnerBean, session);
        cacheManager.getCache("partners").clear();

        return new ModelAndView("partner/PartnerAccountSearch", "command", partnerBean);

    }

    @RequestMapping("/replicatePartner.fin")
    public ModelAndView replicatePartner(@ModelAttribute("partnerBean") PartnerBean partnerBean,
                                            @RequestParam("param100") MultipartFile file1,
                                            @RequestParam("param101") MultipartFile file2,
                                            @RequestParam("param102") MultipartFile file3,
                                            @RequestParam("param103") MultipartFile file4,
                                            HttpSession session) throws IOException, Exception {
        setSessionData(partnerBean, session);

        partnerBean.setAttachment1File(file1);
        partnerBean.setAttachment2File(file2);
        partnerBean.setAttachment3File(file3);
        partnerBean.setAttachment4File(file4);

        if(partnerBean.getLoadingAgent().equalsIgnoreCase(partnerBean.getBranch())
                || partnerService.isDuplicatePartnerCode(partnerBean)){
            logger.info("Partner can not be imported in the same branch");
            partnerBean.setErrorMsg("Partner can not be imported in the same branch");
            return new ModelAndView("partner/PartnerAccount", "command", partnerBean);
        }

        partnerBean.setLoadingAgentCode(partnerBean.getLoadingAgent());
        partnerBean.setReplicate(true);
        partnerService.replicatePartner(partnerBean);
        cacheManager.getCache("partners").clear();

        return new ModelAndView("partner/PartnerAccountSearch", "command", partnerBean);

    }

    @RequestMapping("/updatePartnerAccount.fin")
    public ModelAndView updatePartnerAccount(@ModelAttribute("partnerBean") PartnerBean partnerBean,
                                         @RequestParam("param100") MultipartFile file1,
                                         @RequestParam("param101") MultipartFile file2,
                                         @RequestParam("param102") MultipartFile file3,
                                         @RequestParam("param103") MultipartFile file4,
                                         HttpSession session) throws IOException, Exception {
        String note = partnerBean.getNote();
        setSessionData(partnerBean, session);
        partnerBean.setNote(note);
        partnerBean.setAttachment1File(file1);
        partnerBean.setAttachment2File(file2);
        partnerBean.setAttachment3File(file3);
        partnerBean.setAttachment4File(file4);

        partnerBean.setFilterValue(partnerBean.getFileserverRootPath());
        partnerService.updatePartnerAccount(partnerBean, session);
        cacheManager.getCache("partners").clear();

        return new ModelAndView("partner/PartnerAccount", "command", partnerBean);
    }

    @RequestMapping("/kycAttachment.fin")
    public ModelAndView kycAttachment(@ModelAttribute("partnerBean") PartnerBean partnerBean,@RequestParam("kycId") int kycId,
                                      @RequestParam("partnerName") String partnerName, @RequestParam("fileName") String fileName,
                                      HttpServletResponse response, HttpSession session) throws SQLException, IOException {

        setSessionData(partnerBean, session);
        //String path = partnerBean.getFileserverRootPath()+ Constants.PARTNER_KYC_MODULE+"\\"+partnerName.trim()+"\\"+fileName;
        //String localPath = partnerBean.getFileserverRootPath()+ "partner\\";
        String blobContainer = "partner/"+kycId;
        String endPoint = partnerBean.getAzureEndPoint();
        String localPath = "C:\\temp\\";

        partnerService.getAttachmentFile(localPath, blobContainer, response, fileName, partnerBean);
        return new ModelAndView("operation/PartnerKyc", "command", partnerBean);
    }

    @RequestMapping("/deletePartnerKyc.fin")
    public ModelAndView deletePartnerKyc(@ModelAttribute("partnerBean") PartnerBean partnerBean,
                                         HttpServletResponse response) throws SQLException {
        partnerService.deletePartnerKYC(partnerBean);
        return new ModelAndView("partner/PartnerSearchKyc", "command", partnerBean);
    }

    @RequestMapping("/importToFinance.fin")
    public ModelAndView importToFinance(@ModelAttribute("partnerBean") PartnerBean partnerBean,
                                            HttpSession session) throws IOException, Exception {
        setSessionData(partnerBean, session);
        partnerBean.setLoadingAgentCode(partnerBean.getLoadingAgent());
        partnerBean.setReplicate(true);
        partnerService.importPartnerToFinace(partnerBean, session);

        if(StringUtils.hasText(partnerBean.getErrorMsg())){
            return new ModelAndView("partner/PartnerKyc", "command", partnerBean);
        }

        return new ModelAndView("partner/PartnerAccountSearch", "command", partnerBean);

    }

}
