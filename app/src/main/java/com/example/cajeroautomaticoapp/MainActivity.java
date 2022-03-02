package com.example.cajeroautomaticoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    float saldoTotal;
    TextView infoSaldo;
    EditText nuevoSaldo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nuevoSaldo = (EditText) findViewById(R.id.nuevoSaldo);
        infoSaldo = (TextView) findViewById(R.id.infoSaldo);
        ConsultaDatos(null);
    }

    public void Consignar(View view){
        float nuevoSaldoCast = Float.parseFloat(nuevoSaldo.getText().toString());
        saldoTotal += nuevoSaldoCast;

        transaccionBD();

        infoSaldo.setText(("$" + saldoTotal));
    }

    public void Retirar(View view){
        float nuevoSaldoCast = Float.parseFloat(nuevoSaldo.getText().toString());
        nuevoSaldoCast = saldoTotal - nuevoSaldoCast;
        if(nuevoSaldoCast < 0){
            AlertDialog.Builder dialogo = new AlertDialog.Builder(this);

            dialogo.setTitle("Error");
            dialogo.setMessage("El saldo a retirar es mayor al disponible");
            dialogo.setPositiveButton(Html.fromHtml("<font color='#000000'>Continuar</font>"), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialogo.create();

            dialogo.show();
        }else{
            saldoTotal = nuevoSaldoCast;
            transaccionBD();
            infoSaldo.setText(("$" + saldoTotal));
        }
    }

    public void ConsultaDatos(View view) {
        CajeroBD dbSaldo = new CajeroBD(this, "db", null, 1);
        SQLiteDatabase db = dbSaldo.getWritableDatabase();
        Cursor fila = db.rawQuery("select totalSaldo from Saldos order by id desc limit 1", null);

        if (fila.moveToFirst()) {
            saldoTotal = Float.parseFloat(fila.getString((0)));
            infoSaldo.setText(("$" + saldoTotal));
        }
    }

    public void transaccionBD(){
        CajeroBD dbSaldo = new CajeroBD(this, "db", null, 1);
        SQLiteDatabase db = dbSaldo.getWritableDatabase();
        ContentValues insertSaldo = new ContentValues();
        insertSaldo.put("totalSaldo",saldoTotal);
        db.insert("Saldos",null,insertSaldo);
        db.close();
        Toast.makeText(this, "Transaccion realizada correctamente", Toast.LENGTH_SHORT).show();
    }
}