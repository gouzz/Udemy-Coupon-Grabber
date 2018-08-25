package udemycoupon.CouponWebsites;

import udemycoupon.Course;
import udemycoupon.Parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static udemycoupon.Constants.DAYS_RANGE;

public class PromoCoupons24 implements Parser {

    public PromoCoupons24(List<Course> courses){
        this.courses = courses;
    }

    List<Course> courses;

    @Override
    public void grabCouponUrl() {

        Calendar calendar = new GregorianCalendar();
        for (int i = 0; i < DAYS_RANGE; i += 5) {

            String url = getCouponUrlByDay(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            Elements coupons = doc.select("a:containsOwn(https://www.udemy.com)");
            for (Element tag : coupons) {
                String couponUrl = parseUrl(tag.toString());
                Course course = new Course(couponUrl);
                saveCoupon(course, new ArrayList<>());
                //   courses.add(course);
            }

            calendar.add(Calendar.DAY_OF_MONTH, -5);
        }
    }

    @Override
    public String parseUrl(String couponUrl) {
        return couponUrl.split("href=\"")[1].split("\"")[0];
    }

    @Override
    public void saveCoupon(Course course, List<Course> courses) {
        courses.add(course);
    }

    public String getCouponUrlByDay(int day, int month, int year) {
        return "https://www.promocoupons24.com/search?updated-max=" + year + "-" + month + "-" + day + "T00:00:00-23:00&max-results=50&start=0&by-date=true";
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

}
