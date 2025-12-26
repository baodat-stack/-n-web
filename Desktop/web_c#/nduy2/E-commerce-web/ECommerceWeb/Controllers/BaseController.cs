using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using ECommerceWeb.Models.Entity;
using SessionExtensions = ECommerceWeb.Helpers.SessionExtensions;

namespace ECommerceWeb.Controllers
{
    public class BaseController : Controller
    {
        protected int? CurrentUserId => SessionExtensions.Get<int?>(HttpContext.Session, "UserId");
        protected string? CurrentUsername => SessionExtensions.Get<string?>(HttpContext.Session, "Username");
        protected string? CurrentUserRole => SessionExtensions.Get<string?>(HttpContext.Session, "UserRole");

        protected bool IsAuthenticated => CurrentUserId.HasValue;
        protected bool IsAdmin => CurrentUserRole == Role.ADMIN.ToString();
        protected bool IsCustomer => CurrentUserRole == Role.CUSTOMER.ToString();

        // Method để redirect về login nếu chưa đăng nhập
        protected IActionResult RedirectToLoginWithReturnUrl()
        {
            return RedirectToAction("Login", "Account", new { returnUrl = HttpContext.Request.Path });
        }

        // Method để kiểm tra quyền admin
        protected IActionResult? CheckAdminAccess()
        {
            if (!IsAuthenticated)
                return RedirectToLoginWithReturnUrl();

            if (!IsAdmin)
                return RedirectToAction("Index", "Home"); // Hoặc trang AccessDenied

            return null; // Cho phép tiếp tục
        }

        // Method để kiểm tra đã đăng nhập
        protected IActionResult? CheckAuthentication()
        {
            if (!IsAuthenticated)
                return RedirectToLoginWithReturnUrl();

            return null; // Cho phép tiếp tục
        }
    }
}