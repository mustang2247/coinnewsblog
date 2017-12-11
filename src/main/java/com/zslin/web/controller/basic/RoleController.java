package com.zslin.web.controller.basic;

import com.zslin.basic.auth.annotations.AdminAuth;
import com.zslin.basic.auth.annotations.Token;
import com.zslin.basic.auth.iservice.IRoleService;
import com.zslin.basic.auth.model.Role;
import com.zslin.basic.auth.service.MenuServiceImpl;
import com.zslin.basic.auth.service.RoleMenuServiceImpl;
import com.zslin.basic.auth.tools.TokenTools;
import com.zslin.basic.exception.SystemException;
import com.zslin.basic.tools.PageableTools;
import com.zslin.basic.tools.PinyinToolkit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 角色管理Controller
 */
@Controller
@RequestMapping(value="admin/role")
@AdminAuth(name = "角色管理", psn="权限管理", orderNum = 2, pentity=0, porderNum=2)
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @Autowired
    private MenuServiceImpl menuServiceImpl;

    @Autowired
    private RoleMenuServiceImpl roleMenuServiceImpl;

    /** 列表 */
    @AdminAuth(name = "角色列表", orderNum = 1, icon="icon-list")
    @RequestMapping(value="list", method= RequestMethod.GET)
    public String list(Model model, Integer page, HttpServletRequest request) {
        Page<Role> datas = roleService.findAll(PageableTools.basicPage(page));
        model.addAttribute("datas", datas);
        return "admin/role/list";
    }

    @AdminAuth(name="角色授权", orderNum=5, type="2")
    @RequestMapping(value="menus/{id}", method=RequestMethod.GET)
    public String menus(Model model, @PathVariable Integer id, HttpServletRequest request) {
        String treeJson = menuServiceImpl.queryTreeJson(null);
//		System.out.println(treeJson);
//		treeJson = treeJson.substring(1, treeJson.length()-1);
        model.addAttribute("role", roleService.findById(id)); //获取当前角色

        List<Integer> curAuthList = roleService.listRoleMenuIds(id);
        StringBuffer sb = new StringBuffer();
        for(Integer aid : curAuthList) {sb.append(aid).append(",");}
        sb.append("0");
        model.addAttribute("curAuth", sb.toString()); //获取角色已有的菜单id
        model.addAttribute("treeJson", treeJson);
        return "admin/role/menus";
    }

    /** 添加角色 */
    @Token(flag=Token.READY)
    @AdminAuth(name = "添加角色", orderNum = 2, icon="icon-plus")
    @RequestMapping(value="add", method=RequestMethod.GET)
    public String add(Model model, HttpServletRequest request) {
        Role role = new Role();
        model.addAttribute("role", role);
        return "admin/role/add";
    }

    /** 添加角色POST */
    @Token(flag=Token.CHECK)
    @RequestMapping(value="add", method=RequestMethod.POST)
    public String add(Model model, Role role, HttpServletRequest request) {
//		Boolean isRepeat = (Boolean) request.getAttribute("isRepeat");
        if(TokenTools.isNoRepeat(request)) { //不是重复提交
            role.setSn(PinyinToolkit.cn2Spell(role.getName(), ""));
            roleService.save(role);
        }
        return "redirect:/admin/role/list";
    }

    @Token(flag=Token.READY)
    @AdminAuth(name="修改角色", orderNum=3, type="2")
    @RequestMapping(value="update/{id}", method=RequestMethod.GET)
    public String update(Model model, @PathVariable Integer id, HttpServletRequest request) {
        Role r = roleService.findById(id);
        model.addAttribute("role", r);
        return "admin/role/update";
    }

    @Token(flag=Token.CHECK)
    @RequestMapping(value="update/{id}", method=RequestMethod.POST)
    public String update(Model model, @PathVariable Integer id, Role role, HttpServletRequest request) {
//		Boolean isRepeat = (Boolean) request.getAttribute("isRepeat");
        if(TokenTools.isNoRepeat(request)) {
            Role r = roleService.findById(id);
            r.setName(role.getName());
            roleService.save(r);
        }
        return "redirect:/admin/role/list";
    }

    @AdminAuth(name="删除角色", orderNum=4, type="2")
    @RequestMapping(value="delete/{id}", method=RequestMethod.POST)
    public @ResponseBody String delete(@PathVariable Integer id) {
        try {
            roleService.delete(id);
            return "1";
        } catch (Exception e) {
            return "0";
        }
    }

    @RequestMapping(value = "addOrDelRoleMenu", method = RequestMethod.POST)
    @AdminAuth(name="为角色授权资源", orderNum=5, type="2")
    public @ResponseBody String addOrDelRoleMenu(Integer roleId, Integer menuId) {
        try {
            roleMenuServiceImpl.addOrDelete(roleId, menuId);
        } catch (Exception e) {
            throw new SystemException("为角色授权资源失败");
        }
        return "1";
    }
}