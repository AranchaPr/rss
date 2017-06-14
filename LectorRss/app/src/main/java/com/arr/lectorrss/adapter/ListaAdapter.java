package com.arr.lectorrss.adapter;

import android.widget.CursorAdapter;
import android.content.Context;
import android.database.Cursor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.arr.lectorrss.R;
import com.arr.lectorrss.bd.Bd;
import com.arr.lectorrss.http.HttpSingleton;


public class ListaAdapter extends CursorAdapter {

    private static final String TAG = ListaAdapter.class.getSimpleName();

    /**
     * View holder para evitar multiples llamadas de findViewById()
     */
    static class ViewHolder {
        TextView titulo;
        TextView descripcion;
        NetworkImageView imagen;

        int tituloI;
        int descripcionI;
        int imagenI;
    }

    public ListaAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.item_layout, null, false);

        ViewHolder vh = new ViewHolder();

        // Almacenar referencias
        vh.titulo = (TextView) view.findViewById(R.id.titulo);
        vh.descripcion = (TextView) view.findViewById(R.id.descripcion);
        vh.imagen = (NetworkImageView) view.findViewById(R.id.imagen);

        // Setear indices
        vh.tituloI = cursor.getColumnIndex(Bd.ColumnEntradas.TITULO);
        vh.descripcionI = cursor.getColumnIndex(Bd.ColumnEntradas.DESCRIPCION);
        vh.imagenI = cursor.getColumnIndex(Bd.ColumnEntradas.URL_MINIATURA);

        view.setTag(vh);

        return view;
    }

    public void bindView(View view, Context context, Cursor cursor) {

        final ViewHolder vh = (ViewHolder) view.getTag();

        // Setear el texto al titulo
        vh.titulo.setText(cursor.getString(vh.tituloI));

        // Obtener acceso a la descripción y su longitud
        int ln = cursor.getString(vh.descripcionI).length();
        String descripcion = cursor.getString(vh.descripcionI);

        // Acorto la longitud de la descripción...
        if (ln >= 100)
            vh.descripcion.setText(descripcion.substring(0, 100)+"...");
        else vh.descripcion.setText(descripcion);

        // Obtener URL de la imagen
        String thumbnailUrl = cursor.getString(vh.imagenI);

        // Obtener instancia del ImageLoader
        ImageLoader imageLoader = HttpSingleton.getInstance(context).getImageLoader();

        // Volcar datos en el image view
        vh.imagen.setImageUrl(thumbnailUrl, imageLoader);

    }
    
}
