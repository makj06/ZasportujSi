package com.example.myapplication.Adaptery;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatoveTypy.OblibeneMisto;
import com.example.myapplication.Interface.KlikatelnyRVInterface;
import com.example.myapplication.R;

import java.util.List;

public class OblibeneMistoAdapter extends RecyclerView.Adapter<OblibeneMistoAdapter.MujViewHolder5>{
    //Inicializace proměnných
    private Context context;
    private List<OblibeneMisto> oblMistoList;
    private static KlikatelnyRVInterface klikatelnyRVInterface;

    //Konstroktor
    public OblibeneMistoAdapter(Context context, List<OblibeneMisto> oblMistoList, KlikatelnyRVInterface klikatelnyRVInterface) {
        this.context = context;
        this.oblMistoList = oblMistoList;
        this.klikatelnyRVInterface = klikatelnyRVInterface;
    }

    /**
     * Metoda, která dává jednotlivé listy do recycler view
     * @param parent Rodič listu
     * @return
     */
    @NonNull
    @Override
    public MujViewHolder5 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oblibene_misto_list, parent, false);
        return new OblibeneMistoAdapter.MujViewHolder5(v, klikatelnyRVInterface);
    }

    /**
     * Metoda, která je zodpovědná za dávání dat do jednotlivých listů
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MujViewHolder5 holder, int position) {
        String ulice = oblMistoList.get(position).getUlice();
        String cisloPopisne = oblMistoList.get(position).getCisloPopisne();
        String mesto = oblMistoList.get(position).getMesto();

        if(ulice ==null){
            ulice = "";
        }

        if(cisloPopisne == null){
            cisloPopisne = "";
        }

        if(mesto ==null){
            mesto = "";
        }

        String poloha = ulice + " " + cisloPopisne  + " " +mesto;

        holder.nazevTV.setText(oblMistoList.get(position).getNazev());
        holder.adresaTV.setText(poloha);
    }

    /**
     * Metoda, která vrací velikost listu
     * @return velikost
     */
    @Override
    public int getItemCount() {
        return oblMistoList.size();
    }

    public static class MujViewHolder5 extends RecyclerView.ViewHolder {

        private TextView nazevTV, adresaTV;

        public MujViewHolder5(@NonNull View itemView, KlikatelnyRVInterface klikatelnyRVInterface) {
            super(itemView);

            nazevTV = itemView.findViewById(R.id.oblListNazevTV);
            adresaTV = itemView.findViewById(R.id.oblListAdresaTV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (OblibeneMistoAdapter.klikatelnyRVInterface != null) {
                        int pozice = getAdapterPosition();

                        if (pozice != RecyclerView.NO_POSITION) {
                            OblibeneMistoAdapter.klikatelnyRVInterface.onItemClick(pozice);
                        }
                    }

                }
            });


        }
    }
}


