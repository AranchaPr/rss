package com.arr.lectorrss.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.arr.lectorrss.parser.Item;

import java.util.HashMap;
import java.util.List;


public final class FeedBd extends SQLiteOpenHelper {

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_TITULO = 1;
    private static final int COLUMN_DESC = 2;
    private static final int COLUMN_URL = 3;

    private static FeedBd singleton;
    private static final String TAG = FeedBd.class.getSimpleName();
    public static final String DATABASE_NAME = "Feed.db";
    public static final int DATABASE_VERSION = 1;


    private FeedBd(Context context) {
        super(context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION);

    }

    /**
     * Retorna la instancia del singleton
     *
     * @param context contexto de ejecución
     * @return Instancia
     */
    public static synchronized FeedBd getInstance(Context context) {
        if (singleton == null) {
            singleton = new FeedBd(context.getApplicationContext());
        }
        return singleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creación de la tabla "entrada""
        db.execSQL(Bd.CREAR_ENTRADA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Bd.ENTRADA_TABLE_NAME);
        onCreate(db);
    }

    /**
     * Devuelve todas las filas de la tabla "entrada"
     *
     * @return cursor con los registros
     */
    public Cursor obtenerEntradas() {

        return getWritableDatabase().rawQuery(
                "select * from " + Bd.ENTRADA_TABLE_NAME, null);
    }

    /**
     * Devuelve una  fila de la tabla "entrada"
     *
     * @return cursor con los registros
     */
    public Cursor obtenerEntrada(int id) {

        return getWritableDatabase().rawQuery(
                "select * from " + Bd.ENTRADA_TABLE_NAME + " where " + Bd.ColumnEntradas.ID  +"=" +id, null);
    }

    /**
     * Inserta un registro en la tabla entrada
     *
     * @param titulo      titulo de la entrada
     * @param descripcion decripcion de la entrada
     * @param url         url del articulo
     * @param thumb_url   url de la miniatura
     */
    public void insertarEntrada(
            String titulo,
            String descripcion,
            String url,
            String thumb_url) {

        ContentValues values = new ContentValues();
        values.put(Bd.ColumnEntradas.TITULO, titulo);
        values.put(Bd.ColumnEntradas.DESCRIPCION, descripcion);
        values.put(Bd.ColumnEntradas.URL, url);
        values.put(Bd.ColumnEntradas.URL_MINIATURA, thumb_url);


        getWritableDatabase().insert(Bd.ENTRADA_TABLE_NAME,null,values);
    }

    /**
     * Actualiza una fila de la tabla "entrada"
     *
     * @param id          identificador de la entrada
     * @param titulo      titulo nuevo de la entrada
     * @param descripcion descripcion nueva para la entrada
     * @param url         url nueva para la entrada
     * @param thumb_url   url nueva para la miniatura de la entrada
     */
    public void actualizarEntrada(int id,
                                  String titulo,
                                  String descripcion,
                                  String url,
                                  String thumb_url) {

        ContentValues values = new ContentValues();
        values.put(Bd.ColumnEntradas.TITULO, titulo);
        values.put(Bd.ColumnEntradas.DESCRIPCION, descripcion);
        values.put(Bd.ColumnEntradas.URL, url);
        values.put(Bd.ColumnEntradas.URL_MINIATURA, thumb_url);
        getWritableDatabase().update(
                Bd.ENTRADA_TABLE_NAME,
                values,
                Bd.ColumnEntradas.ID + "=?",
                new String[]{String.valueOf(id)});

    }


    /**
     * Procesa una lista de items para su almacenamiento local
     * y sincronización.
     *
     * @param entries lista de items
     */
    public void sincronizarEntradas(List<Item> entries) {
    /*
    #1  Mapear temporalemente las entradas nuevas para realizar una
        comparación con las locales
    */
        HashMap<String, Item> entryMap = new HashMap<String, Item>();
        for (Item e : entries) {
            entryMap.put(e.getTitle(), e);
        }

    /*
    #2  Obtener las entradas locales
     */
        Log.i(TAG, "Consultar items actualmente almacenados");
        Cursor c = obtenerEntradas();
        assert c != null;
        Log.i(TAG, "Se encontraron " + c.getCount() + " entradas, computando...");

    /*
    #3  Comenzar a comparar las entradas
     */
        int id;
        String titulo;
        String descripcion;
        String url;

        while (c.moveToNext()) {

            id = c.getInt(COLUMN_ID);
            titulo = c.getString(COLUMN_TITULO);
            descripcion = c.getString(COLUMN_DESC);
            url = c.getString(COLUMN_URL);

            Item match = entryMap.get(titulo);
            if (match != null) {
                // Filtrar entradas existentes. Remover para prevenir futura inserción
                entryMap.remove(titulo);

            /*
            #3.1 Comprobar si la entrada necesita ser actualizada
            */
                if ((match.getTitle() != null && !match.getTitle().equals(titulo)) ||
                        (match.getDescripcion() != null && !match.getDescripcion().equals(descripcion)) ||
                        (match.getLink() != null && !match.getLink().equals(url))) {
                    // Actualizar entradas
                    actualizarEntrada(
                            id,
                            match.getTitle(),
                            match.getDescripcion(),
                            match.getLink(),
                            match.getContent().getUrl()
                    );

                }
            }
        }
        c.close();
       for (Item e : entryMap.values()) {
            Log.i(TAG, "Insertado: titulo=" + e.getTitle());
            insertarEntrada(
                    e.getTitle(),
                    e.getDescripcion(),
                    e.getLink(),
                    e.getContent().getUrl()
            );
        }
        Log.i(TAG, "Se actualizaron los registros");


    }

}



