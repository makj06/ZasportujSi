package com.example.myapplication.Adaptery;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatoveTypy.Uzivatel;
import com.example.myapplication.Interface.KlikatelnyRVInterface;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class UzivatelAdapter extends RecyclerView.Adapter<UzivatelAdapter.mujViewHolder2> {

    //Inicializace proměnných
    private final KlikatelnyRVInterface klikatelnyRVInterface;
    private Context context;
    private List<Uzivatel> uzivatelList;

    public UzivatelAdapter(KlikatelnyRVInterface klikatelnyRVInterface, Context context, List<Uzivatel> uzivatelList) {
        this.klikatelnyRVInterface = klikatelnyRVInterface;
        this.context = context;
        this.uzivatelList = uzivatelList;
    }

    @NonNull
    @Override
    public mujViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.uzivatel_list_view,parent,false);
        return new mujViewHolder2(v,klikatelnyRVInterface);

    }

    @Override
    public void onBindViewHolder(@NonNull mujViewHolder2 holder, int position) {
        holder.jmeno.setText(uzivatelList.get(position).getJmeno() + " " + uzivatelList.get(position).getPrijmeni());

        FirebaseStorage fStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = fStorage.getReference();

        StorageReference obrazekReference = storageRef.child("profiloveObrazky/"+ uzivatelList.get(position).getUserID() + ".jpg");

        try {
            File obrazek = File.createTempFile("profiloveObrazky","jpg");
            obrazekReference.getFile(obrazek).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitMap = BitmapFactory.decodeFile(obrazek.getAbsolutePath());
                    holder.profObr.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    holder.profObr.setImageBitmap(bitMap);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return uzivatelList.size();
    }



    public final class mujViewHolder2 extends RecyclerView.ViewHolder{

        private TextView jmeno;
        private ImageView profObr;


        public mujViewHolder2(@NonNull View itemView, KlikatelnyRVInterface klikatelnyRVInterface) {
            super(itemView);

            jmeno = itemView.findViewById(R.id.kamaradListJmenoTV);
            profObr = itemView.findViewById(R.id.kamaradListIV);

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
