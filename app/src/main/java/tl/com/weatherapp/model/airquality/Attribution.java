package tl.com.weatherapp.model.airquality;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribution {

    @SerializedName("url")
    @Expose
    public String url;
    @SerializedName("name")
    @Expose
    public String name;

}