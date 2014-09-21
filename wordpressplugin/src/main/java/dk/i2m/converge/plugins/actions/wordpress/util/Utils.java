/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.i2m.converge.plugins.actions.wordpress.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author fred
 */
public class Utils {

    private static Pattern doublePattern = Pattern.compile("-?\\d+(\\.\\d*)?");

    public static boolean isInteger(String string) {
        try {
            Integer.valueOf(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String string) {
        return doublePattern.matcher(string).matches();
    }

    public static void sslHanshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs,
                    String authType) {
// Trust always
            }

            public void checkServerTrusted(X509Certificate[] certs,
                    String authType) {
// Trust always
            }
        }};

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            // Create empty HostnameVerifier
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            };

            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyManagementException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String flattenArray(String[] array){
           StringBuilder sb = new StringBuilder();
           for(String item:array){
               if(array.length > 0){
               sb.append(",");
               }
               sb.append("'").append(item).append("'");
           }
           return sb.toString();
    }
}
