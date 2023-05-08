package com.example.myapplication.Adaptery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatoveTypy.Udalost;
import com.example.myapplication.Interface.KlikatelnyRVInterface;
import com.example.myapplication.R;

import java.util.List;

/**
 * Třída, která uchovává v sobě data pro recycled view
 */

public class UdalostAdapter extends RecyclerView.Adapter<UdalostAdapter.MujViewHolder> {

    //Inicializace proměnných
    private final KlikatelnyRVInterface klikatelnyRVInterface;
    private Context context;
    private List<Udalost> udalostList;

    //Konstruktor
    public UdalostAdapter(KlikatelnyRVInterface klikatelnyRVInterface, Context context, List<Udalost> udalostList) {
        this.klikatelnyRVInterface = klikatelnyRVInterface;
        this.context = context;
        this.udalostList = udalostList;
    }

    /**
     * Metoda, která dává jednotlivé listy do recycler view
     * @param parent Rodič listu
     * @return
     */

    @NonNull
    @Override
    public UdalostAdapter.MujViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.udalost_list_view, parent, false);
        return new MujViewHolder(v, klikatelnyRVInterface);
    }

    /**
     * Metoda, která je zodpovědná za dávání dat do jednotlivých listů
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull UdalostAdapter.MujViewHolder holder, int position) {
        holder.nazevUdalosti.setText(udalostList.get(position).getNazev());
        holder.dobaTvrvani.setText(udalostList.get(position).getZacatek() + ":" + udalostList.get(position).getKonec());
        holder.misto.setText(udalostList.get(position).getMisto());

        //Výběr ikony podle druhu sportu
        switch (udalostList.get(position).getSport()){
            case "Fotbal":
                holder.ikonaIV.setImageDrawable(context.getDrawable(R.drawable.ic_fotbal));
                break;
            case "Florbal":
                holder.ikonaIV.setImageDrawable(context.getDrawable(R.drawable.ic_florbal));
                break;
            case "Basketbal":
                holder.ikonaIV.setImageDrawable(context.getDrawable(R.drawable.ic_basketbal));
                break;
            case "Tenis":
                holder.ikonaIV.setImageDrawable(context.getDrawable(R.drawable.ic_tenis));
                break;
        }
    }

    /**
     * Metoda, která vrací velikost listu
     * @return velikost
     */
    @Override
    public int getItemCount() {
        return udalostList.size();
    }


    public static class MujViewHolder extends RecyclerView.ViewHolder {

        TextView nazevUdalosti, dobaTvrvani, misto;
        ImageView ikonaIV;

        public MujViewHolder(@NonNull View itemView, KlikatelnyRVInterface klikatelnyRVInterface) {
            super(itemView);

            nazevUdalosti = itemView.findViewById(R.id.IVNazev);
            dobaTvrvani = itemView.findViewById(R.id.IVDelkaKonani);
            misto = itemView.findViewById(R.id.IVKde);
            ikonaIV = itemView.findViewById(R.id.IVIkona);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (klikatelnyRVInterface != null) {
                        int pozice = getAdapterPosition();

                        if (pozice != RecyclerView.NO_POSITION) {
                            klikatelnyRVInterface.onItemClick(pozice);
                        }
                    }

                }
            });
        }
    }
}
