using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace LoggerService.Models;

public class LogEntry
{
    [Key]
    public long LogId { get; set; }

    [Required]
    [MaxLength(100)]
    public string ServiceName { get; set; } = string.Empty;

    [Required]
    [MaxLength(50)]
    public string Environment { get; set; } = string.Empty;

    [Required]
    [MaxLength(20)]
    public string LogLevel { get; set; } = string.Empty;

    [Required]
    public string Message { get; set; } = string.Empty;

    public string? ExceptionDetails { get; set; }
    
    [MaxLength(100)]
    public string? TraceId { get; set; }
    
    [MaxLength(50)]
    public string? ClientIp { get; set; }
    
    [MaxLength(100)]
    public string? CreatedBy { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
