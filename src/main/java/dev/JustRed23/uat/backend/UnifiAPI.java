package dev.JustRed23.uat.backend;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;

public class UnifiAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnifiAPI.class);

    private static final HashMap<String, String> cookies = new HashMap<>();

    protected String host, user, password;
    protected int port;

    protected UnifiAPI() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager manager = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {}

            public void checkServerTrusted(X509Certificate[] chain, String authType) {}

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }; //Create trust manager that accepts all certificates
        HostnameVerifier verifier = (hostname, session) -> true; //Create hostname verifier that accepts all hostnames

        SSLContext context = SSLContext.getInstance("SSL"); //Create context
        context.init(null, new TrustManager[]{manager}, new SecureRandom()); //Init context with trust manager

        HttpsURLConnection.setDefaultHostnameVerifier(verifier);
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    public void connect() throws Exception {
        LOGGER.info("Logging in to controller");
        String address = "https://" + host + ":" + port + ControllerAddresses.API_LOGIN.getRoute();

        LOGGER.debug("Connecting to: " + address);
        URL url = new URL(address);

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        JSONObject json = new JSONObject();
        json.put("username", user);
        json.put("password", password);
        String query = json.toString();

        connection.setRequestMethod(ControllerAddresses.API_LOGIN.getMethod());
        connection.setRequestProperty("Content-length", String.valueOf(query.length()));
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
        connection.setDoOutput(true);
        connection.setDoInput(true);

        DataOutputStream output = new DataOutputStream(connection.getOutputStream());
        output.writeBytes(query);
        output.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            LOGGER.debug(line);
        }

        createCookies(connection);
        connection.disconnect();
    }

    public void destroy() throws Exception {
        LOGGER.info("Logging out of controller");
        query(ControllerAddresses.API_LOGOUT);
        cookies.clear();
    }

    public JSONArray query(ControllerAddresses.Route route) throws Exception {
        return query(route, null);
    }

    public JSONArray query(ControllerAddresses.Route route, JSONObject query) throws Exception {
        String address = "https://" + host + ":" + port + route.getRoute();

        LOGGER.debug("Connecting to: " + address);
        URL url = new URL(address);

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod(route.getMethod());

        if (query != null)
            connection.setRequestProperty("Content-length", String.valueOf(query.toString().length()));

        StringBuilder cookieStr = new StringBuilder();
        for (String cookie : cookies.keySet()) {
            cookieStr.append(cookie).append("=").append(cookies.get(cookie)).append("; ");
        }

        cookieStr = new StringBuilder(cookieStr.substring(0, cookieStr.length() - 2));

        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Cookie", cookieStr.toString());
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.connect();

        if (query != null) {
            DataOutputStream output = new DataOutputStream(connection.getOutputStream());
            output.writeBytes(query.toString());
            output.close();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        connection.disconnect();

        LOGGER.debug("RESP: " + response);

        JSONObject data = new JSONObject(response.toString());

        return data.getJSONArray("data");
    }

    /**
     * Save all cookies received by the server.
     */
    private void createCookies(HttpsURLConnection connection) {
        String headerName;
        for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
            if (headerName.equals("Set-Cookie")) {
                String cookie = connection.getHeaderField(i);
                LOGGER.debug("cookie: " + cookie);

                cookie = cookie.substring(0, cookie.indexOf(";"));
                String cookieName = cookie.substring(0, cookie.indexOf("="));
                String cookieValue = cookie.substring(cookie.indexOf("=") + 1);

                cookies.put(cookieName, cookieValue);
            }
        }
    }

    public static class Builder {

        private final UnifiAPI api;

        public Builder() throws NoSuchAlgorithmException, KeyManagementException {
            api = new UnifiAPI();
        }

        public Builder withHost(String host) {
            api.host = host;
            return this;
        }

        public Builder withPort(int port) {
            api.port = port;
            return this;
        }

        public Builder withUser(String user) {
            api.user = user;
            return this;
        }

        public Builder withPassword(String password) {
            api.password = password;
            return this;
        }

        public UnifiAPI build() {
            return api;
        }
    }
}
