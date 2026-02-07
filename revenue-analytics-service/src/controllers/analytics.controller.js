const AnalyticsService = require('../services/analytics.service');

const AnalyticsController = {
    async getDashboardData(req, res) {
        try {
            const { companyId } = req.query;
            console.log(`[Dashboard] Fetching data for Company ID: ${companyId}`);

            if (!companyId) return res.status(400).json({ error: 'Company ID is required' });

            // Parallel fetch for dashboard
            const [stats, revenueChart, subscriptionChart, recentActivity, topProducts] = await Promise.all([
                AnalyticsService.getDashboardStats(companyId),
                AnalyticsService.getRevenueChartData(companyId),
                AnalyticsService.getSubscriptionChartData(companyId),
                AnalyticsService.getRecentActivity(companyId),
                AnalyticsService.getTopProducts(companyId)
            ]);

            res.json({
                stats,
                revenueChart,
                subscriptionChart,
                recentActivity,
                topProducts
            });
        } catch (error) {
            console.error("[Dashboard] Error:", error);
            res.status(500).json({ error: 'Internal Server Error' });
        }
    },

    async getAnalyticsPageData(req, res) {
        try {
            const { companyId } = req.query;
            console.log(`[Analytics] Fetching page data for Company ID: ${companyId}`);

            if (!companyId) return res.status(400).json({ error: 'Company ID is required' });

            const data = await AnalyticsService.getAnalyticsPageData(companyId);
            res.json(data);
        } catch (error) {
            console.error("[Analytics] Error:", error);
            res.status(500).json({ error: 'Internal Server Error' });
        }
    }
};

module.exports = AnalyticsController;
