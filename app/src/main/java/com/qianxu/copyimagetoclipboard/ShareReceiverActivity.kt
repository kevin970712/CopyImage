package com.qianxu.copyimagetoclipboard

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

class ShareReceiverActivity : Activity() {
  /**
   * Activity 创建时调用的生命周期方法 处理接收的分享意图，提取图片 URI 并复制到剪贴板
   *
   * @param savedInstanceState 保存的实例状态，如果 Activity 被系统销毁并重新创建，则包含之前的状态信息
   */
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (!isImageShareIntent(intent)) {
      showToast(R.string.toast_need_image)
      finish()
      return
    }

    val imageUri: Uri? =
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
      } else {
        @Suppress("DEPRECATION") intent.getParcelableExtra(Intent.EXTRA_STREAM)
      }

    imageUri?.let { copyImageToClipboard(it) } ?: showToast(R.string.toast_image_error)

    finish()
  }

  /**
   * 将图片 URI 复制到系统剪贴板
   *
   * @param imageUri 要复制到剪贴板的图片 URI
   */
  private fun copyImageToClipboard(imageUri: Uri) {
    try {
      val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
      clipboardManager.setPrimaryClip(ClipData.newUri(contentResolver, "Image", imageUri))
      showToast(R.string.toast_copy_success)
    } catch (_: Exception) {
      showToast(R.string.toast_copy_failure)
    }
  }

  /**
   * 显示指定文本资源的 Toast 消息
   *
   * @param message 要显示的文本资源 ID
   */
  private fun showToast(message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }

  /**
   * 判断 Intent 是否为图片分享类型
   *
   * @param intent 要检查的 Intent 对象
   * @return 如果是图片分享 Intent 则返回 true，否则返回 false
   */
  private fun isImageShareIntent(intent: Intent?): Boolean {
    return intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true
  }
}
