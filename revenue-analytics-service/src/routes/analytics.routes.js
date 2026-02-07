const express = require('express');
const router = express.Router();
const AnalyticsController = require('../controllers/analytics.controller');
const authenticateToken = require('../middlewares/jwt.middleware');

// We can keep auth middleware, but user needs to send token.
// The frontend "api" config handles token sending.
router.use(authenticateToken);

router.get('/dashboard', AnalyticsController.getDashboardData);
router.get('/page', AnalyticsController.getAnalyticsPageData);

module.exports = router;
