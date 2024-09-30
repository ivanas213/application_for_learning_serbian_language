import androidx.room.Entity
import androidx.room.PrimaryKey
data class Game(
    val gameType:String,
    val words:List<String>
)
@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey val _id:String,
    val type:String,
    val title: String,
    val description: String?,
    val topic:String,
    val contentBlocks: List<ContentBlock>,
    val createdAt: String,
    val game:Game
)


data class ContentBlock(
    val type: String,
    val textBlock: TextBlock?,
    val imageBlock: ImageBlock?,
    val questionBlock: QuestionBlock?,
    val tableBlock: TableBlock?,
    val videoBlock:VideoBlock?
)
data class VideoBlock(
    val url:String,
    val description:String,
    val position: Int
)
data class TextBlock(
    val content: String,
    val style: Style,
    val position: Int
)

data class Style(
    val fontSize: Int=24,
    val color: String="#FFFFFF",
    val bold: Boolean=false,
    val italic: Boolean=false,
    val alignment: String="left"

)

data class ImageBlock(
    val url: String,
    val description: String?,
    val position: Int,
    val alignment: String
)

data class QuestionBlock(
    val _id:String,
    val questionType: String,
    val type: String,
    val text:String,
    val position: Int,
    val question: String,
    val options: List<String>,
    val correctAnswers: List<String>,
    val correctAnswer: Boolean,
    val correctAnswerMC: String,
    val explanation:String,
    val tags:List<String>,
    var _try:Int=1,
    val incorrectSpelling:String,
    val correctedSpelling:String,
    val left:List<String>,
    val right:List<String>,
    val topic:String

)

data class TableBlock(
    val headers:List<String>,
    val rows:List<List<String>>,
    val position:Int
)


