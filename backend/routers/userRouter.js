const express = require('express');
const router = express.Router();
const userService = require('../services/userService');
const jwt=require("jsonwebtoken")
require('dotenv').config();
const bcrypt = require('bcryptjs');
const middlewares=require('../middleware/authMIddleware')
const {adminMiddleware}=require('../middleware/adminMiddleware')
const sendEmail=require('../services/emailSender')
const {User,ChildResult}=require('../models/user');
const { Lesson } = require('../models/lesson');
const Topic = require('../models/topic');
const avatars=require('../data/avatar_images')

router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;
        const user = await userService.getUserByEmail(email);
        if (!user) {
            return res.status(404).json({ message: 'Invalid credentials' });
        }
      
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(404).json({ message: 'Invalid credentials' });
        }
        const token = jwt.sign({ email: user.email }, process.env.ACCESS_TOKEN_SECRET, { expiresIn: '200h' });
        res.status(200).json({message:"Succesfull login",token,role:user.role});
    } catch (error) {
        console.error('Error during login:', error);
        res.status(500).send('Internal Server Error');
    }
});
router.post('/addChild', middlewares.authMiddleware, async (req, res) => {
    try {
        
        let { name, image } = req.body;
        const email = req.user.email;  

        if (!email || !name) {
            return res.status(400).json({ message: "Sva polja su obavezna." });
        }
        if(!image){
            return res.status(401).json({message:"Name slike"})
        }

        const user = await User.findOne({ email });

        if (!user) {
            return res.status(404).json({ message: "Korisnik nije pronađen." });
        }

        const newChild = {
            name: name,
            image: image,
            progress: {
                completed_lessons: [],
                test_results: []
            }
        };

        user.children.push(newChild);

        await user.save();

        res.status(200).json({ message: "Uspešno dodavanje deteta" });
    } catch (error) {
        console.error('Error during adding child:', error);
        res.status(500).send('Internal Server Error');
    }
});
router.post('/register', async(req, res) => {
    try {
        const { email, password } = req.body;
       
        const token = jwt.sign({ email, password }, process.env.ACCESS_TOKEN_SECRET, { expiresIn: '10h' });
     
        const html_content= `
        <div style="font-family: Arial, sans-serif; line-height: 1.5; color: #333;">
            <h2 style="color: #4CAF50;">Dobrodošli!</h2>
            <p>Hvala što ste se registrovali. Da biste završili proces registracije, molimo vas da kliknete na dugme ispod kako biste verifikovali svoju email adresu:</p>
            <a href="${process.env.BACKEND_URL}/user/verify/${token}" style="display: inline-block; padding: 10px 20px; margin-top: 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;">Verifikujte email</a>
            <p>Ako niste vi zatražili ovu akciju, slobodno zanemarite ovaj email.</p>
            <p>Srdačno,<br />Vaš tim</p>
        </div>
    `
        await sendEmail(email,"Verifikacija email adrese", html_content)
        
        
        
        res.status(200).json({message:"Uspesno poslat mejl!"});

    } catch (error) {
        console.error('Error during registration:', error);
        res.status(500).send('An error occurred during registration');
    }
});
router.get('/getChildren', middlewares.authMiddleware, async (req, res) => {
    console.log("Hello from getChildren")
    try {
        const user = await User.findById(req.user._id)
            .populate({
                path: 'children.progress.completed_lessons', 
                select: 'title' 
            })
            .populate({
                path: 'children.progress.test_results.topicId', 
                select: 'name'
            })
            .populate({
                path: 'children.image',
                select: 'picture' 
            });
    
        res.status(200).json(user.children);
    } catch (error) {
        res.status(500).json({ message: 'Greška na serveru', error });
    }
});

router.get('/verify/:token', async (req, res) => {
    const { token } = req.params;

    try {
        const decoded = jwt.verify(token, process.env.ACCESS_TOKEN_SECRET);
        const { email, password } = decoded;

        const role = "user"; 
        await userService.createUser({email:email, password:password, role:role});

        res.redirect(`myuniqueapp://main_activity?token=${token}`);
        
    } catch (error) {
        console.error('Invalid token:', error);
        res.status(400).send('Invalid token');
    }
});

