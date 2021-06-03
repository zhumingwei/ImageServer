package com.zhumingwei.imageserver

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.util.Log


object ImageProvider {
    private val TAG: String = "ImageProvider";

    fun getAllShownImagesPath(activity: Activity): ArrayList<String?>? {
        Log.d(TAG, "getAllShownImagesPath: ")
        val uri: Uri
        val cursor: Cursor?
        val column_index_data: Int
        val column_index_folder_name: Int
        val listOfAllImages = ArrayList<String?>()
        var absolutePathOfImage: String? = null
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        cursor = activity.contentResolver.query(
            uri, projection, null,
            null, null
        )
        column_index_data = cursor!!.getColumnIndexOrThrow(MediaColumns.DATA)
        column_index_folder_name = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            listOfAllImages.add(absolutePathOfImage)
            Log.i(TAG, "getAllShownImagesPath: item ${absolutePathOfImage}")
        }
        return listOfAllImages
    }
}