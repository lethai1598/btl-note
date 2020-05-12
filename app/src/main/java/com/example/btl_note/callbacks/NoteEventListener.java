package com.example.btl_note.callbacks;

import com.example.btl_note.model.Note;

public interface NoteEventListener {
    /**
     *
     * @param note: note item
     */
    void onNoteClick(Note note);

    /**
     *
     * @param note: note item
     */
    void onNoteLongClick(Note note);
}