router.post('/forgottenPassword', async (req, res) => {
    try {
        const { email } = req.body;
        if (!await userService.getUserByEmail(email)){
            res.status(401).json({"message":"Мејл који сте унели не постоји у бази."})
            return 
        }
        const resetToken = jwt.sign({ email: email }, process.env.ACCESS_TOKEN_SECRET, { expiresIn: '1h' });

        const htmlContet=`
                <div style="font-family: Arial, sans-serif; line-height: 1.5; color: #333;">
                    <h2 style="color: #4CAF50;">Zaboravili ste lozinku?</h2>
                    <p>Da biste promenili lozinku, kliknite na dugme ispod:</p>
                    <a href="${process.env.BACKEND_URL}/user/changePassword/${resetToken}" style="display: inline-block; padding: 10px 20px; margin-top: 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;">Promena lozinke</a>
                    <p>Ako niste vi zatražili ovu akciju, slobodno zanemarite ovaj email.</p>
                    <p>Srdačno,<br />Vaš tim</p>
                </div>
            `
        await sendEmail(email,"Zaboravljena lozinka",htmlContet)
        res.status(200).json({"message":"Послат вам је мејл."})
    } catch (error) {
        console.error('Error during password reset:', error);
        res.status(500).send('An error occurred during password reset');
    }
});
router.get('/changePassword/:token', async (req, res) => {
    console.log("Hello from change password")
    console.log(req.params)
    const { token } = req.params;
    res.send(`
        <!DOCTYPE html>
        <html>
        <head>
            <title>Promena lozinke</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f4;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    height: 100vh;
                    margin: 0;
                }
                .container {
                    background-color: #fff;
                    padding: 20px;
                    border-radius: 8px;
                    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
                    max-width: 400px;
                    width: 100%;
                }
                h2 {
                    text-align: center;
                    margin-bottom: 20px;
                    color: #333;
                }
                label {
                    display: block;
                    margin-bottom: 5px;
                    font-weight: bold;
                }
                input[type="password"] {
                    width: 100%;
                    padding: 10px;
                    margin-bottom: 20px;
                    border-radius: 4px;
                    border: 1px solid #ccc;
                }
                input[type="submit"] {
                    width: 100%;
                    padding: 10px;
                    background-color: #4CAF50;
                    border: none;
                    border-radius: 4px;
                    color: white;
                    font-weight: bold;
                    cursor: pointer;
                    transition: background-color 0.3s;
                }
                input[type="submit"]:hover {
                    background-color: #45a049;
                }
                .message {
                    text-align: center;
                    margin-bottom: 20px;
                    color: red;
                    font-weight: bold;
                }
                .success {
                    color: green;
                }
                .error {
                    color: red;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <h2>Promena lozinke</h2>
                <form action="/user/changePassword/${token}" method="POST" onsubmit="clearErrorMessages()">
                    <label for="password">Nova lozinka:</label>
                    <input type="password" id="password" name="password" required oninvalid="setCustomValidity('Ovo polje je obavezno')" oninput="setCustomValidity('')"><br>
                    <label for="passwordAgain">Ponovo nova lozinka:</label>
                    <input type="password" id="passwordAgain" name="passwordAgain" required oninvalid="setCustomValidity('Ovo polje je obavezno')" oninput="setCustomValidity('')"><br>
                    <input type="submit" value="Potvrdi">
                </form>
                <div id="message" class="error">${req.query.error || ''}</div>
                <div id="message" class="success">${req.query.success || ''}</div>
            </div>
            <script>
                function clearErrorMessages() {
                    document.getElementById('message').innerHTML = '';
                }
            </script>
        </body>
        </html>
    `);
});

