const jwt = require('jsonwebtoken');
const {User} = require('../models/user');
const secretKey = process.env.ACCESS_TOKEN_SECRET;

const authMiddleware = async (req, res, next) => {

    console.log(req.header)
    const authHeader = req.header('Authorization');
    
    if (!authHeader) {
        console.log("Nema tokena")
        return res.status(401).json({ message: 'No token, authorization denied' });
    }
    
    const token = authHeader.split(' ')[1];
    if (!token) {
        console.log("Token nije validan")
        return res.status(401).json({ message: 'Authorization token format is incorrect' });
    }

    try {
        const decoded = jwt.verify(token, secretKey);
        const user = await User.findOne({ email: decoded.email });
        
        if (!user) {
            console.log("Nema korisnika")
            return res.status(401).json({ message: 'Token is not valid' });
        } else {
            if("lessonId" in req.body)

            console.log("User"+user)
            req.user = user;
            next();
        }
    } catch (error) {
        console.log(error)
        return res.status(401).json({ message: 'Token is not valid', error: error.message });
    }
};

module.exports = { authMiddleware };
