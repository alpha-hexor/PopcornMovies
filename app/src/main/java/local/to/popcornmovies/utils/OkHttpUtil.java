package local.to.popcornmovies.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {

    public static final String
            TAG = "test->OkhttpUtil",USER_AGENT;

    static {
        USER_AGENT = HexDecoder.fromHex("204d6f7a696c6c612f352e30202857696e646f7773204e542031302e303b2057696e36343b207836343b2072763a3133342e3029204765636b6f2f32303130303130312046697265666f782f3133342e30");
    }

    public static volatile OkHttpUtil instance;
    public final OkHttpClient _httpClient;
    private final Request.Builder _requestBuilder;

    public static OkHttpUtil getInstance(Context context) {
        if (instance == null) {
            instance = new OkHttpUtil(context);
        }
        return instance;
    }

    private OkHttpUtil(Context context) {
        this._httpClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .cache(new Cache(context.getCacheDir(), Long.MAX_VALUE))
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .sslSocketFactory(createUnsafeSslSocketFactory(), createUnsafeTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .build();

        this._requestBuilder = new Request.Builder()
                .header("User-Agent", USER_AGENT);
    }

    private SSLSocketFactory createUnsafeSslSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private X509TrustManager createUnsafeTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    public Response getHttpRequest(String url) throws Exception {
        Request request = this._requestBuilder.url(url)
                .method("GET", null)
                .build();
        return this._httpClient.newCall(request).execute();
    }

    public Response getHttpRequest(Request.Builder requestBuilder) throws Exception {
        Request request = requestBuilder
                .header("User-Agent", USER_AGENT)
                .build();
        return this._httpClient.newCall(request).execute();
    }

    public Response postHttpRequest(String url, RequestBody requestBody) throws Exception {
        Request request = this._requestBuilder.url(url)
                .method("POST", requestBody)
                .header("User-Agent", USER_AGENT)
                .build();

        return this._httpClient.newCall(request).execute();
    }

    public Bitmap getBitmap(String url) {
        try {
            Request request = this._requestBuilder.url(url)
                    .method("GET", null)
                    .build();
            Response response = this._httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return BitmapFactory.decodeByteArray(response.body().bytes(), 0, (int) response.body().contentLength());
        } catch (Exception e) {
            Log.e(TAG, "Error getting image : " + url, e);
            return null;
        }
    }

    public OkHttpClient getClient(){return this._httpClient;}

}