package com.qianxu.copyimage

import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 媒体存储工具类 */
object MediaStoreUtils {

  /** 外部存储中图片的 URI */
  private val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

  /** 查询时只获取图片 ID */
  private val projection = arrayOf(MediaStore.Images.Media._ID)

  /**
   * 获取设备中最新保存的图片 URI
   *
   * @param contentResolver 内容解析器，用于查询媒体库
   * @return 最新图片的 URI，如果查询失败则返回 null
   */
  suspend fun getLatestImageUri(contentResolver: ContentResolver): Uri? =
    withContext(Dispatchers.IO) {
      runCatching {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
                putString(ContentResolver.QUERY_ARG_SQL_LIMIT, "1")
              }
            contentResolver.query(uri, projection, queryArgs, null)?.use { cursor ->
              cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID).let { idx ->
                if (cursor.moveToFirst()) {
                  val id = cursor.getLong(idx)
                  return@withContext Uri.withAppendedPath(uri, id.toString())
                }
              }
            }
          } else {
            val sortOrder = "${MediaStore.Images.Media._ID} DESC LIMIT 1"
            contentResolver.query(uri, projection, null, null, sortOrder)?.use { cursor ->
              cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID).let { idx ->
                if (cursor.moveToFirst()) {
                  val id = cursor.getLong(idx)
                  return@withContext Uri.withAppendedPath(uri, id.toString())
                }
              }
            }
          }
          null
        }
        .getOrNull()
    }
}
