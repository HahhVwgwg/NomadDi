package com.example.tabyspartner.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.tabyspartner.model.CreditCard
import com.example.tabyspartner.model.History
import java.util.*


@RequiresApi(Build.VERSION_CODES.P)
class DatabaseHandlerHistory(
    var context: Context?,
) : SQLiteOpenHelper(context, "HistoryDatabase.db", null, 1) {

    private val HISTORY_TABLE = "history"
    private val ID = "id"
    private val HISTORY_CARD_NUMBER = "history_card_number"
    private val HISTORY_AMOUNT_SENT = "history_amount_sent"
    private val HISTORY_AMOUNT_TOTAL = "history_amount_total"
    private val HISTORY_AMOUNT_FEE = "history_amount_fee"
    private val HISTORY_CHECK_ID = "history_check_id"
    private val HISTORY_DATE = "history_date"
    private val HISTORY_RECIPIENT = "history_recipient"

    override fun onCreate(db: SQLiteDatabase) {
        val query =
            "CREATE TABLE " + HISTORY_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    HISTORY_CARD_NUMBER + " TEXT, " + HISTORY_AMOUNT_SENT + " TEXT, " + HISTORY_AMOUNT_TOTAL +
                    " TEXT, " + HISTORY_AMOUNT_FEE + " TEXT, " + HISTORY_CHECK_ID + " INTEGER, " + HISTORY_DATE +
                    " TEXT, " + HISTORY_RECIPIENT + " TEXT" + ");"
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + HISTORY_TABLE)
        // Create tables again
        onCreate(db)
    }

    fun insertHistory(history: History) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(HISTORY_CARD_NUMBER, history.history_card_number)
        cv.put(HISTORY_AMOUNT_SENT, history.history_amount_sent)
        cv.put(HISTORY_AMOUNT_TOTAL, history.history_amount_total)
        cv.put(HISTORY_AMOUNT_FEE, history.history_amount_fee)
        cv.put(HISTORY_CHECK_ID, history.history_check_id)
        cv.put(HISTORY_DATE, history.history_date)
        cv.put(HISTORY_RECIPIENT, history.history_recipient)
        val result = db?.insert(HISTORY_TABLE, null, cv)
        if (result?.toInt() == -1) {
            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun getAllHistory(): List<History>? {
        val db = this.readableDatabase
        val historyList: MutableList<History> = ArrayList<History>()
        var cur: Cursor? = null
        db?.beginTransaction()
        try {
            cur = db?.query(HISTORY_TABLE, null, null, null, null, null, null, null)
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        val history = History()
                        history.id = cur.getInt(cur.getColumnIndex(ID))
                        history.history_card_number =
                            cur.getString(cur.getColumnIndex(HISTORY_CARD_NUMBER))
                        history.history_amount_sent =
                            cur.getString(cur.getColumnIndex(HISTORY_AMOUNT_SENT))
                        history.history_amount_total =
                            cur.getString(cur.getColumnIndex(HISTORY_AMOUNT_TOTAL))
                        history.history_amount_fee =
                            cur.getString(cur.getColumnIndex(HISTORY_AMOUNT_FEE))
                        history.history_check_id = cur.getInt(cur.getColumnIndex(HISTORY_CHECK_ID))
                        history.history_date = cur.getString(cur.getColumnIndex(HISTORY_DATE))
                        history.history_recipient =
                            cur.getString(cur.getColumnIndex(HISTORY_RECIPIENT))
                        historyList.add(history)
                    } while (cur.moveToNext())
                }
            }
        } finally {
            db?.endTransaction()
            assert(cur != null)
            cur!!.close()
        }
        return historyList
    }

    fun deleteAllData() {
        val db = this.writableDatabase
        db.execSQL("delete from "+ HISTORY_TABLE);
    }
}