package hello.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Controller
public class DataTestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataTestController.class);


    @RequestMapping("/data")
    public ModelAndView data() {

        LOGGER.info("hello, data");
//        String sql = "select * from test";
//        List<Map<String, Object>> list =  this.jdbcTemplate.queryForList(sql);
//        int result = this.jdbcTemplate.update("update table1 set b=66 where a=1;");
//        LOGGER.info("update result is {}", result);
        ModelAndView modelAndView =null;
        Connection c = null;
        Statement stmt = null;
        try {
            //Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:D:/15ProjectDataBase/test.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");


            stmt = c.createStatement();
            String sql = "SELECT * FROM file_info WHERE id = 1";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                int app_id =rs.getInt("app_id");
                String name = rs.getString("name");
                String size = rs.getString("size");
                String path = rs.getString("path");
                String md5 = rs.getString("md5");
                String create_time = rs.getString("create_time");
                int del = rs.getInt("del");
                modelAndView =  new ModelAndView("data");
                modelAndView.addObject("id", id);
                modelAndView.addObject("app_id", app_id);
                modelAndView.addObject("size", size);
                modelAndView.addObject("path", path);
                modelAndView.addObject("name", name);
                modelAndView.addObject("md5", md5);
                modelAndView.addObject("create_time", create_time);
                modelAndView.addObject("del", del);

            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Operation done successfully");

        return modelAndView;
    }
}


