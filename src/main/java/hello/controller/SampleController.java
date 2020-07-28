package hello.controller;

import com.google.common.base.Strings;
import hello.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * config database https://www.cnblogs.com/liangblog/p/5228548.html
 */
@Controller
@RequestMapping("/")
public class SampleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleController.class);

    private static final List<String> keyList = new ArrayList<>(5);
    static {
        keyList.add("city");
        keyList.add("item");
        keyList.add("id1");
        keyList.add("id2");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("")
    public ModelAndView index(HttpServletRequest request) {
        if (request.getSession().getAttribute("name") == null) {
            ModelAndView modelAndView =  new ModelAndView("login_index");
            return modelAndView;
        }
        final String user = String.valueOf(request.getSession().getAttribute("name"));
        LOGGER.info("login user {}", user);
        ModelAndView modelAndView =  new ModelAndView("index");
        modelAndView.addObject("name", "user");
        modelAndView.addObject("dae", new Date());
        return modelAndView;
    }

    @RequestMapping("view")
    public ModelAndView view() {
        LOGGER.info("hello, myview");
        ModelAndView modelAndView =  new ModelAndView("view");
        modelAndView.addObject("name", "whoAmI");
        modelAndView.addObject("dae", new Date());
        return modelAndView;
    }

    @RequestMapping("test")
    public ModelAndView test() {
        LOGGER.info("hello, test");
//        String sql = "select * from test";
//        List<Map<String, Object>> list =  this.jdbcTemplate.queryForList(sql);
//        int result = this.jdbcTemplate.update("update table1 set b=66 where a=1;");
//        LOGGER.info("update result is {}", result);
        ModelAndView modelAndView =  new ModelAndView("test");
        Data data = new Data();
        data.setKey1("value1");
        data.setKey2("value2");
        data.setKey3("value3");
        modelAndView.addObject("data", data);
        return modelAndView;
    }

    @RequestMapping("file")
    public ModelAndView file() {
        LOGGER.info("hello, file");
        ModelAndView modelAndView =  new ModelAndView("file");
        return modelAndView;
    }

    @RequestMapping("login")
    public ModelAndView login(HttpServletRequest request) {
        if (null != request.getSession().getAttribute("user.name")) {
            final String name = String.valueOf(request.getSession().getAttribute("user.name"));
            if (Strings.isNullOrEmpty(name)) {
                LOGGER.info("user name is null");
                return new ModelAndView("login_index");
            }
            LOGGER.info("user login {}", name);
            ModelAndView modelAndView =  new ModelAndView("index");
            modelAndView.addObject("name", name);
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            modelAndView.addObject("date", sf.format(new Date()));
            return modelAndView;
        }
        final String name = request.getParameter("name");
        final String password = request.getParameter("password");
        if (User.list.get(name) == null || User.list.get(name) != null && !User.list.get(name).equals(password)) {
            ModelAndView modelAndView = new ModelAndView("login_index");
            if (request.getMethod().toLowerCase().equals("post")) {
                modelAndView.addObject("data", "用户名或密码错误，请重新登录");
                LOGGER.info("user name or password error.");
            } else {
                LOGGER.info("login, http get");
            }
            return modelAndView;
        }
        LOGGER.info("user login {}", name);
        request.getSession().setAttribute("user.name", name);
        ModelAndView modelAndView =  new ModelAndView("index");
        modelAndView.addObject("name", name);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        modelAndView.addObject("date", sf.format(new Date()));
        return modelAndView;
    }

    @RequestMapping("logout")
    public ModelAndView logout(HttpServletRequest request) {
        LOGGER.info("hello, file");
        ModelAndView modelAndView =  new ModelAndView("login_index");
        modelAndView.addObject("data", "您已退出系统，欢迎下次再来");
        request.getSession().removeAttribute("user.name");
        return modelAndView;
    }

    @RequestMapping("task")
    public ModelAndView doTask(HttpServletRequest request) {

        LOGGER.info("hello, task");
        final String name = String.valueOf(request.getSession().getAttribute("user.name"));
        ModelAndView modelAndView;
        if (Strings.isNullOrEmpty(name) || name.equals("null")) {
            LOGGER.info("user name is null");
            return new ModelAndView("login_index");
        }
        if (name.equals("abc")) {
            modelAndView = new ModelAndView("task");
            if (request.getMethod().toLowerCase().equals("get")) {
                LOGGER.info("http get method.");
            } else {
                request.getParameterMap().forEach((k, v) -> {
                    if (k.startsWith("_s_")) {
                        final String score = v[0];
                        final String[] keys = k.replace("_s_", "").split("_");
                        final String sql = buildUpdateSql(keys, score);
                        LOGGER.info("update sql is {}", sql);
                        int result = this.jdbcTemplate.update(sql);
                        LOGGER.info("update result is {}", result);
                    }
                });
            }
            List<Map<String, Object>> list = getData("select * from test.test");
            modelAndView.addObject("scoreList", list);
            modelAndView.addObject("file", UploadController.fileList);
        } else {
            modelAndView = new ModelAndView("task_illegal");
        }
        return modelAndView;
    }

    @RequestMapping("score")
    public ModelAndView score(HttpServletRequest request, SearchDto dto) {
        LOGGER.info("score");
        final String name = String.valueOf(request.getSession().getAttribute("user.name"));
        ModelAndView modelAndView;
        if (Strings.isNullOrEmpty(name) || name.equals("null") || request.getMethod().toLowerCase().equals("get")) {
            LOGGER.info("user name is null");
            return new ModelAndView("login_index");
        }
        modelAndView =  new ModelAndView("task");
        String sql = buildSql(dto);
        LOGGER.info("sql is {}", sql);
        List<Map<String, Object>> list = getData(sql);
        modelAndView.addObject("scoreList", list);
        return modelAndView;
    }

    private List<Map<String, Object>> getData(String sql) {
        List<Map<String, Object>> list =  this.jdbcTemplate.queryForList(sql);

        list.forEach(item -> {
            StringBuilder sb = new StringBuilder(10);
            sb.append("_s_");
            sb.append(item.get("city") + "_");
            sb.append(item.get("item") + "_");
            sb.append(item.get("id1") + "_");
            sb.append(item.get("id2") + "");
            item.put("data_key", sb.toString());
        });
        getName(list);
        LOGGER.info("list is {}", list);
        return list;
    }

    private void getName(List<Map<String, Object>> list) {
        list.forEach(item -> {
            if (null != item.get("city") && null != City.list.get(item.get("city"))) {
                item.put("city", City.list.get(item.get("city")));
            }
            if (null != item.get("item") && null != Item.list.get(item.get("item"))) {
                item.put("item", Item.list.get(item.get("item")));
            }
            if (null != item.get("id1") && null != Id1.list.get(item.get("id1"))) {
                item.put("id1", Id1.list.get(item.get("id1")));
            }
            if (null != item.get("id2") && null != Id2.list.get(item.get("id2"))) {
                item.put("id2", Id2.list.get(item.get("id2")));
            }
        });
    }

    private String buildSql(SearchDto dto) {
        StringBuilder sb = new StringBuilder("select * from test.test where true ");
        if (!Strings.isNullOrEmpty(dto.getCity())) {
            sb.append("and city = '" + dto.getCity() + "'");
        }
        if (!Strings.isNullOrEmpty(dto.getItem())) {
            sb.append("and item = '" + dto.getItem() + "'");
        }
        if (!Strings.isNullOrEmpty(dto.getId1())) {
            sb.append("and id1 = '" + dto.getId1() + "'");
        }
        if (!Strings.isNullOrEmpty(dto.getId2())) {
            sb.append("and id2 = '" + dto.getId2() + "'");
        }
        return sb.toString();
    }


    private String buildUpdateSql(String[] keys, String score) {
        if (keys.length != 4) {
            return "";
        }
        StringBuilder sb = new StringBuilder("update test.test set score = '");
        sb.append(score);
        sb.append("' where true ");
        sb.append("and city = '" + keys[0] + "'");
        sb.append("and item = '" + keys[1] + "'");
        sb.append("and id1 = '" + keys[2] + "'");
        sb.append("and id2 = '" + keys[3] + "'");
        return sb.toString();
    }
}