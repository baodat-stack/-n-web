using ECommerceWeb.Controllers;
using ECommerceWeb.Services;
using ECommerceWeb.ViewModels;
using Microsoft.AspNetCore.Mvc;

public class AdminController : BaseController
{
    private const int pageSize = 15;
    private readonly AdminService _adminService;

    public AdminController(AdminService adminService)
    {
        _adminService = adminService;
    }

    // Trang chủ Admin
    public IActionResult Index()
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        return View();
    }

    // =====================================================
    //  USER MANAGEMENT
    // =====================================================

    [HttpGet]
    public async Task<IActionResult> UserManagement()
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        var users = await _adminService.GetAllUsers();
        ViewBag.Active = "User";
        return View(users);
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> DeleteUser(int id)
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        // Không cho phép xóa chính mình
        if (id == CurrentUserId)
        {
            TempData["ErrorMessage"] = "Không thể xóa tài khoản của chính mình!";
            return RedirectToAction("UserManagement");
        }

        var success = await _adminService.DeleteUser(id);
        if (success)
        {
            TempData["SuccessMessage"] = "Xóa người dùng thành công!";
        }
        else
        {
            TempData["ErrorMessage"] = "Không tìm thấy người dùng!";
        }

        return RedirectToAction("UserManagement");
    }

    // =====================================================
    //  PRODUCT MANAGEMENT
    // =====================================================

    public async Task<IActionResult> Product(int page = 0)
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        var (products, totalCount) = await _adminService.GetProductsPaged(page, pageSize);

        int totalPages = (int)Math.Ceiling((double)totalCount / pageSize);

        ViewBag.Active = "Product";
        ViewBag.CurrentPage = page;
        ViewBag.TotalPages = totalPages;

        return View("Product", products);
    }

    [HttpGet]
    public async Task<IActionResult> AddProduct()
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        ViewBag.Categories = await _adminService.GetAllCategories();
        ViewBag.Active = "Product";
        return View();
    }

    [HttpPost]
    public async Task<IActionResult> AddProduct(AdminAddProductVM model)
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        if (!ModelState.IsValid)
        {
            ViewBag.Categories = await _adminService.GetAllCategories();
            ViewBag.Active = "Product";
            return View(model);
        }

        await _adminService.AddProduct(model);
        return RedirectToAction("Product");
    }

    [HttpGet]
    public async Task<IActionResult> EditProduct(int id)
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        var product = await _adminService.GetProductById(id);
        if (product == null)
        {
            return NotFound();
        }

        var pEditVM = new AdminEditProductVM
        {
            Id = product.Id,
            Name = product.Name,
            CategoryId = product.CategoryId,
            Price = product.Price,
            Description = product.Description,
            Status = product.Status
        };

        ViewBag.Categories = await _adminService.GetAllCategories();
        ViewBag.Active = "Product";
        return View(pEditVM);
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> EditProduct(AdminEditProductVM model)
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        if (!ModelState.IsValid)
        {
            ViewBag.Categories = await _adminService.GetAllCategories();
            ViewBag.Active = "Product";
            return View(model);
        }

        var success = await _adminService.UpdateProduct(model);
        if (!success)
        {
            return NotFound();
        }
        return RedirectToAction(nameof(Product));
    }

    public async Task<IActionResult> DeleteProduct(int id)
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        var success = await _adminService.DeleteProduct(id);
        if (!success)
        {
            return NotFound();
        }
        return RedirectToAction(nameof(Product));
    }

    public async Task<IActionResult> SearchProducts(string keyword)
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        if (string.IsNullOrWhiteSpace(keyword))
        {
            int page = ViewBag.CurrentPage ?? 0;
            var (products, totalCount) = await _adminService.GetProductsPaged(page, pageSize);

            int totalPages = (int)Math.Ceiling((double)totalCount / pageSize);

            ViewBag.Active = "Product";
            ViewBag.CurrentPage = page;
            ViewBag.TotalPages = totalPages;
            return PartialView("_ProductTable", products);
        }

        var result = await _adminService.SearchProductsByName(keyword);
        ViewBag.Active = "Product";
        ViewBag.CurrentPage = 0;
        ViewBag.TotalPages = (int)Math.Ceiling((double)result.Count / pageSize);
        return PartialView("_ProductTable", result);
    }

    // =====================================================
    //  CATEGORY MANAGEMENT
    // =====================================================

    [HttpGet]
    public async Task<IActionResult> Category()
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        ViewBag.Active = "Category";
        var categories = await _adminService.GetAllCategories();
        return View(categories);
    }

    [HttpGet]
    public IActionResult AddCategory()
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        ViewBag.Active = "Category";
        return View();
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> AddCategory(string name)
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        if (!ModelState.IsValid || string.IsNullOrWhiteSpace(name))
        {
            ViewBag.Active = "Category";
            return View();
        }

        await _adminService.AddCategory(name);
        return RedirectToAction(nameof(Category));
    }

    [HttpPost]
    [ValidateAntiForgeryToken]
    public async Task<IActionResult> DeleteCategory(int id)
    {
        var authCheck = CheckAdminAccess();
        if (authCheck != null) return authCheck;

        await _adminService.DeleteCategory(id);
        return RedirectToAction("Category");
    }
}