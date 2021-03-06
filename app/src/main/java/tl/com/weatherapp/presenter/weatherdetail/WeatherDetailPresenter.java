package tl.com.weatherapp.presenter.weatherdetail;

import tl.com.weatherapp.model.modelnetwork.ModelNetwork;

public class WeatherDetailPresenter {
    ModelNetwork modelNetwork;

    public WeatherDetailPresenter() {
        modelNetwork = ModelNetwork.getInstance();
    }

    public void updateInformationInPosition(int position){
        modelNetwork.updateInformationByPosition(position);
    }
}
