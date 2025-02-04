package com.github.libretube.util

import android.content.Context
import android.os.Build
import com.github.libretube.constants.DownloadType
import com.github.libretube.extensions.createDir
import com.github.libretube.obj.DownloadedFile
import java.io.File

object DownloadHelper {
    private fun getOfflineStorageDir(context: Context): File {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return context.filesDir

        return try {
            context.getExternalFilesDir(null)!!
        } catch (e: Exception) {
            context.filesDir
        }
    }

    fun getVideoDir(context: Context): File {
        return File(
            getOfflineStorageDir(context),
            "video"
        ).createDir()
    }

    fun getAudioDir(context: Context): File {
        return File(
            getOfflineStorageDir(context),
            "audio"
        ).createDir()
    }

    fun getMetadataDir(context: Context): File {
        return File(
            getOfflineStorageDir(context),
            "metadata"
        ).createDir()
    }

    fun getThumbnailDir(context: Context): File {
        return File(
            getOfflineStorageDir(context),
            "thumbnail"
        ).createDir()
    }

    fun getDownloadedFiles(context: Context): MutableList<DownloadedFile> {
        val videoFiles = getVideoDir(context).listFiles()
        val audioFiles = getAudioDir(context).listFiles()?.toMutableList()

        val files = mutableListOf<DownloadedFile>()

        videoFiles?.forEach {
            var type = DownloadType.VIDEO
            audioFiles?.forEach { audioFile ->
                if (audioFile.name == it.name) {
                    type = DownloadType.AUDIO_VIDEO
                    audioFiles.remove(audioFile)
                }
            }
            files.add(
                DownloadedFile(
                    name = it.name,
                    size = it.length(),
                    type = type
                )
            )
        }

        audioFiles?.forEach {
            files.add(
                DownloadedFile(
                    name = it.name,
                    size = it.length(),
                    type = DownloadType.AUDIO
                )
            )
        }

        return files
    }
}
