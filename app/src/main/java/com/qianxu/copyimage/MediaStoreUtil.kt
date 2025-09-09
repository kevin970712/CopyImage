package com.qianxu.copyimage

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore

/** 媒体存储工具类 */
object MediaStoreUtil {
  private val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
  private val projection = arrayOf(MediaStore.Images.Media._ID)

  /**
   * 获取设备中最新图片的 URI
   *
   * @param contentResolver 内容解析器，用于查询媒体存储
   * @return 最新图片的 URI，如果查询失败或没有图片则返回 null
   */
  fun getLatestImageUri(contentResolver: ContentResolver): Uri? {
    return runCatching {
        val queryArgs =
          Bundle().apply {
            putStringArray(
              ContentResolver.QUERY_ARG_SORT_COLUMNS,
              arrayOf(MediaStore.Images.Media._ID),
            )
            putInt(
              ContentResolver.QUERY_ARG_SORT_DIRECTION,
              ContentResolver.QUERY_SORT_DIRECTION_DESCENDING,
            )
            putInt(ContentResolver.QUERY_ARG_LIMIT, 1)
          }

        contentResolver.query(uri, projection, queryArgs, null)?.use { cursor ->
          if (cursor.moveToFirst()) {
            val idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val id = cursor.getLong(idColumnIndex)
            return@runCatching Uri.withAppendedPath(uri, id.toString())
          }
        }
        null
      }
      .getOrNull()
  }
}
