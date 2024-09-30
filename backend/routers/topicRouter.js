const express = require('express');
const router = express.Router();
const Topic = require('../models/topic');
const {Question}=require('../models/Question')
const middlewares=require('../middleware/authMIddleware')
const mongoose = require('mongoose');


router.post('/addTopic', async (req, res) => {
    const {name, position, grade, type}=req.body
    try {
        const topic=new Topic({name,position,grade,type}); 
        await topic.save()
        res.status(201).json({"message":"Tema uspesno dodata"});
    } catch (error) {
        res.status(500).send('Internal Server Error');
    }
});
router.get('/getTopics',middlewares.authMiddleware,async(req,res)=>{
    try{
        const topics=await Topic.find()
        return res.status(200).json({"topics":topics})
    }catch(error){
        res.status(500).send("Internal server error")
    }
})
router.get('/getAllTopics',middlewares.authMiddleware,async(req,res)=>{
    try{
        const topics=await Topic.find()
        return res.status(200).json({"topics":topics})
    }catch(error){
        console.error('Greska prilikom dohvatanja tema: ', error);
        res.status(500).send("Internal server error")
    }
})
router.get('/getTopics/:grade',middlewares.authMiddleware,async(req,res)=>{
    try{
        const grade = parseInt(req.params.grade, 10);
        const topics=await Topic.find({grade:grade})
        return res.status(200).json(topics)
    }catch(error){
        console.error('Greska prilikom dohvatanja tema: ', error);
        res.status(500).send("Internal server error")
    }
})
router.get('/random_questions_by_topic/:topicId', middlewares.authMiddleware,async (req, res) => {
    try {
        const { topicId } = req.params;

        const randomQuestions = await Question.aggregate([
            { $match: { 
                topic: new mongoose.Types.ObjectId(topicId), 
                questionType: { $nin: ['matching', 'fill','classic'] } 
            } }, 
            { $sample: { size: 20 } } 
        ]);

        const populatedQuestions = await Question.populate(randomQuestions, {
            path: 'lesson',
            select: 'title topic',
        });

        res.status(200).json(populatedQuestions);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error' });
    }
});
module.exports.topicRouter = router;
