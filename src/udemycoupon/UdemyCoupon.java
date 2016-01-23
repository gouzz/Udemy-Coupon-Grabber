package udemycoupon;

import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.HttpStatusException;

public class UdemyCoupon {

    public final String urlGet = "https://www.udemy.com/join/login-popup/?displayType=ajax&display_"
            + "type=popup&showSkipButton=1&returnUrlAfterLogin=https%3A%2F%2Fwww.udemy.com%2F&next"
            + "=https%3A%2F%2Fwww.udemy.com%2F&locale=pt_BR";
    public final String urlPost = "https://www.udemy.com/join/login-popup/?displayType=ajax&display_type"
            + "=popup&showSkipButton=1&returnUrlAfterLogin=https%3A%2F%2Fwww.udemy.com%"
            + "2F&next=https%3A%2F%2Fwww.udemy.com%2F&locale=pt_BR";

    public static void main(String[] args) throws IOException {
        UdemyCoupon uc = new UdemyCoupon();
        uc.getCouponEachPage();
        // uc.readArrayList();
        uc.sendGetLogin();
        uc.sendPostLogin();
        uc.sendGetApplyCoupon();
        //uc.readArrayList();
    }

    Date date = new Date();
    private ArrayList<String> urls = new ArrayList<>();
    private List<String> cookies;
    private String cookiesArray[] = new String[100];
    private String cookieTotal = "";

    public String[] sendGetLogin() throws MalformedURLException, IOException {
        HttpsURLConnection urlConnection
                = (HttpsURLConnection) new URL(urlGet)
                .openConnection();

        cookies = urlConnection.getHeaderFields().get("Set-Cookie");
        for (int i = 0; i < cookies.size(); i++) {
            if (cookies.get(i).contains(";")) {
                cookiesArray[i] = cookies.get(i).split(";")[0];
                System.out.println(i + " - " + cookiesArray[i]);
            }
        }
        return cookiesArray;
    }

    public int sendPostLogin() throws IOException {

        URL obj = new URL(urlPost);

        System.out.println("A conectar...");
        HttpsURLConnection con;
        con = (HttpsURLConnection) obj.openConnection();

        int posTokenArray = 0;
        for (int i = 0; i < cookies.size(); i++) {
            if (i < cookies.size() - 1) {
                cookieTotal += cookiesArray[i] + ";";
            }

            if (cookiesArray[i].contains("csrftoken")) {
                posTokenArray = i;
            }
        }

        con.setUseCaches(false);
        con.setRequestMethod("POST");
        con.setRequestProperty("Host", "www.udemy.com");
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:43.0) Gecko/20100101 Firefox/43.0");
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        con.setRequestProperty("Accept-Language", "pt-PT,pt;q=0.8,en;q=0.5,en-US;q=0.3");
        con.setRequestProperty("Referer", "https://www.udemy.com/join/login-popup/?displayType=ajax&display_type=popup&showSkipButton=1&returnUrlAfterLogin=https%3A%2F%2Fwww.udemy.com%2F&next=https%3A%2F%2Fwww.udemy.com%2F&locale=pt_BR");

        con.setRequestProperty("Cookie", cookieTotal);
        con.setRequestProperty("Connection", "keep-alive");

        con.setDoOutput(true);
        con.setDoInput(true);

        String postParams = "csrfmiddlewaretoken=" + cookiesArray[posTokenArray].split("=")[1] + "&locale"
                + "=pt_BR&email=miguelfbrito11@gmail.com&password=HBewh36&submit=Acessar";

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        System.out.println("\nSending 'POST' request to URL : " + urlPost);
        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + con.getResponseCode());
        System.out.println("Cookies : " + cookieTotal);
        System.out.println("csrfmiddlewaretoken " + cookiesArray[posTokenArray].split("=")[1]);

        BufferedReader in
                = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        System.out.println(response);

        in.close();
        con.disconnect();

        if (response.toString().contains("Por favor, verifique seu e-mail e sua senha.")) {
            System.out.println("Incorrect email or password!");
            return 0;
        } else if (response.toString().contains("nforme um endereço de email válido")) {
            System.out.println("Email syntax invalid!");
            return 0;
        } else {
            System.out.println("You're in!");
            return 1;
        }
    }

    public void sendGetApplyCoupon() throws MalformedURLException, IOException {

        for (int i = 0; i < urls.size(); i++) {

            System.out.println("A conectar a : " + urls.get(i));
            URL obj2;
            HttpsURLConnection con2 = null;

            try {
                Document doc = Jsoup.connect(urls.get(i)).get();
                Elements link;

                if (doc.select("a:containsOwn(Take This Course)").hasText()) {
                    link = doc.select("a:containsOwn(Take This Course)");
                    obj2 = new URL("https://www.udemy.com" + link.attr("href"));
                    System.out.println(obj2.toString());
                } else {
                    link = doc.select("a:containsOwn(Start Learning Now)");
                    obj2 = new URL(link.attr("href"));
                    System.out.println("URL : " + obj2.toString());
                }

                String s = link.attr("href");
                System.out.println(s);

                con2 = (HttpsURLConnection) obj2.openConnection();
                con2.setRequestMethod("GET");
                con2.setRequestProperty("Host", "www.udemy.com");
                con2.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:43.0) Gecko/20100101 Firefox/43.0");
                con2.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                con2.setRequestProperty("Accept-Language", "pt-PT,pt;q=0.8,en;q=0.5,en-US;q=0.3");
                con2.setRequestProperty("Referer", "https://www.udemy.com/join/login-popup/?displayType=ajax&display_type=popup&showSkipButton=1&returnUrlAfterLogin=https%3A%2F%2Fwww.udemy.com%2F&next=https%3A%2F%2Fwww.udemy.com%2F&locale=pt_BR");

                con2.setRequestProperty("Cookie", cookieTotal);
                con2.setRequestProperty("Connection", "keep-alive");

                con2.disconnect();

            } catch (HttpStatusException ex) {
                System.out.println("URL Inválido + " + urls.get(i));
            }

        }

    }

    public void readArrayList() {
        for (String url : urls) {
            System.out.println(url);
        }
    }

    public void getCouponEachPage() throws IOException {

        for (int j = date.getDate(); j > 20; j--) {
            String url = "http://www.promocoupons24.com/search?updated-max=2016-01-" + j + "T23%3A30%3A00-08%3A00&max-results=46";
            Document doc = Jsoup.connect(url).get();

            Elements coupons = doc.select("a:containsOwn(https://www.udemy.com)");

            String couponURL = String.valueOf(coupons);
            String token[] = new String[1000];

            for (int i = 0; i < 30; i++) { // 10 - 12 Coupons per page

                if (couponURL.contains("</a>")) {
                    token = couponURL.split("</a>");
                }
                handleUrl(token[i]);
            }
        }
    }

    public void handleUrl(String token) {
        String tokenEachURL[] = new String[250];
        if (token.contains(">:")) {
            tokenEachURL = token.split(">:");
            if (tokenEachURL[1].contains("&nbsp")) {
                tokenEachURL = tokenEachURL[1].split("&nbsp;");

            }
            if (!urls.contains(tokenEachURL[1].trim())) {
                urls.add(tokenEachURL[1].trim());
            }
        }
    }

}
