package lk.ijse.dep.web.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.dep.web.model.Item;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

/**
 * @author : Shalitha Anuradha <shalithaanuradha123@gmail.com>
 * @since : 2021-04-01
 **/
@WebServlet(name = "ItemServlet",urlPatterns = "/items")
public class ItemServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BasicDataSource cp= (BasicDataSource)getServletContext().getAttribute("cp");

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");

        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        resp.addHeader("Access-Control-Allow-Origin","http://localhost:3000");
        resp.setContentType("application/json");

            try(Connection connection = cp.getConnection();) {
                PrintWriter out = resp.getWriter();

                PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item" + ((code!=null)?" WHERE code=?":""));
                if(code!=null){
                    pstm.setObject(1,code);
                }
                ResultSet rst = pstm.executeQuery();

                ArrayList<Item> itemList =new ArrayList<>();

                while(rst.next()){
                    code = rst.getString(1);
                    String description = rst.getString(2);
                    BigDecimal unitPrice = rst.getBigDecimal(3).setScale(2);
                    int qtyOnHand = rst.getInt(4);
                    itemList.add(new Item(code,description,qtyOnHand,unitPrice));
                }

                if (code != null && itemList.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }else{
                    Jsonb jsonb = JsonbBuilder.create();
                    out.println(jsonb.toJson(itemList));
                }

            } catch (SQLException throwables) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

    }


}
