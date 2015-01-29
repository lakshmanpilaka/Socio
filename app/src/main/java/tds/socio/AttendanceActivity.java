package tds.socio;

/**
 * Created by laks on 29-01-2015.
 */

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import tds.libs.GPSTracker;

public class AttendanceActivity extends BaseActivity {
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        set(navMenuTitles, navMenuIcons);
    }


    public void GetLocation(View view)
    {
        GPSTracker gps = new GPSTracker(AttendanceActivity.this);
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            EditText editTextlat = (EditText)findViewById(R.id.latitude);
            editTextlat.setText(String.valueOf(latitude));

            EditText editTextlong = (EditText)findViewById(R.id.longitude);
            editTextlong.setText(String.valueOf(longitude));
        }
        else
        {
            gps.showSettingsAlert();
        }
    }

}

