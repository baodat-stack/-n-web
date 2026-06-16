package com.ecommerce.servlet;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.ecommerce.dao.AddressDAO;
import com.ecommerce.model.Address;
import com.ecommerce.model.User;

@WebServlet("/addresses")
public class AddressServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        AddressDAO addressDAO = new AddressDAO();
        List<Address> addresses = addressDAO.getAddressesByUser(currentUser.getId());
        
        request.setAttribute("addressList", addresses);
        request.getRequestDispatcher("user/addresses.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/user/login.jsp");
            return;
        }

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String addressLine = request.getParameter("addressLine");
        String city = request.getParameter("city");
        String pincode = request.getParameter("pincode");

        // Basic validation
        if (fullName == null || fullName.trim().isEmpty() || phone == null || phone.trim().isEmpty()
                || addressLine == null || addressLine.trim().isEmpty() || city == null || city.trim().isEmpty()
                || pincode == null || pincode.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/addresses?error=emptyFields");
            return;
        }

        Address address = new Address();
        address.setUserId(currentUser.getId());
        address.setFullName(fullName.trim());
        address.setPhone(phone.trim());
        address.setAddressLine(addressLine.trim());
        address.setCity(city.trim());
        address.setPincode(pincode.trim());

        AddressDAO addressDAO = new AddressDAO();
        if (addressDAO.addAddress(address)) {
            response.sendRedirect(request.getContextPath() + "/addresses?msg=added");
        } else {
            response.sendRedirect(request.getContextPath() + "/addresses?error=addFailed");
        }
    }
}
