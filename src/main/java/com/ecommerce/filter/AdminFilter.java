package com.ecommerce.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.model.User;

@WebFilter(urlPatterns = {
    "/adminDashboard", "/adminOrders", "/updateOrderStatus", 
    "/addProduct", "/updateProduct", "/deleteProduct"
})
public class AdminFilter implements Filter {

    public void init(FilterConfig fConfig) throws ServletException {}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        User user = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (user != null && "admin".equals(user.getRole())) {
            chain.doFilter(request, response);
        } else {
            res.sendRedirect(req.getContextPath() + "/user/login.jsp?error=unauthorized");
        }
    }

    public void destroy() {}
}
