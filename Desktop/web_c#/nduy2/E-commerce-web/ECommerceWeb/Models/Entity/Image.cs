
namespace ECommerceWeb.Models.Entity
{
    public class Image
    {
        public int Id { get; set; }  
        public string Name { get; set; } = "";
        public string Url { get; set; } = "";
        public int ProductID { get; set; }

        public Image() { }
    }

}
