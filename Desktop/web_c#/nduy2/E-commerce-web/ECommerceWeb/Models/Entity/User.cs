using System.ComponentModel.DataAnnotations;
namespace ECommerceWeb.Models.Entity
{
    public enum Role
    {
        CUSTOMER, ADMIN
    }

    public class User
    {
        public int Id { get; set; }
        public string? Username { get; set; }
        public string? Password { get; set; }
        public string? Email { get; set; }
        public string? PhoneNumber { get; set; }
        public string? Address { get; set; }
        public string? AvatarUrl { get; set; }
        public Role Role { get; set; } 
        public List<Order> Orders { get; set; } = new();
        public DateTime CreatedAt { get; set; }

        public User() { }
    }

}
