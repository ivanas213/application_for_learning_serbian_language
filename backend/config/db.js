const mongoose = require('mongoose');
require('dotenv').config();
const base_name = "MojaBaza";
const dbHost= process.env.DB_HOST
const dbPort =process.env.DB_PORT
const url = `mongodb://${dbHost}:${dbPort}/${base_name}`;

const connectDB = async () => {
    try {
        mongoose.connect(url)
        .then(() => {
            console.log('Connected to DB');
        })
        .catch((error) => {
            console.error('Error connecting to MongoDB:', error);
        });
        const db = mongoose.connection;

        db.on('error', (error) => {
            console.error('MongoDB connection error:', error);
        });

        db.once('open', () => {
            console.log('Status open');
        });

        db.on('disconnected', () => {
            console.log('Disconnected from MongoDB');
        });

    } catch (error) {
        console.error('Error while connecting to MongoDB:', error);
        process.exit(1);
    }
};

module.exports.connectDB = connectDB;
