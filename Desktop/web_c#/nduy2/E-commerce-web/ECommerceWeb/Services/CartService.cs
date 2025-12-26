using ECommerceWeb.Models;
using ECommerceWeb.ViewModels;
using Microsoft.AspNetCore.Http;
using SessionExtensions = ECommerceWeb.Helpers.SessionExtensions;
namespace ECommerceWeb.Services
{
    public class CartService
    {
        private readonly AppDbContext _context;
        private readonly ProductService _productService;
        private readonly IHttpContextAccessor _httpContextAccessor;
        public CartService(AppDbContext context, ProductService productService,
            IHttpContextAccessor httpContextAccessor)
        {
            _context = context;
            _productService = productService;
            _httpContextAccessor = httpContextAccessor;
        }
        private ISession Session => _httpContextAccessor.HttpContext!.Session;

        public List<CartItemVM> GetCart()
        {
            return SessionExtensions.Get<List<CartItemVM>>(Session, "cart")
                   ?? new List<CartItemVM>();
        }
        public void ClearCart()
        {
            Session.Remove("cart");
        }
        public void AddToCart(int productId, int quantity = 1)
        {
            var product = _productService.GetProductById(productId);
            if (product == null) return;

            var cart = GetCart();

            var item = cart.FirstOrDefault(c => c.ProductId == productId);
            if (item != null)
            {
                item.Quantity += quantity;
            }
            else
            {
                cart.Add(new CartItemVM
                {
                    ProductId = product.Id,
                    Name = product.Name,
                    ImageUrl = product.Images.FirstOrDefault()?.Url ?? "",
                    Price = product.Price,
                    Quantity = quantity
                });
            }

            SessionExtensions.Set(Session, "cart", cart);
        }
        public CartItemVM? UpdateQuantity(int productId, int quantity)
        {
            var cart = GetCart();
            var item = cart.FirstOrDefault(c => c.ProductId == productId);
            if (item == null) return null;

            if (quantity <= 0)
            {
                cart.Remove(item);
            }
            else
            {
                item.Quantity = quantity;
            }

            SessionExtensions.Set(Session, "cart", cart);
            return item;
        }
        public decimal GetCartTotal()
        {
            var cart = GetCart();
            return cart.Sum(item => item.Price * item.Quantity);
        }
        public void RemoveItem(int productId)
        {
            var cart = GetCart();

            var item = cart.FirstOrDefault(x => x.ProductId == productId);
            if (item == null) return;

            cart.Remove(item);
            SessionExtensions.Set(Session, "cart", cart);
        }
    }
}
