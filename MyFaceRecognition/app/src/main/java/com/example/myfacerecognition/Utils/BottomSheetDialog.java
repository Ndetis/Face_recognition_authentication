package com.example.myfacerecognition.Utils;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfacerecognition.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private TextView id_reco,name_reco,surname_reco,matricule_reco,filiere_reco,niveau_reco,transaction_reco,tranche_reco,somme_reco;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout,container, false);

        id_reco = v.findViewById(R.id.id_reco);
        name_reco = v.findViewById(R.id.name_reco);
        surname_reco = v.findViewById(R.id.surname_reco);
        matricule_reco = v.findViewById(R.id.matricule_reco);
        filiere_reco = v.findViewById(R.id.filiere_reco);
        niveau_reco = v.findViewById(R.id.niveau_reco);
        transaction_reco = v.findViewById(R.id.transaction_reco);
        tranche_reco = v.findViewById(R.id.tranche_reco);
        somme_reco = v.findViewById(R.id.somme_reco);

        // Gets the data from the passed bundle
        try {
            Bundle bundle = getArguments();
            String message = bundle.getString("mText");
            String[] res = message.split(",");

            id_reco.setText("Id:"+" "+res[0]);
            name_reco.setText("Nom:"+" "+res[1].toUpperCase(Locale.ROOT));
            surname_reco.setText("Prenom:"+" "+res[2].toUpperCase(Locale.ROOT));
            matricule_reco.setText("Matricule:"+" "+res[3].toUpperCase(Locale.ROOT));
            filiere_reco.setText("Filière:"+" "+res[4].toUpperCase(Locale.ROOT));
            niveau_reco.setText("Niveau:"+" "+res[5].toUpperCase(Locale.ROOT));
            transaction_reco.setText("Transaction:"+" "+res[6].toUpperCase(Locale.ROOT));
            tranche_reco.setText("Tranche:"+" "+res[7].toUpperCase(Locale.ROOT));
            somme_reco.setText("Somme($):"+" "+res[8]);
        }catch (Exception e){
            e.printStackTrace();
        }

        return  v;
    }

}
