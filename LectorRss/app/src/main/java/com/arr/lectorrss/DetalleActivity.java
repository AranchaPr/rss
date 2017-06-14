package com.arr.lectorrss;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.arr.lectorrss.bd.Bd;
import com.arr.lectorrss.bd.FeedBd;
import com.arr.lectorrss.http.HttpSingleton;

public class DetalleActivity extends AppCompatActivity {


    int tituloI;
    int descripcionI;
    int imagenI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        Intent intent = getIntent();
        Cursor cursor = FeedBd.getInstance(this).obtenerEntrada(intent.getIntExtra("posicion",1));

        // Setear indices
        tituloI = cursor.getColumnIndex(Bd.ColumnEntradas.TITULO);
        descripcionI = cursor.getColumnIndex(Bd.ColumnEntradas.DESCRIPCION);
        imagenI = cursor.getColumnIndex(Bd.ColumnEntradas.URL_MINIATURA);
        cursor.moveToFirst();
        ((TextView) findViewById(R.id.titulo_detalle)).setText(cursor.getString(tituloI));
        ((TextView) findViewById(R.id.descripcion_detalle)).setText(cursor.getString(descripcionI));

        // Obtener URL de la imagen
        String thumbnailUrl = cursor.getString(imagenI);

        // Obtener instancia del ImageLoader
        ImageLoader imageLoader = HttpSingleton.getInstance(this).getImageLoader();

        // Volcar datos en el image view
        ((NetworkImageView) findViewById(R.id.imagen_detalle)).setImageUrl(thumbnailUrl, imageLoader);


    }
}
