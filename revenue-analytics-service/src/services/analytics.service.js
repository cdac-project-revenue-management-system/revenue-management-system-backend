const pool = require('../config/db');

const AnalyticsService = {
  async getDashboardStats(companyId) {
    const connection = await pool.getConnection();
    try {
      // 1. Monthly Revenue
      const [revRows] = await connection.execute(`
        SELECT SUM(amount) as val 
        FROM invoices 
        WHERE company_id = ? AND status = 'PAID' 
        AND MONTH(issue_date) = MONTH(CURRENT_DATE()) AND YEAR(issue_date) = YEAR(CURRENT_DATE())
      `, [companyId]);
      const monthlyRevenue = revRows[0].val || 0;

      // 2. MRR
      const [mrrRows] = await connection.execute(`
        SELECT 
          SUM(
            CASE 
              WHEN p.plan_interval = 'MONTHLY' THEN p.price 
              WHEN p.plan_interval = 'YEARLY' THEN p.price / 12 
              ELSE 0 
            END
          ) as mrr
        FROM subscriptions s
        JOIN plans p ON s.plan_id = p.id
        JOIN products pr ON p.product_id = pr.id
        WHERE pr.company_id = ? AND s.status = 'ACTIVE'
      `, [companyId]);
      const mrr = mrrRows[0].mrr || 0;

      // 3. Active Subscriptions
      const [subRows] = await connection.execute(`
        SELECT COUNT(*) as count 
        FROM subscriptions s
        JOIN plans p ON s.plan_id = p.id
        JOIN products pr ON p.product_id = pr.id
        WHERE pr.company_id = ? AND s.status = 'ACTIVE'
      `, [companyId]);
      const activeSubscriptions = subRows[0].count || 0;

      // 4. Total Clients
      const [clientRows] = await connection.execute(`
        SELECT COUNT(DISTINCT s.client_id) as count
        FROM subscriptions s
        JOIN plans p ON s.plan_id = p.id
        JOIN products pr ON p.product_id = pr.id
        WHERE pr.company_id = ?
      `, [companyId]);
      const totalClients = clientRows[0].count || 0;

      // 5. Churn Rate
      const [churnRows] = await connection.execute(`
        SELECT 
          COUNT(CASE WHEN s.status = 'CANCELLED' THEN 1 END) as cancelled,
          COUNT(*) as total
        FROM subscriptions s
        JOIN plans p ON s.plan_id = p.id
        JOIN products pr ON p.product_id = pr.id
        WHERE pr.company_id = ?
      `, [companyId]);
      const totalSubs = churnRows[0].total || 0;
      const cancelledSubs = churnRows[0].cancelled || 0;
      const churnRate = totalSubs > 0 ? ((cancelledSubs / totalSubs) * 100).toFixed(1) : 0;

      // 6. Failed Payments
      const [failedRows] = await connection.execute(`
        SELECT COUNT(*) as count
        FROM invoices
        WHERE company_id = ? AND status IN ('FAILED', 'OVERDUE')
      `, [companyId]);
      const failedPayments = failedRows[0].count || 0;

      return {
        monthlyRevenue,
        mrr,
        activeSubscriptions,
        totalClients,
        churnRate: `${churnRate}%`,
        failedPayments
      };
    } finally {
      connection.release();
    }
  },

  async getRevenueChartData(companyId) {
    // using execute and flattening query string to avoid any parsing issues
    const sql = `
      SELECT 
        DATE_FORMAT(issue_date, '%b') as month,
        SUM(amount) as revenue,
        (SUM(amount) * 0.8) as mrr 
      FROM invoices
      WHERE company_id = ? AND status = 'PAID'
      AND issue_date >= DATE_SUB(NOW(), INTERVAL 12 MONTH)
      GROUP BY YEAR(issue_date), MONTH(issue_date), DATE_FORMAT(issue_date, '%b')
      ORDER BY YEAR(issue_date), MONTH(issue_date)
    `;
    const [rows] = await pool.execute(sql, [companyId]);
    return rows;
  },

  async getSubscriptionChartData(companyId) {
    const sql = `
      SELECT 
        s.status as name, 
        COUNT(*) as value
      FROM subscriptions s
      JOIN plans p ON s.plan_id = p.id
      JOIN products pr ON p.product_id = pr.id
      WHERE pr.company_id = ?
      GROUP BY s.status
    `;
    const [rows] = await pool.execute(sql, [companyId]);
    return rows;
  },

  async getRecentActivity(companyId) {
    // Invoices
    const sqlInvoices = `
      SELECT 
        i.id, 
        'payment' as type,
        CONCAT('Invoice #', i.id, ' paid') as title,
        c.company_name as description, 
        CONCAT('₹', FORMAT(i.amount, 2)) as amount,
        i.issue_date as dateRaw,
        'success' as status
      FROM invoices i
      JOIN clients c ON i.client_id = c.id
      WHERE i.company_id = ? AND i.status = 'PAID'
      ORDER BY i.issue_date DESC LIMIT 5
    `;
    const [invoices] = await pool.execute(sqlInvoices, [companyId]);

    // Subscriptions
    const sqlSubs = `
      SELECT 
        s.id,
        'subscription' as type,
        'New subscription' as title,
        CONCAT(pl.name, ' - ', c.company_name) as description,
        CONCAT('₹', FORMAT(pl.price, 2)) as amount,
        s.start_date as dateRaw,
        'info' as status
      FROM subscriptions s
      JOIN plans pl ON s.plan_id = pl.id
      JOIN products pr ON pl.product_id = pr.id
      JOIN clients c ON s.client_id = c.id
      WHERE pr.company_id = ?
      ORDER BY s.start_date DESC LIMIT 5
    `;
    const [subs] = await pool.execute(sqlSubs, [companyId]);

    const combined = [...invoices, ...subs].sort((a, b) => new Date(b.dateRaw) - new Date(a.dateRaw)).slice(0, 5);

    return combined.map(item => ({
      ...item,
      time: new Date(item.dateRaw).toLocaleDateString()
    }));
  },

  async getTopProducts(companyId) {
    const sql = `
       SELECT 
         pr.id,
         pr.name,
         pr.revenue,
         pr.active_subscriptions as subscriptions,
         10 as growth,
         LEAST(100, (pr.active_subscriptions * 5)) as progress
       FROM products pr
       WHERE pr.company_id = ?
       ORDER BY pr.revenue DESC
       LIMIT 4
     `;
    const [rows] = await pool.execute(sql, [companyId]);
    return rows;
  },

  async getAnalyticsPageData(companyId) {
    // Reuse getRevenueChartData for mrrData logic
    const mrrData = await this.getRevenueChartData(companyId);

    const churnData = [
      { month: "Jan", churnRate: 2.1, recovered: 0.5 },
      { month: "Feb", churnRate: 1.8, recovered: 0.8 },
      { month: "Mar", churnRate: 2.5, recovered: 1.2 },
      { month: "Apr", churnRate: 1.5, recovered: 0.6 }
    ];

    const sqlRevenueByPlan = `
        SELECT 
          p.name as plan, 
          SUM(i.amount) as revenue, 
          COUNT(DISTINCT i.client_id) as customers
        FROM invoices i
        JOIN subscriptions s ON i.subscription_id = s.id
        JOIN plans p ON s.plan_id = p.id
        WHERE i.company_id = ? AND i.status = 'PAID'
        GROUP BY p.name
      `;
    const [revenueByPlan] = await pool.execute(sqlRevenueByPlan, [companyId]);

    const sqlArpc = `
          SELECT 
            DATE_FORMAT(issue_date, '%b') as month,
            IFNULL(SUM(amount) / NULLIF(COUNT(DISTINCT client_id), 0), 0) as arpc
          FROM invoices
          WHERE company_id = ? AND status = 'PAID'
          AND issue_date >= DATE_SUB(NOW(), INTERVAL 12 MONTH)
          GROUP BY YEAR(issue_date), MONTH(issue_date), DATE_FORMAT(issue_date, '%b')
          ORDER BY YEAR(issue_date), MONTH(issue_date)
      `;
    const [arpcData] = await pool.execute(sqlArpc, [companyId]);

    return {
      mrrData,
      churnData,
      revenueByPlan,
      arpcData
    };
  }
};

module.exports = AnalyticsService;
