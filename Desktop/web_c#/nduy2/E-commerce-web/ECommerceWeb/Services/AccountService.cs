using ECommerceWeb.Models;
using ECommerceWeb.Models.Entity;
using ECommerceWeb.ViewModels;
using Microsoft.EntityFrameworkCore;

using BCrypt.Net;

namespace ECommerceWeb.Services
{
    public class AccountService
    {
        private readonly AppDbContext _context;

        public AccountService(AppDbContext context)
        {
            _context = context;
        }

        public async Task<AuthResult> LoginAsync(string username, string password)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(username) || string.IsNullOrWhiteSpace(password))
                {
                    return new AuthResult
                    {
                        Success = false,
                        Message = "Tên đăng nhập và mật khẩu không được để trống"
                    };
                }

                var user = await _context.Users
                    .FirstOrDefaultAsync(u => u.Username == username);

                if (user == null)
                {
                    return new AuthResult
                    {
                        Success = false,
                        Message = "Tên đăng nhập hoặc mật khẩu không chính xác"
                    };
                }

                // Verify password với BCrypt
                bool isPasswordValid = BCrypt.Net.BCrypt.Verify(password, user.Password);

                if (!isPasswordValid)
                {
                    return new AuthResult
                    {
                        Success = false,
                        Message = "Tên đăng nhập hoặc mật khẩu không chính xác"
                    };
                }

