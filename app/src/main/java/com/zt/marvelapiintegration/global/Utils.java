package com.zt.marvelapiintegration.global;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String DATEFORMAT_DD_MM_YYYY = "dd-MM-yyyy";
    public static final String DDMMYYYY = "dd/MM/yyyy";
    public static final String HHMMA = "hh:mm a";
    public static final String HHMMSS = "hh:mm:ss";
    public static final String DD_MM_YYYY_HH_MM_SS = "dd-MM-yyyy_HH:mm:ss";
    public static final String DD_MMM = "dd MMM";
    public static final String EEE = "EEE";
    public static final String EEEE = "EEEE";
    public static final boolean isDebugging = false;

    public static Typeface getFonts(Activity act) {
        Typeface tf = Typeface.createFromAsset(act.getAssets(),
                "font/Roboto-CondensedItalic.ttf");
        return tf;
    }

    public static Typeface getAwesomeFonts(Activity act) {
        Typeface tf = Typeface.createFromAsset(act.getAssets(), "fontawesome_webfont.ttf");
        return tf;
    }


    public static Typeface getUbuntuFonts(Activity act) {
        Typeface tf = Typeface.createFromAsset(act.getAssets(), "ubuntu_regular.ttf");
        return tf;
    }


    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static boolean isGPSOn(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }

    public static boolean compareDates(String strStartDate, String strEndDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATEFORMAT_DD_MM_YYYY);
            Date date1 = formatter.parse(strStartDate);
            Date date2 = formatter.parse(strEndDate);
            if (date1.compareTo(date2) < 0) {
                return true;
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(HHMMSS);
        try {
            Date date = new Date();
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DDMMYYYY);
        try {
            Date date = new Date();
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS);
        try {
            Date date = new Date();
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDate(int amount) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, amount);
            Date date = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT_DD_MM_YYYY);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDateMonth(int amount) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, amount);
            Date date = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(DD_MMM);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDayName(int amount) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, amount);
            Date date = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(EEE);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDayNameFull(int amount) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, amount);
            Date date = calendar.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat(EEEE);
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static String getUnixTime(String strDate) {
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DDMMYYYY);
        try {
            Date date = sdf.parse(strDate);
            System.out.println(date);
            long unixTime = (long) date.getTime() / 1000;
            return unixTime + "";
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return "";
    }

    public static String getUnixToDateTime(String strDate) {
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        long unixTime = Long.parseLong(strDate);
        Date date = new Date(unixTime * 1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat(DDMMYYYY);

        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static String convertDate(String strDate) {
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("yyyy-MM-dd");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertDatePickerDate(String strDate) {
        //Wed Feb 21 00:00:00 GMT+05:30 2018
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("dd-MM-yyyy");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String flightCheckoutDate(String strDate) {
        //06 Apr, 2018 17:10
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd MMM, yyyy HH:mm");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("HH:mm");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertDatePickerDateMonth(String strDate) {
        //Wed Feb 21 00:00:00 GMT+05:30 2018
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat(DD_MMM);
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertDatePickerDay(String strDate) {
        //Wed Feb 21 00:00:00 GMT+05:30 2018
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat(EEE);
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertDatePickerDayMonthDate(String strDate) {
        //Wed Feb 21 00:00:00 GMT+05:30 2018
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("EEE, MMM dd");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertMonth(String strDate) {
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("dd-MMM-yyyy");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertMonthDate(String strDate) {
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("MMM-dd");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertMonthDate2(String strDate) {
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("MMM dd");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertMonthDateHotel(String strDate) {
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("MMM dd");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertSingleDateHotel(String strDate) {
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("dd");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertMonthDateFlightDetails(String strDate) {
        //22-03-2018
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("MMMM dd");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertFullDayFlightDetails(String strDate) {
        //20 Mar, Tue 2018"
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd MMM, EEE yyyy");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("EEEE, MMM dd");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertDateMonth(String strDate) {
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("dd-MMM");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertDateMonth2(String strDate) {
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("dd-MMM");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertBookingDate(String strDate) {
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("EEE, dd MMM yyyy");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertTime(String strDate) {
        if (strDate == null || strDate.trim().length() == 0) {
            return "";
        }
        try {
            SimpleDateFormat spf = new SimpleDateFormat("HH:mm");
            Date newDate = spf.parse(strDate);
            spf = new SimpleDateFormat("HH");
            String finalDate = spf.format(newDate);
            return finalDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // Email Validation
    public static boolean eMailValidation(String emailstring) {
        if (TextUtils.isEmpty(emailstring)) {
            return false;
        }
        Pattern emailPattern = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher emailMatcher = emailPattern.matcher(emailstring);
        return emailMatcher.matches();
    }

    // Password Validation
    public static boolean passValidation(String strPass) {
        if (TextUtils.isEmpty(strPass)) {
            return false;
        }
        return strPass.length() > 5;
    }

    public static String getDeviceID(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    public static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public static boolean isNotEmptyString(String str) {
        if (str != "" && str != null && str.trim().length() > 0 && !str.equalsIgnoreCase("null")) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmptyArrayList(ArrayList<HashMap<String, String>> arrayList) {
        if (arrayList.size() > 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmptySimpleList(List<String> simpleList) {
        if (simpleList.size() > 0) {
            return true;
        }
        return false;
    }

    public static boolean isNotEmptyHashMap(HashMap<String, String> hashMap) {
        if (hashMap != null && hashMap.size() > 0) {
            return true;
        }
        return false;
    }

    public static void showSnackBar(CoordinatorLayout coordinatorLayout, String strMsg) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, strMsg, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showToast(Context activity, String strMsg) {
        Toast toast = Toast.makeText(activity, strMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public static void showToastLong(Context context, String strMsg, int timeInSecond) {
        final Toast toast = Toast.makeText(context, strMsg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, timeInSecond);
    }

    public static void showAlertDialog(Context context, String title, String msg) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        if (!title.equals("")) {
            adb.setTitle(title);
        }
        adb.setMessage(msg);
        adb.setCancelable(false);
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.create().show();
    }

    public static JSONArray removeJsonObject(JSONArray jsonArray, String strId) {
        JSONArray jsonArray1 = new JSONArray();
        try {

            for (int k = 0; k < jsonArray.length(); k++) {
                JSONObject jsonObject = jsonArray.getJSONObject(k);
                if (!jsonObject.getString("reply_id").equals(strId)) {
                    jsonArray1.put(jsonObject);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray1;
    }

    public static String randomString(int k) {
        final String ALLOWED_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(k);
        for (int i = 0; i < k; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();

    }

    public static boolean isAppInstalled(Context context, String name) {
        try {
            context.getPackageManager().getApplicationInfo(name, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String formattedNumber(String str) {
        if (str == "" || str == null || str.equalsIgnoreCase("null") || str.trim().length() == 0) {
            return "";
        }
        String strNumber = PhoneNumberUtils.formatNumber(str);
        return strNumber;
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static String local(String latitudeFinal, String longitudeFinal) {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + latitudeFinal + "," + longitudeFinal + "&zoom=18&size=280x280&markers=color:red|" + latitudeFinal + "," + longitudeFinal;
    }

    public static CharSequence convertTimeStamp(String str) {
        return DateUtils.getRelativeTimeSpanString(Long.parseLong(str), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static String decimalFormat(String str) {
        if (str == "" || str == null || str.equalsIgnoreCase("null")
                || str.trim().length() == 0) {
            return "";
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        double db = Double.parseDouble(str);
        return decimalFormat.format(db);
    }

    public static String planString(String str) {
        if (str == "" || str == null || str.equalsIgnoreCase("null")
                || str.trim().length() == 0) {
            return "";
        }
        return str.replaceAll("[^0-9]", "");
    }

    public static String base64Encode(String str) {
        try {
            if (str == "" || str == null || str.equalsIgnoreCase("null")
                    || str.trim().length() == 0) {
                return "";
            }
            byte[] data = str.getBytes("UTF-8");
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            return base64;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void openUrl(Activity activity, String url) {
        if (url == "" || url == null || url.equalsIgnoreCase("null")
                || url.trim().length() == 0) {
            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

    public static String bitmapToString(Bitmap bmp) {
        if (bmp == null) {
            return "";
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static String MD5String(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}
