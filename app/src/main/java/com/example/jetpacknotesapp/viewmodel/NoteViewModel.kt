package com.example.jetpacknotesapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jetpacknotesapp.firebase_bd.Note
import com.google.firebase.firestore.FirebaseFirestore

class NoteViewModel: ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    private val firebaseFireStore = FirebaseFirestore.getInstance()
    private val notesCollection = firebaseFireStore.collection(COLLECTION_NOTES)


    init{
        fetchNotes()
    }
    fun addOrUpdateNote(note:Note) {
        if(note.id.isEmpty()) {
            note.id = notesCollection.document().id
        }

        notesCollection.document(note.id).set(note)
            .addOnSuccessListener { fetchNotes() }
            .addOnFailureListener{ it.printStackTrace() }
    }

    private fun fetchNotes() {
        notesCollection.get()
            .addOnSuccessListener { task ->
                val notesList = task.documents.mapNotNull { it.toObject(Note::class.java) }
                _notes.value = notesList
            }
            .addOnFailureListener { it.printStackTrace() }
    }

    fun deleteNote(noteId: String) {
        notesCollection.document(noteId).delete()
            .addOnSuccessListener { fetchNotes() }
            .addOnFailureListener { it.printStackTrace() }
    }

    companion object {
        private const val COLLECTION_NOTES = "notes"
    }
}