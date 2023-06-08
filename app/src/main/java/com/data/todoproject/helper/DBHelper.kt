package com.data.todoproject.helper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.data.todoproject.model.Todo
import java.util.*


class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "todo_db"
    }


    private val TABLE_NAME = "todoTable"

    private val COLUMN_ID = "id"
    private val COLUMN_TITLE = "title"
    private val COLUMN_DESCRIPTION = "description"
    private val COLUMN_TIMESTAMP = "timestamp"


    private val CREATE_TABLE = (
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_DESCRIPTION + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")")

    val allNotes: ArrayList<Todo>
        @SuppressLint("Range")
        get() {
            val notes = ArrayList<Todo>()
            val selectQuery =
                "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_TIMESTAMP + " DESC"

            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val todo = Todo(
                        cursor!!.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP))
                    )
                    todo.id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                    todo.title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
                    todo.desc = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                    todo.timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP))
                    notes.add(todo)
                } while (cursor.moveToNext())
            }
            db.close()
            return notes
        }

    val notesCount: Int
        get() {
            val countQuery = "SELECT  * FROM " + TABLE_NAME
            val db = this.readableDatabase
            val cursor = db.rawQuery(countQuery, null)
            val count = cursor.count
            cursor.close()
            return count
        }


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)  // create notes table
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTodo(todo: Todo): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, todo.title)
        values.put(COLUMN_DESCRIPTION, todo.desc)
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getTodo(id: Long): Todo {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_TITLE, COLUMN_DESCRIPTION, COLUMN_TIMESTAMP),
            COLUMN_ID + "=?",
            arrayOf(id.toString()),
            null,
            null,
            null,
            null
        )

        cursor?.moveToFirst()

        val todo = Todo(
            cursor!!.getInt(cursor.getColumnIndex(COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
            cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
            cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP))
        )

        cursor.close()
        return todo
    }

    fun updateTodo(todo: Todo): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, todo.title)
        values.put(COLUMN_DESCRIPTION, todo.desc)
        return db.update(
            TABLE_NAME,
            values,
            COLUMN_ID + " = ?",
            arrayOf(todo.id.toString())
        )   // updating row
    }

    fun deleteTodo(todo: Todo): Boolean {
        val db = writableDatabase
        db.delete(
            TABLE_NAME,
            COLUMN_ID + " LIKE ?",
            arrayOf(todo.id.toString())
        ) // Issue SQL statement.
        return true
    }
}