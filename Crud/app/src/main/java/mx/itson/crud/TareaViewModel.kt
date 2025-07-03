package mx.itson.crud

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TareaViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _listaTareas = MutableLiveData<List<Tarea>>(emptyList())
    val listaTareas: LiveData<List<Tarea>> = _listaTareas

    init {
        obtenerTareas()
    }

    fun obtenerTareas() {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = db.collection("tareas").get().await()
                val tareas = result.documents.mapNotNull { it.toObject(Tarea::class.java) }
                _listaTareas.postValue(tareas)
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }

    fun agregarTarea(tarea: Tarea) {
        tarea.Id = UUID.randomUUID().toString()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("tareas").document(tarea.Id).set(tarea).await()
                _listaTareas.postValue(_listaTareas.value?.plus(tarea))
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun actualizarTarea(tarea: Tarea) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("tareas").document(tarea.Id).update(tarea.toMap()).await()
                _listaTareas.postValue(_listaTareas.value?.map { if (it.Id == tarea.Id) tarea else it })
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }
    }

    fun borrarTarea(tarea: Tarea) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("tareas").document(tarea.Id).delete().await()
                _listaTareas.postValue(_listaTareas.value?.filter { it.Id != tarea.Id })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}