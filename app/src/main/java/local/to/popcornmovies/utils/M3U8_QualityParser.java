package local.to.popcornmovies.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import local.to.popcornmovies.models.QualityParsedModel;
import okhttp3.Response;

public class M3U8_QualityParser {

    public static final String TAG = "test->UrlPrcrUtl", REGEX_1, REGEX_2;

    static {
        // RESOLUTION=\\d+x(\\d+).*\n+(.+)
        REGEX_1 = HexDecoder.fromHex("5245534f4c5554494f4e3d5c642b78285c642b292e2a0a2b282e2b29");
        // http[s]?://.*
        REGEX_2 = HexDecoder.fromHex("687474705b735d3f3a2f2f2e2a");
    }

    public static ArrayList<QualityParsedModel> processMainUrl(String mainUrl, OkHttpUtil httpUtil) {
        try {
            Log.d(TAG,"Parsing url : "+mainUrl);

            String parentUrl = mainUrl.substring(0, mainUrl.lastIndexOf('/'));
            ArrayList<QualityParsedModel> result = new ArrayList<>();

            result.add(new QualityParsedModel("Auto",mainUrl));

            Response response = httpUtil.getHttpRequest(mainUrl);
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();

                Pattern pattern = Pattern.compile(REGEX_1);
                Matcher matcher = pattern.matcher(responseBody);

                while (matcher.find()) {
                    String resolution = matcher.group(1);
                    String link = matcher.group(2);

                    if (!link.matches(REGEX_2)) {
                        Log.d(TAG, "Half URL detected");
                        link = link.startsWith("/") ? parentUrl + link : parentUrl + "/" + link;
                    }
                    result.add(new QualityParsedModel(resolution,link));
                }
                return result;
            } else {
                Log.e(TAG, "Failed to fetch the URL. Status code: " + response.code());
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Http Exception", e);
            return null;
        }
    }
}

