import android.content.Context
import java.io.IOException
import java.nio.charset.Charset

class JsonReader(private val context: Context) {

    fun loadJSONFromAsset(fileName: String): String? {
        var jsonString: String? = null
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            jsonString = String(buffer, Charset.defaultCharset())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return jsonString
    }
}

