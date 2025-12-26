using System.Threading.Tasks;
using ECommerceWeb.Models;
using ECommerceWeb.Models.Entity;
using ECommerceWeb.ViewModels;
using Microsoft.EntityFrameworkCore;

namespace ECommerceWeb.Services
{
    public class AdminService
    {
        private readonly AppDbContext _context;

        public AdminService(AppDbContext context)
        {
            _context = context;
        }

        // ===== USER MANAGEMENT =====
        public async Task<List<User>> GetAllUsers()
        {
            return await _context.Users
                .OrderByDescending(u => u.CreatedAt)
                .ToListAsync();
        }

        public async Task<User?> GetUserById(int id)
        {
            return await _context.Users
                .Include(u => u.Orders)
                .FirstOrDefaultAsync(u => u.Id == id);
        }

        public async Task<bool> DeleteUser(int id)
        {
            var user = await _context.Users.FindAsync(id);
            if (user == null) return false;

            _context.Users.Remove(user);
            await _context.SaveChangesAsync();
            return true;
        }

        // ===== CATEGORY MANAGEMENT =====
        public async Task<List<Category>?> GetAllCategories()
        {
            return await _context.Categories.ToListAsync();
        }

        public async Task<Category?> GetCategoryById(int id)
        {
            return await _context.Categories
                .FirstOrDefaultAsync(c => c.Id == id);
        }

        public async Task AddCategory(string NewCategoryName)
        {
            string name = NewCategoryName!.Trim();
            var category = new Category
            {
                Name = name
            };

            _context.Categories.Add(category);
            await _context.SaveChangesAsync();
        }

        public async Task DeleteCategory(int id)
        {
            var category = await _context.Categories.FirstOrDefaultAsync(c => c.Id == id);

            if (category == null) return;

            bool hasProducts = await _context.Products
                .AnyAsync(p => p.CategoryId == id);

            if (hasProducts)
            {
                return;
            }

            _context.Categories.Remove(category);
            await _context.SaveChangesAsync();
        }

        // ===== PRODUCT MANAGEMENT =====
        public async Task<(List<AdminProductDetailVM> products, int totalCount)> GetProductsPaged(int page, int pageSize)
        {
            int skip = page * pageSize;

            int totalCount = await _context.Products.CountAsync();

            var products = await _context.Products
                .OrderByDescending(p => p.Id)
                .Skip(skip)
                .Take(pageSize)
                .Include(p => p.Images)
                .Select(p => new AdminProductDetailVM
                {
                    Id = p.Id,
                    Name = p.Name,
                    CategoryId = p.CategoryId,
                    Status = p.Status,
                    Price = p.Price,
                    Images = p.Images,
                    Description = p.Description,
                    Category = _context.Categories
                        .FirstOrDefault(c => c.Id == p.CategoryId)
                })
                .ToListAsync();

            return (products, totalCount);
        }

        public async Task<Product?> GetProductById(int id)
        {
            return await _context.Products
                .Include(p => p.Images)
                .FirstOrDefaultAsync(p => p.Id == id);
        }

        public async Task AddProduct(AdminAddProductVM model)
        {
            if (model == null) return;

            List<Image> images = new();
            foreach (var imageFile in model.Images)
            {
                var (imageName, imagePath) = await UploadImage(imageFile);
                images.Add(new Image
                {
                    Name = imageName,
                    Url = imagePath
                });
            }
            var product = new Product
            {
                Name = model.Name,
                CategoryId = model.CategoryId,
                Status = ProductStatus.IN_STOCK,
                Price = model.Price,
                Description = model.Description,
                Images = images

            };

            _context.Products.Add(product);
            await _context.SaveChangesAsync();
        }

        private async Task<(string imageName, string imagePath)> UploadImage(IFormFile imageFile)
        {
            string uploadsFolder = @"D:\VSCode\Picture";
            if (!Directory.Exists(uploadsFolder))
            {
                Directory.CreateDirectory(uploadsFolder);
            }

            string uniqueFileName = Guid.NewGuid().ToString() + "_" + imageFile.FileName;
            string filePath = Path.Combine(uploadsFolder, uniqueFileName);

            using (var fileStream = new FileStream(filePath, FileMode.Create))
            {
                await imageFile.CopyToAsync(fileStream);
            }

            return (uniqueFileName, filePath);
        }

        public async Task<bool> UpdateProduct(AdminEditProductVM model)
        {
            var product = await _context.Products
                .FirstOrDefaultAsync(p => p.Id == model.Id);

            if (product == null) return false;

            product.Name = model.Name;
            product.CategoryId = model.CategoryId;
            product.Price = model.Price;
            product.Description = model.Description;
            product.Status = model.Status;
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<bool> DeleteProduct(int id)
        {
            var product = await _context.Products
                .Include(p => p.Images)
                .FirstOrDefaultAsync(p => p.Id == id);

            if (product == null) return false;

            _context.Images.RemoveRange(product.Images);

            _context.Products.Remove(product);
            await _context.SaveChangesAsync();
            return true;
        }

        public async Task<List<AdminProductDetailVM>> SearchProductsByName(string keyword)
        {
            return await _context.Products
                .Where(p => p.Name.Contains(keyword))
                .Select(p => new AdminProductDetailVM
                {
                    Id = p.Id,
                    Name = p.Name,
                    CategoryId = p.CategoryId,
                    Status = p.Status,
                    Price = p.Price,
                    Images = p.Images,
                    Description = p.Description,
                    Category = _context.Categories
                        .FirstOrDefault(c => c.Id == p.CategoryId)
                })
                .ToListAsync();
        }
    }
}