package com.example.myapplication.Adaptery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DatoveTypy.Misto;
import com.example.myapplication.Interface.KlikatelnyRVInterface;
import com.example.myapplication.R;

import java.util.List;

public class MistoAdapter extends RecyclerView.Adapter<MistoAdapter.MujViewHolder4> {

    private Context context;
    private List<Misto> mistoList;
    private static KlikatelnyRVInterface klikatelnyRVInterface;

    public MistoAdapter(Context context, List<Misto> mistoList,KlikatelnyRVInterface klikatelnyRVInterface) {
        this.context = context;
        this.mistoList = mistoList;
        this.klikatelnyRVInterface = klikatelnyRVInterface;
    }

    @NonNull
    @Override
    public MujViewHolder4 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mista_list, parent, false);
        return new MujViewHolder4(v, klikatelnyRVInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MujViewHolder4 holder, int position) {
        holder.nazev.setText(mistoList.get(position).getNazev());
    }

    @Override
    public int getItemCount() {
        return mistoList.size();
    }

    public static class MujViewHolder4 extends RecyclerView.ViewHolder {

        TextView nazev;

        public MujViewHolder4(@NonNull View itemView, KlikatelnyRVInterface klikatelnyRVInterface) {
            super(itemView);

            nazev = itemView.findViewById(R.id.mistaNazevTV);

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
