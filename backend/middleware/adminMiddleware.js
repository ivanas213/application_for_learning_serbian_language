const jwt = require('jsonwebtoken');
const {User} = require('../models/user');
const secretKey = process.env.ACCESS_TOKEN_SECRET;

const adminMiddleware = async (req, res, next) => {

    console.log("Hello from admin middleware")
    const authHeader = req.header('Authorization');
    
    if (!authHeader) {
        console.log("Nema tokena")
        return res.status(401).json({ message: 'No token, authorization denied' });
    }
    console.log("ima tokena")
    const token = authHeader.split(' ')[1];
    if (!token) {
        console.log("Token nije validan")
        return res.status(401).json({ message: 'Authorization token format is incorrect' });
    }
    console.log("token je validan")

    try {
        const decoded = jwt.verify(token, secretKey);
        const user = await User.findOne({ email: decoded.email });
        console.log("Da li imamo usera")
        if (!user) {
            console.log("Nema korisnika")
            return res.status(401).json({ message: 'Token is not valid' });
        } else if (user.role!="admin") {
            console.log(user.role)
            return res.status(401).json({message:"Nije dobar tip korisnika."})
        }
        else{
            req.user = user;
            console.log(user)
            next();
        }
    } catch (error) {
        console.log(error)
        return res.status(401).json({ message: 'Token is not valid', error: error.message });
    }
};

module.exports = { adminMiddleware };
