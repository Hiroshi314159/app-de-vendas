package com.trabalho.appvendas.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.trabalho.appvendas.R;
import com.trabalho.appvendas.model.Anuncio;

import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {

    private List<Anuncio> anuncios;
    private Context context;

    public AdapterAnuncios(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_anuncio, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Anuncio anuncio = anuncios.get(position);
        holder.titulo.setText(anuncio.getTitulo());
        holder.valor.setText(anuncio.getValor());
        //Pega primeira imagem da lista
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);

        Picasso.get().load(urlCapa).into(holder.foto);
    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titulo;
        TextView valor;
        ImageView foto;

        @SuppressLint("WrongViewCast")
        public MyViewHolder(View view){
            super(view);

            titulo = itemView.findViewById(R.id.textTitulo);
            valor = itemView.findViewById(R.id.textValor);
            foto = itemView.findViewById(R.id.imageAnuncio);

        }
    }

}
