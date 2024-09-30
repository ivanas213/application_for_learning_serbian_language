const nodemailer = require('nodemailer');
const jwt = require('jsonwebtoken');
require('dotenv').config();
const userService=require('./userService')


async function sendVerificationEmail(user) {
    if (!user || !user.email) {
        throw new Error('User data is incomplete or missing');
    }
    
    const transporter = nodemailer.createTransport({
        host: process.env.SMTP_HOST,
        port: process.env.SMTP_PORT,
        secure: false, 
        auth: {
            user: process.env.SMTP_USERNAME,
            pass: process.env.SMTP_PASSWORD
        }
    });

    const token = jwt.sign(
        { userId: user._id }, 
        process.env.ACCESS_TOKEN_SECRET,
        { expiresIn: '10m' }
    );

    const mailConfigurations = {
        from: process.env.EMAIL_FROM,
        to: user.email, 
        subject: 'Verifikacija email adrese',
        text: `Zdravo ${user.name}, 
               Molimo vas da verifikujete vašu email adresu klikom na sledeći link:
               ${process.env.FRONTEND_URL}/verify/${token} 
               Hvala!`
    };

    try {
        const info = await transporter.sendMail(mailConfigurations);
        return { success: true, message: 'Verification email sent' };
    } catch (error) {
        console.error('Error sending email:', error);
        throw new Error('Failed to send verification email');
    }
}

module.exports = { sendVerificationEmail };
