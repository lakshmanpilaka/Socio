package tds.socio;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by laks on 12-03-2015.
 */
public class OfferService extends Service{
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {

        //Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG).show();
        new AsyncGetEmpDetail().execute();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        //Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDestroy() {
       // Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
    }

    private static String NAMESPACE = "http://tempuri.org/";
    private static String URL = "http://sociowebservice.azurewebsites.net/GenMethods.asmx";
    private static String SOAP_ACTION = "http://tempuri.org/";

    public static class getEmployeeOffers {

        public static Void getOffers(Integer EmpId, Integer LastOfferId ) {

            try {

                SoapObject request = new SoapObject(NAMESPACE, "getOffers");
                PropertyInfo sayHelloPI;

                sayHelloPI = new PropertyInfo();
                sayHelloPI.setName("EmpId");
                sayHelloPI.setValue(EmpId);
                sayHelloPI.setType(Integer.class);
                request.addProperty(sayHelloPI);

                sayHelloPI = new PropertyInfo();
                sayHelloPI.setName("LastOfferId");
                sayHelloPI.setValue(LastOfferId);
                sayHelloPI.setType(Integer.class);
                request.addProperty(sayHelloPI);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
                androidHttpTransport.call(SOAP_ACTION + "getOffers", envelope);
                SoapObject response = (SoapObject) envelope.bodyIn;
                SoapObject array = (SoapObject) response.getProperty(0);

                new addOffersByParseXML(array.toString().replace("anyType{offerString=","").replace("; }",""));

            }
            catch (Exception e) {
                Log.e("List:", e.getMessage());
            }
            return null;
        }
    }

    private class AsyncGetEmpDetail extends AsyncTask<Void,Void,Integer> {


        protected AsyncGetEmpDetail( ) {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Integer doInBackground(Void... params) {

            getEmployeeOffers.getOffers(1,0);
            return 1;
        }


        protected void onPostExecute(Integer result) {

        }

    }
}
