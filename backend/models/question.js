const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const baseOptions = { discriminatorKey: 'questionType', _id: false };

const QuestionSchema = new Schema({
    question: { type: String, required: true },
    type: {
        type: String,
        enum: ['граматика', 'правопис', 'књижевност'],
        required: true
    },
    tags: [String],
    lesson:{type: Schema.Types.ObjectId, ref: 'Lesson'},
    grade: {type:Number, enum:[1,2,3,4]},
    explanation:String,
    topic:{type: Schema.Types.ObjectId, ref: 'Topic'},
    attempts_sum:{type:Number,default:0},
    tried:{type:Number,default:0},
    correct:{type:Number,default:0},
    incorrect:{type:Number,default:0}
}, baseOptions);

const Question = mongoose.model('Question', QuestionSchema);

const TrueFalseQuestionSchema = new Schema({
    correctAnswer: { type: Boolean, required: true } 
});
const ClassicQuestionSchema = new Schema({
    correctAnswers: [{ type: String, required: true }] 
});

const MultipleChoiceQuestionSchema = new Schema({
    options: [{ type: String, required: true }], 
    correctAnswerMC: { type: String, required: true },
});

const MatchingQuestionSchema = new Schema({
    left: [{ type: String, required: true }],
    right: [{ type: String, required: true }]
});
const FillQuestionSchema = new Schema({
    text:{type:String},
    correctAnswers: [{ type: String, required: true }]
});
const SequenceQuestionSchema = new Schema({
    sequence: [{ type: String, required: true }], 
});

const SpellingCorrectionQuestionSchema = new Schema({
    incorrectSpelling: { type: String, required: true }, 
    correctedSpelling: { type: String, required: true }, 
});

const TrueFalseQuestion = Question.discriminator('true_false', TrueFalseQuestionSchema);
const MultipleChoiceQuestion = Question.discriminator('multiple_choice', MultipleChoiceQuestionSchema);
const MatchingQuestion = Question.discriminator('matching', MatchingQuestionSchema);
const SequenceQuestion = Question.discriminator('sequence', SequenceQuestionSchema);
const SpellingCorrectionQuestion = Question.discriminator('spelling_correction', SpellingCorrectionQuestionSchema);
const FillQuestion = Question.discriminator('fill', FillQuestionSchema);
const ClassicQuestion = Question.discriminator('classic', ClassicQuestionSchema);

module.exports = {
    Question,
    TrueFalseQuestion,
    MultipleChoiceQuestion,
    MatchingQuestion,
    SequenceQuestion,
    SpellingCorrectionQuestion,
    FillQuestion,
    ClassicQuestion
};
