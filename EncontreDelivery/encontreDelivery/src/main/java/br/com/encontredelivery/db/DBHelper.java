package br.com.encontredelivery.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	public static String NOME_DB = "encontredelivery";
	public static int VERSAO_DB = 1;

	public DBHelper(Context context) {
		super(context, NOME_DB, null, VERSAO_DB);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(" CREATE TABLE cliente_logado ( " +
				   " id               BIGINT PRIMARY KEY NOT NULL, " + 
				   " nome             VARCHAR(50), " +
				   " email            VARCHAR(50), " +
				   " senha            CHAR(32), " +
				   " id_facebook      VARCHAR(100), " +
				   " fone             VARCHAR(20)," +
				   " data_aniversario DATE);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
