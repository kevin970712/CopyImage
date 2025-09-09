package com.qianxu.copyimage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.net.Uri
import android.util.Log

/** 剪贴板工具类 */
object ClipboardUtil {
  private const val TAG = "ClipboardUtil"

  /**
   * 写入图片 URI 到剪贴板
   *
   * @param context 上下文对象
   * @param uri 图片 URI
   * @return 操作结果
   */
  fun writeImageUriToClipboard(context: Context, uri: Uri): Boolean {
    return try {
      val clipboardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
      val data = ClipData.newUri(context.contentResolver, "Image", uri)
      clipboardManager.setPrimaryClip(data)
      Log.d(TAG, "写入图片 URI 到剪贴板成功: $uri")
      true
    } catch (e: Exception) {
      Log.e(TAG, "写入图片 URI 到剪贴板失败", e)
      false
    }
  }
}
