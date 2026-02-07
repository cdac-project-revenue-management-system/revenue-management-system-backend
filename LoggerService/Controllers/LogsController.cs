using Microsoft.AspNetCore.Mvc;
using LoggerService.Data;
using LoggerService.Models;

namespace LoggerService.Controllers;

[ApiController]
[Route("api/logs")]
public class LogsController : ControllerBase
{
    private readonly AppDbContext _context;

    public LogsController(AppDbContext context)
    {
        _context = context;
    }

    [HttpPost]
    public async Task<IActionResult> CreateLog([FromBody] LogEntry log)
    {
        if (log == null) return BadRequest("Log data is null");

        // Ensure we capture server time, though Model has default, explicit is safer
        log.CreatedAt = DateTime.UtcNow;

        _context.AppLogs.Add(log);
        await _context.SaveChangesAsync();

        return Accepted(); // Returns HTTP 202
    }
}
