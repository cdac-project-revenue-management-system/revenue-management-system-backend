CREATE DATABASE CentralizedLogsDB;
GO

USE CentralizedLogsDB;
GO

CREATE TABLE AppLogs (
    LogId BIGINT IDENTITY(1,1) PRIMARY KEY,
    ServiceName NVARCHAR(100) NOT NULL,
    Environment NVARCHAR(50) NOT NULL,
    LogLevel NVARCHAR(20) NOT NULL,
    Message NVARCHAR(MAX) NOT NULL,
    ExceptionDetails NVARCHAR(MAX) NULL,
    TraceId NVARCHAR(100) NULL,
    ClientIp NVARCHAR(50) NULL,
    CreatedBy NVARCHAR(100) NULL,
    CreatedAt DATETIME2 DEFAULT GETUTCDATE()
);

CREATE INDEX IDX_AppLogs_Created_Service ON AppLogs(CreatedAt, ServiceName);

INSERT INTO AppLogs 
(ServiceName, Environment, LogLevel, Message, ExceptionDetails, TraceId, ClientIp, CreatedBy, CreatedAt)
VALUES
('OrderService', 'DEV', 'INFO',
 'Order created successfully with ID 101',
 NULL,
 'TRACE-DEV-001',
 '127.0.0.1',
 'system',
 GETUTCDATE()),

('PaymentService', 'DEV', 'ERROR',
 'Payment processing failed for Order ID 101',
 'NullReferenceException: Object reference not set to an instance of an object',
 'TRACE-DEV-002',
 '127.0.0.1',
 'payment-worker',
 GETUTCDATE()),

('AuthService', 'QA', 'WARN',
 'Multiple failed login attempts detected',
 NULL,
 'TRACE-QA-003',
 '192.168.1.10',
 'auth-service',
 GETUTCDATE()),

('InventoryService', 'PROD', 'INFO',
 'Inventory updated for Product ID P-778',
 NULL,
 'TRACE-PROD-004',
 '10.20.30.40',
 'inventory-cron',
 GETUTCDATE()),

('OrderService', 'PROD', 'ERROR',
 'Database timeout while fetching orders',
 'SqlException: Timeout expired while executing the query',
 'TRACE-PROD-005',
 '10.20.30.50',
 'order-api',
 GETUTCDATE());
