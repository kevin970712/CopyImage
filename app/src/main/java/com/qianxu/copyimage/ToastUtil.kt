package com.qianxu.copyimage

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT

/** Toast 消息工具类 */
object ToastUtil {
  /**
   * 显示短时间的 Toast 消息
   *
   * @param context 上下文对象
   * @param message 字符串资源 ID
   */
  fun showToast(context: Context, message: Int) {
    Toast.makeText(context, message, LENGTH_SHORT).show()
  }
}
