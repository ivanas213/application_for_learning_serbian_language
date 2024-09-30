const nodemailer = require('nodemailer');

const transporter = nodemailer.createTransport({
    host: process.env.SMTP_HOST,
    port: process.env.SMTP_PORT,
    secure: false,
    auth: {
        user: process.env.SMTP_USERNAME,
        pass: process.env.SMTP_PASSWORD
    }
});

const sendEmail = async (to, subject, htmlContent) => {
    try {
        const mailOptions = {
            from: process.env.EMAIL_FROM,
            to: to,
            subject: subject,
            html: htmlContent,
        };

        await transporter.sendMail(mailOptions);
    } catch (error) {
        console.error(`Failed to send email to ${to}:`, error);
        throw new Error('Failed to send email');
    }
};

module.exports = sendEmail;
