package local.to.popcornmovies.utils;

public class HexDecoder {
    public static final String TAG = "test->HxDcd";
    public static String fromHex(String hex) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String hexPair = hex.substring(i, i + 2);
            result.append((char) Integer.parseInt(hexPair, 16));
        }
        return result.toString();
    }
}
