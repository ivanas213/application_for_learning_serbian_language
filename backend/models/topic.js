const mongoose = require('mongoose');
const Schema = mongoose.Schema;



const TopicSchema = Schema({

    name:String,
    position:Number,
    grade :{type:Number, enum:[1,2,3,4]},
    type: { type: String, enum: ['граматика', 'правопис', 'књижевност']}
  
});


const Topic = mongoose.model('Topic', TopicSchema);
module.exports=Topic