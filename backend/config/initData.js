const {User} = require('../models/user'); 
// const Avatar = require('../models/avatar'); 
// const images=require('../data/avatar_images')
require('dotenv').config();
const adminPassword=process.env.ADMIN_PASSWORD
const adminEmail=process.env.ADMIN_EMAIL
const bcrypt = require('bcryptjs');

const initializeData = async () => {
  try {
    const adminExists = await User.findOne({ role: 'admin' });
    if (!adminExists) {
      const salt = await bcrypt.genSalt(10);
      let p = await bcrypt.hash(adminPassword, salt);
      const adminUser = new User({
        email:adminEmail ,
        password: adminPassword, 
        role: 'admin'
      });
      await adminUser.save();
      console.log('Admin user created');
    }

    // const avatars = [
    //   { name: 'Boy1', picture: images.boy1 },
    //   { name: 'Boy2', picture: images.boy2 },
    //   { name: 'Child1', picture: images.child1 },
    //   { name: 'Dinosaur1', picture: images.dinosaur1 },
    //   { name: 'Girl1', picture: images.girl1 },
    //   { name: 'Girl2', picture: images.girl2 },
    //   { name: 'Girl3', picture: images.girl3 },
    //   { name: 'Girl4', picture: images.girl4 },

    // ];
    //const avatarCount = await Avatar.countDocuments();
    // if (avatarCount ==0) {
    //   await Avatar.insertMany(avatars);
    //   console.log('Avatars added');
    // }

  } catch (error) {
    console.error('Error initializing data:', error);
  }
};

module.exports.initializeData = initializeData;
