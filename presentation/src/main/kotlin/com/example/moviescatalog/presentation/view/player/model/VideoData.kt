package com.example.moviescatalog.presentation.view.player.model

import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.DrmConfiguration
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import java.util.UUID

@OptIn(UnstableApi::class)
data class VideoData(
    val id: String,
    val drmScheme: DrmScheme = DrmScheme.Widevine,
    val url: String? = null,
    val licenseUrl: String? = null,
) {

    val mediaItem: MediaItem?
        get() {
            if (url.isNullOrBlank())
                return null

            return MediaItem.Builder()
                .setUri(url)
                .setMimeType(inferStreamType().toMimeType())
                .setMediaMetadata(MediaMetadata.Builder().build())
                .setMediaId(id)
                .setDrmConfiguration(
                    DrmConfiguration.Builder(drmScheme.toUUID())
                        .setLicenseUri(licenseUrl)
                        .setMultiSession(true)
                        .build()
                )
                .build()
        }

    private fun inferStreamType(): StreamType {
        val uri = url?.toUri() ?: return StreamType.AutoDetect
        return if (
            uri.getBooleanQueryParameter(
                "format",
                false
            ) && uri.getQueryParameter("format") == "9"
        )
            StreamType.Dash
        else if (
            uri.getBooleanQueryParameter(
                "format",
                false
            ) && (uri.getQueryParameter("format") == "2" || uri.getQueryParameter("format") == "11")
        )
            StreamType.Hls
        else if (uri.lastPathSegment?.contains("m3u8") == true)
            StreamType.Hls
        else if (uri.lastPathSegment?.contains(".mpd") == true)
            StreamType.Dash
        else
            StreamType.AutoDetect
    }

    internal enum class StreamType {
        AutoDetect,
        Dash,
        Hls,
        SS;

        internal fun toMimeType(): String? {
            return when (this) {
                Dash -> MimeTypes.APPLICATION_MPD
                Hls -> MimeTypes.APPLICATION_M3U8
                SS -> MimeTypes.APPLICATION_SS
                else -> null
            }
        }
    }

    enum class DrmScheme {
        Nil,
        Widevine,
        PlayReady,
        ClearKey,
        CommonPSSH;

        companion object {
            fun fromUUID(uuid: UUID): DrmScheme {
                return when (uuid) {
                    C.WIDEVINE_UUID -> Widevine
                    C.PLAYREADY_UUID -> PlayReady
                    C.CLEARKEY_UUID -> ClearKey
                    C.COMMON_PSSH_UUID -> CommonPSSH
                    else -> Nil
                }
            }
        }

        fun toUUID(): UUID {
            return when (this) {
                Widevine -> C.WIDEVINE_UUID
                PlayReady -> C.PLAYREADY_UUID
                ClearKey -> C.CLEARKEY_UUID
                CommonPSSH -> C.COMMON_PSSH_UUID
                else -> C.UUID_NIL
            }
        }
    }
}