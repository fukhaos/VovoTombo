package br.com.munif.vovo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements SensorListener {

    private boolean tomboA = false;
    private boolean tombo = false;
    private float valX[] = new float[100];
    private float valY[] = new float[100];
    private float valZ[] = new float[100];
    private Uri notification;
    private Ringtone r;
    private long ultimoAviso;
    private int avisos = 0;

    private double max = 0;

    private int indice = 0;

    private SensorManager sm = null;
    private SharedPreferences prefs;
    private double latitude;
    private double longitude;
    private Location location;

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciaSensor();

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        ultimoAviso = System.currentTimeMillis();



        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Toast.makeText(this, "O nome é: " + prefs.getString("nomevovo", "Nome não setado"), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "O fone é: " + prefs.getString("telcontato", "Fone não setado"), Toast.LENGTH_SHORT).show();


        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.i("lat", String.valueOf(latitude));
        Log.i("long", String.valueOf(longitude));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ConfigActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void iniciaSensor() {

        if (sm == null) {
            sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sm.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, 100);
        }

    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {

        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            valX[indice] = values[0];
            valY[indice] = values[1];
            valZ[indice] = values[2];
            indice++;
            if (indice == 100) {
                indice = 0;
                max = 0;
                ((TextView) findViewById(R.id.tombo)).setText("de boa " + avisos);
            }
            float sX = 0, sY = 0, sZ = 0;
            for (int i = 0; i < 100; i++) {
                sX += valX[i];
                sY += valY[i];
                sZ += valZ[i];
            }

            ((TextView) findViewById(R.id.textViewX)).setText("" + sX / 100);
            ((TextView) findViewById(R.id.textViewY)).setText("" + sY / 100);
            ((TextView) findViewById(R.id.textViewZ)).setText("" + sZ / 100);
            double media = Math.sqrt(sX * sX + sY * sY + sZ * sZ);
            if (media > max) {
                max = media;
            }
            ((TextView) findViewById(R.id.textViewS)).setText("" + media);
            ((TextView) findViewById(R.id.textViewM)).setText("" + max);

            tomboA = tombo;
            tombo = max > 1100;


            if (tomboA == false && tombo == true && (System.currentTimeMillis() - 10000) > ultimoAviso) {
                enviaSMS();
                toca();
                ultimoAviso = System.currentTimeMillis();
                avisos++;
            }

            if (tombo) {
                ((TextView) findViewById(R.id.tombo)).setText("TOMBO!!" + avisos);
            }

        }


    }

    private void enviaSMS() {

        String nome = prefs.getString("nomevovo", "Nome não setado");
        String fone = prefs.getString("telcontato", "Fone não setado");

        String message = String.format("O vovô(ó) %s sofreu um queda as %s no local http://maps.google.com/?q=%s+%s", nome, nome, String.valueOf(latitude), String.valueOf(longitude));


        String uri = message;
        SmsManager smsManager = SmsManager.getDefault();
        StringBuffer smsBody = new StringBuffer();
        smsBody.append(Uri.parse(uri));
        smsManager.sendTextMessage(fone, null, smsBody.toString(), null, null);

        Toast.makeText(this, "Mensagem enviada com sucesso", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }

    public void toca() {
        try {
            if (!r.isPlaying()) {
                r.play();
            }else{
                r.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
