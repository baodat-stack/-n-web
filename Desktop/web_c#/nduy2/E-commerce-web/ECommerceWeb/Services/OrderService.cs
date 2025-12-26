using ECommerceWeb.Models;
using ECommerceWeb.Models.Entity;
using ECommerceWeb.Services;
using Microsoft.EntityFrameworkCore;

public class OrderService
{
    private readonly AppDbContext _context;
    private readonly CartService _cartService;

    public OrderService(AppDbContext context, CartService cartService)
    {
        _context = context;
        _cartService = cartService;
    }

    // Tạo đơn hàng mới từ giỏ hàng
    public Order CreateOrder(User user, PaymentMethod paymentMethod)
    {
        var cart = _cartService.GetCart();
        if (cart == null || !cart.Any())
            throw new InvalidOperationException("Giỏ hàng trống!");

        var order = new Order
        {
            User = user,
            Status = OrderStatus.PENDING,
            PaymentMethod = paymentMethod,
            CreatedAt = DateTime.Now,
            DeliveryTime = DateTime.Now.AddDays(3),
            TotalAmount = cart.Sum(c => (decimal)c.Price * c.Quantity),
            Items = cart.Select(c => new OrderItem
            {
                Product = _context.Products.Find(c.ProductId),
                Quantity = c.Quantity,
                Price = (double)c.Price,
                CreatedAt = DateTime.Now
            }).ToList()
        };

        _context.Orders.Add(order);
        _context.SaveChanges();

        _cartService.ClearCart();
        return order;
    }

    // Lấy chi tiết đơn hàng
    public Order? GetOrderById(int id)
    {
        return _context.Orders
            .Include(o => o.Items)
            .ThenInclude(i => i.Product)
            .Include(o => o.User)
            .FirstOrDefault(o => o.Id == id);
    }
}