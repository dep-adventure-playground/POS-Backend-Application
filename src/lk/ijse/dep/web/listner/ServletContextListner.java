package lk.ijse.dep.web.listner;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.SQLException;

/**
 * @author : Shalitha Anuradha <shalithaanuradha123@gmail.com>
 * @since : 2021-03-28
 **/
@WebListener
public class ServletContextListner implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        BasicDataSource bds=new BasicDataSource();
        bds.setUsername("root");
        bds.setPassword("1234");
        bds.setUrl("jdbc:mysql://localhost:3306/dep6");
        bds.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        bds.setConnectionProperties("useSSL=false");
        bds.setInitialSize(5);
        bds.setMaxTotal(5);
        ServletContext ctx = sce.getServletContext();
        ctx.setAttribute("cp",bds);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        BasicDataSource cp = (BasicDataSource) sce.getServletContext().getAttribute("cp");
        try {
            cp.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
