const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const analyticsRoutes = require('./routes/analytics.routes');

const app = express();

app.use(express.json());
app.use(cors());
app.use(helmet());
app.use(morgan('dev'));

app.use('/api/analytics', analyticsRoutes);

app.get('/', (req, res) => {
    res.json({ message: 'Analytics Service is running' });
});

module.exports = app;
