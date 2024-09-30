import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.imageView)

    fun bind(imageBlock: ImageBlock) {
        val base64Image = imageBlock.url

        val decodedString: ByteArray = Base64.decode(base64Image, Base64.DEFAULT)

        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        if (decodedByte != null) {
            imageView.setImageBitmap(decodedByte)
        } else {
        }
        imageView.setImageBitmap(decodedByte)

        imageView.contentDescription = imageBlock.description

        val params = imageView.layoutParams as LinearLayout.LayoutParams
        when (imageBlock.alignment) {
            "center" -> params.gravity = Gravity.CENTER
            "left" -> params.gravity = Gravity.START
            "right" -> params.gravity = Gravity.END
            else -> params.gravity = Gravity.NO_GRAVITY
        }
        imageView.layoutParams = params
    }
}
