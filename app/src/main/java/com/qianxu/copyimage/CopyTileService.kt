package com.qianxu.copyimage

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.qianxu.copyimage.ClipboardUtil.writeImageUriToClipboard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CopyTileService : TileService() {
  companion object {
    private const val TAG = "CopyTileService"
  }

  private val mainScope = CoroutineScope(Dispatchers.Main)
  private var isProcessing = false

  override fun onStartListening() {
    super.onStartListening()

    resetTileState()
  }

  override fun onClick() {
    super.onClick()

    // 如果正在处理中或复制成功，忽略点击
    if (isProcessing) {
      return
    }

    // 设置为处理中状态，防止重复点击
    isProcessing = true
    qsTile?.apply {
      state = Tile.STATE_UNAVAILABLE
      updateTile()
    }

    // 在协程中执行获取图片和写入剪贴板的操作
    mainScope.launch {
      Log.d(TAG, "查询图片")
      val imageUri = MediaStoreUtil.getLatestImageUri(contentResolver)
      if (imageUri != null) {
        Log.d(TAG, "最新图片: $imageUri")
        writeImageUriToClipboard(this@CopyTileService, imageUri)
        updateTileState(true)
      } else {
        updateTileState(false)
      }
    }
  }

  /**
   * 更新磁贴状态
   *
   * @param isSuccess 是否成功复制图片
   */
  private fun updateTileState(isSuccess: Boolean) {
    qsTile?.apply {
      if (isSuccess) {
        state = Tile.STATE_ACTIVE
        label = getString(R.string.tile_copied)
      } else {
        state = Tile.STATE_INACTIVE
        label = getString(R.string.tile_failed)
        isProcessing = false
      }
      updateTile()
    }
  }

  /** 重置磁贴状态 */
  private fun resetTileState() {
    qsTile?.apply {
      state = Tile.STATE_INACTIVE
      label = getString(R.string.tile_name)
      updateTile()
    }
    isProcessing = false
  }
}
