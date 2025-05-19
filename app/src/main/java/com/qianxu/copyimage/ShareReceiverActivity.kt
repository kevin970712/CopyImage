package com.qianxu.copyimage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.content.IntentCompat
import com.qianxu.copyimage.ClipboardUtils.writeImageUriToClipboard
import com.qianxu.copyimage.ToastUtil.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShareReceiverActivity : Activity() {

  companion object {
    private const val TAG = "ShareReceiverActivity"
  }

  private val mainScope = CoroutineScope(Dispatchers.Main)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (!isImageShareIntent(intent)) {
      showToast(this, R.string.toast_need_image)
      finish()
      return
    }

    val receivedUri = IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)

    Log.d(TAG, "收到分享: $receivedUri")

    receivedUri?.let {
      mainScope.launch {
        val cacheFileUri =
          withContext(Dispatchers.IO) {
            try {
              contentResolver.openInputStream(receivedUri)?.use { it ->
                val bitmap = BitmapFactory.decodeStream(it)
                bitmap?.let { writeImageToCache(it) }
              }
            } catch (e: Exception) {
              Log.e(TAG, e.message, e)
              null
            }
          }

        Log.d(TAG, "缓存文件: $cacheFileUri")

        cacheFileUri?.let {
          writeImageUriToClipboard(this@ShareReceiverActivity, cacheFileUri)
          showToast(this@ShareReceiverActivity, R.string.toast_image_copied_success)
        } ?: showToast(this@ShareReceiverActivity, R.string.toast_image_read_failed)
      }
    } ?: showToast(this, R.string.toast_image_read_failed)

    finish()
  }

  /**
   * 检查 Intent 是否为图片分享类型
   *
   * @param intent 要检查的 Intent 对象
   * @return 检查结果
   */
  private fun isImageShareIntent(intent: Intent?): Boolean {
    return intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true
  }

  /**
   * 写入 Bitmap 到缓存目录
   *
   * @param bitmap 要保存的 Bitmap 对象
   * @return 缓存文件的 Uri，如果保存失败则返回 null
   */
  private fun writeImageToCache(bitmap: Bitmap): Uri? {
    return try {
      val receivedImageFile = java.io.File(cacheDir, "received_image.png")
      java.io.FileOutputStream(receivedImageFile).use { it ->
        bitmap.compress(CompressFormat.PNG, 100, it)
      }
      bitmap.recycle()
      getUriForFile(this, getString(R.string.file_provider_authority), receivedImageFile)
    } catch (e: Exception) {
      Log.e(TAG, "写入 Bitmap 到缓存目录失败", e)
      null
    }
  }
}
