package com.example.ivan.dailyweather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.IntegerRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private APIDataGetter api;

    static public List<String> cities = new ArrayList<String>() {{
        add("Moscow");
        add("Rio");
        add("Kiev");
    }};


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    public MainActivity() {
        this.api = new APIDataGetter();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        PlaceholderFragment.activity = MainActivity.this;

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private int sunset;
        private int sunrise;

        private static Activity activity;

        View rootView;
        TextView cityTextView;
        ImageView backgroundImageView;
        TextView detailsTextView;
        TextView currentTempTextView;
        TextView iconTextView;
        TextView maxTemp;
        TextView minTemp;
        TextView pressure;
        TextView humidity;
        SwipeRefreshLayout refresher;

        WeatherResponse data;

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onMessageEvent(WeatherUnit response) {
            if (!response.is_completed || response.fragmentID != this.getArguments().getInt(ARG_SECTION_NUMBER))
                return;

            this.data = response.response;
            this.updateUI();

            if (this.data == null && this.isResumed()) {
                if (response.fragmentID == this.getArguments().getInt(ARG_SECTION_NUMBER)) {
                    Toast.makeText(rootView.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void updateUI() {
            refresher.setRefreshing(false);
            if (this.data == null)
                return;

            cityTextView.setText(this.data.getName() + ", " + this.data.getOtherInfo().getCountry());

            backgroundSetter(this.data);

            detailsTextView.setText(this.data.getWeather().get(0).getWeatherInfoMain());

            currentTempTextView.setText(String.valueOf((int) this.data.getMainTemp().getTemp()) + "°C");

            iconSetter(this.data);

            maxTemp.setText(this.data.getMainTemp().getTemp_max() + "°C");

            minTemp.setText(this.data.getMainTemp().getTemp_min() + "°C");

            pressure.setText(String.valueOf("Pressure: " + this.data.getMainTemp().getPressure()));
            humidity.setText(String.valueOf("Humidity: " + this.data.getMainTemp().getHumidity()));
        }

        private void iconSetter(WeatherResponse data) {
            if (data.getWeather().get(0).getWeatherInfoMain().equalsIgnoreCase("clouds") && data.getCloudsClass().getClouds() > 10) {
                iconTextView.setText(R.string.sunny_clouds);
            } else if (data.getWeather().get(0).getWeatherInfoMain().equalsIgnoreCase("Rain") || data.getWeather().get(0).getWeatherInfoMain().equalsIgnoreCase("Thunderstorm")) {
                iconTextView.setText(R.string.rainy);
            } else if (data.getWeather().get(0).getDescription().equalsIgnoreCase("light rain_pic")) {
                iconTextView.setText(R.string.rain_light);
            } else if (data.getWeather().get(0).getWeatherInfoMain().equalsIgnoreCase("Snow")) {
                iconTextView.setText(R.string.snowy);
            } else if (data.getWeather().get(0).getWeatherInfoMain().equalsIgnoreCase("Clear")) {
                iconTextView.setText(R.string.sun_clear);
            }
        }

        public void backgroundSetter(WeatherResponse data) {
            switch (data.getWeather().get(0).getWeatherInfoMain().toLowerCase()) {
                case "clouds":
                case "mist":
                case "smoke":
                    backgroundImageView.setImageResource(R.drawable.cloud_pic);
                    break;

                case "haze":
                    backgroundImageView.setImageResource(R.drawable.haze_pic);
                    break;

                case "thunderstorm":
                case "rain":
                    backgroundImageView.setImageResource(R.drawable.rain_pic);
                    break;

                case "clear":
                    backgroundImageView.setImageResource(R.drawable.sunny_pic);
                    break;

                case "snow":
                    backgroundImageView.setImageResource(R.drawable.snow_pic);
                    break;
            }
        }

        public void updateWeather() {
            int position = this.getArguments().getInt(ARG_SECTION_NUMBER);
            if (position == 0) {
                LocationManager locationManager = (LocationManager) rootView.getContext().getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    EventBus.getDefault().post(new WeatherUnit(position, loc.getLatitude(), loc.getLongitude()));
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
                }
            } else {
                EventBus.getDefault().post(new WeatherUnit(position, cities.get(position - 1)));
            }
        }

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
            EventBus.getDefault().register(this);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);

            cityTextView = rootView.findViewById(R.id.city_text_view);
            backgroundImageView = rootView.findViewById(R.id.background_image_view);
            detailsTextView = rootView.findViewById(R.id.description_text_view);
            currentTempTextView = rootView.findViewById(R.id.temp_text_view);
            iconTextView = rootView.findViewById(R.id.icon_text_view);
            refresher = rootView.findViewById(R.id.swiperefresh);
            maxTemp = rootView.findViewById(R.id.max_temp);
            minTemp = rootView.findViewById(R.id.min_temp);
            pressure = rootView.findViewById(R.id.pressure);
            humidity = rootView.findViewById(R.id.humidity);

            refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateWeather();
                }
            });

            if (this.data == null)
                this.updateWeather();
            this.updateUI();
            return rootView;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {

                // Check if user triggered a refresh:
                case R.id.swiperefresh:
                    ((SwipeRefreshLayout) item).setRefreshing(true);
                    this.updateWeather();
                    return true;
            }

            return super.onOptionsItemSelected(item);

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public List<PlaceholderFragment> fragmentCache = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            if (position >= fragmentCache.size()) {
                PlaceholderFragment pf = PlaceholderFragment.newInstance(position);
                fragmentCache.add(pf);
            }

            return fragmentCache.get(position);
        }

        @Override
        public int getCount() {
            return cities.size() + 1;
        }
    }
}
