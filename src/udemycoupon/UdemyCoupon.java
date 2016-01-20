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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UdemyCoupon {

    ArrayList<String> urls = new ArrayList<>();

    public final String urlGet = "https://www.udemy.com/join/login-popup/?displayType=ajax&display_"
            + "type=popup&showSkipButton=1&returnUrlAfterLogin=https%3A%2F%2Fwww.udemy.com%2F&next"
            + "=https%3A%2F%2Fwww.udemy.com%2F&locale=pt_BR";
    public final String urlPost = "https://www.udemy.com/join/login-popup/?displayType=ajax&display_type"
            + "=popup&showSkipButton=1&returnUrlAfterLogin=https%3A%2F%2Fwww.udemy.com%"
            + "2F&next=https%3A%2F%2Fwww.udemy.com%2F&locale=pt_BR";

    public static void main(String[] args) throws IOException {
        UdemyCoupon d = new UdemyCoupon();

        d.getCouponEachPage();
        d.readArrayList();
        //d.sendPost();
        //d.grabCookies();
    }

    public void sendPost() throws IOException {
        /*Get Cookies*/

        HttpsURLConnection urlConnection
                = (HttpsURLConnection) new URL(urlGet)
                .openConnection();

        List<String> cookies = urlConnection.getHeaderFields().get("Set-Cookie");

        String token[] = null;
        String token2[] = new String[10];
        String header, data;
        String cookiesRequested[][] = new String[25][200];
        for (int i = 0; i < cookies.size(); i++) {
            //  System.out.println(cookies.get(i));
            if (cookies.get(i).contains(";")) {
                token = cookies.get(i).split(";");
                if (token[0].contains("=")) {
                    token2 = token[0].split("=");
                    if (token2.length == 2 && token2[0].trim().length() > 0 && token2[1].trim().length() > 0) {
                        cookiesRequested[0][i] = token2[0];
                        cookiesRequested[1][i] = token2[1];
                    } else {
                        cookiesRequested[0][i] = token2[0];
                        cookiesRequested[1][i] = "";
                    }
                    //   System.out.println(cookiesRequested[0][i] + "=" + cookiesRequested[1][i]);
                }

            }
        }

        /* Send the Request with the cookies*/
        URL obj = new URL(urlPost);

        System.out.println("A conectar...");
        HttpsURLConnection con;
        con = (HttpsURLConnection) obj.openConnection();

        String cookieTotal = "";
        int posTokenArray = 0;
        for (int i = 0; i < cookies.size(); i++) {
            cookieTotal += cookiesRequested[0][i] + "=" + cookiesRequested[1][i];
            if (i < cookies.size() - 1) {
                cookieTotal += ";";
            }
            if (cookiesRequested[0][i].contains("csrftoken")) {
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

        //   System.out.println(cookieTotal);
        con.setRequestProperty("Cookie", cookieTotal);
        con.setRequestProperty("Connection", "keep-alive");

        con.setDoOutput(true);
        con.setDoInput(true);

        String postParams = "csrfmiddlewaretoken=" + cookiesRequested[1][posTokenArray] + "&locale"
                + "=pt_BR&email=testeom&password=tete&submit=Acessar";

        // Send post request
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        System.out.println("csrfmiddlewaretoken " + cookiesRequested[1][posTokenArray]);
        System.out.println(con.getResponseCode());

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + urlPost);
        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in
                = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        System.out.println(response);

        if (response.toString().contains("Por favor, verifique seu e-mail e sua senha.")) {
            System.out.println("Incorrect email or password!");
        } else if (response.toString().contains("nforme um endereço de email válido")) {
            System.out.println("Email syntax invalid!");
        } else {
            System.out.println("You're in!");
        }

        in.close();
        con.disconnect();
    }

    public void readArrayList() {

        for (String url : urls) {
            System.out.println(url);
        }

    }

    public void getCouponEachPage() throws IOException {

        for (int j = 19; j > 0; j--) {
            String url = "http://www.promocoupons24.com/search?updated-max=2016-01-" + j + "T23%3A30%3A00-08%3A00&max-results=46";
            Document doc = null;
            doc = Jsoup.connect(url).get();

            Elements coupons = doc.select("a:containsOwn(https://www.udemy.com)");

            String couponURL = String.valueOf(coupons);
            String token[] = new String[1000];

            for (int i = 0; i < 30; i++) { // 10 - 12 Coupons per page

                if (couponURL.contains("</a>")) {
                    token = couponURL.split("</a>");
                }
                //      System.out.println("Day : " + j);
                handleURL(token[i]);
            }
        }
    }

    //handleURL(String token) -> Handles each url so it's parsed in the correct format.
    public void handleURL(String token) {
        String tokenEachURL[] = new String[1000];
        //     String tokenEachURL2[] = new String [1000];

        if (token.contains(">:")) {
            tokenEachURL = token.split(">:");

            if (tokenEachURL[1].contains("&nbsp")) {
                tokenEachURL = tokenEachURL[1].split("&nbsp;");

            }
            urls.add(tokenEachURL[1].trim());

        }
    }
}
