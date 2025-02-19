package com.qianxu.image.shareimagetoclipboard

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

class MainActivity : Activity() {
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

  private fun copyImageToClipboard(imageUri: Uri) {
    val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    clipboardManager.setPrimaryClip(ClipData.newUri(contentResolver, "Image", imageUri))

    showToast(R.string.toast_copy_success)
  }

  private fun showToast(message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }

  private fun isImageShareIntent(intent: Intent?): Boolean {
    return intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true
  }
}