                return new AuthResult
                {
                    Success = true,
                    Message = "Đăng nhập thành công",
                    User = user
                };
            }
            catch (Exception ex)
            {
                return new AuthResult
                {
                    Success = false,
                    Message = "Đã xảy ra lỗi trong quá trình đăng nhập. Vui lòng thử lại sau."
                };
            }
        }

        public async Task<AuthResult> RegisterAsync(RegisterVM model)
        {
            try
            {
                // Kiểm tra username đã tồn tại
                var existingUser = await _context.Users
                    .FirstOrDefaultAsync(u => u.Username == model.Username);

                if (existingUser != null)
                {
                    return new AuthResult
                    {
                        Success = false,
                        Message = "Tên đăng nhập đã tồn tại"
                    };
                }

                // Kiểm tra email đã tồn tại
                if (!string.IsNullOrWhiteSpace(model.Email))
                {
                    var existingEmail = await _context.Users
                        .FirstOrDefaultAsync(u => u.Email == model.Email);

                    if (existingEmail != null)
                    {
                        return new AuthResult
                        {
                            Success = false,
                            Message = "Email đã được sử dụng"
                        };
                    }
                }

                // Kiểm tra mật khẩu xác nhận
                if (model.Password != model.ConfirmPassword)
                {
                    return new AuthResult
                    {
                        Success = false,
                        Message = "Mật khẩu xác nhận không khớp"
                    };
                }

                // Mã hóa mật khẩu với BCrypt (salt tự động, ngẫu nhiên)
                string hashedPassword = BCrypt.Net.BCrypt.HashPassword(model.Password);

                var user = new User
                {
                    Username = model.Username,
                    Password = hashedPassword,
                    Email = model.Email,
                    PhoneNumber = model.PhoneNumber,
                    Address = model.Address,
                    Role = Role.CUSTOMER, // Luôn là CUSTOMER khi đăng ký
                    CreatedAt = DateTime.Now
                };

                _context.Users.Add(user);
                await _context.SaveChangesAsync();

                return new AuthResult
                {
                    Success = true,
                    Message = "Đăng ký thành công",
                    User = user
                };
            }
            catch (Exception zex)
            {
                return new AuthResult
                {
                    Success = false,
                    Message = "Đã xảy ra lỗi trong quá trình đăng ký. Vui lòng thử lại sau."
                };
            }
        }

        public async Task<User?> GetUserByIdAsync(int userId)
        {
            try
            {
                return await _context.Users
                    .Include(u => u.Orders)
                    .FirstOrDefaultAsync(u => u.Id == userId);
            }
            catch
            {
                return null;
            }
        }

        public async Task<AuthResult> UpdateProfileAsync(int userId, UserProfileVM model)
        {
            try
            {
                var user = await _context.Users.FindAsync(userId);

                if (user == null)
                {
                    return new AuthResult
                    {
                        Success = false,
                        Message = "Không tìm thấy người dùng"
                    };
                }

                // Kiểm tra email mới có bị trùng không (ngoại trừ email của chính user)
                if (!string.IsNullOrWhiteSpace(model.Email) && model.Email != user.Email)
                {
                    var existingEmail = await _context.Users
                        .FirstOrDefaultAsync(u => u.Email == model.Email && u.Id != userId);

                    if (existingEmail != null)
                    {
                        return new AuthResult
                        {
                            Success = false,
                            Message = "Email đã được sử dụng bởi tài khoản khác"
                        };
                    }
                }

                user.Email = model.Email;
                user.PhoneNumber = model.PhoneNumber;
                user.Address = model.Address;

                await _context.SaveChangesAsync();

                return new AuthResult
                {
                    Success = true,
                    Message = "Cập nhật thông tin thành công",
                    User = user
                };
            }
            catch (Exception ex)
            {
                return new AuthResult
                {
                    Success = false,
                    Message = "Đã xảy ra lỗi khi cập nhật thông tin"
                };
            }
        }

        public async Task<List<Order>> GetUserOrdersAsync(int userId)
        {
            try
            {
                return await _context.Orders
                    .Include(o => o.Items)
                        .ThenInclude(i => i.Product)
                            .ThenInclude(p => p.Images)
                    .Where(o => o.User!.Id == userId)
                    .OrderByDescending(o => o.CreatedAt)
                    .ToListAsync();
            }
            catch
            {
                return new List<Order>();
            }
        }

        public async Task<Order?> GetOrderDetailAsync(int orderId, int userId)
        {
            try
            {
                return await _context.Orders
                    .Include(o => o.Items)
                        .ThenInclude(i => i.Product)
                            .ThenInclude(p => p.Images)
                    .FirstOrDefaultAsync(o => o.Id == orderId && o.User!.Id == userId);
            }
            catch
            {
                return null;
            }
        }

        public async Task<AuthResult> ChangePasswordAsync(int userId, string oldPassword, string newPassword)
        {
            try
            {
                var user = await _context.Users.FindAsync(userId);

                if (user == null)
                {
                    return new AuthResult
                    {
                        Success = false,
                        Message = "Không tìm thấy người dùng"
                    };
                }

                // Verify mật khẩu cũ
                bool isOldPasswordValid = BCrypt.Net.BCrypt.Verify(oldPassword, user.Password);

                if (!isOldPasswordValid)
                {
                    return new AuthResult
                    {
                        Success = false,
                        Message = "Mật khẩu cũ không chính xác"
                    };
                }

                // Hash mật khẩu mới với salt ngẫu nhiên
                user.Password = BCrypt.Net.BCrypt.HashPassword(newPassword);
                await _context.SaveChangesAsync();

                return new AuthResult
                {
                    Success = true,
                    Message = "Đổi mật khẩu thành công"
                };
            }
            catch (Exception ex)
            {
                return new AuthResult
                {
                    Success = false,
                    Message = "Đã xảy ra lỗi khi đổi mật khẩu"
                };
            }
        }

        // Kiểm tra username đã tồn tại (cho validation realtime)
        public async Task<bool> UsernameExistsAsync(string username)
        {
            try
            {
                return await _context.Users
                    .AnyAsync(u => u.Username == username);
            }
            catch
            {
                return false;
            }
        }

        // Kiểm tra email đã tồn tại (cho validation realtime)
        public async Task<bool> EmailExistsAsync(string email)
        {
            try
            {
                return await _context.Users
                    .AnyAsync(u => u.Email == email);
            }
            catch
            {
                return false;
            }
        }

    }


    public class AuthResult
    {
        public bool Success { get; set; }
        public string Message { get; set; } = "";
        public User? User { get; set; }
    }
}


