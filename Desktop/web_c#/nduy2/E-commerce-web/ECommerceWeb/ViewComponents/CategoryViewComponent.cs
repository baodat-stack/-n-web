using Microsoft.AspNetCore.Mvc;
using ECommerceWeb.Services;

namespace ECommerceWeb.ViewComponents
{
    public class CategoryViewComponent : ViewComponent
    {
        private readonly ProductService _productService;

        public CategoryViewComponent(ProductService productService) => _productService = productService;

        public IViewComponentResult Invoke()
        {
            var data = _productService.GetCategories();

            return View(data);
        }
    }
}