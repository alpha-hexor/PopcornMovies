package local.to.popcornmovies.rooom_database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "url_cache")
public class UrlCacheEntity {

    @PrimaryKey
    @NonNull
    public final String url;

    @ColumnInfo(name = "data",typeAffinity = ColumnInfo.BLOB)
    public final byte[] data;

    public UrlCacheEntity(@NonNull String url, byte[] data) {
        this.url = url;
        this.data = data;
    }
}
