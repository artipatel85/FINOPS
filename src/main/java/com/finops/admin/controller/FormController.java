package com.finops.admin.controller;

import com.finops.admin.model.FormBean;
import com.finops.admin.service.FormService;
import com.finops.controller.AbstractController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;

@Controller
public class FormController extends AbstractController {

    @Autowired
    private FormService formService;

    @RequestMapping("/formSearch.fin")
    public ModelAndView formSearch(@ModelAttribute("reportBean") FormBean formBean) {
        return new ModelAndView("admin/FormSearch", "command", formBean);
    }

    @RequestMapping(value = "/formGson.fin", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String formGson(@ModelAttribute("formBean") FormBean formBean,
                        HttpServletRequest request, HttpSession session) {
        //setSessionData(reportBean, session);
        List<FormBean> list = formService.findAll();
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("aaData", gson.toJsonTree(list));
        return jsonResponse.toString();
    }

    @RequestMapping(value = "/retrieveForm.fin")
    public ModelAndView retrieveForm(@ModelAttribute("formBean") FormBean formBean,
                                     @RequestParam("code") int code, HttpSession session) throws SQLException {
        formBean = formService.findById(code);
        formBean.setAction("UPDATE");
        return new ModelAndView("admin/Form", "command", formBean);
    }
}
