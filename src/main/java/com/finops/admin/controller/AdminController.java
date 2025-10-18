package com.finops.admin.controller;

import com.finops.admin.model.AdminBean;
import com.finops.admin.model.RoleBean;
import com.finops.admin.service.AdminService;
import com.finops.controller.AbstractController;
import com.finops.report.model.ReportBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.kafka.clients.admin.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController extends AbstractController {

    @Autowired
    private AdminService adminService;

    @RequestMapping("/adminSearch.fin")
    public ModelAndView adminSearch(@ModelAttribute("adminBean") AdminBean adminBean,
                                   @RequestParam("param") String param) {
        switch (param) {
            case "User" -> {
                return new ModelAndView("admin/UserSearch", "command", adminBean);
            }
            case "Role" -> {
                return new ModelAndView("admin/RoleSearch", "command", adminBean);
            }
            case "Port" -> {
                return new ModelAndView("admin/PortSearch", "command", adminBean);
            }
            case "Size" -> {
                return new ModelAndView("admin/SizeSearch", "command", adminBean);
            }
            case "Unit" -> {
                return new ModelAndView("admin/UnitSearch", "command", adminBean);
            }
            case "Currency" -> {
                return new ModelAndView("admin/CurrencySearch", "command", adminBean);
            }
            case "Salesman" -> {
                return new ModelAndView("admin/SalesmanSearch", "command", adminBean);
            }
            case "Terminal" -> {
                return new ModelAndView("admin/TerminalSearch", "command", adminBean);
            }
            case "Country" -> {
                return new ModelAndView("admin/CountrySearch", "command", adminBean);
            }
        }
        return null;
    }

    @RequestMapping(value = "/adminGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String adminGson(@ModelAttribute("adminBean") AdminBean adminBean, HttpServletRequest request,
                    @RequestParam("param") String param) throws SQLException {
        setSessionData(adminBean, request.getSession());
        List<AdminBean> list = null;
        switch (param) {
            case "User" -> list = adminService.findAllUsers(adminBean);
            case "Role" -> list = adminService.findAllRoles(adminBean);
            case "Port" -> list = adminService.findAllPorts(adminBean);
            case "Size" -> list = adminService.findAllSizes(adminBean);
            case "Unit" -> list = adminService.findAllUnits(adminBean);
            case "Currency" -> list = adminService.findAllCurrencies(adminBean);
            case "Salesman" -> list = adminService.findAllSalesman(adminBean);
            case "Terminal" -> list = adminService.findAllTerminals(adminBean);
            case "Country" -> list = adminService.findAllCountries(adminBean);
        }
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping(value = "/retrieveAdmin.fin")
    public ModelAndView retrieveAdmin(@ModelAttribute("adminBean") AdminBean adminBean,
                                     @RequestParam("code") String code, HttpSession session,
                                     @RequestParam("param") String param) throws SQLException {
        adminBean.setPortCode(code);
        String viewName = "";
        switch (param) {
            //case "User" -> adminBean = adminService.findAllUsers(adminBean);
            //case "Role" -> adminBean = adminService.findAllRoles(adminBean);
            case "Port" -> {
                adminBean = adminService.retrievePort(adminBean);
                viewName = "admin/Port";
            }

        }

        adminBean.setAction("UPDATE");
        return new ModelAndView(viewName, "command", adminBean);
    }

    @RequestMapping("/docSerialNo.fin")
    public ModelAndView docSerialNo(@ModelAttribute("adminBean") AdminBean adminBean, HttpSession session) throws SQLException {
        setSessionData(adminBean, session);
        List<ReportBean> list = adminService.getDocSerialNo(adminBean);
        adminBean.setReconList(list);
        return new ModelAndView("admin/DocSerial", "command", adminBean);
    }

    @RequestMapping("/saveDocSerialNo.fin")
    public ModelAndView saveDocSerialNo(@ModelAttribute("adminBean") AdminBean adminBean, HttpSession session) throws SQLException {
        setSessionData(adminBean, session);
        adminService.saveDocSerialNo(adminBean);
        return new ModelAndView("admin/DocSerial", "command", adminBean);
    }

    @RequestMapping(value = "/retrieveCurrency.fin")
    public ModelAndView retrieveCurrency(@ModelAttribute("adminBean") AdminBean adminBean,
                                         @RequestParam("code") String code, HttpSession session) throws SQLException {
        adminBean.setCurrencyCode(code);
        //setSessionData(adminBean, session);
        adminBean = adminService.retrieveCurrency(adminBean);
        adminBean.setAction("UPDATE");
        return new ModelAndView("admin/Currency", "command", adminBean);
    }

    @RequestMapping("/currencySave.fin")
    public ModelAndView currencySave(@ModelAttribute("adminBean") AdminBean adminBean, HttpSession session) {
        setSessionData(adminBean, session);
        adminService.createCurrency(adminBean);
        return new ModelAndView("admin/CurrencySearch", "command", adminBean);
    }

    @RequestMapping(value = "/retrieveSalesman.fin")
    public ModelAndView retrieveSalesman(@ModelAttribute("adminBean") AdminBean adminBean,
                                         @RequestParam("code") String code, HttpSession session) throws SQLException {
        adminBean.setCode(code);
        //setSessionData(adminBean, session);
        adminBean = adminService.retrieveSalesman(adminBean);
        adminBean.setAction("UPDATE");
        return new ModelAndView("admin/Salesman", "command", adminBean);
    }

    @RequestMapping("/salesmanSave.fin")
    public ModelAndView saveSalesman(@ModelAttribute("adminBean") AdminBean adminBean, HttpSession session) {
        setSessionData(adminBean, session);
        adminService.saveSalesman(adminBean);
        return new ModelAndView("admin/SalesmanSearch", "command", adminBean);
    }

    @RequestMapping("/terminalSearch.fin")
    public ModelAndView terminalSearch(@ModelAttribute("adminBean") AdminBean adminBean) {
        return new ModelAndView("admin/TerminalSearch", "command", adminBean);
    }

    @RequestMapping("/terminal.fin")
    public ModelAndView terminal(@ModelAttribute("adminBean") AdminBean adminBean) {
        adminBean.setAction("SAVE");
        return new ModelAndView("admin/Terminal", "command", adminBean);
    }

    @RequestMapping("/country.fin")
    public ModelAndView country(@ModelAttribute("adminBean") AdminBean adminBean) {
        adminBean.setAction("SAVE");
        return new ModelAndView("admin/Country", "command", adminBean);
    }

    @RequestMapping(value = "/retrieveTerminal.fin")
    public ModelAndView retrieveTerminal(@ModelAttribute("adminBean") AdminBean adminBean,
                                         @RequestParam("code") String code, HttpSession session) throws SQLException {
        adminBean.setCode(code);
        adminBean = adminService.retrieveTerminal(adminBean);
        adminBean.setAction("UPDATE");
        return new ModelAndView("admin/Terminal", "command", adminBean);
    }

    @RequestMapping(value = "/retrieveCountry.fin")
    public ModelAndView retrieveCountry(@ModelAttribute("adminBean") AdminBean adminBean,
                                         @RequestParam("code") String code, HttpSession session) throws SQLException {
        adminBean.setCode(code);
        adminBean = adminService.retrieveCountry(adminBean);
        adminBean.setAction("UPDATE");
        return new ModelAndView("admin/Country", "command", adminBean);
    }

    @RequestMapping("/terminalSave.fin")
    public ModelAndView terminalSave(@ModelAttribute("adminBean") AdminBean adminBean, HttpSession session) {
        setSessionData(adminBean, session);
        adminService.saveTerminal(adminBean);
        return new ModelAndView("admin/TerminalSearch", "command", adminBean);
    }

    @RequestMapping("/countrySave.fin")
    public ModelAndView countrySave(@ModelAttribute("adminBean") AdminBean adminBean, HttpSession session) {
        setSessionData(adminBean, session);
        adminService.saveCountry(adminBean);
        return new ModelAndView("admin/CountrySearch", "command", adminBean);
    }

    @RequestMapping(value = "/retrieveUser.fin")
    public ModelAndView retrieveUser(@ModelAttribute("adminBean") AdminBean adminBean,
                                     @RequestParam("code") String code, HttpSession session) throws SQLException {
        adminBean.setUserProfileId(code);
        setSessionData(adminBean, session);
        AdminBean command = adminService.retrieveUser(adminBean);
        Map<String, String> roleList = listToMap(adminService.findAllRoles(adminBean));
        command.setRoleList(roleList);
        command.setAction("UPDATE");
        return new ModelAndView("admin/User", "command", command);
    }

    @RequestMapping("/userSave.fin")
    public ModelAndView userSave(@ModelAttribute("adminBean") AdminBean adminBean, HttpSession session) {
        setSessionData(adminBean, session);
        adminService.createUser(adminBean);
        return new ModelAndView("admin/UserSearch", "command", adminBean);
    }

    @RequestMapping("/user.fin")
    public ModelAndView user(@ModelAttribute("reportBean") AdminBean adminBean) {
        adminBean.setAction("SAVE");
        Map<String, String> roleList = listToMap(adminService.findAllRoles(adminBean));
        adminBean.setRoleList(roleList);
        return new ModelAndView("admin/User", "command", adminBean);
    }

    @RequestMapping("/port.fin")
    public ModelAndView port(@ModelAttribute("reportBean") AdminBean adminBean) {
        adminBean.setAction("SAVE");
        return new ModelAndView("admin/Port", "command", adminBean);
    }

    @RequestMapping("/portSave.fin")
    public ModelAndView portSave(@ModelAttribute("adminBean") AdminBean adminBean, HttpSession session) {
        setSessionData(adminBean, session);
        adminService.creatPort(adminBean);
        return new ModelAndView("admin/UserSearch", "command", adminBean);
    }

    @RequestMapping("/role.fin")
    public ModelAndView createRole(@ModelAttribute("adminBean") AdminBean roleBean) {
        roleBean.setAction("SAVE");
        return new ModelAndView("admin/Role", "command", roleBean);
    }

    @RequestMapping("/saveRoles.fin")
    public ModelAndView saveRoles(@ModelAttribute("adminBean") AdminBean roleBean, HttpSession session) throws SQLException {
        setSessionData(roleBean, session);
        if ("SAVE".equalsIgnoreCase(roleBean.getAction())) {
            adminService.saveRole(roleBean);
        } else {
            adminService.updateRole(roleBean);
        }

        return new ModelAndView("admin/RoleSearch", "command", roleBean);
    }

    @RequestMapping(value = "/retrieveRole.fin")
    public ModelAndView retrieveRole(@ModelAttribute("adminBean") AdminBean adminBean,
                                     @RequestParam("roleName") String roleName, HttpSession session) throws SQLException {;
        adminBean.setName(roleName);
        adminService.retrieveRole(adminBean);
        adminBean.setAction("UPDATE");
        return new ModelAndView("admin/Role", "command", adminBean);
    }

    private Map listToMap(List<AdminBean> list) {
        Map<String, String> map = new HashMap<>();
        for (AdminBean bean : list) {
            map.put(bean.getRole(), bean.getRole());
        }
        return map;
    }
}
