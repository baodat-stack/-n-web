using ECommerceWeb.Models.Entity;

namespace ECommerceWeb.ViewModels
{
    public class ProductVM
    {
        public int Id { get; set; }
        public string? Name { get; set; }
        public int CategoryId { get; set; }
        public ProductStatus Status { get; set; }
        public decimal Price { get; set; }
        public List<Image> Images { get; set; } = new();
        public string? Description { get; set; }
        public int sold { get; set; }
    }
}
