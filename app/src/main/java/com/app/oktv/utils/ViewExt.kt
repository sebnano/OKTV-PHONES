package  com.app.oktv.utils

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.regex.Pattern


val isPostcode = Pattern.compile("^(?i)(?=.{7,50}\$)(([0-9a-zA-ZÀ-ÿ]+[0-9a-zA-ZÀ-ÿ_]*[-\\\\._\\\\+&])*[0-9a-zA-ZÀ-ÿ]+)@([-0-9a-zA-ZÀ-ÿ]+[.])+([a-zA-ZÀ-ÿ]{2}|(aero|arpa|biz|com|coop|edu|gov|info|int|mil|mobi|museum|name|net|org|pro|travel|us))\$")

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.inivisible() {
    visibility = View.INVISIBLE
}

fun setReview(review: Double): String {
    return String.format("%.01f", review)
}

fun setPrice(price: String): String {
    return try {
        String.format("%.02f", price.toDouble())
    } catch (e:java.lang.NumberFormatException) {
        Log.e("Exception", "----->$e")
        price
    }
}

/*@OptIn(DelicateCoroutinesApi::class)
fun convertCurrency(
    amount: String,
    callback: CurrencyConversionCallback
) {
    val url: String = "https://api.freecurrencyapi.com/v1/latest" + "?apikey=" + "fca_live_evEo7iGcecrowVQQepa7bxiP6IfOSv33n5FbeYH1" + "&currencies=" + "EUR" + "&base_currency=" + "USD"

    val request = Request.Builder()
        .url(url)
        .build()

    GlobalScope.launch(Dispatchers.IO) {
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }

                val responseBody = response.body?.string()
                val gson = Gson()
                val apiResponse = gson.fromJson(responseBody, CurrencyModel::class.java)

                // Access the data
                val eurValue = apiResponse.data.EUR
                val convertedCurrency = convertCurrency(amount.toDouble(), eurValue)

                // Notify success through callback
                callback.onConversionResult(convertedCurrency)
            }
        } catch (e: IOException) {
            callback.onError("Error occurred: ${e.message}")
        }
    }
}*/

  fun convertCurrency(amount: Double, exchangeRate: Double): String {
    return try {
        val convertedAmount = BigDecimal.valueOf(amount * exchangeRate)
            .setScale(2, RoundingMode.HALF_UP)
        convertedAmount.toString()
    } catch (e : java.lang.NumberFormatException) {
        amount.toString()
    }
}

fun addSpacesToPhoneNumber(phoneNumber: String): String {
    val formattedNumber = StringBuilder(phoneNumber)

    // Determine the positions to insert spaces based on the length of the number
    val spacePositions = listOf(4, 8)

    // Insert spaces at the determined positions
    spacePositions.forEach { position ->
        if (position < formattedNumber.length) {
            formattedNumber.insert(position, " ")
        }
    }

    return formattedNumber.toString()
}


fun View.closeKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginInDp: Int) :
    RecyclerView.ItemDecoration() {
    private val horizontalMarginInPx: Int =
        context.resources.getDimension(horizontalMarginInDp).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.right = horizontalMarginInPx
        outRect.left = horizontalMarginInPx
    }
}


//@SuppressLint("LogNotTimber")
//fun showLog(type: Constant.LogType, tag: String, message: Any) {
//    when (type) {
//        Constant.LogType.verbose -> {
//            Log.v(tag, message.toString())
//        }
//        Constant.LogType.debug -> {
//            Log.d(tag, message.toString())
//        }
//        Constant.LogType.info -> {
//            Log.i(tag, message.toString())
//        }
//        Constant.LogType.error -> {
//            Log.e(tag, message.toString())
//        }
//        Constant.LogType.warn -> {
//            Log.w(tag, message.toString())
//        }
//    }
//}
//
//fun showVerboseLog(tag: String, message: Any) {
//    showLog(Constant.LogType.verbose, tag, message)
//}
//fun showDebugLog(tag: String, message: Any) {
//    showLog(Constant.LogType.debug, tag, message)
//}
//fun showInfoLog(tag: String, message: Any) {
//    showLog(Constant.LogType.info, tag, message)
//}
//fun showErrorLog(tag: String, message: Any) {
//    showLog(Constant.LogType.error, tag, message)
//}
//fun showWarnLog(tag: String, message: Any) {
//    showLog(Constant.LogType.warn, tag, message)
//}



fun openLocationInMap(lat: String, lng: String, location: String, context: Context) {
    val uriBegin = "geo:$lat,$lng"
    val query = "$lat,$lng($location)"
    val encodedQuery = Uri.encode(query)
    val uriString = "$uriBegin?q=$encodedQuery&z=16"
    val uri = Uri.parse(uriString)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}

fun String.isValidEmail() = !isNullOrEmpty() && isPostcode.matcher(this).matches()
//fun String.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidMobile() = !isNullOrEmpty() && Patterns.PHONE.matcher(this).matches()

fun String.isValidPassword() = this.length > 5

//fun String?.mediaFullUrl() = if (this == null) null else MyRetrofit.mediaBaseUrl.plus(this)

fun getMapImageURL(latitude: String, longitude: String, size: String): String {
    return "http://maps.google.com/maps/api/staticmap?center=$latitude,$longitude&zoom=15&size=$size&sensor=false&key=AIzaSyDC528LWI3jsJSPQLJErgtL5urzOIQHBhg"
}

//fun String?.decodeEmoji(): String = if (this == null) "" else StringEscapeUtils.unescapeJava(this)