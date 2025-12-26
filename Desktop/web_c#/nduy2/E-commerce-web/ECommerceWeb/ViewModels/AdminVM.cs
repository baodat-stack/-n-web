using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using ECommerceWeb.Models;
using ECommerceWeb.Models.Entity;
namespace ECommerceWeb.ViewModels
{
    public class AdminProductDetailVM
    {
        public int Id { get; set; }
        public string? Name { get; set; }
        public int CategoryId { get; set; }
        public ProductStatus Status { get; set; }
        public decimal Price { get; set; }
        public List<Image> Images { get; set; } = new();
        public string? Description { get; set; }
        public Category? Category { get; set; }
    }

    public class AdminAddProductVM
    {
        [Required(ErrorMessage = "Phải nhập tên sản phẩm")]
        [StringLength(200, ErrorMessage = "Tên sản phẩm tối đa 200 ký tự")]
        public string? Name { get; set; }

        [Required(ErrorMessage = "Danh mục không được để trống")]
        public int CategoryId { get; set; }

        [Required(ErrorMessage = "Giá sản phẩm là bắt buộc")]
        [Range(0, double.MaxValue, ErrorMessage = "Giá phải lớn hơn hoặc bằng 0")]
        public decimal Price { get; set; }
        
        [StringLength(2000, ErrorMessage = "Mô tả tối đa 2000 ký tự")]
        public string? Description { get; set; }

        [Required(ErrorMessage = "Phải nhập ít nhất 1 ảnh")]
        [MinLength(1, ErrorMessage = "Phải nhập ít nhất 1 ảnh")]
        public List<IFormFile> Images { get; set; } = new();

    }

    public class AdminEditProductVM
    {
        [Required]
        public int Id { get; set; }

        [Required(ErrorMessage = "Phải nhập tên sản phẩm")]
        [StringLength(200, ErrorMessage = "Tên sản phẩm tối đa 200 ký tự")]
        public string? Name { get; set; }

        [Required(ErrorMessage = "Danh mục không được để trống")]
        public int CategoryId { get; set; }

        [Required(ErrorMessage = "Giá sản phẩm là bắt buộc")]
        [Range(0, double.MaxValue, ErrorMessage = "Giá phải lớn hơn hoặc bằng 0")]
        public decimal Price { get; set; }

        [StringLength(2000, ErrorMessage = "Mô tả tối đa 2000 ký tự")]
        public string? Description { get; set; }
        [Required]
        public ProductStatus Status { get; set; }

    }


}