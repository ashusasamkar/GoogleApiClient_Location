package ashjadhav.example.com.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // private final String LOG_TAG = "LOG_TAG";
    // private FusedLocationProviderClient mFusedLocationClient;
    private TextView txtOutput;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int MY_REQUEST=101;
    Boolean permission_granted=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        txtOutput = (TextView) findViewById(R.id.txtOutput);

    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        //Log.i(LOG_TAG, "onStart method");
        Toast.makeText(this, "GoogleApiClient connected..... ", Toast.LENGTH_SHORT).show();
    }
    protected void onResume(){
        super.onResume();
        if(permission_granted){
            if(mGoogleApiClient.isConnected()){
                requestLocationUpdates();
            }
        }
    }
    protected void onPause(){
        super.onPause();
        if(permission_granted){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        }
    }

    private void requestLocationUpdates() {
        //1- create a Location Request called mLocationRequest
        //2- set its proirity to high accuracy
        //3- set it to update every second(1000ms)
        //4- call requestLocationUpdates in the Api with this request
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, MY_REQUEST);
            }
            else{
                permission_granted=true;
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void onStop() {
        super.onStop();
        if (permission_granted) {
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
        requestLocationUpdates();

        }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "onLocation Changed", Toast.LENGTH_SHORT).show();
        //String text=location.toString();
        //Toast.makeText(this, "Location : "+text, Toast.LENGTH_SHORT).show();
        //Log.i(LOG_TAG,location.toString());
        txtOutput.setText(Double.toString(location.getLatitude())+","+Double.toString(location.getLongitude()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MY_REQUEST:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission_granted = true;
                }

                else{
                    permission_granted=false;
                    Toast.makeText(this, "This app requires Location Permission to be granted...", Toast.LENGTH_SHORT).show();
            }

                return;

        }
    }
}
