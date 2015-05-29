package com.example.administrator.STUM;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class WeightDialog extends DialogFragment implements View.OnClickListener{

    EditText weight;
    CheckBox sports, weather;
    Button cancel, change;
    String input;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.userdrink2, null);
        sports = (CheckBox) alertLayout.findViewById(R.id.sports);
        weather = (CheckBox) alertLayout.findViewById(R.id.weather);
        weight = (EditText) alertLayout.findViewById(R.id.weight_input);

        cancel = (Button) alertLayout.findViewById(R.id.cancel);
        change = (Button) alertLayout.findViewById(R.id.change);

        builder.setTitle("체중 변경");
        builder.setView(alertLayout);
        change.setOnClickListener(this);
        cancel.setOnClickListener(this);

        AlertDialog dialog = builder.create();
        return dialog;
    }

    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.change:

                //String value = weight.getText().toString();
                //Toast.makeText(, "Username: " + value, Toast.LENGTH_SHORT).show();

                input = weight.getText().toString();
                if(input.equals("")){
                    break;
                }

                else {
                    int water_size_result = Integer.parseInt(weight.getText().toString()) * 31;

                    if (sports.isChecked()) {
                        water_size_result = (water_size_result / 100) * 150;
                    }
                    if (weather.isChecked()) {
                        water_size_result = (water_size_result / 100) * 110;
                    }
                    upload(water_size_result);

                }

                dismiss();
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    void upload(int water_size_result){

        ParseObject DrinkValues = new ParseObject("UserDrink");//파스 오브젝트 생성
        ParseUser user = ParseUser.getCurrentUser();
        DrinkValues.put("User", user);
        DrinkValues.put("Drink", water_size_result);

        DrinkValues.saveInBackground();
    }

    @Override
    public void onStart() {
        super.onStart();

        //Window window = getDialog().getWindow();
        //window.setBackgroundDrawableResource(android.R.color.transparent);

        final Resources res = getResources();
        final int blue = res.getColor(R.color.myBlue);
        final int black = res.getColor(R.color.gray1);


        // Title
        final int titleId = res.getIdentifier("alertTitle", "id", "android");
        final View title = getDialog().findViewById(titleId);
        if (title != null) {
            ((TextView) title).setTextColor(black);
        }


        // Title divider
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        final View titleDivider = getDialog().findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(blue);
        }
    }
}
