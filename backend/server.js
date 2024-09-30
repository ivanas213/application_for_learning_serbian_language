const express = require('express');
const connectDB = require('./config/db').connectDB;
const userRouter = require('./routers/userRouter').userRouter;
const lessonRouter=require('./routers/lessonRouter').lessonRouter
const avatarRouter=require('./routers/avatarRouter').avatarRouter
const topicRouter=require('./routers/topicRouter').topicRouter
const {initializeData}=require('./config/initData')

const cors = require('cors');
const app = express();
const PORT = process.env.PORT;

app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ limit: '10mb', extended: true }));
const router=express.Router()
app.use(router)
router.use('/user',userRouter); 
router.use('/lesson',lessonRouter)
router.use('/avatar',avatarRouter)
router.use('/topic',topicRouter)
app.use(cors());

connectDB().then(() => {
    initializeData()
    app.listen(PORT, '0.0.0.0', () => {
        console.log(`Server slusa na portu ${PORT}...`);
    });
}).catch((error) => {
    console.error('Greška pri pokretanju servera:', error);
});
