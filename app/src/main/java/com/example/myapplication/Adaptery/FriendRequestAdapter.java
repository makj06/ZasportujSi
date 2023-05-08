package com.example.myapplication.Adaptery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatoveTypy.Uzivatel;
import com.example.myapplication.Interface.KlikatelnyRVInterface;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FriendRequestAdapter  extends RecyclerView.Adapter<FriendRequestAdapter.MujViewHolder6>{

    private final KlikatelnyRVInterface klikatelnyRVInterface;
    private Context context;
    private List<Uzivatel> uzivatelList;


    public FriendRequestAdapter(KlikatelnyRVInterface klikatelnyRVInterface, Context context, List<Uzivatel> uzivatelList) {
        this.klikatelnyRVInterface = klikatelnyRVInterface;
        this.context = context;
        this.uzivatelList = uzivatelList;
    }

    @NonNull
    @Override
    public FriendRequestAdapter.MujViewHolder6 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_list,parent,false);
        return new FriendRequestAdapter.MujViewHolder6(v,klikatelnyRVInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestAdapter.MujViewHolder6 holder, @SuppressLint("RecyclerView") int position) {

        String userID = uzivatelList.get(position).getUserID();
        Uzivatel uzivatel = uzivatelList.get(position);

        holder.nazev.setText(uzivatelList.get(position).getJmeno() + " "+uzivatelList.get(position).getPrijmeni());

        holder.odebratBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();

                fStore.collection("Uživatelé").document(fAuth.getCurrentUser().getUid()).collection("FriendRequest").document(userID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            uzivatelList.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Pozvánka byla odebrána", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(context, "Nepodařilo se odebrat pozvánku. zkuste později", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        holder.pridatBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();

                fStore.collection("Uživatelé").document(fAuth.getCurrentUser().getUid()).collection("FriendList").document(userID).set(uzivatel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            fStore.collection("Uživatelé").document(userID).collection("FriendList").document(fAuth.getCurrentUser().getUid()).set(uzivatel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        fStore.collection("Uživatelé").document(fAuth.getCurrentUser().getUid()).collection("FriendRequest").document(userID).delete();
                                        uzivatelList.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Uživatel by přidán", Toast.LENGTH_SHORT).show();
                                    }else {
                                        fStore.collection("Uživatelé").document(fAuth.getCurrentUser().getUid()).collection("FriendList").document(userID).delete();
                                        Toast.makeText(context, "Nebylo možné přidat uživatele. Zkuste prosím později.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(context, "Nebylo možné přidat uživatele. Zkuste prosím později.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return uzivatelList.size();
    }

    public class MujViewHolder6 extends RecyclerView.ViewHolder {

        private TextView nazev;
        private Button pridatBTN, odebratBTN;

        public MujViewHolder6(@NonNull View itemView, KlikatelnyRVInterface klikatelnyRVInterface) {
            super(itemView);

            nazev = itemView.findViewById(R.id.FRnazev);
            pridatBTN = itemView.findViewById(R.id.FRPridatBTN);
            odebratBTN = itemView.findViewById(R.id.FROdebratBTN);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(klikatelnyRVInterface != null){
                        int pozice = getAdapterPosition();

                        if (pozice != RecyclerView.NO_POSITION){
                            klikatelnyRVInterface.onItemClick(pozice);
                        }
                    }

                }
            });
        }
    }
}

