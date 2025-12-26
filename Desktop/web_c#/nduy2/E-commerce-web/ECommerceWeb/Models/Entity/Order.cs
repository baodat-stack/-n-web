namespace ECommerceWeb.Models.Entity
{
    public enum OrderStatus
    {
        PENDING, CONFIRMED,
        SHIPPING, DELIVERED,
        RETURNED, CANCELLED
    }

    public enum PaymentMethod
    {
        CASH, CREDIT_CARD, MOMO, VNPAY
    }
    public class Order
    {
        public int Id { get; set; }
        public User? User { get; set; }
        public OrderStatus Status { get; set; }
        public List<OrderItem> Items { get; set; } = new();
        public decimal TotalAmount { get; set; }
        public PaymentMethod PaymentMethod { get; set; }
        public DateTime DeliveryTime { get; set; }
        public DateTime CreatedAt { get; set; }

        public Order() { }  // required
    }

}
