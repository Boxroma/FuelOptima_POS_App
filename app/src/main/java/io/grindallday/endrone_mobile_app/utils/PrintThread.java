package io.grindallday.endrone_mobile_app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.ctk.sdk.PosApiHelper;

import io.grindallday.endrone_mobile_app.R;
import io.grindallday.endrone_mobile_app.model.Product;
import io.grindallday.endrone_mobile_app.model.Sale;
import io.grindallday.endrone_mobile_app.model.Station;
import io.grindallday.endrone_mobile_app.model.User;

public class PrintThread extends Thread {
    private static final String TAG = "PrintThread";
    private boolean printThreadFinished = true;
    private int BatteryV;
    private Context context;
    private Sale sale;
    private User user;
    PosApiHelper posApiHelper = PosApiHelper.getInstance();
    int ret = 4;
    private int RESULT_CODE = 0;

    public boolean isThreadFinished() {
        return printThreadFinished;
    }

    public PrintThread(Context context, Sale sale, User user){
        this.context = context;
        this.sale = sale;
        this.user = user;
    }

    private int getValue() {
        int value = 3;
        return value;
    }

    @SuppressLint("DefaultLocale")
    public void run(){
        Log.d(TAG, "Print_Thread[ run ] run() begin");

        synchronized (this) {
            printThreadFinished = false;

            try{
                ret = posApiHelper.PrintInit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "init code:" + ret);

            ret = getValue();
            Log.d(TAG, "getValue():" + ret);

            posApiHelper.PrintSetGray(ret);
            Log.d(TAG, "PrintSetGray():" );

            ret = posApiHelper.PrintCheckStatus();
            Log.d(TAG, "PrintCheckStatus():" );

            checkStatus(ret);

            Log.d(TAG, "Lib_PrnStart" );

            posApiHelper.PrintSetFont((byte) 24, (byte) 24, (byte) 0x00);
            posApiHelper.PrintBmp(BitmapFactory.decodeResource(context.getResources(), R.drawable.endrone_logo_bw));
            posApiHelper.PrintStr("Endrone Petroleum");
            posApiHelper.PrintStr("Corporation LTD");
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr(String.format("Station: %s", user.getStationName()));
            posApiHelper.PrintStr(String.format("Address: %s", user.getStationAddress()));
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr(String.format("Attendant Name: %s %s", user.getFirstName(), user.getSecondName()));
//            posApiHelper.PrintStr(String.format("Attendant ID: %s", user.getUid()));
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr(String.format("Client Type: %s", sale.getClientType()));
            posApiHelper.PrintStr(String.format("Client Name: %s", sale.getClientName()));
//            posApiHelper.PrintStr(String.format("Client Id: %s", sale.getClientId()));
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr(String.format("Timestamp: %s", sale.getTime().toDate()));
            posApiHelper.PrintStr("================================");
            posApiHelper.PrintStr("Item      Price    Quant   Total");
            posApiHelper.PrintStr("================================");
            // For loop
            for(Product product : sale.getProductList()){
                posApiHelper.PrintStr(String.format("%s %,.2f %,.2f %,.2f",product.getName(),product.getPrice(),product.getQuantity(),(product.getQuantity() * product.getPrice())));
            }
            posApiHelper.PrintStr("================================");
            posApiHelper.PrintStr(String.format("Total Price:          %,.2f", sale.getTotal()));
            posApiHelper.PrintStr(String.format("Before Tax:          %,.2f", sale.getTotal()-(sale.getTotal()*0.16)));
            posApiHelper.PrintStr(String.format("Tax VAT:          %,.2f", sale.getTotal()*0.16));
            posApiHelper.PrintStr("================================");
            posApiHelper.PrintStr(String.format("Total Price Paid: %,.2f", sale.getTotal()));
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr("\n");
            posApiHelper.PrintStr("\n");

            Log.d(TAG, "Printing");

            final long starttime_long = System.currentTimeMillis();
            ret = posApiHelper.PrintStart();
            Log.e(TAG, "PrintStart ret = " + ret);

            //msg1.what = ENABLE_RG;

            //handler.sendMessage(msg1);

            checkStatus(ret);

        }
    }

    private void checkStatus(int ret){
        if (ret == -1) {
            RESULT_CODE = -1;
            Log.d(TAG, "Lib_PrnCheckStatus fail, ret = " + ret);
            Toast.makeText(context,"Error, No Paper ",Toast.LENGTH_SHORT).show();
            printThreadFinished = true;
        } else if (ret == -2) {
            RESULT_CODE = -1;
            Log.d(TAG, "Lib_PrnCheckStatus fail, ret = " + ret);
            Toast.makeText(context,"Error, Printer Too Hot ",Toast.LENGTH_SHORT).show();
            printThreadFinished = true;
        } else if (ret == -3) {
            RESULT_CODE = -1;
            Log.d(TAG, "voltage = " + (BatteryV * 2));
            Toast.makeText(context,"Battery less :" + (BatteryV * 2),Toast.LENGTH_SHORT).show();
            printThreadFinished = true;
        }
        else
        {
            RESULT_CODE = 0;
        }
    }

}
