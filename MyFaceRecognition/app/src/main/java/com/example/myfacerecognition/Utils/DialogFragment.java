package com.example.myfacerecognition.Utils;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfacerecognition.R;

public class DialogFragment extends android.app.DialogFragment {

    private static final String TAG = "DialogFragment";

    public interface OnInputListener {
        void sendInput(String input);
    }
    public OnInputListener mOnInputListener;

    private EditText ue_code_head,filiere_head,intitule_head,semestre_head,annee_head,grade_head;
    private Button button_head;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.header_dialog_fragment, container, false);

        ue_code_head = view.findViewById(R.id.ue_code_head);
        filiere_head = view.findViewById(R.id.filiere_head);
        intitule_head = view.findViewById(R.id.intitule_head);
        semestre_head = view.findViewById(R.id.semestre_head);
        annee_head = view.findViewById(R.id.annee_head);
        grade_head = view.findViewById(R.id.grade_head);
        button_head =view.findViewById(R.id.head_button);

        button_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ue_code_head.getText().toString().isEmpty()||filiere_head.getText().toString().isEmpty()||semestre_head.getText().toString().isEmpty()||intitule_head.getText().toString().isEmpty()||annee_head.getText().toString().isEmpty()||grade_head.getText().toString().isEmpty()){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        Toast.makeText(getContext(), "Empty field...please fill all field", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Log.d(TAG, "onClick: capturing input");
                    String input = ue_code_head.getText().toString()+";"+filiere_head.getText().toString() +";"+intitule_head.getText().toString()+";"+semestre_head.getText().toString()+";"+annee_head.getText().toString()+";"+grade_head.getText().toString()+";"+".";
                    mOnInputListener.sendInput(input);
                    getDialog().dismiss();
                }
            }
        });

        return  view;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputListener = (OnInputListener)getActivity();
        }
        catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: "+ e.getMessage());
        }
    }
}
