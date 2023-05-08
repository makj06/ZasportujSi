package com.example.myapplication.Adaptery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatoveTypy.Uzivatel;
import com.example.myapplication.DatoveTypy.Zprava;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MujViewHolder3>{

    //Inicializace proměnných
    private Context context;
    private List<Zprava> zpravaList;
    private String IDOdesilatele;

    private static final int zpravaOdeslana = 1;
    private static final int zpravaPrijata = 2;

    //Konstruktor
    public ChatAdapter(Context context, List<Zprava> zpravaList, String IDOdesilatele) {
        this.context = context;
        this.zpravaList = zpravaList;
        this.IDOdesilatele=IDOdesilatele;
    }

    /**
     * Metoda, která dává jednotlivé listy do recycler view
     * @param parent Rodič listu
     * @return
     */

    @NonNull
    @Override
    public ChatAdapter.MujViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == zpravaOdeslana){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zprava_odeslana_list, parent, false);
            return new ChatAdapter.MujViewHolder3(v);
        } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.zprava_prijata_list, parent, false);
                return new ChatAdapter.MujViewHolder3(v);
        }
    }

    /**
     * Metoda, která je zodpovědná za dávání dat do jednotlivých listů
     * @param holder
     * @param position
     */

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MujViewHolder3 holder, @SuppressLint("RecyclerView") int position) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("Uživatelé").document(zpravaList.get(position).getIDOdesilatele()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Uzivatel uzivatel = task.getResult().toObject(Uzivatel.class);
                if (uzivatel!=null) {


                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm d.MM.yyyy", Locale.getDefault());
                    String datum = dateFormat.format(zpravaList.get(position).getOdeslano());

                    holder.zprava.setText(zpravaList.get(position).getText());
                    holder.datum.setText(datum);
                    holder.jmeno.setText(uzivatel.getJmeno() + " " + uzivatel.getPrijmeni());
                } else {
                    Toast.makeText(context, "zprávy nebylo možné načíst", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Metoda, která vrací styl listu, který se používá v recycler view
     * @param position poloha zprávy v poli
     * @return druh layoutu
     */
    @Override
    public int getItemViewType(int position) {
        if (zpravaList.get(position).getIDOdesilatele().equals(IDOdesilatele)) {
            return zpravaOdeslana;
        } else {
            return zpravaPrijata;
        }

    }

    /**
     * Metoda, která vrací velikost listu
     * @return velikost
     */
    @Override
    public int getItemCount() {
        return zpravaList.size();
    }


    public static class MujViewHolder3 extends RecyclerView.ViewHolder {

        TextView zprava,datum, jmeno;

        public MujViewHolder3(@NonNull View itemView) {
            super(itemView);

            zprava = itemView.findViewById(R.id.zpravaTextTV);
            datum = itemView.findViewById(R.id.zpravaCas);
            jmeno = itemView.findViewById(R.id.zpravaJmenoTV);
        }
    }
}

