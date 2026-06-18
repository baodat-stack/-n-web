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

import com.ecommerce.util.CSRFUtil;

@WebFilter("/*")
public class CsrfFilter implements Filter {

    public void init(FilterConfig fConfig) throws ServletException {}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            if (!CSRFUtil.validateToken(req)) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
                return;
            }
        }
        
        // For GET requests or valid POSTs, make sure session has a token for the JSP to use
        if (req.getSession(false) != null && req.getSession().getAttribute("csrfToken") == null) {
            CSRFUtil.generateToken(req.getSession());
        }

        chain.doFilter(request, response);
    }

    public void destroy() {}
}
