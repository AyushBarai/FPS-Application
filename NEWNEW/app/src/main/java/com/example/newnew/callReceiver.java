package com.example.newnew;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class callReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context arg0, Intent arg1) {

        if(arg1.getAction().equals("android.intent.action.PHONE_STATE")){

            String state = arg1.getStringExtra(TelephonyManager.EXTRA_STATE);
            Intent local = new Intent(arg0, MainActivity.class);
            local.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            local.setAction("service.to.activity.transfer");
            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                Log.d("TAG", "Inside Extra state off hook");
                String number = arg1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if(number != null){
                    Log.e("TAG", "outgoing number : " + number);
                    local.putExtra("number",number);
                }
            }

            else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                Log.e("TAG", "Inside EXTRA_STATE_RINGING");
                String number = arg1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if(number != null){
                    Log.e("TAG", "incoming number : " + number);
                    local.putExtra("number",number);
                }
            }
            else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Log.d("TAG", "Inside EXTRA_STATE_IDLE");
                String number = arg1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if(number != null){
                    Log.e("TAG", "IDLE number : " + number);
                    local.putExtra("number",number);
                }
            }
            arg0.startActivity(local);

        }
    }
}
