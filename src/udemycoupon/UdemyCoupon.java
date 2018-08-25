package udemycoupon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.util.*;

import org.jsoup.HttpStatusException;

import static udemycoupon.Constants.DAYS_RANGE;

//
public class UdemyCoupon {

    private CookieManager cookieManager = new CookieManager();
    public static final String urlGet =
            "https://www.udemy.com/join/login-popup/?next=https://www.udemy.com/&returnUrlAfterLogin=https://www.udemy.com/&display_type=popup&showSkipButton=1&displayType=ajax&locale=pt_BR&force_login=1";
    public final String urlPost = "https://www.udemy.com/join/login-popup/?displayType=ajax&display_type"
            + "=popup&showSkipButton=1&returnUrlAfterLogin=https%3A%2F%2Fwww.udemy.com%"
            + "2F&next=https%3A%2F%2Fwww.udemy.com%2F&locale=pt_BR";
    public static final String urlCourses = "https://www.udemy.com/courses";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36";

    private static final String email = "d11ecepuku@zippiex.com";
    private static final String password = "gomas123";

    Set<Course> courses = new HashSet<>();

    public static void main(String[] args) throws IOException {
        UdemyCoupon uc = new UdemyCoupon();

        //uc.getCouponEachPage();
        uc.sendGetLogin(urlGet);
        uc.sendPostLogin();
        //  uc.sendGetLogin(urlCourses);
        // uc.sendGetApplyCoupon();
    }

    public void getCouponEachPage() throws IOException {

        Calendar calendar = new GregorianCalendar();

        for (int i = 0; i < DAYS_RANGE; i += 5) {

            String url = getCouponUrlByDay(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));

            Document doc = Jsoup.connect(url).get();
            Elements coupons = doc.select("a:containsOwn(https://www.udemy.com)");

            for (Element tag : coupons) {

                Course course = new Course(tag.toString());
                course.extractUrlFromTag();
                courses.add(course);
            }

            calendar.add(Calendar.DAY_OF_MONTH, -5);
        }

        System.out.println(courses.size());

