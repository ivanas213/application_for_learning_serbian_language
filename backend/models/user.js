const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const bcrypt = require('bcryptjs');

const testSchema = new mongoose.Schema({
    topicId: {
        type: Schema.Types.ObjectId, ref: 'Topic' 
        ,required: true
    },
    result: {
        type: Number,
        required: true,
        min: 0, 
        max: 100 
    }
});

const progressSchema = new mongoose.Schema({
    completed_lessons: {
        type: [{ type: Schema.Types.ObjectId, ref: 'Lesson' }],
        default: []
    },
    test_results: {
        type: [testSchema],
        default: []
    }
});



const ChildSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true,
        index: true 
    },
    birthdate: {
        type: Date
    },
    image: {
        type: Schema.Types.ObjectId, ref: 'Avatar' 
    },
    progress: {
        type: progressSchema,
        default: {}
    },
    selectedGrade:{
        type:Number,
        default:0
    }
});

const userSchema = new Schema({
    email: {
        type: String,
        unique: true,
        required: true,
        // validate: {
        //     // validator: function(v) {
        //     //    
        //     //     return /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v);
        //     // },
        //     message: props => `${props.value} is not a valid email address!`
        // },
        index: true 
    },
    password: {
        type: String,
        required: true
    },
    role: {
        type: String,
        enum: ['admin', 'user'],
        default: 'user'
    },
    children: {
        type: [ChildSchema],
        validate: [arrayLimitChildren, '{PATH} exceeds the limit of 4 children'], 
        default: []
    }
});

function arrayLimitChildren(val) {
    return val.length <= 4;
}

userSchema.pre('save', async function(next) {
    try {
        if (this.isModified('password') || this.isNew) {
            const salt = await bcrypt.genSalt(10);
            this.password = await bcrypt.hash(this.password, salt);
        }
        next();
    } catch (error) {
        next(error); 
    }
});

userSchema.methods.comparePassword = async function(candidatePassword) {
    try {
        return await bcrypt.compare(candidatePassword, this.password);
    } catch (error) {
        throw new Error(error); 
    }
};
const ChildResultSchema = new mongoose.Schema({
    user: {
        type: Schema.Types.ObjectId, ref: 'User' 
        ,required: true
    },
    childName:{
        type:String,
        required:true
    },
    lesson:{type:String},
    topic:{type:String},
    result:{type:String},
    createdAt: { type: Date, default: Date.now }
});
const User = mongoose.model('User', userSchema);
const ChildResult=mongoose.model('ChildResult',ChildResultSchema)
module.exports = {User,ChildResult};
