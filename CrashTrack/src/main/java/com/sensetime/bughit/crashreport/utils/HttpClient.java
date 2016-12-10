package com.sensetime.bughit.crashreport.utils;

import android.text.TextUtils;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by inx on 2016/8/30.
 */
public class HttpClient {

    private static HttpClient INSTANCE;
    private static final int TIME_OUT = 3000;

    private static final String DEFAULT_END_POINT = "http://192.168.2.114:8000";
    private final String endPoint;
    private static final String PATH_SIMPLE_JSON = "/simple";
    public static final String PATH_FULL_FILE = "/full";

    private HttpClient(String endPoint) {
        this.endPoint = endPoint != null ?
                endPoint :
                DEFAULT_END_POINT;
    }

    public static void init(String urlEndPoint) {
        INSTANCE = new HttpClient(urlEndPoint);
    }

    public static HttpClient getInstance() {
        if (INSTANCE==null) INSTANCE = new HttpClient(null);
        return INSTANCE;
    }

    public enum Method {
        GET,
        POST
    }

    public String send(String urlString, Method method, JsonObject json) throws BadResponseException, NetworkException {
        String jsonStr = json.toString();
        return send(urlString, method, jsonStr);
    }

    public String send(String urlString, Method method, String jsonStr) throws BadResponseException, NetworkException {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        BufferedReader br = null;
        StringBuilder result = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(-1);
            conn.setConnectTimeout(TIME_OUT);
            conn.addRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.addRequestProperty("Accept", "application/json");
            conn.setRequestMethod(method.toString());

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(jsonStr);
            dos.flush();
            dos.close();

            int statusCode = conn.getResponseCode();
            if (statusCode / 100 != 2) {
                throw new BadResponseException(urlString, statusCode);
            }
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            throw new NetworkException(urlString, e);
        } finally {
            if (conn != null) conn.disconnect();
            try {
                if (dos != null) dos.close();
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (TextUtils.isEmpty(result)) {
            return null;
        }
        return result.toString();
    }

    public String uploadFile(String urlString, File file,String dir) throws BadResponseException, NetworkException {
//        final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        final String BOUNDARY = "----------" + System.currentTimeMillis();// 边界标识 随机生成
        final String  LINE_END = "\r\n";
        final String NEW_START =  "--"+BOUNDARY + LINE_END;
        final String END =  "--"+BOUNDARY + "--" + LINE_END;

        String result = null;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        InputStream is = null;
        InputStream input = null;

        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(Method.POST.toString());
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);

            dos = new DataOutputStream(
                    conn.getOutputStream());

            if (file != null) {
                StringBuilder sb = new StringBuilder();

                bodyAppendStringPrefix(sb, NEW_START, "parent_dir");
                sb.append(dir)
                        .append(LINE_END);

                bodyAppendFilePrefix(sb, NEW_START, "file", file.getName());

                dos.write(sb.toString().getBytes());
                is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len ;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();

                //body append end
                dos.write(LINE_END.getBytes());
                byte[] end_data = END.getBytes();

                dos.write(end_data);
                dos.flush();

                int statusCode = conn.getResponseCode();
                if (statusCode / 100 != 2) {
                    throw new BadResponseException(urlString, statusCode);
                }
                input = conn.getInputStream();
                StringBuilder sb1 = new StringBuilder();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                Log.e("Upload", "result : " + result);
            }
        } catch (IOException e) {
            throw new NetworkException(urlString, e);
        } finally {
            if (conn != null) conn.disconnect();
            try {
                if (dos != null) dos.close();
                if (is != null) is.close();
                if (input != null) input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * demo :
     * ------WebKitFormBoundaryayUpQMRBmAYA7vBp
     * Content-Disposition: form-data; name="parent_dir"(\r\n)
     * (\r\n)
     */
    private void bodyAppendStringPrefix(StringBuilder sb, String new_start, String name) {
        sb.append(new_start)
                .append("Content-Disposition: form-data; ")
                .append("name=\"" + name + "\"")
                .append("\r\n\r\n");
    }

    /**
     * demo :
     * ------WebKitFormBoundaryayUpQMRBmAYA7vBp
     * Content-Disposition: form-data; name="file"; filename="1474169595786.zip"
     * Content-Type: application/x-zip-compressed
     * (\r\n)
     */
    private void bodyAppendFilePrefix(StringBuilder sb, String new_start, String name, String  fileName) {
        sb.append(new_start)
                .append("Content-Disposition: form-data; ")
                .append("name=\"" + name + "\"; ")
                .append("filename=\"" + fileName + "\"")
                .append("\r\n")
                .append("Content-Type: application/x-zip-compressed")
                .append("\r\n\r\n");
    }

    public String getSimpleUrl() {
        return endPoint + PATH_SIMPLE_JSON;
    }

    public String getFullUrl() {
        return endPoint + PATH_FULL_FILE;
    }

    public static class BadResponseException extends Exception {
        public BadResponseException(String url, int responseCode) {
            super(String.format(Locale.CHINA, "Got non-200 response code (%d) from %s", responseCode, url));
        }
    }

    public static class NetworkException extends IOException {
        public NetworkException(String url, Exception ex) {
            super(String.format("Network error when posting to %s", url));
            initCause(ex);
        }
    }

}
