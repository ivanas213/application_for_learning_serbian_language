const express = require('express');
const router = express.Router();
const {ChildResult}=require('../models/user');
const {Lesson,LessonType,GameType, HangmanGame, MemoryGame} = require('../models/lesson');
const middlewares=require('../middleware/authMIddleware')
const {
    TrueFalseQuestion,
    MultipleChoiceQuestion,
    MatchingQuestion,
    SequenceQuestion,
    SpellingCorrectionQuestion,
    FillQuestion,
    ClassicQuestion,
    Question
} = require('../models/Question'); 
const mongoose = require('mongoose');



router.get('/grade/:gradeNumber', async (req, res) => {
    try {
        const gradeNumber = parseInt(req.params.gradeNumber);

        if (isNaN(gradeNumber) || gradeNumber < 1 || gradeNumber > 4) {
            return res.status(400).json({ error: 'Invalid grade number' });
        }

        const lessons = await Lesson.find({ 'topic.grade': gradeNumber }).populate('topic');

        res.json(lessons);
    } catch (error) {
        res.status(500).json({ error: 'Failed to fetch lessons' });
    }
});

router.post('/addLesson', async (req, res) => {
    try {
        const { type, title, description, topic, contentBlocks, words, cards, gridSize, gameType, difficultyLevel } = req.body;
        let lesson;

        switch (type) {
            case "лекција":
                lesson = new LessonType({ title, description, topic, contentBlocks });
                break;

            case "тест":
                break;

            case "игрица":
                let game;
                if (gameType === "вешалице") {
                    game = new HangmanGame({ gameType, difficultyLevel, words }); 
                } else if (gameType === "меморија") {
                    game = new MemoryGame({ gameType, difficultyLevel, cards, gridSize }); 
                } else {
                    return res.status(400).json({ error: 'Unknown game type' });
                }

                await game.save();

                lesson = new GameType({ title, description, game: game._id,topic });
                break;

            default:
                return res.status(400).json({ error: 'Invalid lesson type' });
        }

        await lesson.save();
        res.status(201).json({ message: 'Lesson added successfully', lesson: lesson });

    } catch (error) {
        res.status(500).json({ error: 'Failed to add lesson: ' + error });
    }
});


router.get('/getLessons', async (_, res) => {
    try {
        const lessons = await Lesson.find().populate('topic');
        res.json(lessons);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
});
router.get('/getLessons/:topicId', async (req, res) => {
    try {
        const { topicId } = req.params;

        
        const lessons = await Lesson.find({ topic: topicId })
            .populate({
                path: 'contentBlocks.questionBlock', 
            })
            .populate({
                path: 'game', 
            });

        res.status(200).send(lessons);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: error.message });
    }
});

router.get('/getLessonTitles/:topicId', async (req, res) => {
    try {
        const { topicId } = req.params;

        const lessons = await Lesson.find({ topic: topicId }, { _id: 1, title: 1, type:1 });

        res.status(200).send(lessons);
    } catch (error) {
        console.error(error);
        res.status(500).json({ "message": error.message });
    }
});


router.post('/:lessonId/addQuestion', async (req, res) => {
    try {
        
        const { lessonId } = req.params;
        const { questionBlock } = req.body;
        questionBlock.lesson=lessonId
        let question;
        switch (questionBlock.questionType) {
            case 'true_false':
                question = new TrueFalseQuestion(questionBlock);
                break;
            case 'multiple_choice':
                question = new MultipleChoiceQuestion(questionBlock);
                break;
            case 'matching':
                question = new MatchingQuestion(questionBlock);
                break;
            case 'sequence':
                question = new SequenceQuestion(questionBlock);
                break;
            case 'spelling_correction':
                question = new SpellingCorrectionQuestion(questionBlock);
                break;
            case 'fill':
                question = new FillQuestion(questionBlock);
                break;
            case "classic":
                question=new ClassicQuestion(questionBlock)
                break
            default:
                return res.status(400).json({ error: 'Invalid question type' });
        }

        const lesson = await Lesson.findById(lessonId);
        if (!lesson) {
            return res.status(404).json({ error: 'Lesson not found' });
        }

        lesson.contentBlocks.push({
            type: 'question',
            questionBlock: question._id,
        });

        await question.save();
        await lesson.save();

        res.status(201).json({ message: 'Question added successfully', lesson });
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: 'Server error' });
    }
});
router.put('/updateQuestionTopicsTest', async (req, res) => {
    try {
        const questions = await Question.find().populate('lesson');

        for (const question of questions) {
            if (question.lesson && question.lesson.topic) {
                question.topic = question.lesson.topic;
                await question.save(); 
            }
        }

        res.status(200).json({ message: 'topics updated successfully' });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Failed to update question topics: ' + error.message });
    }
});

