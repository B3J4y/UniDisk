package uzuzjmd.competence.crawler.geomapper;

/**
 * Created by dehne on 03.02.2016.
 */
public class HochschulResultSet {
    private Integer id;
    private String street;
    private String city;
    Double lat;
    Double lon;

    public HochschulResultSet(Integer id, String street, String city) {
        this.id = id;
        this.street = street;
        this.city = city;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
