package hughpearse.myapplication004;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "TTS-SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Spinner voiceSpinner = findViewById(R.id.voiceSpinner);
        Locale defaultLocale = Locale.getDefault();
        final Locale[] availableLocales = defaultLocale.getAvailableLocales();
        Log.d(TAG, "availableLocales: " + availableLocales.length);

        List<String> voiceSpinnerOptions = new ArrayList<String>();
        for(Locale l : availableLocales){
            voiceSpinnerOptions.add(l.toLanguageTag());
        }
        ArrayAdapter<String> voiceSpinnerAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,voiceSpinnerOptions);
        voiceSpinner.setAdapter(voiceSpinnerAdapter);
        voiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                Locale loc = (Locale)Array.get(availableLocales, (int) l);
                editor.putString("mDefaultLocale", loc.toLanguageTag());
                Log.d(TAG, "saving mDefaultLocale: " + loc.toLanguageTag());
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Locale defaultLocale = Locale.getDefault();
                int offset = Arrays.asList(availableLocales).indexOf(defaultLocale);
                voiceSpinner.setSelection(offset);
            }
        });
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String voiceSpinnerDefault = sharedPref.getString("mDefaultLocale",defaultLocale.toLanguageTag());
        Locale searchLocale = new Locale(voiceSpinnerDefault);
        int offset = Arrays.asList(availableLocales).indexOf(searchLocale);
        voiceSpinner.setSelection(offset);
    }
}
