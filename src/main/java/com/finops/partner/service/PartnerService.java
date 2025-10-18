package com.finops.partner.service;

import com.finops.admin.controller.LoginController;
import com.finops.admin.model.FormBean;
import com.finops.admin.model.GeneralBean;
import com.finops.admin.model.LoginBean;
import com.finops.aop.MeasureTime;
import com.finops.finance.bean.LedgerBean;
import com.finops.finance.dao.AccountDAO;
import com.finops.partner.dao.PartnerDAO;
import com.finops.partner.model.PartnerBean;
import com.finops.util.AzureBlobUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class PartnerService {

    private static final Logger logger = LoggerFactory.getLogger(PartnerService.class);

    @Autowired
    private PartnerDAO partnerDAO;

    @Autowired
    private AccountDAO accountDAO;

    public List<PartnerBean> findPartnersByPage(PartnerBean bean){
        return partnerDAO.findPartnersByPage(bean);
    }

    public List<PartnerBean> findKYCByPage(PartnerBean bean){
        return partnerDAO.findKYCByPage(bean);
    }

    public void retrieveKYC(PartnerBean partnerBean) {
        PartnerBean entity = partnerDAO.retrieveKYC(partnerBean);
        BeanUtils.copyProperties(entity, partnerBean);

    }

    public PartnerBean retrievePartnerAccount(PartnerBean partnerBean) {
        return partnerDAO.retrievePartnerAccount(partnerBean);
    }

    @MeasureTime
    @Cacheable("partners")
    public Map<String, PartnerBean> fetchAllPartners(String branch){
        PartnerBean bean = new PartnerBean();
        bean.setLoadingAgent(branch);
        List<PartnerBean> partnerBeanList = partnerDAO.fetchAllPartners(bean);

        Map<String, PartnerBean> partnerMap =
                new HashMap<>();


        for(PartnerBean pb : partnerBeanList){
            partnerMap.put(pb.getPartnerCode(), pb);
        }
        Map<String, Map<String, PartnerBean>> partnerCache = new HashMap<>();
        partnerCache.put(branch, partnerMap);
        return partnerCache.get(branch);
    }

    public PartnerBean getPartnerData(String partnerCode, String branch, PartnerService partnerService) {
        Map<String, PartnerBean> pbMap = partnerService.fetchAllPartners(branch);

        if(partnerCode != null && pbMap.get(partnerCode) != null) {
            return pbMap.get(partnerCode);
        }
        return new PartnerBean();
    }


    public boolean isDuplicatePartnerKyc(PartnerBean partnerBean) {
        return partnerDAO.isDuplicatePartnerKyc(partnerBean);
    }

    public boolean isDuplicatePartner(PartnerBean partnerBean) {
        return partnerDAO.isDuplicatePartner(partnerBean);
    }

    public boolean isDuplicatePartnerCode(PartnerBean partnerBean) {
        return partnerDAO.isDuplicatePartnerCode(partnerBean);
    }

    public boolean isDuplicateGSTIN(PartnerBean partnerBean) {
        return partnerDAO.isDuplicateGSTIN(partnerBean);
    }

    @Transactional
    public void createPartnerKYC(PartnerBean partnerBean) {
        partnerDAO.createPartnerKYC(partnerBean);
    }

    @Transactional
    @MeasureTime
    public void updatePartnerKYC(PartnerBean partnerBean, HttpSession session) {
        String tempPartnerCode = partnerBean.getPartnerCode();
        if(hasAccess(session) && ("PROMOTE".equalsIgnoreCase(partnerBean.getAction()) || partnerBean.isReplicate())) {
            if (!StringUtils.hasText(partnerBean.getPartnerCode()) || partnerBean.isReplicate()) {
                if(!partnerBean.isReplicate()){
                    partnerDAO.createPartner(partnerBean);
                }
                else{
                    partnerDAO.createPartnerCode(partnerBean);
                }
                if ("Y".equalsIgnoreCase(partnerBean.getCreditorType()) || "Y".equalsIgnoreCase(partnerBean.getDebtorType())) {
                    if(!partnerBean.isReplicate()){
                        accountDAO.importPartnerInToFinance(partnerBean);
                    }
                }
                partnerDAO.createPartnerDetails(partnerBean);
            }
            else{
                partnerDAO.updatePartnerKYC(partnerBean);
            }
        }
        if (!StringUtils.hasText(tempPartnerCode) && !partnerBean.isReplicate()) {
            partnerDAO.updatePartnerKYC(partnerBean);
        }
    }

    @Transactional
    public void replicatePartner(PartnerBean pb){
        partnerDAO.createPartnerCode(pb);
        partnerDAO.replicatePartnerDetails(pb);
    }

    public boolean hasAccess(HttpSession session){
        LoginBean loginVo = (LoginBean) session.getAttribute("loginLst");
        boolean isFinUser = "Y".equals(loginVo.getUserBean().getIsFinUser());
        boolean isAdministrator = "A".equals(loginVo.getUserBean().getRole());
        if(!isAdministrator){
            Map<Integer, FormBean> ddlMap = loginVo.getFormBeanMap();
            FormBean dto = ddlMap.get(1145);
            if(dto == null || !Pattern.matches("(Y)\\|(Y)\\|(Y)\\|(.)\\|(.)\\|(.)\\|(.)\\|(Y)", dto.getPattern())){
                return false;
            }
        }
        return true;
    }

    @Transactional
    @MeasureTime
    public void updatePartnerAccount(PartnerBean partnerBean, HttpSession session) {
        partnerDAO.updatePartnerAccount(partnerBean);
    }

    public void getAttachmentFile(String localPath, String containerName, HttpServletResponse response,
                                  String fileName, PartnerBean pb) throws IOException {
        OutputStream targetFile = null;
        AzureBlobUtil.downloadBlob(fileName, containerName, pb.getAzureEndPoint(), localPath);
        try {
            byte[] file = Files.readAllBytes(Paths.get(localPath+fileName));//"C:\\Bhaumik\\Test\\PartnerKYC\\1\\IEC.jpg"
            targetFile = response.getOutputStream();
            targetFile.write(file);

            targetFile.flush();
        } finally {
            targetFile.close();
        }
    }

    @Transactional
    public void deletePartnerKYC(PartnerBean partnerBean) {
        for(int id: partnerBean.getIds()) {
            partnerDAO.delete(id);
        }
    }

    @Transactional
    public void importPartnerToFinace(PartnerBean partnerBean, HttpSession session) {
        LedgerBean ledgerBean = new LedgerBean();
        ledgerBean.setAcctName(partnerBean.getDescription1());
        ledgerBean.setAcctYear(partnerBean.getAcctYear());
        LedgerBean account = accountDAO.retrieveLedgerByName(ledgerBean);
        if(account == null){
            accountDAO.importPartnerInToFinance(partnerBean);
            partnerDAO.linkPartner(partnerBean, partnerBean.getPartnerAcctCode());
        }
        else{
            String msg = "Account Already exists! ";
            if(partnerBean.getPartnerAcctCode() == 0){
                partnerDAO.linkPartner(partnerBean, account.getCodeCombinationId());
                msg = msg + "Finance Account linked with the Partner successfully!!";
            }

            partnerBean.setErrorMsg(msg);
            logger.debug("Account Already exists! No ledger account created!!");
        }

    }
}
