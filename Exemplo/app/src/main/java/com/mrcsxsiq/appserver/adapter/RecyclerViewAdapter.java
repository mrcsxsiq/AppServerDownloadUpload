package com.mrcsxsiq.appserver.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mrcsxsiq.appserver.MainActivity;
import com.mrcsxsiq.appserver.R;
import com.mrcsxsiq.appserver.app.CustomApplication;
import com.mrcsxsiq.appserver.domain.Arquivo;
import com.mrcsxsiq.appserver.utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>  {

    private List<Arquivo> listArquivos;
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;

    public RecyclerViewAdapter(Activity activity, List<Arquivo> listArquivos) {
        this.mActivity = activity;
        this.listArquivos = listArquivos;
        this.mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_arquivo, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder itemViewHolder, final int position) {

        itemViewHolder.textViewNomeArquivo.setText(listArquivos.get(position).getNome());
        itemViewHolder.textViewTamanho.setText(listArquivos.get(position).getTamanho());

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mActivity).baixarArquivo(listArquivos.get(itemViewHolder.getAdapterPosition()).getNome());
            }
        });

        itemViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);

                dialog.setTitle("Confirmação");
                dialog.setMessage("Deseja apagar o arquivo?\n"+listArquivos.get(itemViewHolder.getAdapterPosition()).getNome());
                dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        JsonArrayRequest req = new JsonArrayRequest(
                                Config.urlDeletarArquivo(listArquivos.get(itemViewHolder.getAdapterPosition()).getNome()),
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {

                                        if (response.length() > 0) {
                                            Toast.makeText(mActivity, "Arquivo apagado", Toast.LENGTH_SHORT).show();
                                        } else {

                                            Toast.makeText(mActivity, "Arquivo não apagado", Toast.LENGTH_SHORT).show();
                                        }

                                        ((MainActivity) mActivity).onRefresh();

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //Toast.makeText(mActivity, "Arquivo não apagado", Toast.LENGTH_SHORT).show();
                                ((MainActivity) mActivity).onRefresh();
                            }
                        });

                        CustomApplication.getInstance().addToRequestQueue(req);



                    }
                });
                dialog.setNegativeButton("Não", null);
                dialog.show();

                return false;
            }
        });


        String tipo = listArquivos.get(position).getTipo();
        if (tipo.contains("pdf")){
            itemViewHolder.imageView.setImageResource(R.drawable.file_pdf);
        } else if (tipo.contains("video")){
            itemViewHolder.imageView.setImageResource(R.drawable.file_video);
        } else if (tipo.contains("image")){
            itemViewHolder.imageView.setImageResource(R.drawable.file_image);
        } else if (tipo.contains("audio")){
            itemViewHolder.imageView.setImageResource(R.drawable.file_music);
        } else if (tipo.contains("document")){
            itemViewHolder.imageView.setImageResource(R.drawable.file_document);
        } else {
            itemViewHolder.imageView.setImageResource(R.drawable.file);
        }

    }

    @Override
    public int getItemCount() {
        return listArquivos.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewNomeArquivo;
        public TextView textViewTamanho;
        public ImageView imageView;

        public ItemViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textViewNomeArquivo = (TextView) view.findViewById(R.id.textViewNomeArquivo);
            textViewTamanho = (TextView) view.findViewById(R.id.textViewTamanho);
        }

    }
}
