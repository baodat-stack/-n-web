namespace ECommerceWeb.Models.Entity
{
    public class OrderItem
    {
        public int Id { get; set; }   // sửa: cần setter
        public Product? Product { get; set; }
        public int Quantity { get; set; }
        public double Price { get; set; }
        public DateTime CreatedAt { get; set; }

        public OrderItem() { }   // EF needs this
    }

}
