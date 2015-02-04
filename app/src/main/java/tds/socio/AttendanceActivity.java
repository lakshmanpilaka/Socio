package tds.socio;

/**
 * Created by laks on 29-01-2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tds.libs.GPSTracker;
import tds.libs.SntpClient;
import tds.libs.TimestampConvertor;

public class AttendanceActivity extends BaseActivity {
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    long lngCurrentTime = -1;

    String mCurrentPhotoPath;


    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Socio_" + timeStamp + "_";

        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File storageDir =  Environment.getExternalStorageDirectory();

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

//        Save a file: path for use with ACTION_VIEW intents

        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;

    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent;
        File photoFile = null;

        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
    }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {

        // Get the dimensions of the View

        int targetW = 50;
        int targetH = 100;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        set(navMenuTitles, navMenuIcons);

        new RetrieveTimeWS().execute();

    }

    public void MarkInAttendance(View view)
    {
        GPSTracker gps = new GPSTracker(AttendanceActivity.this);

        if(gps.canGetLocation())
        {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

//          String StrdistBetCoor = Double.toString(gps.distance(latitude, longitude, 17.3986852, 78.3896163, 'C'));
//          Toast.makeText(getApplicationContext(),StrdistBetCoor,Toast.LENGTH_LONG).show();

            try
            {
                dispatchTakePictureIntent();

                List<Employee> employees;
                employees = Employee.listAll(Employee.class);

                String internalEmpId = employees.get(0).getInternalEmpId();

                new AttendanceWS().execute(Double.toString(latitude), Double.toString(longitude),internalEmpId, "1" );
            }

           catch (Exception e) {
               Log.e("Capture Image Error: " , e.getMessage());
           }
        }
        else
        {
            gps.showSettingsAlert();
        }
    }

    public void MarkOutAttendance(View view)
    {
        GPSTracker gps = new GPSTracker(AttendanceActivity.this);

        if(gps.canGetLocation())
        {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

//          String StrdistBetCoor = Double.toString(gps.distance(latitude, longitude, 17.3986852, 78.3896163, 'C'));
//          Toast.makeText(getApplicationContext(),StrdistBetCoor,Toast.LENGTH_LONG).show();

            try
            {
                dispatchTakePictureIntent();

                List<Employee> employees;
                employees = Employee.listAll(Employee.class);

                String internalEmpId = employees.get(0).getInternalEmpId();

                new AttendanceWS().execute(Double.toString(latitude), Double.toString(longitude),internalEmpId, "2" );
            }




            catch (Exception e) {
                Log.e("Capture Image Error: " , e.getMessage());
            }
        }
        else
        {
            gps.showSettingsAlert();
        }
    }

    private static String NAMESPACE = "http://tempuri.org/";
    private static String URL = "http://sociowebservice.azurewebsites.net/RegAuthenticate.asmx";
    private static String SOAP_ACTION = "http://tempuri.org/";

    public static class AttendanceMarking {

        public static String logAttendance(String latitude, String longitude, String empId, String LogFlag) {

            String resTxt = "this should not come";
            SoapObject request = new SoapObject(NAMESPACE, "logAttendance");
            PropertyInfo sayHelloPI;

            sayHelloPI = new PropertyInfo();
            sayHelloPI.setName("Latitude");
            sayHelloPI.setValue(latitude);
            sayHelloPI.setType(String.class);
            request.addProperty(sayHelloPI);

            sayHelloPI = new PropertyInfo();
            sayHelloPI.setName("Longitude");
            sayHelloPI.setValue(longitude);
            sayHelloPI.setType(String.class);
            request.addProperty(sayHelloPI);

            sayHelloPI = new PropertyInfo();
            sayHelloPI.setName("EmpId");
            sayHelloPI.setValue(empId);
            sayHelloPI.setType(String.class);
            request.addProperty(sayHelloPI);

            sayHelloPI = new PropertyInfo();
            sayHelloPI.setName("LogFlag");
            sayHelloPI.setValue(LogFlag);
            sayHelloPI.setType(String.class);
            request.addProperty(sayHelloPI);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            try {
                androidHttpTransport.call(SOAP_ACTION + "logAttendance", envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

                resTxt = response.toString();

            } catch (Exception e) {
                resTxt = e.getMessage();
            }
            //Return resTxt to calling object
            return resTxt;
        }
    }

    private class AttendanceWS extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            return  AttendanceMarking.logAttendance(params[0],params[1],params[2],params[3] );
        }
    }

    class RetrieveTimeWS extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            try {
                SntpClient currentTime = new SntpClient();

                if (currentTime.requestTime("2.in.pool.ntp.org", 60000)) {
                    lngCurrentTime = currentTime.getNtpTime();

                    TextView TVcurrentTime = (TextView) findViewById(R.id.DateTimeNow);

                    TimestampConvertor tsc = new TimestampConvertor();
                    TVcurrentTime.setText(tsc.usingDateAndCalendarWithTimeZone(lngCurrentTime));
                }
            } catch (Exception e) {
                Log.e("Async Task - Exception ", e.getMessage());
            }
            return null;
        }
    }
}
