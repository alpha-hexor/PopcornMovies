package local.to.popcornmovies.utils;

import android.net.Uri;

import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;

import java.util.ArrayList;
import java.util.List;

import local.to.popcornmovies.models.Subtitle;

public class SubtitleConfigurationUtils {

    public static final String TAG = "test->SbtlCnf";

    public static List<MediaItem.SubtitleConfiguration> getSubititleConfigurations(ArrayList<Subtitle> subtitles) {
        List<MediaItem.SubtitleConfiguration> result = new ArrayList<>();

        for (int i = 0; i < subtitles.size(); i++) {
            Subtitle subtitle = subtitles.get(i);
            String label = subtitle.label != null && !subtitle.label.isEmpty() ? subtitle.label : "Subtitle " + (i + 1);
            String mimeType = getMimeType(subtitle.file);
            Uri uri = Uri.parse(subtitle.file);
            result.add(
                    new MediaItem.SubtitleConfiguration.Builder(uri)
                            .setLabel(label)
                            .setId(String.valueOf(i))
                            .setRoleFlags(C.ROLE_FLAG_SUBTITLE)
                            .setLanguage(getLanguageCode(label))
                            .setMimeType(mimeType)
                            .build()
            );
        }
        return result;
    }

    private static String getLanguageCode(String fullLanguageName) {
        String lowerCase = fullLanguageName.toLowerCase();
        if (lowerCase.equals("english")) {
            return "en";
        } else if (lowerCase.equals("french")) {
            return "fr";
        } else if (lowerCase.equals("spanish")) {
            return "es";
        } else if (lowerCase.equals("german")) {
            return "de";
        } else if (lowerCase.equals("hindi")) {
            return "hi";
        } else if (lowerCase.equals("japanese")) {
            return "ja";
        } else if (lowerCase.equals("chinese")) {
            return "zh";
        }
        return "und"; // "und" stands for undefined
    }

    private static String getMimeType(String fileUrl) {
        if (fileUrl.endsWith(".srt")) {
            return MimeTypes.APPLICATION_SUBRIP; // SubRip
        } else if (fileUrl.endsWith(".vtt")) {
            return MimeTypes.TEXT_VTT; // WebVTT
        } else if (fileUrl.endsWith(".ttml")) {
            return MimeTypes.APPLICATION_TTML; // TTML
        } else if (fileUrl.endsWith(".ssa") || fileUrl.endsWith(".ass")) {
            return MimeTypes.TEXT_SSA; // SubStation Alpha
        } else {
            return MimeTypes.APPLICATION_SUBRIP; // Fallback for unknown formats
        }
    }

}
