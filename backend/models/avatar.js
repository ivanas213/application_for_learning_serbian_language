const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const avatarSchema = new Schema({
    name: { type: String},
    picture: { type: String}
});

const Avatar = mongoose.model('Avatar', avatarSchema);

module.exports = Avatar;
