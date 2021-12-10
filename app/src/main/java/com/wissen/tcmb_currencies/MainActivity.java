package com.wissen.tcmb_currencies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {

    // ListView Object In LayoutFile
    ListView lv;
    // ListView Controller Object Which Projects Data
    BaseAdapter ba;

    // This Object Stores Scapped Parts From XML
    // We'll Use This As Our Data Source
    Elements dataSource = new Elements();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.lv);

        // Set Base Adapter To Project Data On ListView

        ba = new BaseAdapter() {
            // How many items will be added to ListView ?
            @Override
            public int getCount() {
                return dataSource.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                // If List Item Is Created For The First Time
                if (convertView == null)
                {
                    convertView = getLayoutInflater().inflate(R.layout.currency_item, null);
                }

                // Access TextViews Which Are Stored On ConvertView
                TextView tvCode = convertView.findViewById(R.id.tvCode);
                TextView tvBuy = convertView.findViewById(R.id.tvBuy);
                TextView tvSell = convertView.findViewById(R.id.tvSell);

                // Modify Their Values

                /*
                DataSource Object Stores Small XML Chunks Like :
                <Currency CrossOrder="0" Kod="USD" CurrencyCode="USD">
                        <Unit>1</Unit>
                        <Isim>ABD DOLARI</Isim>
                        <CurrencyName>US DOLLAR</CurrencyName>
                        <ForexBuying>13.7432</ForexBuying>
                        <ForexSelling>13.7679</ForexSelling>
                        <BanknoteBuying>13.7335</BanknoteBuying>
                        <BanknoteSelling>13.7886</BanknoteSelling>
                        <CrossRateUSD/>
                        <CrossRateOther/>

                </Currency>
                 */


                tvCode.setText( dataSource.get(position).attr("Kod"));
                tvBuy.setText(dataSource.get(position).select("ForexBuying").text());
                tvSell.setText(dataSource.get(position).select("ForexSelling").text());

                // Return Updated (Or Newly Created) ConverView Object
                return convertView;
            }
        };

        // Add This Adapter To ListView
        lv.setAdapter(ba);

        // When UI Composition Created, Call getData Method to Fetch Data And Update UI
        getData();
    }

    void getData()
    {
        // We'll Execute An AsyncTask To Get XML Content From
        // https://www.tcmb.gov.tr/kurlar/today.xml
        // And We2ll collect all <currency>....</currency> chunks To DataSource Object As An ArrayListy
        new AsyncTask<String,String,String>()
        {
            ProgressDialog pd;

            // Before Running Background Job, Create A Progress  Dialog And Show It On Screen
            protected void onPreExecute()
            {
                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("Loading Data");
                pd.setIndeterminate(true);
                pd.show();
            }

            // Background JOB - Fetch Data From tcmb.gov.tr
            protected String doInBackground(String... strings)
            {
                try
                {
                    dataSource = Jsoup
                                    .connect("https://www.tcmb.gov.tr/kurlar/today.xml")
                                    .get()
                                    .select("currency");

                    Log.e("x","Got "+dataSource.size()+" Currencies");
                } catch (Exception e) {
                    Log.e("x","Error Fetching Data : "+e);
                }
                return null;
            }

            // Right After, Background Job Finished
            protected void onPostExecute(String s)
            {
                // If Dialog Is Showing, Dissmiss It
                if (pd.isShowing())
                    pd.dismiss();

                // Update ListView Contents
                ba.notifyDataSetChanged();
            }
        }.execute();
    }










    ////////////////////// STEP 2
    /**
     * Normally This App Loads Data When User Execute The App,
     * But As For Step 2, We'll Add A Menu Action To Refresh The Data
     * 1 - implement onCreateOptionsMenu Method To Add Icon On Action Bar
     * 2 - implement onOptionsItemSelected Method To Determine What will Happen When User clicks The
     * Menu Item
     */
    /*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu
                .add("Refresh")
                .setIcon(android.R.drawable.ic_menu_rotate)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }
    */
    /*
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String itemName = item.getTitle().toString();
        if (itemName.equals("Refresh"))
        {
            getData();
        }
        return super.onOptionsItemSelected(item);
    }

     */
}