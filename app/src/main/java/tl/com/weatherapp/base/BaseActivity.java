package tl.com.weatherapp.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import tl.com.weatherapp.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment != null) {
                    if (fragment.isVisible() && fragment instanceof BaseFragment) {
                        ((BaseFragment) fragment).onBackPressed();
                        return;
                    }
                }
            }
        }
        showDialogDelete();
    }

    public void showDialogDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.quit_tiltle));
        builder.setMessage(getString(R.string.quit_message));
        builder.setCancelable(false);
        builder.setNegativeButton(getString(R.string.back_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.quit_tiltle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackRoot();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    public void onBackRoot() {
        super.onBackPressed();
    }
}
