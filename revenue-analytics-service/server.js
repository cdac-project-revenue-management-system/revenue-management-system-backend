require('dotenv').config();
const app = require('./src/app');
const pool = require('./src/config/db');

const PORT = process.env.PORT || 3001;

// Test DB connection on startup
pool.getConnection()
    .then(conn => {
        console.log("Database connected successfully");
        conn.release();
    })
    .catch(err => {
        console.error("Database connection failed:", err);
        process.exit(1);
    });

const server = app.listen(PORT, () => {
    console.log(`Analytics Service running on port ${PORT}`);
});

server.on('error', (err) => {
    console.error('Server failed to start:', err);
});
