package udemycoupon;

import java.util.List;

public interface Parser {

    public void grabCouponUrl();
    public String parseUrl(String couponUrl);
    public void saveCoupon(Course course, List<Course> courses);

    default public void sayHi(){
        System.out.println("Hi!");
    }
}
