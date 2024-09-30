const {User} = require('../models/user');



const getUserByEmail = async (e) => {
    return await User.findOne({ email:e }).exec();
};

const createUser = async (userData) => {
    try {
        const user = new User(userData);
        return await user.save();
    } catch (err) {
        console.log(err)
    }
};




const addChild = async (email, childName, birthdate, image) => {
    const newChild = {
        name: childName,
        birthdate: birthdate,
        image: image
    };
    const u=await getUserByEmail(email)

    try {
        const user = await User.findByIdAndUpdate(
            u._id,
            { $push: { children: newChild } },
            { new: true, useFindAndModify: false }
        ).exec();

        return user;
    } catch (err) {
        console.error('Error adding child:', err);
        throw err;
    }
};


module.exports = {
    getUserByEmail,
    createUser,
    addChild
};
