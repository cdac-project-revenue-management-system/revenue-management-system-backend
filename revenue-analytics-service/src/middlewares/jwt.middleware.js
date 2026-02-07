const jwt = require('jsonwebtoken');

const authenticateToken = (req, res, next) => {
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1];

    if (!token) {
        console.log("No token provided");
        return res.sendStatus(401);
    }

    // Debugging logs
    // console.log("Verifying token:", token.substring(0, 10) + "...");

    // Java backend uses Base64 decoding for the key.
    const secret = Buffer.from(process.env.JWT_SECRET, 'base64');

    jwt.verify(token, secret, (err, user) => {
        if (err) {
            console.error("JWT Verification failed:", err.message);
            // console.error("Token:", token);
            return res.sendStatus(403);
        }
        req.user = user;
        next();
    });
};

module.exports = authenticateToken;
