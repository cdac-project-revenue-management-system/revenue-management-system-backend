using Microsoft.EntityFrameworkCore;
using LoggerService.Models;

namespace LoggerService.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
    {
    }

    public DbSet<LogEntry> AppLogs { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // Explicitly map to table name if needed, though AppLogs property does it
        modelBuilder.Entity<LogEntry>().ToTable("AppLogs");
    }
}
