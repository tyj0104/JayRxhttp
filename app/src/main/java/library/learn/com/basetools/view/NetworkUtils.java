package library.learn.com.basetools.view;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by jay on 2017/11/29.
 */

public class NetworkUtils {
    public NetworkUtils() {
    }

    public static String getNetworkType(Context ctx) {
        String strNetworkType = "";
        NetworkInfo networkInfo = ((ConnectivityManager) ctx.getSystemService("connectivity")).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == 1) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == 0) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        strNetworkType = "2G";
                        break;
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                        strNetworkType = "3G";
                        break;
                    case 13:
                        strNetworkType = "4G";
                        break;
                    default:
                        if (!_strSubTypeName.equalsIgnoreCase("TD-SCDMA") && !_strSubTypeName.equalsIgnoreCase("WCDMA") && !_strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = _strSubTypeName;
                        } else {
                            strNetworkType = "3G";
                        }
                }
            }
        }

        return strNetworkType;
    }
}

