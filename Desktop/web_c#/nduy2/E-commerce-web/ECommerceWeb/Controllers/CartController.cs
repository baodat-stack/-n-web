using Microsoft.AspNetCore.Mvc;
using ECommerceWeb.ViewModels;
using ECommerceWeb.Services;
using SessionExtensions = ECommerceWeb.Helpers.SessionExtensions;

namespace ECommerceWeb.Controllers
{
    public class CartController : BaseController
    {
        private readonly CartService _cartService;

        public CartController(CartService cartService)
        {
            _cartService = cartService;
        }

        public IActionResult AddToCart(int productId, int quantity = 1)
        {
            _cartService.AddToCart(productId, quantity);
            return RedirectToAction("Index", "Cart");
        }

        public IActionResult Index()
        {
            var cart = _cartService.GetCart();
            return View(cart);
        }

        [HttpPost]
        public IActionResult UpdateQuantity([FromBody] CartItemVM model)
        {
            var updatedItem = _cartService.UpdateQuantity(model.ProductId, model.Quantity);

            if (updatedItem == null)
            {
                return Json(new
                {
                    quantity = 0,
                    cartTotal = _cartService.GetCartTotal()
                });
            }

            return Json(new
            {
                productId = updatedItem.ProductId,
                quantity = updatedItem.Quantity,
                totalItem = updatedItem.Price * updatedItem.Quantity,
                cartTotal = _cartService.GetCartTotal()
            });
        }

        [HttpPost]
        public IActionResult Remove(int productId)
        {
            _cartService.RemoveItem(productId);
            return Json(new { cartTotal = _cartService.GetCartTotal() });
        }
    }
}