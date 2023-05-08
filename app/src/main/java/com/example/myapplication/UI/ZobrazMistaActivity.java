package com.example.myapplication.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.Adaptery.MistoAdapter;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.DatoveTypy.Misto;
import com.example.myapplication.Interface.KlikatelnyRVInterface;
import com.example.myapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ZobrazMistaActivity extends AppCompatActivity implements KlikatelnyRVInterface {


    private ArrayList<Misto> mistoArrayList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private Handler zobrazMistaHandler = new Handler();
    private MistoAdapter mistoAdapter;
    private RecyclerView mistaRV;
    private TextView nazev;
    private ImageView zpetIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zobraz_mista);

        String sport = getIntent().getStringExtra("sport");

        mistaRV = findViewById(R.id.MistaRV);
        nazev = findViewById(R.id.mistaNazev);
        zpetIV = findViewById(R.id.mistaZpetIV);

        switch (sport){
            case "fotbalové hřiště":
                nazev.setText("Fotbalové hřiště");
                break;
            case "multifunkční hala":
                nazev.setText("Florbalové haly");
                break;
            case "basketbalové hřiště":
                nazev.setText("Basketbalové hřiště");
                break;
            case "tenisové hřiště":
                nazev.setText("Tenisové hřiště");
                break;
        }

        //zprovoznení imageview pro přechod zpět
        zpetIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZobrazMistaActivity.this.finish();
            }
        });

        zprovozniRV();
        new fentchData().start();

    }


    /**
     * Metoda, která má za úkol zprovoznit recycler view.
     */
    private void zprovozniRV() {
        mistoArrayList = new ArrayList<>();
        mistoAdapter = new MistoAdapter(ZobrazMistaActivity.this,mistoArrayList,this);
        mistaRV.setHasFixedSize(true);
        mistaRV.setLayoutManager(new LinearLayoutManager(this));
        mistaRV.setAdapter(mistoAdapter);
    }

    /**
     * Metoda, která se provede při kliknutí na jednotlivé listy z recycler vievu
     * @param pozice
     */
    @Override
    public void onItemClick(int pozice) {
        String ID = mistoArrayList.get(pozice).getMistoID();

        Intent intent = new Intent(ZobrazMistaActivity.this,MistoDetailyActivity.class);

        intent.putExtra("id",ID);

        startActivity(intent);

    }

    /**
     * Vnořená třída, která zabezpečuje vyhledání dat z Places api.
     */
    class fentchData extends Thread {

        String data = "";
        String sport = getIntent().getStringExtra("sport");
        String vzdalenost = getIntent().getStringExtra("vzdalenost");
        Double zemDelka = getIntent().getDoubleExtra("zemDelka",0);
        Double zemSirka = getIntent().getDoubleExtra("zemSirka",0);
        String key = BuildConfig.placesKey;

        //Spuštění vyhledávání
        @Override
        public void run() {
            try {
                zobrazMistaHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog = new ProgressDialog(ZobrazMistaActivity.this);
                        progressDialog.setMessage("Data se načítají");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                    }
                });

                //Získání dat z google api
                URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ zemSirka + ","+ zemDelka +"&radius=" + vzdalenost + "&keyword=" + sport + "&key="+key);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                String pagetoken;

                //Uložení do proměnné data
                while ((line = bufferedReader.readLine()) != null) {
                    data = data + line;
                }

                //Pokud data nejsou prázdní, tak se zpracujou. A vytvoří se list míst, které se následně nahrají do Recycler view
                if (!data.isEmpty()) {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    mistoArrayList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectZArr = jsonArray.getJSONObject(i);

                        Integer pocetRecenzi = jsonObjectZArr.getInt("user_ratings_total");

                        if (pocetRecenzi > 1) {
                            String nazev = jsonObjectZArr.getString("name");
                            String documentID = jsonObjectZArr.getString("place_id");
                            String poloha = jsonObjectZArr.getString("vicinity");

                            Misto misto = new Misto(nazev, poloha, documentID);

                            mistoArrayList.add(misto);
                        }
                    }
                    pagetoken = jsonObject.getString("next_page_token");

                    if(pagetoken != null){
                        //Je nutné na chvíli uspat vlkáno, protože google api nepodporuje posílání requestů rychle za sebou.
                        sleep(1000);
                    }

                    //V případě, že MistoArraylist je menší než 40 nebo pagetoken se nerovná 40, tak se nenačnou už další data.
                    donactiData(pagetoken, jsonObject);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
            }

            //Ukončení vlákna
            zobrazMistaHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        mistoAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        /**
         * Metoda, která je odpovědná za donačtení dat. Data se donačnou v přápadě, že pagetoken není null
         * nebo počet záznamů v arrayListu je menší než 40.
         */
        private void donactiData(String pagetoken, JSONObject jsonObject) throws IOException, JSONException {
            if (pagetoken != null) {
                for (int i3 = 0; i3 < 2; i3++) {
                    if (pagetoken != null) {
                        URL url2 = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=" + pagetoken + "&key="+key);
                        HttpURLConnection httpURLConnection2 = (HttpURLConnection) url2.openConnection();
                        InputStream inputStream2 = httpURLConnection2.getInputStream();
                        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream2));
                        String line2;
                        String data2 = "";

                        while ((line2 = bufferedReader2.readLine()) != null) {
                            data2 = data2 + line2;
                        }

                        if (!data2.isEmpty()) {
                            JSONObject jsonObject2 = new JSONObject(data2);
                            JSONArray jsonArray2 = jsonObject2.getJSONArray("results");
                            for (int i1 = 0; i1 < jsonArray2.length(); i1++) {
                                JSONObject jsonObjectZArr = jsonArray2.getJSONObject(i1);

                                Integer pocetRecenzi = jsonObjectZArr.getInt("user_ratings_total");

                                if (pocetRecenzi > 1) {
                                    String nazev = jsonObjectZArr.getString("name");
                                    String documentID = jsonObjectZArr.getString("place_id");
                                    String poloha = jsonObjectZArr.getString("vicinity");

                                    Misto misto = new Misto(nazev, poloha, documentID);

                                    mistoArrayList.add(misto);
                                }
                            }
                            pagetoken = jsonObject.getString("next_page_token");
                        }

                    }
                }
            }
        }
    }
}