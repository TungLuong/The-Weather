package tl.com.weatherapp.view.weatheraddress;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import tl.com.weatherapp.IListenerWeatherAddressAdapter;
import tl.com.weatherapp.R;
import tl.com.weatherapp.adapter.ItemWeatherAddressAdapter;
import tl.com.weatherapp.base.BaseActivity;
import tl.com.weatherapp.base.BaseFragment;
import tl.com.weatherapp.model.weather.WeatherResult;
import tl.com.weatherapp.presenter.weatheraddress.WeatherAddressPresenter;
import tl.com.weatherapp.view.main.MainActivity;
import tl.com.weatherapp.view.settings.SettingActivity;

public class WeatherAddressFragment extends BaseFragment implements IWeatherAddressView, IListenerWeatherAddressAdapter {

    private RecyclerView rcvWeatherAddress;
    private ImageView btnAddAddress;
    private ItemWeatherAddressAdapter addressAdapter;
    private WeatherAddressPresenter presenter;
    private ImageView btnSetting;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather_address, container, false);
        initView(view);
        presenter = new WeatherAddressPresenter(this);
        presenter.getResultWeather();
        return view;
    }


    private void initView(View view) {
        rcvWeatherAddress = view.findViewById(R.id.rcv_weather_address);

        btnAddAddress = view.findViewById(R.id.btn_add_address);
        btnSetting = view.findViewById(R.id.btn_setting);
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openSearchAddressFragment();
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(settingIntent);
            }
        });
    }

    private ItemTouchHelper.Callback createCallBack() {
        ItemTouchHelper.SimpleCallback simpleCallback;
        simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int oldPo = viewHolder.getAdapterPosition();
                int newPo = target.getAdapterPosition();
                if (oldPo == 0 || newPo == 0) return false;
                presenter.moveItem(oldPo,newPo);
                addressAdapter.notifyItemMoved(oldPo, newPo);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        return simpleCallback;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (addressAdapter != null) {
            addressAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deleteItem(int position) {
        presenter.deleteItem(position);
        addressAdapter.notifyDataSetChanged();
    }

    @Override
    public void openWeatherHomeFragment() {
//        ((BaseActivity) getActivity()).onBackRoot();
        ((MainActivity) getActivity()).openWeatherHomeFragment();
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) getActivity()).openWeatherHomeFragment();
    }


    @Override
    public void getWeatherResult(List<WeatherResult> weatherResults) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvWeatherAddress.setLayoutManager(layoutManager);

        addressAdapter = new ItemWeatherAddressAdapter(weatherResults, getContext(), this);
        //     ((ItemWeatherAddressAdapter) addressAdapter).setMode(Attributes.Mode.Single);
        rcvWeatherAddress.setAdapter(addressAdapter);

        ItemTouchHelper helper = new ItemTouchHelper(createCallBack());
        helper.attachToRecyclerView(rcvWeatherAddress);
    }

    @Override
    public void notifyItemChange(int position) {
        addressAdapter.notifyItemChanged(position);
    }
}
