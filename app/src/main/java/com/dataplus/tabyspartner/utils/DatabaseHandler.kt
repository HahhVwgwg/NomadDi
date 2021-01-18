package com.dataplus.tabyspartner.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dataplus.tabyspartner.model.CreditCard
import java.util.*


@RequiresApi(Build.VERSION_CODES.P)
class DatabaseHandler(
    var context: Context?,
) : SQLiteOpenHelper(context,"CreditCardListDatabase.db",null,1) {

    private val VERSION = 1
    private val NAME = "CreditCardListDatabase"
    private val CREDIT_CARD_TABLE = "credit_card"
    private val ID = "id"
    private val CREDIT_CARD_NAME = "credit_card_name"//CREDIT_CARD_NAME CREDIT_CARD_NUMBER
    private val CREDIT_CARD_NUMBER = "credit_card_number"
//    private val CREATE_CREDIT_CARD_TABLE =
//        "CREATE TABLE " + CREDIT_CARD_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
//                CREDIT_CARD_NAME + "TEXT, " + CREDIT_CARD_NUMBER + "TEXT)"



    override fun onCreate(db: SQLiteDatabase) {
       val query =
        "CREATE TABLE " + CREDIT_CARD_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                CREDIT_CARD_NAME + " TEXT, " + CREDIT_CARD_NUMBER + " TEXT);"
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS "+CREDIT_CARD_TABLE)
        // Create tables again
        onCreate(db)
    }

    fun insertTask(creditCard: CreditCard) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(CREDIT_CARD_NAME, creditCard.creditCardName)
        cv.put(CREDIT_CARD_NUMBER, creditCard.creditCardNumber)
        val result = db?.insert(CREDIT_CARD_TABLE, null, cv)
        if(result?.toInt() == -1) {
            Toast.makeText(context,"Ошибка!",Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(context,"Карта успешно добавлена",Toast.LENGTH_SHORT).show()
        }
    }

    fun getAllCreditCards(): List<CreditCard>? {
        val db = this.readableDatabase
        val creditCardList: MutableList<CreditCard> = ArrayList<CreditCard>()
        var cur: Cursor? = null
        db?.beginTransaction()
        try {
            cur = db?.query(CREDIT_CARD_TABLE, null, null, null, null, null, null, null)
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        val creditCard = CreditCard()
                        creditCard.id = cur.getInt(cur.getColumnIndex(ID))
                        creditCard.creditCardName = cur.getString(cur.getColumnIndex(CREDIT_CARD_NAME))
                        creditCard.creditCardNumber = cur.getString(cur.getColumnIndex(CREDIT_CARD_NUMBER))
                        creditCardList.add(creditCard)
                    } while (cur.moveToNext())
                }
            }
        } finally {
            db?.endTransaction()
            assert(cur != null)
            cur!!.close()
        }
        return creditCardList
    }
    //CREDIT_CARD_NAME CREDIT_CARD_NUMBER
    fun updateCreditCardName(id: Int, cardName: String?) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(CREDIT_CARD_NAME, cardName)
        db?.update(CREDIT_CARD_TABLE, cv, "$ID= ?", arrayOf(id.toString()))
    }

    fun updateCreditCardNumber(id: Int, cardNumber: String?) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(CREDIT_CARD_NUMBER, cardNumber)
        db?.update(CREDIT_CARD_TABLE, cv, "$ID= ?", arrayOf(id.toString()))
    }

    fun deleteCardItem(id: Int) {
        val db = this.writableDatabase
        db?.delete(CREDIT_CARD_TABLE, "$ID= ?", arrayOf(id.toString()))

    }

    fun deleteAllData() {
        val db = this.writableDatabase
        db.execSQL("delete from "+ CREDIT_CARD_TABLE);
    }
}