router.post('/changePassword/:token', async (req, res) => {
    const { token } = req.params;
    const { password, passwordAgain } = req.body;

    try {
        const decoded = jwt.verify(token, process.env.ACCESS_TOKEN_SECRET);
        const email = decoded.email;

      
        if (password.length < 8) {
            return res.redirect(`/user/changePassword/${token}?error=Lozinka mora imati barem 8 karaktera`);
        }
        if (!/[A-Z]/.test(password)) {
            return res.redirect(`/user/changePassword/${token}?error=Lozinka mora imati barem jedno veliko slovo`);
        }
        if (!/[0-9]/.test(password)) {
            return res.redirect(`/user/changePassword/${token}?error=Lozinka mora imati barem jedan broj`);
        }
        if (password !== passwordAgain) {
            return res.redirect(`/user/changePassword/${token}?error=Lozinke se ne poklapaju`);
        }

        const user = await userService.getUserByEmail(email);
        if (!user) {
            return res.redirect(`/user/changePassword/${token}?error=Korisnik nije pronađen`);
        }

        const isMatch = await bcrypt.compare(password, user.password);
        if (isMatch) {
            return res.redirect(`/user/changePassword/${token}?error=Lozinka je ista kao i prethodna`);
        }

        const hashedPassword = await bcrypt.hash(password, 10);

        await User.updateOne({ email }, { password: hashedPassword });

        res.redirect(`myuniqueapp://login_activity?`);

    } catch (error) {
        console.error('Error changing password:', error);
        res.status(500).send('An error occurred while changing the password');
    }
});
router.put('/updateGrade/:childId',middlewares.authMiddleware, async (req, res) => {
    const { childId } = req.params;
    const userId=req.user._id
    const { selectedGrade } = req.body; 
    try {
        const user = await User.findById(userId);
        if (!user) {
            return res.status(404).json({ message: 'Корисник није пронађен' });
        }

        const child = user.children.id(childId);
        if (!child) {
            return res.status(404).json({ message: 'Дете није пронађено' });
        }

        child.selectedGrade = selectedGrade;

        await user.save();

        res.status(200).json({ message: 'Изабрани разред је успешно ажуриран', child });
    } catch (error) {
        res.status(500).json({ message: 'Грешка на серверу', error: error.message });
    }
});
router.get('/child/:childId', middlewares.authMiddleware, async (req, res) => {
    try {
        const childId = req.params.childId;
        const userId = req.user._id;

        const user = await User.findById(userId)
            .populate({
                path: 'children.progress.completed_lessons', 
                select: 'title' 
            })
            .populate({
                path: 'children.progress.test_results.topicId', 
                select: 'name' 
            })
            .populate({
                path: 'children.image',
                select: 'picture'
            });

        const child = user.children.id(childId); 

        if (!child) {
            return res.status(404).json({ message: 'Dete nije pronađeno' });
        }

        return res.status(200).json(child);
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: 'Server error' });
    }
});



router.post('/child/addLesson/:childId', middlewares.authMiddleware, async (req, res) => {
    try {
        const { lessonId } = req.body; 
        const { childId } = req.params; 

        const user = req.user;

        const child = user.children.id(childId);

        if (!child) {

            return res.status(404).json({ message: 'Dete nije pronađeno' });
        }
        const lesson=await Lesson.findById(lessonId)
        const childResult=new ChildResult({
            user:user,
            lesson:lesson.title,
            childName:child.name
        })
        await childResult.save()
        if (child.progress.completed_lessons.includes(lessonId)) {

            return res.status(200).json({ message: 'Lekcija je već završena' });
        }
        child.progress.completed_lessons.push(lessonId);
        
        await user.save();
        res.status(200).json({ message: 'Lekcija je uspešno dodata' });
    } catch (error) {
        console.log(error)
        res.status(500).json({ message: error });
    }
});
router.post('/child/addTestResult/:childId', middlewares.authMiddleware, async (req, res) => {
    const { topicId, result } = req.body;
    const { childId } = req.params;
    const user = req.user;
    const resultAsInt = parseInt(result, 10); 

    const child = user.children.id(childId);
    const topic=await Topic.findById(topicId)
    const childResult=new ChildResult({
        user:user,
        childName:child.name,
        topic:topic.name,
        result:result
    })
    await childResult.save()
    if (!child) {
        return res.status(404).json({ message: 'Dete nije pronađeno' });
    }

    const existingTest = child.progress.test_results.find(test => test.topicId.toString() === topicId);

    if (existingTest) {
        if (result > existingTest.result) {
            await child.save();
            existingTest.result = resultAsInt; 
        } else {
            return res.status(200).json({ message: 'Rezultat nije ažuriran jer je manji ili jednak postojećem' });
        }
    } else {
        const newTest = {
            topicId: topicId,
            result: resultAsInt
        };
        child.progress.test_results.push(newTest);
    }

    try {
        await user.save();
        res.status(200).json({ message: 'Test rezultat uspešno dodat/ažuriran' });
    } catch (error) {
        console.error('Greška prilikom čuvanja korisnika', error);
        res.status(500).json({ message: 'Greška prilikom čuvanja korisnika' });
    }
});
router.get('/users',adminMiddleware, async (_req, res) => {
    try {
        const users = await User.find({ role: 'user' })
            .populate({
                path: 'children.progress.completed_lessons', 
                select: 'title' 
            })
            .populate({
                path: 'children.progress.test_results.topicId', 
                select: 'name' 
            })
            .populate({
                path:'children.image',
                select:"picture"
            })
        res.status(200).json(users);
    } catch (error) {
        console.error('Грешка при дохватању корисника:', error);
        res.status(500).json({ message: 'Грешка при дохватању корисника' });
    }
});
module.exports.userRouter = router;
