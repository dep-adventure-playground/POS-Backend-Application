package lk.ijse.dep.web.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import lk.ijse.dep.web.model.Customer;
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /* CORS Policy */
//        resp.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");

        String code = req.getParameter("code");
        if (code == null || !code.matches("P\\d{3}")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        try (Connection connection = cp.getConnection()) {

            Jsonb jsonb = JsonbBuilder.create();
            Item item = jsonb.fromJson(req.getReader(), Item.class);

            /*   Validation Logic  */
            /*   Validation Logic  */
            if(item.getCode()!=null || item.getDescription()==null || item.getQtyOnHand()==0 || item.getUnitPrice()==null){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (item.getDescription().trim().isEmpty() || item.getUnitPrice().compareTo(new BigDecimal("0"))!=1 ||
                    item.getQtyOnHand()<0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE code=?");
            pstm.setObject(1, code);

            if (pstm.executeQuery().next()) {
                pstm = connection.prepareStatement("UPDATE item SET description=?,qtyOnHand=?,unitPrice=? WHERE code=?");
                pstm.setObject(1, item.getDescription());
                pstm.setObject(2, item.getQtyOnHand());
                pstm.setObject(3, item.getUnitPrice());
                pstm.setObject(4, code);
                boolean success = pstm.executeUpdate() > 0;
                if (success) {
                    resp.sendError(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } catch (JsonbException exp) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            exp.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /* CORS Policy */
//        resp.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");

        String code = req.getParameter("code");
        if (code == null || !code.matches("P\\d{3}")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        try (Connection connection = cp.getConnection();) {

            PreparedStatement pstm = connection.prepareStatement("SELECT * FROM item WHERE code=?");
            pstm.setObject(1, code);

            if (pstm.executeQuery().next()) {
                pstm = connection.prepareStatement("DELETE FROM item where code = ?");
                pstm.setObject(1, code);
                if (pstm.executeUpdate() > 0) {
                    resp.sendError(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("ABC");
        BasicDataSource cp= (BasicDataSource)getServletContext().getAttribute("cp");
        try(Connection connection=cp.getConnection()) {
            Jsonb jsonb=JsonbBuilder.create();
            Item item = jsonb.fromJson(req.getReader(), Item.class);

            /*   Validation Logic  */
            if(item.getCode()==null || item.getDescription()==null || item.getQtyOnHand()==0 || item.getUnitPrice()==null){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (!item.getCode().matches("P\\d{3}") || item.getDescription().trim().isEmpty() ||
                    item.getUnitPrice().compareTo(new BigDecimal("0"))!=1 || item.getQtyOnHand()<0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            PreparedStatement pstm = connection.prepareStatement("INSERT INTO item VALUES (?,?,?,?)");
            pstm.setObject(1,item.getCode());
            pstm.setObject(2,item.getDescription());
            pstm.setObject(3,item.getUnitPrice());
            pstm.setObject(4,item.getQtyOnHand());

            if (pstm.executeUpdate()>0) {
                resp.sendError(HttpServletResponse.SC_CREATED);
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();
        } catch (SQLException throwables) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throwables.printStackTrace();
        } catch (JsonbException exp) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            exp.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");

        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
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