        for (Course c : courses) {
            System.out.println(c.toString());
        }

    }



    Date date = new Date();
    private ArrayList<String> urls = new ArrayList<>();
    private List<String> cookies;
    private List<String> individualCookies = new ArrayList<>();
    private String cookieTotal = "";

    public void sendGetLogin(String url) throws IOException {
        CookieHandler.setDefault(cookieManager);

        HttpsURLConnection urlConnection = (HttpsURLConnection) new URL(url).openConnection();
        urlConnection.setRequestProperty("User-Agent", USER_AGENT);

        cookies = urlConnection.getHeaderFields().get("Set-Cookie");
        for (String c : cookies) {
            if (c.contains(";")) {
                String currentCookie = c.split(";")[0];
                individualCookies.add(currentCookie);
            }
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        // writeHtml(response.toString());

        in.close();
    }

    public int sendPostLogin() throws IOException {

        CookieHandler.setDefault(cookieManager);

        URL obj = new URL(urlPost);
        System.out.println("Connecting...");

        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setInstanceFollowRedirects(true);

        int posTokenArray = 0;
        for (int i = 0; i < cookies.size(); i++) {
            cookieTotal += individualCookies.get(i);
            System.out.println(i + " - " + individualCookies.get(i));
            if (i < cookies.size() - 1) {
                cookieTotal += ";";
            }
            // TODO : Fix NullPointException here
            if (individualCookies.get(i) != null) {
                if (individualCookies.get(i).contains("csrftoken")) {
                    posTokenArray = i;
                }
            }
        }

        con.setUseCaches(false);
        con.setRequestMethod("POST");
        con.setRequestProperty("Host", "www.udemy.com");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        con.setRequestProperty("Accept-Language", "pt-PT,pt;q=0.8,en;q=0.5,en-US;q=0.3");
        con.setRequestProperty("Referer", urlGet);
        con.setRequestProperty("Cookie", cookieTotal);

        con.setDoOutput(true);
        con.setDoInput(true);

        String postParams = "csrfmiddlewaretoken=" + individualCookies.get(posTokenArray).split("=")[1] + "&locale"
                + "=pt_BR&email=" + email + "&password=" + password + "&submit=Acessar";

        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        System.out.println("\nSending 'POST' request to URL : " + urlPost);
        System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + con.getResponseCode());
        System.out.println("Cookies : " + cookieTotal);
        System.out.println("csrfmiddlewaretoken : " + individualCookies.get(posTokenArray).split("=")[1]);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        writeHtml(response.toString());
        in.close();
        // con.disconnect();

        if (response.toString().contains("Por favor, verifique seu e-mail e sua senha.")) {
            System.out.println("Response to Login : Incorrect email or password!");
        } else if (response.toString().contains("nforme um endereço de email válido")) {
            System.out.println("Response to Login : Email syntax invalid!");
        } else if (response.toString().contains("excedeu o número máximo de")) {
            System.out.println("Response to Login : Banned for an hour due to a lot of login trials!");
        } else {
            System.out.println("Response to Login : Logged successfully!");
        }

        return 1;
    }

    public void sendGetApplyCoupon() throws MalformedURLException, IOException {
        System.out.println("\n\n ApplyCoupon::");
        CookieHandler.setDefault(cookieManager);

        for (int i = 0; i < urls.size()/* urls.size() */; i++) {

            System.out.println("\nConnecting to : " + urls.get(i));
            URL obj2 = null;
            HttpsURLConnection con2 = null;

            try {
                Document doc = Jsoup.connect(urls.get(i)).get();
                Elements link;
                String url = "";

                if (doc.select("a:containsOwn(Take This Course)").hasText()) {
                    link = doc.select("a:containsOwn(Take This Course)");
                    url = ("https://www.udemy.com" + link.attr("href"));
                } else {
                    link = doc.select("a:containsOwn(Start Learning Now)");
                    url = (link.attr("href"));
                }

                String s = link.attr("href");
                // System.out.println(s);

                UdemyCoupon uc = new UdemyCoupon();
                // uc.sendGetLogin(urls.get(i));

                try {
                    obj2 = new URL(url);
                } catch (MalformedURLException ex) {
                    System.out.println("Erro!");
                    continue;
                }
                System.out.println("URL :: " + url);

                con2 = (HttpsURLConnection) obj2.openConnection();
                con2.setRequestMethod("GET");
                con2.setRequestProperty("Host", "www.udemy.com");
                con2.setRequestProperty("User-Agent",
                        USER_AGENT);

                con2.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                con2.setRequestProperty("Accept-Language", "pt-PT,pt;q=0.8,en;q=0.5,en-US;q=0.3");
                con2.setRequestProperty("Referer",
                        "https://www.udemy.com/join/login-popup/?displayType=ajax&display_type=popup&showSkipButton=1&returnUrlAfterLogin=https%3A%2F%2Fwww.udemy.com%2F&next=https%3A%2F%2Fwww.udemy.com%2F&locale=pt_BR");

                con2.setRequestProperty("Referer", url);
                con2.setRequestProperty("Cookie", cookieTotal);
                con2.setRequestProperty("Connection", "keep-alive");

                System.out.println("");
                System.out.println("Cookies Apply Coupon : " + cookieTotal);
                System.out.println("Response Code: " + con2.getResponseCode());

                BufferedReader in = new BufferedReader(new InputStreamReader(con2.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println(response);

                in.close();
                con2.disconnect();

            } catch (HttpStatusException ex) {
                System.out.println("Invalid URL :  " + urls.get(i));
            }

        }

    }

    public String getCouponUrlByDay(int day, int month, int year) {
        return "https://www.promocoupons24.com/search?updated-max=" + year + "-" + month + "-" + day + "T00:00:00-23:00&max-results=50&start=0&by-date=true";
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

    public void writeHtml(String text) throws IOException {
        FileWriter f;
        BufferedWriter bw;
        try {
            f = new FileWriter("file.html");
            bw = new BufferedWriter(f);
            bw.write(text);
            f.close();
        } catch (IOException e) {
            System.out.println("Failed to write on file!");
            e.printStackTrace();
        }
        String command = "cmd.exe /c start chrome D:/Coding/Udemy-Coupon-Grabber/file.html";
        Runtime.getRuntime().exec(command);
    }

}
