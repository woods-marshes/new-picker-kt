package io.github.woods_marshes.ui.picker.nav.data.preset

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.woods_marshes.base.collection.model.CollectionBase
import io.github.woods_marshes.base.common.data.Nameable
import io.github.woods_marshes.base.contentresolver.AllFoldersCollection
import io.github.woods_marshes.base.contentresolver.DownloadCollection
import io.github.woods_marshes.base.contentresolver.MimeType
import io.github.woods_marshes.ui.R
import io.github.woods_marshes.ui.common.data.extension.navHostRoute

enum class PresetNavLocation(
    @StringRes override val nameStringRes: Int,
    val icon: ImageVector,
    val group: PresetNavLocationGroup,
    val mimeTypeGroup: MimeType.Group?
) : Nameable {
    AllFolders(
        nameStringRes = R.string.ui_nav_destination_all_folders,
        icon = Icons.Outlined.Folder,
        group = PresetNavLocationGroup.Common,
        mimeTypeGroup = null
    ),
    Download(
        nameStringRes = R.string.ui_nav_destination_download,
        icon = Icons.Outlined.Download,
        group = PresetNavLocationGroup.Common,
        mimeTypeGroup = null
    ),
    AudioLibrary(
        nameStringRes = R.string.ui_nav_destination_audio,
        icon = Icons.Outlined.Headphones,
        group = PresetNavLocationGroup.Libraries,
        mimeTypeGroup = MimeType.Group.Audio
    ),
    ImageLibrary(
        nameStringRes = R.string.ui_nav_destination_images,
        icon = Icons.Outlined.Image,
        group = PresetNavLocationGroup.Libraries,
        mimeTypeGroup = MimeType.Group.Image
    ),
    // DocumentLibrary(
    //     nameRes = R.string.nav_destination_documents,
    //     icon = Icons.Outlined.InsertDriveFile,
    //     group = NavLocationGroup.Libraries,
    //     mimeTypeGroup = MimeType.Group.Text
    // ),
    VideoLibrary(
        nameStringRes = R.string.ui_nav_destination_videos,
        icon = Icons.Outlined.Videocam,
        group = PresetNavLocationGroup.Libraries,
        mimeTypeGroup = MimeType.Group.Video
    );

    val navHostRoute: String
        get() = correspondingCollection?.navHostRoute ?: name

    val uri: Uri
        get() = when(this) {
            AllFolders -> MediaStore.Files.getContentUri("external")
            Download -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Files.getContentUri("external")
            }
            AudioLibrary -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            ImageLibrary -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            // DocumentLibrary -> MediaStore.Files.getContentUri("external")
            VideoLibrary -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

    val correspondingCollection: CollectionBase?
        get() = when(this) {
            AllFolders -> AllFoldersCollection
            Download -> DownloadCollection
            else -> null
        }

    val hasCorrespondingCollection: Boolean
        get() = correspondingCollection != null

}