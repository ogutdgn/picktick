package com.example.back_end

import android.content.ContentValues
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.serialization.descriptors.PrimitiveKind

class SQLiteUserDataBaseHelper {
    object DataBase_Columns : BaseColumns {
        const val TABLE_NAME = "UserDataBase"

        object USER_DB : BaseColumns {
            const val USER_DATA_BASE_NAME = "user_db"

            const val TABLE_NAME = "listings"

            const val USER_NAME_TITLE = "User_First_Name"
            const val USER_NAME_SUB = "user_name"

            const val USER_LAST_NAME_TITLE = "User_Last_Name"
            const val USER_LAST_NAME_SUB = "user_last_name"

            const val USER_EMAIL_TITLE = "User_Email"
            const val USER_EMAIL_SUB = "user_email"

            const val USER_PWD_TITLE = "User_Password"
            const val USER_PWD_SUB = "user_password"

            const val USER_ADMIN_TITLE = "User_Admin"
            const val USER_ADMIN_SUB = "user_admin"

            const val USER_PICTURE_TITLE = "User_Picture"
            const val USER_PICTURE_SUB = "user_picture"
        }
    }

    class SQLiteUser_DataBase_Helper(context: Context) :
        SQLiteOpenHelper(context, DataBase_Columns.USER_DB.TABLE_NAME, null, 1) {
        private val SQL_CREATE_LISTINGS = """
            CREATE TABLE ${DataBase_Columns.USER_DB.TABLE_NAME} (
            ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${DataBase_Columns.USER_DB.USER_NAME_SUB} TEXT NOT NULL,
            ${DataBase_Columns.USER_DB.USER_LAST_NAME_SUB} TEXT NOT NULL,
            ${DataBase_Columns.USER_DB.USER_EMAIL_SUB} TEXT NOT NULL,
            ${DataBase_Columns.USER_DB.USER_PWD_SUB} TEXT NOT NULL,
            ${DataBase_Columns.USER_DB.USER_ADMIN_SUB} BOOLEAN NOT NULL,
            ${DataBase_Columns.USER_DB.USER_PICTURE_SUB} TEXT
            )"""

        fun addUser(
            firstName: String,
            lastName: String,
            email: String,
            password: String,
            admin: Boolean,
            picture: String
        ): Long {
            val db = writableDatabase
            val values = ContentValues().apply {
                put(DataBase_Columns.USER_DB.USER_NAME_SUB, firstName)
                put(DataBase_Columns.USER_DB.USER_LAST_NAME_SUB, lastName)
                put(DataBase_Columns.USER_DB.USER_EMAIL_SUB, email)
                put(DataBase_Columns.USER_DB.USER_PWD_SUB, password)
                put(DataBase_Columns.USER_DB.USER_ADMIN_SUB, admin)
                put(DataBase_Columns.USER_DB.USER_PICTURE_SUB, picture)
            }
            return db.insert(DataBase_Columns.USER_DB.TABLE_NAME, null, values)
        }
            override fun onCreate(db: SQLiteDatabase) {
                db.execSQL(SQL_CREATE_LISTINGS)
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                //drop and recreate on upgrade
                db.execSQL("DROP TABLE IF EXISTS ${DataBase_Columns.USER_DB.TABLE_NAME}")
                onCreate(db)
            }

            fun get_listing_from_id(id_listing: Int): Map<String, String?> {
                val db = readableDatabase
                val cursor = db.query(
                    DataBase_Columns.USER_DB.TABLE_NAME,
                    null,
                    "${BaseColumns._ID} = ?",
                    arrayOf(id_listing.toString()),
                    null,
                    null,
                    null
                )
                if (!cursor.moveToFirst()) {
                    cursor.close()
                    return emptyMap() //wut
                }

                val listing = mapOf<String, String?>(
                    "id" to cursor.getString(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
                    "first_name" to cursor.getString(cursor.getColumnIndexOrThrow(DataBase_Columns.USER_DB.USER_NAME_SUB)),
                    "last_name" to cursor.getString(cursor.getColumnIndexOrThrow(DataBase_Columns.USER_DB.USER_LAST_NAME_SUB)),
                    "email" to cursor.getString(cursor.getColumnIndexOrThrow(DataBase_Columns.USER_DB.USER_EMAIL_SUB)),
                    "password" to cursor.getString(cursor.getColumnIndexOrThrow(DataBase_Columns.USER_DB.USER_PWD_SUB)),
                    "admin" to cursor.getString(cursor.getColumnIndexOrThrow(DataBase_Columns.USER_DB.USER_ADMIN_SUB)),
                    "picture" to cursor.getString(cursor.getColumnIndexOrThrow(DataBase_Columns.USER_DB.USER_PICTURE_SUB))                )
                cursor.close()
                return listing
            }
        }
    }