router.post('/:lessonId/addQuestions', async (req, res) => {
    try {
        const { lessonId } = req.params;
        const { questions } = req.body; 
        

        const lesson = await Lesson.findById(lessonId);
        if (!lesson) {
            return res.status(404).json({ error: 'Lesson not found' });
        }

        const questionsToSave = [];
        const contentBlocksToAdd = [];
        for (const q of questions) {
            q.questionBlock.lesson = lessonId;
            let question
            switch (q.questionBlock.questionType) {
                case 'true_false':
                    question = new TrueFalseQuestion(q.questionBlock);
                    break;
                case 'multiple_choice':
                    question = new MultipleChoiceQuestion(q.questionBlock);
                    break;
                case 'matching':
                    question = new MatchingQuestion(q.questionBlock);
                    break;
                case 'sequence':
                    question = new SequenceQuestion(q.questionBlock);
                    break;
                case 'spelling_correction':
                    question = new SpellingCorrectionQuestion(q.questionBlock);
                    break;
                case 'fill':
                    question = new FillQuestion(q.questionBlock);
                    break;
                case "classic":
                    question = new ClassicQuestion(q.questionBlock);
                    break;
                default:
                    return res.status(400).json({ error: `Invalid question type: ${q.type}` });
            }

            questionsToSave.push(question);
            contentBlocksToAdd.push({
                type: 'question',
                questionBlock: question._id,
            });
        }

        await Promise.all(questionsToSave.map(question => question.save()));

        lesson.contentBlocks.push(...contentBlocksToAdd);
        await lesson.save();

        res.status(201).json({ message: 'Questions added successfully', lesson });
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: error});
    }
});


router.post('/updateTypeToLesson', async (req, res) => {
    try {
        
        const result = await Lesson.updateMany({}, { type: 'лекција' });
        
        res.status(200).json({ message: 'Successfully updated all documents', updatedCount: result.nModified });
    } catch (error) {
        res.status(500).json({ message: 'An error occurred while updating documents', error: error.message });
    }
});

router.post('/child/addLesson/:childId', middlewares.authMiddleware, async (req, res) => {
    try {
        console.log("Hello from addLesson")
        const { lessonId } = req.body; 
        const { childId } = req.params; 

        const user = req.user;

        const child = user.children.id(childId);
        const lesson=await Lesson.findById(lessonId)
        const childResult=new ChildResult({
            user:user,
            lesson:lesson.title,
            childName:child.name
        })
        await childResult.save()
        if (!child) {
            console.log('Dete nije pronađeno')

            return res.status(404).json({ message: 'Dete nije pronađeno' });
        }

        if (child.progress.completed_lessons.includes(lessonId)) {
            console.log('Lekcija je već završena')

            return res.status(200).json({ message: 'Lekcija je već završena' });
        }
        
        child.progress.completed_lessons.push(lessonId);

        await user.save();
        res.status(200).json({ message: 'Lekcija je uspešno dodata' });
    } catch (error) {
        res.status(500).json({ message: error });
    }
});
router.put('/:id/blocks', async (req, res) => {
    try {
        const lessonId = req.params.id;

        const { contentBlocks } = req.body;
   
        if (!contentBlocks || contentBlocks.length === 0) {
            return res.status(400).json({ error: 'Blokovi sadržaja su obavezni' });
        }

        let lesson = await LessonType.findById(lessonId);
        if (!lesson) {
            return res.status(404).json({ error: 'Lekcija nije pronađena' });
        }

        for (let block of contentBlocks) {
            switch (block.type) {
                case 'text':
                    lesson.contentBlocks.push({
                        type: 'text',
                        textBlock: {
                            content: block.textBlock.content,
                            style: block.textBlock.style,
                            position: block.textBlock.position
                        }
                    });
                    break;
                case 'image':
                    lesson.contentBlocks.push({
                        type: 'image',
                        imageBlock: {
                            url: block.imageBlock.url,
                            description: block.imageBlock.description,
                            position: block.imageBlock.position,
                            alignment: block.imageBlock.alignment
                        }
                    });
                    break;
                case 'table':
                    lesson.contentBlocks.push({
                        type: 'table',
                        tableBlock: {
                            headers: block.tableBlock.headers,
                            rows: block.tableBlock.rows,
                            position: block.tableBlock.position
                        }
                    });
                    break;
                default:
                    return res.status(400).json({ error: `Nepoznat tip bloka: ${block.type}` });
            }
        }

        await lesson.save();

        res.status(200).json({ message: 'Blokovi uspešno dodati u lekciju', lesson });
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: 'Greška na serveru' });
    }
});
router.get('/getLessonById/:lessonId', async (req, res) => {
    try {
        const { lessonId } = req.params;

        const lesson = await Lesson.findById(lessonId)
            .populate({
                path: 'contentBlocks.questionBlock', 
            })
            .populate({
                path: 'game', 
            });

        if (!lesson) {
            return res.status(404).json({ message: 'Lesson not found' });
        }

        res.status(200).send(lesson);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: error.message });
    }
});

router.get('/questions-by-topic/:topicId', async (req, res) => {
    try {
        const { topicId } = req.params;

        const questions = await Question.find({ topic: topicId })
            .populate({
                path: 'lesson', 
                select: 'title topic' 
            })
            .exec();

        const filteredQuestions = questions.filter(question => {
            return question.lesson && question.lesson.topic.toString() === topicId;
        });

        res.status(200).json(filteredQuestions);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error' });
    }
});



module.exports = router;
module.exports.lessonRouter = router;
