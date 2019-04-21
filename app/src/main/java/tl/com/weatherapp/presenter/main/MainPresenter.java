package tl.com.weatherapp.presenter.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import tl.com.weatherapp.R;
import tl.com.weatherapp.common.Common;
import tl.com.weatherapp.model.modelnetwork.ModelNetwork;
import tl.com.weatherapp.view.main.IMainView;

public class MainPresenter implements IMainPresenter {
    private ModelNetwork modelNetwork;
    private Context mContext;
    private AlertDialog dialogInternet;
    private AlertDialog dialogLocation;
    private BroadcastReceiver receiver;
    private IMainView iMainView;

    public MainPresenter(Context mContext) {
        this.mContext = mContext;
        modelNetwork = ModelNetwork.getInstance();
        modelNetwork.create(mContext);
        modelNetwork.setiMainPresenter(this);
    }

    public void setiMainView(IMainView iMainView) {
        this.iMainView = iMainView;
    }

    public void start() {
        registerBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.location.PROVIDERS_CHANGED");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    if (!isConnectedNetwork(mContext)) {
                        if(dialogInternet == null) {
                            dialogInternet = buildDialogInternet(mContext).show();
                        }else dialogInternet.show();
                        iMainView.showNotifiConnection(true);
                    }
                    else {
                        if (dialogInternet != null)
                            dialogInternet.dismiss();
                        if (!isEnabledLocation(mContext)) {
                            if (dialogLocation == null) {
                                dialogLocation = buildDialogLocation(context).show();
                            }else dialogLocation.show();
                        }
                        iMainView.showNotifiConnection(false);
                    }

                   // modelNetwork.create(mContext);
                    modelNetwork.loadDataForMainPresenter();


                } else if (intent.getAction().equals("android.location.PROVIDERS_CHANGED")) {
                    if (!isEnabledLocation(mContext)) {
                        if (dialogLocation == null) {
                            dialogLocation = buildDialogLocation(context).show();
                        }else dialogLocation.show();

                    } else {
                        modelNetwork.updateInformationByAddressId(Common.CURRENT_ADDRESS_ID, Common.UPDATE_ALL_WIDGET);
                        //Toast.makeText(mContext, R.string.gps_network_enabled, Toast.LENGTH_SHORT).show();
                        if (dialogLocation != null) dialogLocation.dismiss();
                    }

                }
            }
        };
        mContext.registerReceiver(receiver, filter);
    }


    public boolean isConnectedNetwork(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }

    public AlertDialog.Builder buildDialogInternet(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(mContext.getString(R.string.title_disconnect));
        builder.setMessage(mContext.getString(R.string.message_disconnect));

        builder.setNegativeButton(mContext.getString(R.string.setting), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                mContext.startActivity(intent);

            }
        });

        builder.setPositiveButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        return builder;
    }

    public AlertDialog.Builder buildDialogLocation(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setMessage(R.string.gps_network_not_enabled);

        builder.setNegativeButton(mContext.getString(R.string.setting), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);

            }
        });

        builder.setPositiveButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        return builder;
    }

    public boolean isEnabledLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled || network_enabled;
    }

    @Override
    public void loadDataFinish() {

        iMainView.openWeatherHomeFragment();

    }

    public void setCurrentPagerByAppWidgetId(int appWidgetId) {
        modelNetwork.setCurrentPagerByAppWidgetId(appWidgetId);
    }

    public void setCurrentPager(int positionPager) {
        modelNetwork.setCurrentPager(positionPager);
    }

    public void isNotReceiver() {
        modelNetwork.isNotReceiver();
    }

    public void unregisterReceiver() {
        try {
            mContext.unregisterReceiver(receiver);
        }catch (Exception e){
            Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_SHORT);
        }
    }
}
