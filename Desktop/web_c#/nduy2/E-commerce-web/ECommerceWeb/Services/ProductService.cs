using ECommerceWeb.Models;
using ECommerceWeb.Models.Entity;
using ECommerceWeb.ViewModels;
using Microsoft.EntityFrameworkCore;
using System.Globalization;
using System.Text;
using System.Text.RegularExpressions;
using System.Globalization;
using System.Text;
namespace ECommerceWeb.Services
{
    public class ProductService
    {
        private readonly AppDbContext _context;

        public ProductService(AppDbContext context)
        {
            _context = context;
        }
        public IEnumerable<ProductVM> GetNewProducts()
        {
            return _context.Products
                .Include(p => p.Images).ToList()
                .Where(p => p.Status == ProductStatus.IN_STOCK)
                .OrderByDescending(p => p.Id)
                .Take(10)
                .Select(p => new ProductVM
                {
                    Id = p.Id,
                    Name = p.Name ?? "",
                    CategoryId = p.CategoryId,
                    Images = p.Images,
                    Price = p.Price,
                    Description = p.Description,
                    sold = p.sold
                })
                .ToList();
        }
        public IEnumerable<ProductVM> GetAllProducts()
        {
            return _context.Products
                .Include(p => p.Images).ToList()
                .Where(p => p.Status == ProductStatus.IN_STOCK)
                .OrderByDescending(p => p.Id)
                .Select(p => new ProductVM
                {
                    Id = p.Id,
                    Name = p.Name ?? "",
                    Images = p.Images,
                    Price = p.Price,
                    Description = p.Description,
                    sold = p.sold
                })
                .ToList();
        }
        public ProductVM? GetProductById(int id)
        {
            return _context.Products
                .Include(p => p.Images)
                .Where(p => p.Id == id)
                .Select(p => new ProductVM
                 {
                     Id = p.Id,
                     Name = p.Name ?? "",
                     Images = p.Images,
                     Price = p.Price,
                    Description = p.Description,
                    sold = p.sold
                })
                .FirstOrDefault(p => p.Id == id);
        }

        public IEnumerable<CategoryVM> GetCategories()
        {
            return _context.Categories
                .OrderBy(c => c.Name)
                .Select(c => new CategoryVM
                {
                    Id = c.Id,
                    Name = c.Name ?? "",
                    url = c.url ?? ""
                })
                .ToList();
        }

        public IEnumerable<ProductVM> getRelatedProducts(int categoryId, int currentProductId)
        {
            return _context.Products
                .Include(p => p.Images)
                .Where(p => p.CategoryId == categoryId
                    && p.Status == ProductStatus.IN_STOCK
                    && p.Id != currentProductId)
                .OrderByDescending(p => p.Id)
                .Take(4)
                .Select(p => new ProductVM
                {
                    Id = p.Id,
                    Name = p.Name ?? "",
                    Images = p.Images,
                    Price = p.Price,
                    Description = p.Description,
                    sold = p.sold
                })
                .ToList();
        }
        public IEnumerable<ProductVM> SearchProduct(string? query)
        {
            var product = GetAllProducts();
            if (!string.IsNullOrEmpty(query))
            {
                String norQuery = RemoveDiacritics(query).ToLower();
                product = product.Where(p => RemoveDiacritics(p.Name).ToLower().Contains(norQuery));
            }
            return product;
        }


public static string RemoveDiacritics(string text)
    {
        if (string.IsNullOrEmpty(text)) return text;

        var normalized = text.Normalize(NormalizationForm.FormD);
        var sb = new StringBuilder();

        foreach (var c in normalized)
        {
            var uc = CharUnicodeInfo.GetUnicodeCategory(c);
            if (uc != UnicodeCategory.NonSpacingMark)
            {
                sb.Append(c);
            }
        }

        return sb.ToString().Normalize(NormalizationForm.FormC);
    }



}
}
