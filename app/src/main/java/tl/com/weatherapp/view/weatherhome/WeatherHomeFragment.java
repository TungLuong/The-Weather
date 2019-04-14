package tl.com.weatherapp.view.weatherhome;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import tl.com.weatherapp.R;
import tl.com.weatherapp.adapter.WeatherFragmentAdapter;
import tl.com.weatherapp.base.BaseFragment;
import tl.com.weatherapp.model.airquality.AirQuality;
import tl.com.weatherapp.model.weather.WeatherResult;
import tl.com.weatherapp.presenter.weatherhome.WeatherHomePresenter;
import tl.com.weatherapp.view.main.MainActivity;

public class WeatherHomeFragment extends BaseFragment implements IWeatherHomeView {


    private ViewPager viewPager;
  //  private int curPositionPager = 0;
    private CircleIndicator indicator;
    private ImageView btnOpenWeatherAddressFragment;
    private WeatherHomePresenter presenter;
    private WeatherFragmentAdapter adapter;

    public WeatherHomeFragment() {
    }

    public static WeatherHomeFragment newInstance() {
        WeatherHomeFragment fragment = new WeatherHomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_home, container, false);
        initView(view);
        presenter = new WeatherHomePresenter(this);
        presenter.getResultWeather();
        return view;
    }

    private void initView(View view) {
        viewPager = view.findViewById(R.id.view_pager_weather_fragment);
        indicator = view.findViewById(R.id.circle_indicator);


        btnOpenWeatherAddressFragment = view.findViewById(R.id.btn_open_weather_address_activity);
        btnOpenWeatherAddressFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).openWeatherAddressFragment();
                presenter.setCurrPositionPagerForModel(viewPager.getCurrentItem());
            }
        });

    }


    @Override
    public void onBackPressed() {
        ((MainActivity)getActivity()).showDialogDelete();
    }

    public WeatherFragmentAdapter getAdapter() {
        return adapter;
    }

//    public void setCurPositionPager(int curPositionPager) {
//        this.curPositionPager = curPositionPager;
//    }



    @Override
    public void getResultList(List<WeatherResult> weatherResultList, List<AirQuality> airQualityLis) {
        adapter = new WeatherFragmentAdapter(getChildFragmentManager(),weatherResultList,airQualityLis);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        adapter.registerDataSetObserver(indicator.getDataSetObserver());
    }

    @Override
    public void setCurrPositionPager(int curPositionPager) {
        viewPager.setCurrentItem(curPositionPager);
    }

    @Override
    public void notifyItemChange(int position) {
        adapter.notifyDataSetChanged();
    }

}
