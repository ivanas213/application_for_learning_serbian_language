import android.graphics.Typeface
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tableLayout: TableLayout = itemView.findViewById(R.id.tableLayout)

    fun bind(tableBlock: TableBlock) {
        tableLayout.removeAllViews()

        val headerRow = TableRow(itemView.context)
        for (header in tableBlock.headers) {
            val textView = TextView(itemView.context)
            textView.text = header
            textView.setTypeface(null, Typeface.BOLD)
            textView.setBackgroundResource(R.drawable.table_cell_border)
            textView.setPadding(16, 16, 16, 16)
            textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            textView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.teal_700))  // Boja pozadine
            headerRow.addView(textView)
        }
        tableLayout.addView(headerRow)

        for (row in tableBlock.rows) {
            val tableRow = TableRow(itemView.context)
            for (cell in row) {
                val textView = TextView(itemView.context)
                textView.text = cell
                textView.setPadding(16, 16, 16, 16)
                textView.setBackgroundResource(R.drawable.table_cell_border)
                textView.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                tableRow.addView(textView)
            }
            tableLayout.addView(tableRow)
        }
    }
}
