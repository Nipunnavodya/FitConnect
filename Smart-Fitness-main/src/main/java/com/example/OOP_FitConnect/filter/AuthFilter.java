package com.example.OOP_FitConnect.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session   = httpRequest.getSession(false);
        boolean     isLoggedIn = session != null && session.getAttribute("userId") != null;
        String      userRole   = session != null ? (String) session.getAttribute("userRole") : null;
        String      requestURI = httpRequest.getRequestURI();

        boolean isAjax = "XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With"));

        if (isLoggedIn) {
            // Admin-only paths
            if (requestURI.startsWith("/admin/") && !"ADMIN".equals(userRole)) {
                forbidden(httpRequest, httpResponse, isAjax);
                return;
            }
            // Instructor-only paths (ADMIN can also access for support)
            if (requestURI.startsWith("/instructor/")
                    && !"INSTRUCTOR".equals(userRole)
                    && !"ADMIN".equals(userRole)) {
                forbidden(httpRequest, httpResponse, isAjax);
                return;
            }
            chain.doFilter(request, response);
        } else {
            if (isAjax) {
                httpResponse.setContentType("application/json");
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("{\"error\":\"Not authenticated\",\"redirect\":\"/register\"}");
            } else {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            }
        }
    }

    private void forbidden(HttpServletRequest req, HttpServletResponse res, boolean isAjax)
            throws IOException {
        if (isAjax) {
            res.setContentType("application/json");
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            res.getWriter().write("{\"error\":\"Access denied\",\"redirect\":\"/verification-result\"}");
        } else {
            res.sendRedirect(req.getContextPath() + "/verification-result");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}
