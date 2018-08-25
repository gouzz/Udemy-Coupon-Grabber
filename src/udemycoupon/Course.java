package udemycoupon;

import java.util.Objects;

public class Course {

    public Course(String couponUrl){
        this.setCouponUrl(couponUrl);
    }

    private String name;
    private String couponUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCouponUrl() {
        return couponUrl;
    }

    public void setCouponUrl(String couponUrl) {
        this.couponUrl = couponUrl;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(name, course.name) &&
                Objects.equals(couponUrl, course.couponUrl);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, couponUrl);
    }

    @Override
    public String toString() {
        return "Course{" +
                "name='" + name + '\'' +
                ", couponUrl='" + couponUrl + '\'' +
                '}';
    }
}
