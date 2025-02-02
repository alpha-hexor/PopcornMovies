package local.to.popcornmovies.models;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;

import java.util.List;

public class QualityParsedModel {
    public final String quality, videoSource;
    public static List<MediaItem.SubtitleConfiguration> subtitleConfigurations;
    public static String rootUrl;

    public QualityParsedModel(String quality, String videoSource) {
        this.quality = quality;
        this.videoSource = videoSource;
    }

    @NonNull
    @Override
    public String toString() {
        return "QualityParsedModel{" +
                "quality='" + quality + '\'' +
                ", videoSource='" + videoSource + '\'' +
                '}';
    }
}
