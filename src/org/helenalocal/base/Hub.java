package org.helenalocal.base;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Created by abbie on 1/24/14.
 */
public abstract class Hub implements IHub {
    private String filename ="hl-out.txt";
    protected Calendar lastRefreshTS;
    public static final int CSA = 1;
    public static final int GROWER = 2;
    public static final int SALES = 3;
    public static final int MOCK = 4;
    public static final String BACKEND = "BACKEND";
    public static final String FRONTEND = "FRONTEND";

    private Product createProduct(String receiveString) {
        Product out = new Product();
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
        simpleStringSplitter.setString(receiveString);
        Iterator<String> iterator = simpleStringSplitter.iterator();

        // String productDesc, Integer unitsAvailable, Double unitPrice, String unitDesc, String note
        if (iterator.hasNext()) {
            out.setProductDesc(iterator.next());
        }
        if (iterator.hasNext()) {
            out.setUnitsAvailable(Integer.valueOf(iterator.next()));
        }
        if (iterator.hasNext()) {
            Object o = iterator.next().replace("$", "");
            out.setUnitPrice(Double.parseDouble(o.toString()));
        }
        if (iterator.hasNext()) {
            out.setUnitDesc(iterator.next());
        }
        if (iterator.hasNext()) {
            out.setNote(iterator.next());
        }
        return out;
    }

    protected void setFilename(String filename) {
        this.filename = filename;
    }

    protected void writeToFile(Context context, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(Hub.BACKEND, "File (" + filename + ") write failed: " + e.toString());
        }
    }

    protected void writeToFile(Context context,BufferedReader rd) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            String line = "";
            while ((line = rd.readLine()) != null) {
                outputStreamWriter.write(line +'\n');
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(Hub.BACKEND, "File (" + filename + ") write failed: " + e.toString());
        }
    }

    protected ArrayList<Product> readFromFile(Context context) {
        ArrayList<Product> myProducts = new ArrayList<Product>();
        try {
            // get the time the file was last changed here
            File myFile = new File(context.getFilesDir() +"/" + filename);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            String lastRefreshTSStr = sdf.format(myFile.lastModified());
            Log.w(Hub.BACKEND, "Using file (" + filename + ") last modified on : " + lastRefreshTSStr);
            lastRefreshTS = sdf.getCalendar();

            // create products from the file here
            InputStream inputStream = context.openFileInput(filename);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                boolean firstTime = true;
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    if (firstTime) {
                        // remove header
                        firstTime = false;
                    } else {
                        // build products
                        myProducts.add(createProduct(receiveString));
                    }
                }
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e(Hub.BACKEND, "File  (" + filename + ") not found: " + e.toString());
        } catch (IOException e) {
            Log.e(Hub.BACKEND, "Can not read file  (" + filename + ") : " + e.toString());
        }
        Log.w(Hub.BACKEND, "Number of products loaded: " + myProducts.size());
        return myProducts;
    }

    public Calendar getLastRefreshTS() {
        return lastRefreshTS;
    }
}
