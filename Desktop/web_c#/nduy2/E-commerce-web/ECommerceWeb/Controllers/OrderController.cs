using ECommerceWeb.Models.Entity;
using ECommerceWeb.Services;
using ECommerceWeb.ViewModels;
using Microsoft.AspNetCore.Mvc;
using SessionExtensions = ECommerceWeb.Helpers.SessionExtensions;

namespace ECommerceWeb.Controllers
{
    public class OrderController : BaseController
    {
        private readonly ProductService _productService;
        private readonly OrderService _orderService;
        private readonly AccountService _accountService;

        public OrderController(
            ProductService productService,
            OrderService orderService,
            AccountService accountService)
        {
            _productService = productService;
            _orderService = orderService;
            _accountService = accountService;
        }

        public IActionResult Checkout(int? productId, int quantity = 1)
        {
            // Kiểm tra đăng nhập
            var authCheck = CheckAuthentication();
            if (authCheck != null) return authCheck;

            List<CartItemVM> cart;

            if (productId.HasValue)
            {
                // Mua ngay 1 sản phẩm
                var product = _productService.GetProductById(productId.Value);
                if (product == null)
                {
                    return NotFound();
                }

                cart = new List<CartItemVM>
                {
                    new CartItemVM
                    {
                        ProductId = product.Id,
                        Name = product.Name ?? "",
                        ImageUrl = product.Images.FirstOrDefault()?.Url ?? "",
                        Price = product.Price,
                        Quantity = quantity
                    }
                };
            }
            else
            {
                // Mua từ giỏ hàng
                cart = SessionExtensions.Get<List<CartItemVM>>(HttpContext.Session, "cart")
                       ?? new List<CartItemVM>();

                if (!cart.Any())
                {
                    TempData["ErrorMessage"] = "Giỏ hàng của bạn đang trống!";
                    return RedirectToAction("Index", "Cart");
                }
            }

            return View(cart);
        }

        [HttpPost]
        public async Task<IActionResult> ConfirmOrder(PaymentMethod paymentMethod = PaymentMethod.CASH)
        {
            // Kiểm tra đăng nhập
            var authCheck = CheckAuthentication();
            if (authCheck != null) return authCheck;

            try
            {
                // Lấy thông tin user từ database
                var user = await _accountService.GetUserByIdAsync(CurrentUserId!.Value);

                if (user == null)
                {
                    TempData["ErrorMessage"] = "Không tìm thấy thông tin tài khoản, vui lòng thử lại.";
                    // HttpContext.Session.Clear();
                    return RedirectToAction("Login", "Account");
                }

                // Tạo đơn hàng
                _orderService.CreateOrder(user, paymentMethod);

                TempData["SuccessMessage"] = "Đặt hàng thành công!";
                return RedirectToAction("Success"); 
            }
            catch (Exception ex)
            {
                TempData["ErrorMessage"] = "Có lỗi xảy ra: " + ex.Message;
                return RedirectToAction("Checkout");
            }
        }

        public IActionResult Success()
        {
            return View();
        }
    }
}