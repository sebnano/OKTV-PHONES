package com.oktv_mobile.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

@SuppressLint("Range")
class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $OPERATOR_TABLE($operatorColumn_nombre_operador TEXT, $operatorColumn_max_devices TEXT, $operatorColumn_operador TEXT)")

        db.execSQL("CREATE TABLE $DEVICES_TABLE($deviceColumn_field_id_opera TEXT, $deviceColumn_pais_dispositivos TEXT, $deviceColumn_node_id TEXT, " +
                "$deviceColumn_field_dispositivo_mac TEXT, $deviceColumn_field_dispositivo_nombre TEXT, $deviceColumn_field_dispositivo_ip TEXT)")

        db.execSQL("CREATE TABLE $BANNERS_TABLE($bannerColumn_field_position TEXT, $bannerColumn_status TEXT, $bannerColumn_field_active_banner TEXT, " +
                "$bannerColumn_type TEXT, $bannerColumn_field_imagen TEXT, $bannerColumn_field_automatic_slide TEXT)")

        db.execSQL("CREATE TABLE $CATEGORIES_TABLE($categoriesColumn_category TEXT, $categoriesColumn_id_category TEXT)")

        db.execSQL("CREATE TABLE $CHANNELS_TABLE(" +
                "$channelColumn_field_logo TEXT, " +
                "$channelColumn_field_channel_category TEXT, " +
                "$channelColumn_field_url TEXT, " +
                "$channelColumn_field_videotype TEXT, " +
                "$channelColumn_abreviacion TEXT, " +
                "$channelColumn_field_genres TEXT, " +
                "$channelColumn_field_ad_play_back_on_start_of_v TEXT, " +
                "$channelColumn_field_type_of_censorship TEXT, " +
                "$channelColumn_title TEXT, " +
                "$channelColumn_field_id TEXT, " +
                "$channelColumn_field_default TEXT, " +
                "$channelColumn_protected_channel TEXT, " +
                "$channelColumn_field_bitrate TEXT, " +
                "$channelColumn_posicion TEXT, " +
                "$channelColumn_field_tags TEXT, " +
                "$channelColumn_type TEXT, " +
                "$channelColumn_field_epg_code TEXT, " +
                "$channelColumn_nid TEXT, " +
                "$channelColumn_status TEXT, " +
                "$channelColumn_EPG_ID_Channel TEXT, " +
                "$channelColumn_field_ad_play_back_url TEXT, " +
                "$channelColumn_tipo_transmision TEXT, " +
                "$channelColumn_isLiked INTEGER, " +
                "$channelColumn_favNodeId TEXT)")

        db.execSQL("CREATE TABLE $EPG_TABLE(" +
                "$epgColumn_id TEXT, " +
                "$epgColumn_displayName TEXT, " +
                "$epgColumn_icon TEXT, " +
                "$epgColumn_nid TEXT, " +
                "$epgColumn_field_url TEXT, " +
                "$epgColumn_posicion TEXT)")

        db.execSQL("CREATE TABLE $EPG_PROGRAMS_TABLE(" +
                "$epgProgramsColumn_epgId TEXT, " +
                "$epgProgramsColumn_title TEXT, " +
                "$epgProgramsColumn_descr TEXT, " +
                "$epgProgramsColumn_start REAL, " +
                "$epgProgramsColumn_stop REAL)")

        db.execSQL("CREATE TABLE $LANGUAGES_TABLE(" +
                "$languagesColumn_field_key TEXT, " +
                "$languagesColumn_value TEXT, " +
                "$languagesColumn_image TEXT)")

        db.execSQL("CREATE TABLE $TRANSLATIONS_TABLE(" +
                "$translationColumn_field_key TEXT, " +
                "$translationColumn_field_value TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS $OPERATOR_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $DEVICES_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $BANNERS_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $CATEGORIES_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $CHANNELS_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $EPG_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $EPG_PROGRAMS_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $LANGUAGES_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $TRANSLATIONS_TABLE")
        onCreate(db)
    }

    fun truncateDataFromTable(db: SQLiteDatabase, tableName: String) {
        db.execSQL("DELETE FROM $tableName")
    }

    fun truncateAllTables() {
        val db = this.writableDatabase
        truncateDataFromTable(db, OPERATOR_TABLE)
        truncateDataFromTable(db, DEVICES_TABLE)
        truncateDataFromTable(db, BANNERS_TABLE)
        truncateDataFromTable(db, CATEGORIES_TABLE)
        truncateDataFromTable(db, CHANNELS_TABLE)
        db.close()
    }

    fun getRowCounts(db: SQLiteDatabase?, tableName: String) : Int {
        return if (db != null) {
            val cursor = db.rawQuery("SELECT count(1) as count FROM $tableName", null)
            var count = 0
            if (cursor.moveToFirst()) {
                count = cursor.getInt(cursor.getColumnIndex("count"))
            }
            cursor.close()
            count
        } else {
            0
        }
    }

    fun addOperator(db: SQLiteDatabase?, nombre_operador: String, max_devices: String, operador: String) {
        val values = ContentValues()
        values.put(operatorColumn_nombre_operador, nombre_operador)
        values.put(operatorColumn_max_devices, max_devices)
        values.put(operatorColumn_operador, operador)
        db?.insert(OPERATOR_TABLE, null, values)
    }

    fun addDevices(db: SQLiteDatabase?, field_id_opera: String, pais_dispositivos: String, node_id: String, field_dispositivo_mac: String, field_dispositivo_nombre: String, field_dispositivo_ip: String) {
        val values = ContentValues()
        values.put(deviceColumn_field_id_opera, field_id_opera)
        values.put(deviceColumn_pais_dispositivos, pais_dispositivos)
        values.put(deviceColumn_node_id, node_id)
        values.put(deviceColumn_field_dispositivo_mac, field_dispositivo_mac)
        values.put(deviceColumn_field_dispositivo_nombre, field_dispositivo_nombre)
        values.put(deviceColumn_field_dispositivo_ip, field_dispositivo_ip)
        db?.insert(DEVICES_TABLE, null, values)
    }

    fun addBanners(db: SQLiteDatabase?, field_position: String, status: String, field_active_banner: String, type: String, field_imagen: String, field_automatic_slide: String) {
        val values = ContentValues()
        values.put(bannerColumn_field_position, field_position)
        values.put(bannerColumn_status, status)
        values.put(bannerColumn_field_active_banner, field_active_banner)
        values.put(bannerColumn_type, type)
        values.put(bannerColumn_field_imagen, field_imagen)
        values.put(bannerColumn_field_automatic_slide, field_automatic_slide)
        db?.insert(BANNERS_TABLE, null, values)
    }

    fun addCategories(db: SQLiteDatabase?, category: String, id_category: String) {
        val values = ContentValues()
        values.put(categoriesColumn_category, category)
        values.put(categoriesColumn_id_category, id_category)
        db?.insert(CATEGORIES_TABLE, null, values)
    }

    fun addChannels(db: SQLiteDatabase?,
                    field_logo: String,
                    field_channel_category: String,
                    field_url: String,
//                    field_videotype: String,
//                    abreviacion: String,
//                    field_genres: String,
//                    field_ad_play_back_on_start_of_v: String,
//                    field_type_of_censorship: String,
                    title: String,
//                    field_id: String,
//                    field_default: String,
                    protected_channel: String,
//                    field_bitrate: String,
                    posicion: String,
//                    field_tags: String,
//                    type: String,
//                    field_epg_code: String,
                    nid: String,
//                    status: String,
                    EPG_ID_Channel: String,
//                    field_ad_play_back_url: String,
//                    tipo_transmision: String) {
    ) {
        val values = ContentValues()
        values.put(channelColumn_field_logo, field_logo)
        values.put(channelColumn_field_channel_category, field_channel_category)
        values.put(channelColumn_field_url, field_url)
//        values.put(channelColumn_field_videotype, field_videotype)
//        values.put(channelColumn_abreviacion, abreviacion)
//        values.put(channelColumn_field_genres, field_genres)
//        values.put(channelColumn_field_ad_play_back_on_start_of_v, field_ad_play_back_on_start_of_v)
//        values.put(channelColumn_field_type_of_censorship, field_type_of_censorship)
        values.put(channelColumn_title, title)
//        values.put(channelColumn_field_id, field_id)
//        values.put(channelColumn_field_default, field_default)
        values.put(channelColumn_protected_channel, protected_channel)
//        values.put(channelColumn_field_bitrate, field_bitrate)
        values.put(channelColumn_posicion, posicion)
//        values.put(channelColumn_field_tags, field_tags)
//        values.put(channelColumn_type, type)
//        values.put(channelColumn_field_epg_code, field_epg_code)
        values.put(channelColumn_nid, nid)
//        values.put(channelColumn_status, status)
        values.put(channelColumn_EPG_ID_Channel, EPG_ID_Channel)
//        values.put(channelColumn_field_ad_play_back_url, field_ad_play_back_url)
//        values.put(channelColumn_tipo_transmision, tipo_transmision)
        values.put(channelColumn_isLiked, 0)
        values.put(channelColumn_favNodeId, "")

        db?.insert(CHANNELS_TABLE, null, values)
    }

    fun updateChannel(db: SQLiteDatabase?, title: String, isLiked: Int, favNodeId: String) {
        val contentValues = ContentValues()
        contentValues.put(channelColumn_isLiked, isLiked)
        contentValues.put(channelColumn_favNodeId, favNodeId)
        db?.update(CHANNELS_TABLE, contentValues, "title='$title'",null)
    }

    fun updateChannelByPosicion(db: SQLiteDatabase?, posicion: String, isLiked: Int, favNodeId: String) {
        val contentValues = ContentValues()
        contentValues.put(channelColumn_isLiked, isLiked)
        contentValues.put(channelColumn_favNodeId, favNodeId)
        db?.update(CHANNELS_TABLE, contentValues, "posicion='$posicion'",null)
    }

    fun addEPGData(db: SQLiteDatabase?, id: String, displayName: String, icon: String, nid: String, field_url: String, posicion: String) {
        val values = ContentValues()
        values.put(epgColumn_id, id)
        values.put(epgColumn_displayName, displayName)
        values.put(epgColumn_icon, icon)
        values.put(epgColumn_nid, nid)
        values.put(epgColumn_field_url, field_url)
        values.put(epgColumn_posicion, posicion)
        db?.insert(EPG_TABLE, null, values)
    }

    fun addEPGProgramData(db: SQLiteDatabase?, epgId: String, title: String, descr: String, start: Double, stop: Double) {
        val values = ContentValues()
        values.put(epgProgramsColumn_epgId, epgId)
        values.put(epgProgramsColumn_title, title)
        values.put(epgProgramsColumn_descr, descr)
        values.put(epgProgramsColumn_start, start)
        values.put(epgProgramsColumn_stop, stop)
        db?.insert(EPG_PROGRAMS_TABLE, null, values)
    }

    fun addLanguage(db: SQLiteDatabase?, key: String, value: String, image: String) {
        val values = ContentValues()
        values.put(languagesColumn_field_key, key)
        values.put(languagesColumn_value, value)
        values.put(languagesColumn_image, image)
        db?.insert(LANGUAGES_TABLE, null, values)
    }

    fun addTranslation(db: SQLiteDatabase?, key: String, value: String) {
        val values = ContentValues()
        values.put(translationColumn_field_key, key)
        values.put(translationColumn_field_value, value)
        db?.insert(TRANSLATIONS_TABLE, null, values)
    }


    fun getLocalTableData(tableName: String): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $tableName", null)
    }

    fun getChannelData(query: String): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery(query, null)
    }

    companion object {
        private const val DATABASE_NAME = "OKTv"
        private val DATABASE_VERSION = 1
        const val OPERATOR_TABLE = "operators"
        const val DEVICES_TABLE = "devices"
        const val BANNERS_TABLE = "banners"
        const val CATEGORIES_TABLE = "categories"
        const val CHANNELS_TABLE = "channels"
        const val EPG_TABLE = "epg"
        const val EPG_PROGRAMS_TABLE = "epgPrograms"
        const val LANGUAGES_TABLE = "languages"
        const val TRANSLATIONS_TABLE = "translations"

        const val operatorColumn_nombre_operador = "nombre_operador"
        const val operatorColumn_max_devices = "max_devices"
        const val operatorColumn_operador = "operador"

        const val deviceColumn_field_id_opera = "field_id_opera"
        const val deviceColumn_pais_dispositivos = "pais_dispositivos"
        const val deviceColumn_node_id = "node_id"
        const val deviceColumn_field_dispositivo_mac = "field_dispositivo_mac"
        const val deviceColumn_field_dispositivo_nombre = "field_dispositivo_nombre"
        const val deviceColumn_field_dispositivo_ip = "field_dispositivo_ip"

        const val bannerColumn_field_position = "field_position"
        const val bannerColumn_status = "status"
        const val bannerColumn_field_active_banner = "field_active_banner"
        const val bannerColumn_type = "type"
        const val bannerColumn_field_imagen = "field_imagen"
        const val bannerColumn_field_automatic_slide = "field_automatic_slide"

        const val categoriesColumn_category = "category"
        const val categoriesColumn_id_category = "id_category"

        const val channelColumn_field_logo = "field_logo"
        const val channelColumn_field_channel_category = "field_channel_category"
        const val channelColumn_field_url = "field_url"
        const val channelColumn_field_videotype = "field_videotype"
        const val channelColumn_abreviacion = "abreviacion"
        const val channelColumn_field_genres = "field_genres"
        const val channelColumn_field_ad_play_back_on_start_of_v = "field_ad_play_back_on_start_of_v"
        const val channelColumn_field_type_of_censorship = "field_type_of_censorship"
        const val channelColumn_title = "title"
        const val channelColumn_field_id = "field_id"
        const val channelColumn_field_default = "field_default"
        const val channelColumn_protected_channel = "protected_channel"
        const val channelColumn_field_bitrate = "field_bitrate"
        const val channelColumn_posicion = "posicion"
        const val channelColumn_field_tags = "field_tags"
        const val channelColumn_type = "type"
        const val channelColumn_field_epg_code = "field_epg_code"
        const val channelColumn_nid = "nid"
        const val channelColumn_status = "status"
        const val channelColumn_EPG_ID_Channel = "EPG_ID_Channel"
        const val channelColumn_field_ad_play_back_url = "field_ad_play_back_url"
        const val channelColumn_tipo_transmision = "tipo_transmision"
        const val channelColumn_isLiked = "isLiked"
        const val channelColumn_favNodeId = "favNodeId"

        const val epgColumn_id = "id"
        const val epgColumn_displayName = "displayName"
        const val epgColumn_icon = "icon"
        const val epgColumn_nid = "nid"
        const val epgColumn_field_url = "field_url"
        const val epgColumn_posicion = "posicion"

        const val epgProgramsColumn_epgId = "epgId"
        const val epgProgramsColumn_title = "title"
        const val epgProgramsColumn_descr = "descr"
        const val epgProgramsColumn_start = "start"
        const val epgProgramsColumn_stop = "stop"

        const val languagesColumn_field_key = "field_key"
        const val languagesColumn_value = "value"
        const val languagesColumn_image = "image"

        const val translationColumn_field_key = "field_key"
        const val translationColumn_field_value = "field_value"

    }
}