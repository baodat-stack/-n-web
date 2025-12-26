using ECommerceWeb.Models.Entity;
using ECommerceWeb.Services;
using ECommerceWeb.ViewModels;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace ECommerceWeb.Controllers
{
    public class HomeController : BaseController
    {
        private readonly ProductService _productService;

        public HomeController(ProductService productService)
        {
            _productService = productService;
        }

        public IActionResult Index(int? cate)
        {
            var products = _productService.GetNewProducts();
            if (cate.HasValue)
            {
                products = products.Where(p => p.CategoryId == cate.Value).ToList();
            }
            return View(products);
        }

        public IActionResult Category()
        {
            var categories = _productService.GetCategories();
            return View(categories);
        }

        public IActionResult Detail(int id)
        {
            var product = _productService.GetNewProducts().FirstOrDefault(p => p.Id == id);

            if (product == null)
            {
                return NotFound();
            }

            var relatedProducts = _productService.getRelatedProducts(product.CategoryId, product.Id);
            ViewBag.RelatedProducts = relatedProducts;
            return View(product);
        }

        public IActionResult Search(string? query)
        {
            var products = _productService.SearchProduct(query);
            return View(products);
        }

        [HttpGet]
        public IActionResult GetSuggestions(string? query)
        {
            var products = _productService.SearchProduct(query);
            var suggestions = products
                .Select(p => new {
                    name = p.Name,
                    imageUrl = p.Images.Select(i => i.Url).FirstOrDefault(),
                    price = p.Price,
                    link = Url.Action("Detail", "Home", new { id = p.Id })
                })
                .Take(4)
                .ToList();
            return Json(suggestions);
        }
    }
}