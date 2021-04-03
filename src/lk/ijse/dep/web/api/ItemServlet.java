package lk.ijse.dep.web.api;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author : Shalitha Anuradha <shalithaanuradha123@gmail.com>
 * @since : 2021-04-01
 **/
@WebServlet(name = "ItemServlet",urlPatterns = "/items")
public class ItemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        resp.addHeader("Access-Control-Allow-Origin","http://localhost:3000");
        resp.setContentType("text/html");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<div>");
            out.println("<h1>Item Servlet</h1>");
            try {
                Connection connection = cp.getConnection();
                Statement stm = connection.createStatement();
                ResultSet rst = stm.executeQuery("SELECT * FROM item");
                out.println("<table style='border-collapse: collapse;border:1px solid black'>");
                out.println("<thead><tr><th>Code</th>" +
                        "<th>Description</th>"+
                        "<th>QtyOnHand</th>"+
                        "<th>UnitPrice</th>"+
                        "</tr></thead><tbody>");
                while(rst.next()){
                    String code = rst.getString(1);
                    String description = rst.getString(2);
                    String unitPrice = rst.getBigDecimal(3).setScale(2).toPlainString();
                    int qtyOnHand = rst.getInt(4);
                    out.println("<tr>" +
                            "<td>" + code + "</td>"+
                            "<td>" + description + "</td>"+
                            "<td>" + qtyOnHand + "</td>"+
                            "<td>" + unitPrice + "</td>"+
                            "</tr>");
                }
                connection.close();
                out.println("</tbody></table>");
                out.println("</div>");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
