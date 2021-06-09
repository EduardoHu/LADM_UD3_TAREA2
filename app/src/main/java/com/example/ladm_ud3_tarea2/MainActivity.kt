package com.example.ladm_ud3_tarea2

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var baseremota = FirebaseFirestore.getInstance()
    var dataLista =ArrayList<String>()
    var listaID = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        estatusentrega.setOnClickListener {
            cantidad.isEnabled=estatusentrega.isChecked
            descripcion.isEnabled=estatusentrega.isChecked
            precio.isEnabled=estatusentrega.isChecked
        }

        baseremota.collection("Restaurant")
            .addSnapshotListener { value, error ->
                if(error!= null){
                    mensaje(error.message!!)
                    return@addSnapshotListener
                }

                dataLista.clear()
                for(document in value!!){
                    var cadena = "CELULAR ${document.getString("celular")} FECHA: ${document.getString("fecha")} NombreCliente: ${document.getString("NombreCliente")} Cantidad: ${document.getString("cantidad")} Descripcion: ${document.getString("descripcion")} Precio: ${document.getString("precio")}"
                    dataLista.add(cadena)
                    listaID.add(document.id.toString())
                }
                listaped.adapter=ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,dataLista)
                listaped.setOnItemClickListener { parent, view, position, id ->
                    dialogoActualiza(position)
                }
            }
        button2.setOnClickListener {
            insertar()
        }
        button3.setOnClickListener {
            consultaDatos()
        }


    }

    private fun consultaDatos() {
        var señal = Dialog(this)

        señal.setContentView(R.layout.consulta)

        var valor = señal.findViewById<EditText>(R.id.valor)
        var posicion = señal.findViewById<Spinner>(R.id.clave)
        var buscar = señal.findViewById<Button>(R.id.buscar)
        var cerrar = señal.findViewById<Button>(R.id.cerrar)

        señal.show()
        cerrar.setOnClickListener {
            señal.dismiss()
        }
        buscar.setOnClickListener {
            if(valor.text.isEmpty()){
                Toast.makeText(this,"DEBES PONER VALOR PARA BUSCAR", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            buscarDato(valor.text.toString(),posicion.selectedItemPosition)
            señal.dismiss()
        }
    }

    private fun buscarDato(valor: String, clave: Int) {
            when(clave){
                0->{consultaNombre(valor)}
            }
    }

    private fun consultaCelular(valor: String) {
        baseremota.collection("Restaurant")
            .whereEqualTo("celular",valor)
            .addSnapshotListener { value, error ->
                if(error!=null){
                    resultado.setText("ERROR,NO SE PUEDE COECTAR")
                    return@addSnapshotListener
                }
                var res = ""
                for(document in value!!){
                    res+= "ID"+document.id+"\\nCELULAR:"+document.getString("celular")+
                            "\nFecha: "+document.getString("fecha")+
                            "\nNombre Cliente: "+document.getString("NombreCliente")+
                            "\nCantidad: "+document.get("pedido.cantidad")+
                            "\nDescripcion: "+document.get("pedido.descripcion")+
                            "\nPrecio: "+document.get("pedido.precio")
                }

                if(res.indexOf("null")>=0) {
                    res = res.substring(0,res.indexOf("pedido"))
                }
                resultado.setText(res)
            }
    }

    private fun consultaNombre(valor: String) {
        baseremota.collection("Restaurant")
            .whereEqualTo("NombreCliente",valor)
            .addSnapshotListener { value, error ->
                if(error!=null){
                    resultado.setText("ERROR,NO SE PUEDE COECTAR")
                    return@addSnapshotListener
                }
                var res = ""
                for(document in value!!){
                    res+= "ID"+document.id+"\\nNombreCliente:"+document.getString("NombreCliente")+
//celular fecha
                            "\nCelular: "+document.getString("celular")+
                            "\nFecha: "+document.getString("fecha")+
                            "\nStatus: "+document.getString("estadoentrega")
                            "\nCantidad: "+document.get("pedido.cantidad")+
                            "\nDescripcion: "+document.get("pedido.descripcion")+
                            "\nPrecio: "+document.get("pedido.precio")
                }

                if(res.indexOf("null")>=0) {
                    res = res.substring(0,res.indexOf("pedido"))
                }
                resultado.setText(res)
            }
    }

    private fun alerta(s: String) {
        Toast.makeText(this,s,Toast.LENGTH_LONG).show()
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->}
            .show()
    }

    private fun dialogoActualiza(posicion:Int){
        var idElegido = listaID.get(posicion)
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage("¿QUE DESEAS HACER CON \n ${dataLista.get(posicion)}?")
            .setPositiveButton("ELIMINAR"){d,i->
                eliminar(idElegido)
            }
                .setNeutralButton("ACTUALIZAR"){d,i->
                    //eliminar(idElegido)
                    var intent = Intent(this,MainActivity3::class.java)
                    intent.putExtra("idElegido",idElegido)
                    startActivity(intent)
                }
            .setNegativeButton("CANCELAR"){d,i->}.show()

    }

    private fun eliminar(idElegido: String) {
        baseremota.collection("Restaurant")
                .document(idElegido)
                .delete()
                .addOnSuccessListener {
                    alerta("Se elimino con exito")
                }
                .addOnFailureListener {
                    mensaje("Error ${it.message}")
                }
    }

    private fun insertar(){
        var datosInsertar = hashMapOf(
            "celular"  to txtcelular.text.toString(),
            "estadoentrega" to estadoentrega.text.toString(),
            "fecha" to txtfecha.text.toString(),
            "NombreCliente" to nombrecliente.text.toString()

        )
        baseremota.collection("Restaurant")
            .add(datosInsertar)
            .addOnSuccessListener {
                if(estatusentrega.isChecked==true){
                    var pedido = hashMapOf(
                        "cantidad" to cantidad.text.toString().toInt(),
                        "descripcion" to descripcion.text.toString(),
                        "precio" to precio.text.toString().toFloat()
                    )
                    baseremota.collection("Restaurant")
                        .document(it.id)
                        .update("pedido",pedido as Map<String,Any>)
                }
                Toast.makeText(this,"SE AGREGARON LOS DATOS CORRECTAMENTE",Toast.LENGTH_LONG)
                    .show()
                //txtcelular.setText("")
               /* txtfecha.setText("")
                nombrecliente.setText("")
                cantidad.setText("")
                descripcion.setText("")
                precio.setText("")*/
            }
            .addOnFailureListener{
                mensaje("ERROR: ${it.message!!}")
            }
    }
}