package tl.com.weatherapp.view.searchaddress;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import tl.com.weatherapp.R;
import tl.com.weatherapp.base.BaseActivity;
import tl.com.weatherapp.base.BaseFragment;
import tl.com.weatherapp.presenter.searchaddress.SearchAddressPresenter;
import tl.com.weatherapp.view.main.MainActivity;

public class SearchAddressFragment extends BaseFragment implements PlaceSelectionListener {

    private PlaceAutocompleteFragment autocompleteFragment;
    private SearchAddressPresenter presenter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_find_address, container, false);
        initView(view);
        presenter = new SearchAddressPresenter();
        return view;
    }

    private void initView(View view) {
        autocompleteFragment =
                (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(
                        R.id.place_autocomplete_fragment);
        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(filter);
        autocompleteFragment.setOnPlaceSelectedListener(this);
    }

    @Override
    public void onPlaceSelected(Place place) {
        presenter.addAddress(place);
        getActivity().onBackPressed();

    }

    @Override
    public void onError(Status status) {
        Toast.makeText(getContext(), status.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (autocompleteFragment != null){
            getActivity().getFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
        }
    }


    //    @Override
//    public void onBackPressed() {
//        if (autocompleteFragment != null){
//            getActivity().getFragmentManager().beginTransaction().remove(autocompleteFragment).commit();
//        }
////        ((MainActivity) getActivity()).openWeatherAddressFragment();
//        ((BaseActivity)getActivity()).onBackRoot();
//    }
}
