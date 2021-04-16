package lk.ijse.dep.web.api;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import lk.ijse.dep.web.model.*;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Shalitha Anuradha <shalithaanuradha123@gmail.com>
 * @since : 2021-03-27
 **/

@WebServlet(name = "OrderServlet", urlPatterns = "/orders")
public class OrderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//        Jsonb jsonb=JsonbBuilder.create();
//        Order order = jsonb.fromJson(req.getReader(), Order.class);
        BasicDataSource cp = (BasicDataSource) getServletContext().getAttribute("cp");
        try (Connection connection = cp.getConnection()) {
            Jsonb jsonb = JsonbBuilder.create();
            Order order = jsonb.fromJson(req.getReader(), Order.class);

            /*   Validation Logic  */
            if (order.getOrderId() == null || order.getOrderDate() == null || order.getCustomerId() == null || order.getOrderDetails()==null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (!order.getOrderId().matches("OD\\d{3}") || !order.getOrderDate().matches("\\d{4}-\\d{2}-\\d{2}") ||
                    !order.getCustomerId().matches("C\\d{3}")){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            for (OrderDetails orderDetails :order.getOrderDetails()) {
                if(orderDetails.getOrderId()==null || orderDetails.getItemCode()==null || orderDetails.getQty()<=0 || orderDetails.getUnitPrice()==null){
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                if(!orderDetails.getOrderId().matches("OD\\d{3}") || !orderDetails.getItemCode().matches("P\\d{3}") || orderDetails.getUnitPrice().compareTo(new BigDecimal("0"))!=1){
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }


            PreparedStatement pstm = connection.prepareStatement("INSERT INTO orders VALUES (?,?,?)");
            pstm.setObject(1, order.getOrderId());
            pstm.setObject(2, order.getOrderDate());
            pstm.setObject(3, order.getCustomerId());

            boolean insertedToOrders=pstm.executeUpdate() > 0;
            boolean insertedToOrderDetails=true;
            for (OrderDetails orderDetails :order.getOrderDetails()) {
                pstm = connection.prepareStatement("INSERT INTO order_detail VALUES (?,?,?,?)");
                pstm.setObject(1, orderDetails.getOrderId());
                pstm.setObject(2, orderDetails.getItemCode());
                pstm.setObject(3, orderDetails.getQty());
                pstm.setObject(4, orderDetails.getUnitPrice());
                if (!(pstm.executeUpdate()>0)) {
                    insertedToOrderDetails=false;
                    return;
                }
            }

            if (insertedToOrders && insertedToOrderDetails) {
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
}
