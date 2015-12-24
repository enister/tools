import com.sun.net.httpserver.BasicAuthenticator;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

public class HTTP {
    public static void main(String[] args) throws IOException {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("******","******".toCharArray());
            }
        });
        URL url = new URL(url);
//        URL url = new URL(url);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");

        con.setDoInput(true);
        con.setDoOutput(true);

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write("login=************");
        out.write("&");
        out.write("password=************");
        out.flush();
        out.close();
        con.connect();
//        System.out.println(con.getResponseCode());
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while((line = in.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println(con.getHeaderFields());
        List<String> tokens = con.getHeaderFields().get("Set-Cookie");
        String ss = tokens.get(0).split(";")[0];
        System.out.println(ss);
        in.close();

        URL url2 = new URL("url");
        HttpURLConnection con2 = (HttpURLConnection)url2.openConnection();
        con2.setRequestMethod("GET");
        con2.setRequestProperty("Cookie",ss);
        con2.connect();
        StringBuilder sb = new StringBuilder();
        in = new BufferedReader(new InputStreamReader(con2.getInputStream()));
        while((line = in.readLine()) != null) {
            sb = sb.append(line);
        }
        in.close();
        System.out.println(sb.toString());
        String json = sb.toString().replaceAll("\\\\","").replaceAll("\"\\[","\\[").replaceAll("\\]\"","\\]");
        System.out.println(json);
        int cnt = 1;
        try (JsonParser parser = Json.createParser(new StringReader(json))) {
            while (parser.hasNext()) {
                JsonParser.Event e = parser.next();
                System.out.println(e.toString());
                if (e == JsonParser.Event.KEY_NAME) {
                    String es = parser.getString();
                    System.out.println(es);
                    if (es.equals("fields")) {
                        parser.next();
                        while(parser.hasNext()) {
                            e = parser.next();
                            if (e == JsonParser.Event.END_OBJECT) break;
                            String field = parser.getString();
                            e = parser.next();
                            String value = "";
                            if (e == JsonParser.Event.VALUE_TRUE) {
                                value = "true";
                            } else {
                                value = parser.getString();
                            }
                            System.out.println(field + " " + value);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}
