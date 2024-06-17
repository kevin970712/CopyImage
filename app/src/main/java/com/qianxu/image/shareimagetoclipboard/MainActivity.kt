package com.qianxu.image.shareimagetoclipboard

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 如果接收到的是图片分享，就复制到剪贴板
        if (intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            val imageUri: Uri? = intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            if (imageUri != null) {
                copyImageToClipboard(imageUri)
            } else {
                Toast.makeText(this, R.string.toast_image_error, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, R.string.toast_share_image, Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun copyImageToClipboard(imageUri: Uri) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newUri(contentResolver, "Image", imageUri))

        Toast.makeText(this, R.string.toast_copy_success, Toast.LENGTH_SHORT).show()
    }
}