package hughpearse.myapplication004;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by HUGHPearse on 04/02/2018.
 */

public class ExtractFromTxt {

    private static final String TAG = "TTS-ExtractFromTxt";

    /**
     *
     * @param fileName
     * @return ArrayList<String> sentences
     * @throws Exception
     */
    public ArrayList<String> extract(String fileName){
        Log.i(TAG, "Parsing txt file");
        String data = "";
        ArrayList<String> sentences = new ArrayList<String>();
        try {
            FileInputStream fstream = new FileInputStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    data += line + "\n";
                }
            } finally {
                br.close();
            }
        } catch(Exception e){
            Log.i(TAG,e.toString());
        }
        //use positive look behind regular expression to split file in to sentences
        sentences = new ArrayList<>(Arrays.asList(data.split("(?<=[\\.])|(?=[\n])|(?<=[\n])")));
        return sentences;
    }
}
