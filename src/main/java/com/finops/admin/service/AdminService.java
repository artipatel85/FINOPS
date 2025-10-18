package com.finops.admin.service;

import com.finops.admin.dao.AdminDAO;
import com.finops.admin.dao.RoleFormAccessDAO;
import com.finops.admin.model.AdminBean;
import com.finops.admin.model.FormBean;
import com.finops.admin.model.GeneralBean;
import com.finops.admin.model.RoleBean;
import com.finops.aop.MeasureTime;
import com.finops.partner.model.PartnerBean;
import com.finops.report.model.ReportBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private AdminDAO adminDAO;

    @Autowired
    private RoleFormAccessDAO roleFormAccessDAO;

    @Cacheable("signatures")
    public Map<String, String> getSignatures(String key) {
        List<GeneralBean> signatureBeans = adminDAO.getSignatures();

        Map<String, Map<String, String>> signatureMap =
                new HashMap<>();

        for(GeneralBean gb : signatureBeans){
            Map<String, String> masterValue = signatureMap.get(gb.getParam1()+gb.getParam2());
            if(masterValue != null){
                masterValue.put(gb.getParam4(), gb.getParam3());
            }
            else {
                masterValue = new HashMap<>();
                masterValue.put(gb.getParam4(), gb.getParam3());
                signatureMap.put(gb.getParam1()+gb.getParam2(), masterValue);
            }
        }

        return signatureMap.get(key);
    }

    public List<AdminBean> findAllUsers(AdminBean bean) {
        return adminDAO.findAllUsers(bean);
    }

    public List<AdminBean> findAllRoles(AdminBean bean) {
        return adminDAO.findAllRoles(bean);
    }

    public List<AdminBean> findAllPorts(AdminBean bean) {
        return adminDAO.findAllPorts(bean);
    }

    @Cacheable("ports")
    public Map<String, String> fetchAllPorts(String key){

        List<AdminBean> allPorts = adminDAO.findAllPorts(new AdminBean());
        Map<String, String> portMap = new HashMap<>();

        for(AdminBean pb : allPorts){
            portMap.put(pb.getPortCode(), pb.getDescription());
        }
        Map<String, Map<String, String>> portCache = new HashMap<>();
        portCache.put("ports", portMap);
        return portCache.get(key);
    }

    public String getPortName(String portCode, AdminService adminService) {
        Map<String, String> ports = adminService.fetchAllPorts("ports");
        return ports.get(portCode);
    }

    public AdminBean retrievePort(AdminBean adminBean) {
        return adminDAO.retrievePort(adminBean);
    }

    public List<ReportBean> getDocSerialNo(AdminBean adminBean) {
        return adminDAO.getDocSerialNo(adminBean);
    }

    public void saveDocSerialNo(AdminBean adminBean) {
        adminDAO.saveDocSerialNo(adminBean);
    }

    public List<AdminBean> findAllSizes(AdminBean adminBean) {
        return adminDAO.findAllSizes(adminBean);
    }

    public List<AdminBean> findAllUnits(AdminBean adminBean) {
        return adminDAO.findAllUnits(adminBean);
    }

    @MeasureTime
    @Cacheable("units")
    public Map<String, String> fetchAllUnits(String units){

        List<AdminBean> unitList = findAllUnits(new AdminBean());
        Map<String, String> portMap = new HashMap<>();

        for(AdminBean pb : unitList){
            portMap.put(pb.getUnitCode(), pb.getUnitName());
        }
        Map<String, Map<String, String>> unitCache = new HashMap<>();
        unitCache.put(units, portMap);
        return unitCache.get(units);
    }

    @MeasureTime
    @Cacheable("sizes")
    public Map<String, String> fetchAllSizes(String sizes){

        List<AdminBean> sizeList = findAllSizes(new AdminBean());
        Map<String, String> portMap = new HashMap<>();

        for(AdminBean pb : sizeList){
            portMap.put(pb.getSizeCode(), pb.getSizeName());
        }
        Map<String, Map<String, String>> sizeCache = new HashMap<>();
        sizeCache.put(sizes, portMap);
        return sizeCache.get(sizes);
    }

    @MeasureTime
    @Cacheable("states")
    public Map<String, String> fetchAllStates(String states){

        List<AdminBean> stateList = findAllStates(new AdminBean());
        Map<String, String> stateMap = new HashMap<>();

        for(AdminBean pb : stateList){
            stateMap.put(pb.getUnitCode(), pb.getUnitName());
        }
        Map<String, Map<String, String>> sizeCache = new HashMap<>();
        sizeCache.put(states, stateMap);
        return sizeCache.get(states);
    }

    private List<AdminBean> findAllStates(AdminBean adminBean) {
        return adminDAO.findAllStates(adminBean);
    }

    @MeasureTime
    @Cacheable("finactProperties")
    public Map<String, String> getFinactProperties() {
        List<ReportBean> formBeans = adminDAO.getFinactProperties();
        return formBeans.stream().collect(Collectors.toMap(ReportBean::getParam1, formBean -> formBean.getParam2()));
    }

    public List<AdminBean> findAllCurrencies(AdminBean adminBean) {
        return adminDAO.findAllCurrency(adminBean);
    }

    public List<AdminBean> findAllCountries(AdminBean adminBean) {
        return adminDAO.findAllCountries(adminBean);
    }

    public AdminBean retrieveCurrency(AdminBean adminBean) {
        return adminDAO.retrieveCurrency(adminBean);
    }

    public void createCurrency(AdminBean adminBean) {
        if ("SAVE".equalsIgnoreCase(adminBean.getAction())) {
            adminDAO.createCurrency(adminBean);
        }
        else{
            adminDAO.updateCurrency(adminBean);
        }
    }

    public List<AdminBean> findAllSalesman(AdminBean adminBean) {
        return adminDAO.findAllSalesman(adminBean);
    }

    @Cacheable("salesman")
    public Map<String, String> fetchSalesMan(String salesman) {
        List<AdminBean> salesManList = findAllSalesman(new AdminBean());
        Map<String, String> salesManMap = new HashMap<>();

        for(AdminBean pb : salesManList){
            salesManMap.put(pb.getCode(), pb.getName());
        }
        Map<String, Map<String, String>> salesManCache = new HashMap<>();
        salesManCache.put("salesman", salesManMap);
        return salesManCache.get(salesman);
    }

    public AdminBean retrieveSalesman(AdminBean adminBean) {
        return adminDAO.retrieveSalesman(adminBean);
    }

    public void saveSalesman(AdminBean adminBean) {
        if ("SAVE".equalsIgnoreCase(adminBean.getAction())) {
            adminDAO.saveSalesman(adminBean);
        }
        else{
            adminDAO.updateSalesman(adminBean);
        }
    }

    public List<AdminBean> findAllTerminals(AdminBean adminBean) {
        return adminDAO.findAllTerminals(adminBean);
    }

    public AdminBean retrieveTerminal(AdminBean adminBean) {
        return adminDAO.retrieveTerminal(adminBean);
    }

    public AdminBean retrieveCountry(AdminBean adminBean) {
        return adminDAO.retrieveCountry(adminBean);
    }

    public void saveTerminal(AdminBean adminBean) {
        if ("SAVE".equalsIgnoreCase(adminBean.getAction())) {
            adminDAO.saveTerminal(adminBean);
        }
        else{
            adminDAO.updateTerminal(adminBean);
        }
    }

    public void saveCountry(AdminBean adminBean) {
        if ("SAVE".equalsIgnoreCase(adminBean.getAction())) {
            adminDAO.saveCountry(adminBean);
        }
        else{
            adminDAO.updateCountry(adminBean);
        }
    }

    public AdminBean retrieveUser(AdminBean adminBean) {
        return adminDAO.retrieveUser(adminBean);
    }

    public void createUser(AdminBean bean) {
        if ("SAVE".equalsIgnoreCase(bean.getAction())) {
            adminDAO.saveUser(bean);
        }
        else{
            adminDAO.updateUser(bean);
        }
    }

    public void creatPort(AdminBean bean) {
        if ("SAVE".equalsIgnoreCase(bean.getAction())) {
            adminDAO.savePort(bean);
        }
        else{
            adminDAO.updatePort(bean);
        }
    }

    @Transactional
    public void saveRole(AdminBean roleBean) {
        adminDAO.saveRole(roleBean);
    }

    @Transactional
    public void updateRole(AdminBean roleBean) {
        adminDAO.updateRole(roleBean);
    }

    public void retrieveRole(AdminBean roleBean) {
        AdminBean roleEntity = adminDAO.retrieveRole(roleBean);
        roleBean.setDescription(roleEntity.getDescription());
        List<FormBean> privList = roleFormAccessDAO.fetchUserPrivilegeMap(roleBean.getName(), false);
        roleBean.setPriviledgeList(privList);
    }
}
