package com.example.ladm_ud3_tarea2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestore.getInstance
import kotlinx.android.synthetic.main.activity_main3.*

class MainActivity3 : AppCompatActivity() {
    var baseremota = getInstance()
    var id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        var extra = intent.extras
        id = extra!!.getString("idElegido")!!

        baseremota.collection("Restaurant")
            .document(id)
            .get()
            .addOnSuccessListener {
                /*txtcelular2.setText(it.getString("celular"))
                txtfecha2.setText(it.getString("fecha"))
                nombrecliente2.setText(it.getString("NombreCliente"))*/
                cantidad2.setText(it.getString("cantidad"))
                descripcion2.setText(it.getString("descripcion"))
                precio2.setText(it.getString("precio"))
            }
            .addOnFailureListener {
                alerta("ERROR NO EXISTE ID ${id}")
            }
        buttonAct.setOnClickListener {
            actualizar()
        }
        Bregragresar.setOnClickListener {
            finish()
        }

    }

    private fun actualizar() {
        baseremota.collection("Restaurant")
            .document(id)
            .update("cantidad",cantidad2.text.toString(),"descripcion",descripcion2.text.toString(),"precio",precio2.text.toString())
            .addOnSuccessListener {
                alerta("SE ACTUALIZO CON EXITO")
            }
            .addOnFailureListener{
                mensaje("ERROR NO SE PUDO ACTUALIZAR")
            }
    }

    private fun alerta(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->}
            .show()
    }
}