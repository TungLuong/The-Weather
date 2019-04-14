package tl.com.weatherapp.model.airquality;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class City {

    @SerializedName("geo")
    @Expose
    public List<Float> geo = null;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("url")
    @Expose
    public String url;

}
