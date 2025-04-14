package com.example.jetpacknotesapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetpacknotesapp.firebase_bd.Note
import com.example.jetpacknotesapp.viewmodel.NoteViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen() {
    val notesViewModel: NoteViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentNote by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = {Text("My Notes", fontSize = 24.sp)})
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Note") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add") },
                onClick = {
                    currentNote = null
                    showBottomSheet = true
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NotesUI(notesViewModel) { selectedNote ->
                currentNote = selectedNote
                showBottomSheet = true
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                NoteBottomSheet(
                    note = currentNote,
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                        }
                    },
                    onSave = { note ->
                        notesViewModel.addOrUpdateNote(note)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            showBottomSheet = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NoteBottomSheet(
    note: Note?,
    onDismiss: () -> Unit,
    onSave: (Note) -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = if (note == null) "Add Note" else "Edit Note", fontSize = 20.sp)

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onDismiss) { Text("Cancel") }
            Button(onClick = { onSave(Note(title = title, content = content, id = note?.id ?: "")) }) {
                Text("Save")
            }

//            Button(onClick = {
//                val noteToSave = note?.copy(title =title, content = content) ?: Note(title,content)
//                onSave(noteToSave.copy(color = noteToSave.color))
//            }) {

        }
    }
}


@Composable
fun NotesUI(noteViewModel: NoteViewModel, onEdit: (Note) -> Unit) {
    val allNotes by noteViewModel.notes.observeAsState(emptyList())

    LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(150.dp), modifier =Modifier.padding(8.dp)) {
        items(allNotes) {note ->
            NoteCard(note,onEdit, onDelete = {noteViewModel.deleteNote(note.id)})
        }
    }
}

@Composable
fun NoteCard(note: Note, onEdit: (Note) -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable { onEdit(note) },
        colors = CardDefaults.cardColors(containerColor = Color(generateRandomColor()))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = note.title, fontSize = 18.sp, color = Color.Black)
            Text(text = note.content, fontSize = 14.sp, color = Color.DarkGray)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onDelete) {
                    Text("Delete", color = Color.Red)
                }
            }
        }
    }
}

private fun generateRandomColor():Int {
    val random = java.util.Random()
    return android.graphics.Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
}

//fun randomColor(): Color {
//    val colors = listOf(
//        Color(0xFFD5A1A1),
//        Color(0xFFB07387),
//        Color(0xFFD4B1DE),
//        Color(0xFFB3CCDC),
//        Color(0xFFB2DCDA),
//        Color(0xFFB8E1B9),
//        Color(0xFFBCA9E7),
//        Color(0xFFD3BB9A),
//    )
//
//    return colors[Random.nextInt(colors.size)]
//}