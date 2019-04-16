package tl.com.weatherapp.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import tl.com.weatherapp.IListenerWeatherAddressAdapter;
import tl.com.weatherapp.R;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.modelnetwork.ModelNetwork;
import tl.com.weatherapp.model.weather.WeatherResult;

public class ItemWeatherAddressAdapter extends RecyclerSwipeAdapter<ItemWeatherAddressAdapter.ItemWeatherAddressViewHolder> {

    private List<WeatherResult> weatherResultList;
    private Context mContext;
    private IListenerWeatherAddressAdapter iListenerWeatherAddressAdapter;
    private SharedPreferences sharedPreferences;
    private String tempUnit;

    public ItemWeatherAddressAdapter(List<WeatherResult> weatherResultList, Context mContext, IListenerWeatherAddressAdapter iListenerWeatherAddressAdapter) {
        this.weatherResultList = weatherResultList;
        this.mContext = mContext;
        this.iListenerWeatherAddressAdapter = iListenerWeatherAddressAdapter;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        tempUnit = sharedPreferences.getString(mContext.getString(R.string.pref_temp_unit),"");
    }

    @NonNull
    @Override
    public ItemWeatherAddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_address, parent, false);
        return new ItemWeatherAddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemWeatherAddressViewHolder holder, final int position) {

        if (weatherResultList.get(position) == null) return;
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        if (position == 0) {
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, null);
        }else {
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wraper));
        }
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemManger.removeShownLayouts(holder.swipeLayout);
                if(position == Common.CURRENT_ADDRESS_ID){
                    //Toast.makeText(mContext,"Cannot remove location",Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }else {
                    iListenerWeatherAddressAdapter.deleteItem(position);
                }
                mItemManger.closeAllItems();
            }
        });

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iListenerWeatherAddressAdapter.openWeatherHomeFragment();
                ModelNetwork.getInstance().setCurrentPager(holder.getAdapterPosition());
            }
        });
        String icon_name = weatherResultList.get(position).getCurrently().getIcon().replace('-', '_')+"_compact";
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/drawable/" + icon_name);
        Picasso.get()
                .load(uri)
                .fit()
                .into(holder.background);
        if (position == 0) {
            holder.icLocation.setVisibility(View.VISIBLE);
        } else {
            holder.icLocation.setVisibility(View.GONE);
        }

//        Picasso.get().load(new StringBuilder("https://darksky.net/images/weather-icons/")
//                .append(weatherResultList.get(position).getCurrently().getIcon())
//                .append(".png").toString()).into(holder.icWeather);

        if(tempUnit.equals(mContext.getString(R.string.pref_temp_default_value))){
            holder.tvTemp.setText(Common.covertFtoC(weatherResultList.get(position).getCurrently().getTemperature()) + tempUnit);
        }
        else{
            holder.tvTemp.setText((int)weatherResultList.get(position).getCurrently().getTemperature() + tempUnit);
        }


        holder.tvAddress.setText(weatherResultList.get(position).getAddress());
        String lastTimeUpdate = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            lastTimeUpdate = mContext.getString(R.string.last_update) +  ": " + Common.convertUnixToTime(weatherResultList.get(position).getCurrently().getTime());
        }
        holder.tvTimeLastUpdate.setText(lastTimeUpdate);

        mItemManger.bindView(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        if (weatherResultList == null) return 0;
        return weatherResultList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class ItemWeatherAddressViewHolder extends RecyclerView.ViewHolder {
        private ImageView background;
        private TextView tvAddress;
        private TextView tvTimeLastUpdate;
        private ImageView icLocation;
        private ImageView icWeather;
        private TextView tvTemp;
        private TextView btnDelete;
        public SwipeLayout swipeLayout;

        public ItemWeatherAddressViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe);
            background = itemView.findViewById(R.id.background);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvTimeLastUpdate = itemView.findViewById(R.id.tv_time_update);
            tvTemp = itemView.findViewById(R.id.tv_temp);
            icWeather = itemView.findViewById(R.id.img_icon);
            icLocation = itemView.findViewById(R.id.is_location_enable);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
