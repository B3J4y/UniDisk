package uzuzjmd.competence.crawler.geomapper;

/**
 * Created by dehne on 03.02.2016.
 */
public class Address {
    private String state_district;

    private String house_number;

    private String county;

    private String suburb;

    private String state;

    private String college;

    private String city_district;

    private String road;

    private String postcode;

    private String country_code;

    private String neighbourhood;

    private String country;

    private String city;

    public String getState_district() {
        return state_district;
    }

    public void setState_district(String state_district) {
        this.state_district = state_district;
    }

    public String getHouse_number() {
        return house_number;
    }

    public void setHouse_number(String house_number) {
        this.house_number = house_number;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getCity_district() {
        return city_district;
    }

    public void setCity_district(String city_district) {
        this.city_district = city_district;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "ClassPojo [state_district = " + state_district + ", house_number = " + house_number + ", county = " + county + ", suburb = " + suburb + ", state = " + state + ", college = " + college + ", city_district = " + city_district + ", road = " + road + ", postcode = " + postcode + ", country_code = " + country_code + ", neighbourhood = " + neighbourhood + ", country = " + country + ", city = " + city + "]";
    }
}