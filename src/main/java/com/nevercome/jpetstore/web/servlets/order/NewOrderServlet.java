package com.nevercome.jpetstore.web.servlets.order;

import com.nevercome.jpetstore.domain.model.Account;
import com.nevercome.jpetstore.domain.model.Cart;
import com.nevercome.jpetstore.domain.model.Order;
import com.nevercome.jpetstore.service.OrderService;
import com.nevercome.jpetstore.service.impl.OrderServiceImpl;
import com.nevercome.jpetstore.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/order/newOrder")
public class NewOrderServlet extends HttpServlet {

    private static final String SHIPPING = "/WEB-INF/views/modules/order/ShippingForm.jsp";
    private static final String CONFIRM_ORDER = "/WEB-INF/views/modules/order/ConfirmOrder.jsp";
    private static final String VIEW_ORDER = "/WEB-INF/views/modules/order/ViewOrder.jsp";

    private OrderService orderService = OrderServiceImpl.getInstance();

    private Order order = new Order();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        order.setCardType(request.getParameter("order.cardType"));
        order.setBillAddress1(request.getParameter("order.billAddress1"));
        order.setBillAddress2(request.getParameter("order.billAddress2"));
        order.setBillCity(request.getParameter("order.billCity"));
        order.setBillCountry(request.getParameter("order.billCountry"));
        order.setBillState(request.getParameter("order.billState"));
        order.setBillToFirstName(request.getParameter("order.billToFirstName"));
        order.setBillToLastName(request.getParameter("order.billToLastName"));
        order.setBillZip(request.getParameter("order.billZip"));
        order.setCourier(request.getParameter("order.courier"));
        order.setCreditCard(request.getParameter("order.creditCard"));
        order.setExpiryDate(request.getParameter("order.expiryDate"));
        if (StringUtils.isNotBlank(request.getParameter("shippingAddressRequired"))) {
            order.setShippingAddressRequired(true);
        }
        if (StringUtils.isNotBlank(request.getParameter("confirmed"))) {
            order.setConfirmed(true);
        }
        if (order.isShippingAddressRequired()) {
            request.getRequestDispatcher(SHIPPING).forward(request, response);
        } else if (!order.isConfirmed()) {
            request.setAttribute("order", order);
            request.getRequestDispatcher(CONFIRM_ORDER).forward(request, response);
        } else {
            Account account =(Account) request.getSession().getAttribute("account");
            request.setAttribute("order", order);
            order.setUsername(account.getUserId());
            orderService.insertOrder(order);
            request.getSession().setAttribute("cart", null);
            String message = "Thank you, your order has been submitted.";
            request.setAttribute("message", message);
            request.getRequestDispatcher(VIEW_ORDER).forward(request, response);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
