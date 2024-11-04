const jwt = require('jsonwebtoken');
const {User} = require('../models/user');
const secretKey = process.env.ACCESS_TOKEN_SECRET;

const adminMiddleware = async (req, res, next) => {


    const authHeader = req.header('Authorization');
    
    if (!authHeader) {
        return res.status(401).json({ message: 'No token, authorization denied' });
    }
    const token = authHeader.split(' ')[1];
    if (!token) {
        return res.status(401).json({ message: 'Authorization token format is incorrect' });
    }

    try {
        const decoded = jwt.verify(token, secretKey);
        const user = await User.findOne({ email: decoded.email });

        if (!user) {
            return res.status(401).json({ message: 'Token is not valid' });
        } else if (user.role!="admin") {
            console.log(user.role)
            return res.status(401).json({message:"Nije dobar tip korisnika."})
        }
        else{
            req.user = user;
            next();
        }
    } catch (error) {
        console.log(error)
        return res.status(401).json({ message: 'Token is not valid', error: error.message });
    }
};

module.exports = { adminMiddleware };
