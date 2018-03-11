package com.zafirstojanovski.morty.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.zafirstojanovski.morty.AboutInfo.AboutCreator;
import com.zafirstojanovski.morty.AboutInfo.AboutMorty;
import com.zafirstojanovski.morty.R;

import static com.zafirstojanovski.morty.Fragments.ChatFragment.SHARED_PREFERENCES;

/**
 * Created by Zafir Stojanovski on 2/25/2018.
 */

public class SettingsFragment extends Fragment {

    private OnDeleteMessagesListener onDeleteMessagesListener;
    private AlertDialog alertDialog;

    public interface OnDeleteMessagesListener {
        void onDeleteMessages();
    }

    private LinearLayout deleteMessagesListener;
    private LinearLayout aboutMortyListener;
    private LinearLayout aboutCreatorListener;
    private Context context;
    private Activity activity;
    private SharedPreferences sharedPreferences;

    public static final String CENSOR_SWITCH = "com.zafirstojanovski.morty.Fragments.SettingsFragment.CENSOR_SWITCH";
    public static final String SYNC_SWITCH = "com.zafirstojanovski.morty.Fragments.SettingsFragment.SYNC_MESSAGES";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onDeleteMessagesListener = (OnDeleteMessagesListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement OnDeleteMessagesListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public SettingsFragment() {}

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initContext();
        initSwitches();
        initAlertDialog();
        initDeleteMessagesListener();
        initAboutSection();
    }

    private void initContext() {
        this.context = getContext();
        this.activity = getActivity();
        this.sharedPreferences = activity.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    private void initSwitches() {
        Switch censorSwitch = activity.findViewById(R.id.censorSwitch);
        Switch syncSwitch = activity.findViewById(R.id.syncSwitch);

        censorSwitch.setChecked(
                sharedPreferences
                        .getBoolean(CENSOR_SWITCH, true)
        );

        syncSwitch.setChecked(
                sharedPreferences
                        .getBoolean(SYNC_SWITCH, true)
        );

        saveSwitchState(CENSOR_SWITCH, censorSwitch.isChecked());
        saveSwitchState(SYNC_SWITCH, syncSwitch.isChecked());

        censorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
                saveSwitchState(CENSOR_SWITCH, state);
            }
        });

        syncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
                saveSwitchState(SYNC_SWITCH, state);
            }
        });
    }

    private void initAlertDialog(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        onDeleteMessagesListener.onDeleteMessages();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder alertDialogBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alertDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            alertDialogBuilder = new AlertDialog.Builder(context);
        }

        alertDialog = alertDialogBuilder
                .setTitle("Are you sure?")
                .setMessage("This action cannot be undone.")
                .setPositiveButton("Delete", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .create();
    }

    private void initDeleteMessagesListener() {
        deleteMessagesListener = getActivity().findViewById(R.id.deleteMessagesView);
        deleteMessagesListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternetConnection()){
                    alertDialog.show();
                }
                else {
                    Toast.makeText(activity, R.string.connect_to_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveSwitchState(String switchName, Boolean switchState){
        sharedPreferences
                .edit()
                .putBoolean(switchName, switchState)
                .apply();
    }

    private boolean checkInternetConnection(){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private void initAboutSection() {
        aboutMortyListener = getActivity().findViewById(R.id.aboutMorty);
        aboutMortyListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(
                        new Intent(
                                activity,
                                AboutMorty.class
                        )
                );
            }
        });

        aboutCreatorListener = getActivity().findViewById(R.id.aboutCreator);
        aboutCreatorListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(
                        new Intent(
                                activity,
                                AboutCreator.class
                        )
                );
            }
        });
    }
}