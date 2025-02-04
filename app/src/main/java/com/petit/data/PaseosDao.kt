package com.petit.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.petit.model.Mascotas
import com.petit.model.Paseos

class PaseosDao {
    private var codigoUsuario: String
    private var firestrore: FirebaseFirestore

    init {
        val usuario = Firebase.auth.currentUser?.email
        //val usuario = Firebase.auth.currentUser?.uid
        codigoUsuario="$usuario"
        firestrore = FirebaseFirestore.getInstance()
        firestrore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun getAllData() : MutableLiveData<List<Paseos>> {
        val listaPaseos =  MutableLiveData<List<Paseos>>()
        firestrore.collection("PaseosApp")
            .document(codigoUsuario)
            .collection("misPaseos")
            .addSnapshotListener{snapshot, e ->
                if(e != null){
                    return@addSnapshotListener
                }
                if(snapshot != null){
                    val lista = ArrayList<Paseos>()
                    val paseos = snapshot.documents
                    paseos.forEach{
                        val paseos = it.toObject(Paseos::class.java)
                        if(paseos!= null){
                            lista.add(paseos)
                        }
                    }
                    listaPaseos.value = lista
                }
            }
        return listaPaseos
    }


    fun savePaseos(paseos: Paseos){
        val document: DocumentReference
        if(paseos.id.isEmpty()){
            document = firestrore
                .collection("PaseosApp")
                .document(codigoUsuario)
                .collection("misPaseos")
                .document()
            paseos.id = document.id
        }else{
            document = firestrore.collection("PaseosApp")
                .document(codigoUsuario)
                .collection("misPaseos")
                .document(paseos.id)
        }
        val set = document.set(paseos)
        set.addOnSuccessListener {
            Log.d("AddPaseos","Paseos Agregada")
        }
            .addOnCanceledListener {
                Log.e("AddPaseos","Paseos NO Agregada")
            }
    }


    suspend fun deletePaseos(paseos: Paseos){
        if(paseos.id.isNotEmpty()){
            firestrore
                .collection("PaseosApp")
                .document(codigoUsuario)
                .collection("misPaseos")
                .document(paseos.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("deletePaseos", "Paseos eliminada")
                }
                .addOnCanceledListener {
                    Log.e("deletePaseos", "Paseos NO eliminada")
                }
        }
    }

}