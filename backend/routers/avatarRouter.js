const express = require('express');
const router = express.Router();
const Avatar=require('../models/avatar')


router.get('/getAvatars', async (_, res) => {
    try {
        const avatars = await Avatar.find(); 

        res.status(200).json(avatars);
    } catch (error) {
        res.status(500).send('Internal Server Error'+error);
    }
});
router.get('/:id/picture', async (req, res) => {
    try {
        const avatarId = req.params.id; 
        const avatar = await Avatar.findById(avatarId); 

        if (!avatar) {
            return res.status(404).json({ error: 'Avatar not found' }); 
        }

        res.json({ image: avatar.picture }); 
    } catch (error) {
        res.status(500).json({ error: 'Internal server error' +error}); 
    }
});
module.exports.avatarRouter = router;
