package tl.com.weatherapp.presenter.searchaddress;

import com.google.android.gms.location.places.Place;

import tl.com.weatherapp.model.modelnetwork.ModelNetwork;

public class SearchAddressPresenter {
    private ModelNetwork modelNetwork;

    public SearchAddressPresenter() {
        modelNetwork = ModelNetwork.getInstance();
    }

    public void addAddress(Place place) {
        modelNetwork.addAddress(place);
    }
}
