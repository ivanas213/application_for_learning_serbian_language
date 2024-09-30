const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const TextBlockSchema = new mongoose.Schema({
  content: { type: String, required: true },
  style: {
      fontSize: Number,
      color: String,
      bold: Boolean,
      italic: Boolean,
      alignment: { type: String, enum: ['left', 'center', 'right'], default: 'left' }
  },
  annotations: [{ type: String }], 
  position: Number,
});

const ImageBlockSchema = new mongoose.Schema({
    url: { type: String, required: true }, 
    description: String,
    position: Number, 
    alignment: { type: String, enum: ['left', 'center', 'right'], default: 'center' }, 
});
const AudioBlockSchema = new mongoose.Schema({
  url: { type: String, required: true },
  description: String, 
  position: Number, 
});
const VideoBlockSchema = new mongoose.Schema({
  url: { type: String, required: true }, 
  description: String, 
  position: Number, 
});
const TableBlockSchema = new mongoose.Schema({
  headers: [{ type: String, required: true }], 
  rows: [[{ type: String, required: true }]], 
  position: Number 
});

const GameSchema = new mongoose.Schema({
  game: { type: Schema.Types.ObjectId, ref: 'Game' },
});


const BaseLessonSchema = new mongoose.Schema({
  title: { type: String, required: true },
  description: String,
  topic: { type: Schema.Types.ObjectId, ref: 'Topic' },
  createdAt: { type: Date, default: Date.now },
}, { discriminatorKey: 'type' });

const LessonSchema = new mongoose.Schema({
  contentBlocks: [{
    type: { type: String, enum: ['text', 'image', 'audio', 'video', 'question','table'], required: true },
    textBlock: TextBlockSchema,
    imageBlock: ImageBlockSchema,
    audioBlock: AudioBlockSchema,
    videoBlock: VideoBlockSchema,
    questionBlock: { type: Schema.Types.ObjectId, ref: 'Question' },
    tableBlock: TableBlockSchema 
  }]
});
const TestSchema = new mongoose.Schema({
  questions: [{ type: Schema.Types.ObjectId, ref: 'Question', required: true }],
  passingScore: { type: Number, required: true },
  timeLimit: Number
});

const Lesson = mongoose.model('Lesson', BaseLessonSchema);
const LessonType = Lesson.discriminator('лекција', LessonSchema);
const GameType = Lesson.discriminator('игрица', GameSchema);
const TestType = Lesson.discriminator('тест', TestSchema);





const BaseGameSchema = new mongoose.Schema({
  gameType: { type: String, required: true }, 
}, { discriminatorKey: 'type'}); 

const HangmanSchema = new mongoose.Schema({
  words: [{ type: String, required: true }]
});

const MemorySchema = new mongoose.Schema({
  cards: [{
    image: { type: String, required: true }, 
    isMatched: { type: Boolean, default: false }
  }],
  gridSize: { type: Number, required: true }, 
  attempts: { type: Number, default: 0 }, 
  matchesFound: { type: Number, default: 0 } 
});

const Game = mongoose.model('Game', BaseGameSchema);

const HangmanGame = Game.discriminator('вешалице', HangmanSchema);
const MemoryGame = Game.discriminator('меморија', MemorySchema);

module.exports = { Game, HangmanGame, MemoryGame };


module.exports = { Lesson, LessonType, GameType, TestType, HangmanGame, MemoryGame };
