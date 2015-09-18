package br.com.encontredelivery.dao;

import br.com.encontredelivery.db.DBHelper;
import br.com.encontredelivery.model.Cliente;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ClienteDao {
	
	private SQLiteDatabase db;

	public ClienteDao(Context ctx) {
		db = new DBHelper(ctx).getWritableDatabase();
	}

	public void inserir(Cliente cliente) {
		db.beginTransaction();
		
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put("id", cliente.getId());
			contentValues.put("nome", cliente.getNome());
			contentValues.put("email", cliente.getEmail());
			contentValues.put("senha", cliente.getSenha());
			contentValues.put("fone", cliente.getFone());
			
			if (!cliente.getIdFacebook().equals("")) {
				contentValues.put("id_facebook", cliente.getIdFacebook());
				contentValues.put("data_aniversario", cliente.getDataAniversario());
			}
			
			if (db.insert("cliente_logado", null, contentValues) > -1)
				db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public void editar(Cliente cliente) {
		db.beginTransaction();
		
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put("nome", cliente.getNome());
			contentValues.put("fone", cliente.getFone());
			
			if (!cliente.getSenha().equals("")) {
				contentValues.put("senha", cliente.getSenha());
			}

			if (db.update("cliente_logado", contentValues, " id = ? ", new String[]{ String.valueOf(cliente.getId()) }) > -1)
				db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public void apagar() {
		db.beginTransaction();
		
		try {
			if (db.delete("cliente_logado", null, null) > -1)
				db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	public Cliente getCliente() {
		Cursor cursor = db.rawQuery(" SELECT * FROM cliente_logado", null);
		
		Cliente cliente = null;

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			
			cliente = new Cliente();
			cliente.setId(cursor.getLong(cursor.getColumnIndex("id")));
			cliente.setNome(cursor.getString(cursor.getColumnIndex("nome")));
			cliente.setEmail(cursor.getString(cursor.getColumnIndex("email")));
			cliente.setSenha(cursor.getString(cursor.getColumnIndex("senha")));
			cliente.setIdFacebook(cursor.getString(cursor.getColumnIndex("id_facebook")));
			cliente.setFone(cursor.getString(cursor.getColumnIndex("fone")));
			cliente.setDataAniversario(cursor.getString(cursor.getColumnIndex("data_aniversario")));
		}
		
		cursor.close();

		return cliente;
	}
}
