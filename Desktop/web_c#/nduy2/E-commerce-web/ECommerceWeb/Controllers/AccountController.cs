using Microsoft.AspNetCore.Mvc;
using ECommerceWeb.Models.Entity;
using ECommerceWeb.Services;
using ECommerceWeb.ViewModels;
using SessionExtensions = ECommerceWeb.Helpers.SessionExtensions;

namespace ECommerceWeb.Controllers
{
    public class AccountController : BaseController
    {
        private readonly AccountService _accountService;

        public AccountController(AccountService accountService)
        {
            _accountService = accountService;
        }

        [HttpGet]
        public IActionResult Login(string? returnUrl = null)
        {
            ViewData["ReturnUrl"] = returnUrl;
            return View();
        }

        [HttpPost]
        public async Task<IActionResult> Login(LoginVM model, string? returnUrl = null)
        {
            ViewData["ReturnUrl"] = returnUrl;

            if (!ModelState.IsValid)
            {
                return View(model);
            }

            var result = await _accountService.LoginAsync(model.Username, model.Password);

            if (result.Success)
            {
                // Lưu thông tin user vào session
                SessionExtensions.Set(HttpContext.Session, "UserId", result.User!.Id);
                SessionExtensions.Set(HttpContext.Session, "Username", result.User.Username);
                SessionExtensions.Set(HttpContext.Session, "UserRole", result.User.Role.ToString());

                // ⭐ PHÂN QUYỀN: ADMIN → Admin Panel, CUSTOMER → Trang chủ
                if (result.User.Role == Role.ADMIN)
                {
                    return RedirectToAction("Index", "Admin");
                }
                else
                {
                    // Customer → về trang chủ hoặc returnUrl
                    if (!string.IsNullOrEmpty(returnUrl) && Url.IsLocalUrl(returnUrl))
                    {
                        return Redirect(returnUrl);
                    }
                    return RedirectToAction("Index", "Home");
                }
            }

            // Hiển thị lỗi màu đỏ bên dưới textbox
            ModelState.AddModelError(string.Empty, result.Message);
            return View(model);
        }

        [HttpGet]
        public IActionResult Register(string? returnUrl = null)
        {
            ViewData["ReturnUrl"] = returnUrl;
            return View();
        }

        [HttpPost]
        public async Task<IActionResult> Register(RegisterVM model, string? returnUrl = null)
        {
            ViewData["ReturnUrl"] = returnUrl;

            if (!ModelState.IsValid)
            {
                return View(model);
            }

            var result = await _accountService.RegisterAsync(model);

            if (result.Success)
            {
                // Tự động đăng nhập sau khi đăng ký (Role = CUSTOMER)
                SessionExtensions.Set(HttpContext.Session, "UserId", result.User!.Id);
                SessionExtensions.Set(HttpContext.Session, "Username", result.User.Username);
                SessionExtensions.Set(HttpContext.Session, "UserRole", result.User.Role.ToString());

                TempData["SuccessMessage"] = "Đăng ký tài khoản thành công!";

                if (!string.IsNullOrEmpty(returnUrl) && Url.IsLocalUrl(returnUrl))
                {
                    return Redirect(returnUrl);
                }

                return RedirectToAction("Index", "Home");
            }

            // Hiển thị lỗi màu đỏ
            ModelState.AddModelError(string.Empty, result.Message);
            return View(model);
        }

        public IActionResult Logout()
        {
            // Chỉ xóa session khi logout
            HttpContext.Session.Clear();
            return RedirectToAction("Index", "Home");
        }

        [HttpGet]
        public async Task<IActionResult> Profile()
        {
            var authCheck = CheckAuthentication();
            if (authCheck != null) return authCheck;

            var user = await _accountService.GetUserByIdAsync(CurrentUserId!.Value);

            if (user == null)
            {
                HttpContext.Session.Clear();
                return RedirectToAction("Login");
            }

            var model = new UserProfileVM
            {
                Id = user.Id,
                Username = user.Username,
                Email = user.Email,
                PhoneNumber = user.PhoneNumber,
                Address = user.Address,
                CreatedAt = user.CreatedAt
            };

            return View(model);
        }

        [HttpPost]
        public async Task<IActionResult> UpdateProfile(UserProfileVM model)
        {
            var authCheck = CheckAuthentication();
            if (authCheck != null) return authCheck;

            if (!ModelState.IsValid)
            {
                return View("Profile", model);
            }

            var result = await _accountService.UpdateProfileAsync(CurrentUserId!.Value, model);

            if (result.Success)
            {
                TempData["SuccessMessage"] = "Cập nhật thông tin thành công!";
                return RedirectToAction("Profile");
            }

            ModelState.AddModelError(string.Empty, result.Message);
            return View("Profile", model);
        }

        [HttpGet]
        public async Task<IActionResult> Orders()
        {
            var authCheck = CheckAuthentication();
            if (authCheck != null) return authCheck;
            ViewBag.Username = CurrentUsername;

            var orders = await _accountService.GetUserOrdersAsync(CurrentUserId!.Value);
            return View(orders);
        }

        [HttpGet]
        public async Task<IActionResult> OrderDetail(int id)
        {
            var authCheck = CheckAuthentication();
            if (authCheck != null) return authCheck;

            var order = await _accountService.GetOrderDetailAsync(id, CurrentUserId!.Value);

            if (order == null)
            {
                return NotFound();
            }

            return View(order);
        }

        // ⭐ ĐỔI MẬT KHẨU - Redirect về Profile thay vì JSON
        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> ChangePassword(ChangePasswordVM model)
        {
            var authCheck = CheckAuthentication();
            if (authCheck != null) return authCheck;

            if (!ModelState.IsValid)
            {
                TempData["ErrorMessage"] = "Vui lòng điền đầy đủ thông tin";
                return RedirectToAction("Profile");
            }

            var result = await _accountService.ChangePasswordAsync(
                CurrentUserId!.Value,
                model.OldPassword,
                model.NewPassword
            );

            if (result.Success)
            {
                TempData["SuccessMessage"] = result.Message;
            }
            else
            {
                TempData["ErrorMessage"] = result.Message;
            }

            // Redirect về Profile
            return RedirectToAction("Profile");
        }

        // API để kiểm tra username đã tồn tại (cho validation realtime)
        [HttpGet]
        public async Task<IActionResult> CheckUsername(string username)
        {
            if (string.IsNullOrWhiteSpace(username))
            {
                return Json(new { exists = false });
            }

            var exists = await _accountService.UsernameExistsAsync(username);
            return Json(new { exists });
        }

        // API để kiểm tra email đã tồn tại (cho validation realtime)
        [HttpGet]
        public async Task<IActionResult> CheckEmail(string email)
        {
            if (string.IsNullOrWhiteSpace(email))
            {
                return Json(new { exists = false });
            }

            var exists = await _accountService.EmailExistsAsync(email);
            return Json(new { exists });
        }
    }
}