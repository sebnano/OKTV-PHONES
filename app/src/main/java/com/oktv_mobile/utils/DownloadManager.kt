package com.oktv_mobile.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.lifecycle.*
import com.oktv_mobile.R
import com.oktv_mobile.retrofit.model.UserDetailClass
import com.oktv_mobile.ui.activity.HomeActivity
import com.oktv_mobile.ui.model.homemodel.HomeBannerModel
import com.oktv_mobile.ui.model.loginmodel.ConfiguracionModel
import com.oktv_mobile.ui.model.loginmodel.LanguageStringModel
import com.oktv_mobile.ui.viewmodel.HomeVM
import com.oktv_mobile.ui.viewmodel.LoginVM
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_setting.*

object DownloadManager {

    private lateinit var applicationContext: Context
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var viewModelStoreOwner: ViewModelStoreOwner
    private var databaseHandler: DBHelper? = null
    private var db: SQLiteDatabase? = null

    private lateinit var loginVM : LoginVM
    private lateinit var homeVM : HomeVM

    private val _data = MutableLiveData<String>()
    val data: LiveData<String> = _data

    private val _langData = MutableLiveData<String>()
    val langData: LiveData<String> = _langData

    private val _langKeywordsData = MutableLiveData<String>()
    val langKeywordsData: LiveData<String> = _langKeywordsData

    fun initialize(context: Context, lifecycleOwner: LifecycleOwner, viewModelStoreOwner: ViewModelStoreOwner) {
        this.applicationContext = context.applicationContext
        this.lifecycleOwner = lifecycleOwner
        this.viewModelStoreOwner = viewModelStoreOwner
        databaseHandler = DBHelper(applicationContext, null)

        loginVM = ViewModelProvider(viewModelStoreOwner, ViewModelProvider.NewInstanceFactory())[LoginVM::class.java]
        homeVM = ViewModelProvider(viewModelStoreOwner, ViewModelProvider.NewInstanceFactory())[HomeVM::class.java]

//        databaseHandler!!.truncateDataFromTable(DBHelper.BANNERS_TABLE)
//        databaseHandler!!.truncateDataFromTable(DBHelper.OPERATOR_TABLE)
//        databaseHandler!!.truncateDataFromTable(DBHelper.DEVICES_TABLE)
//        databaseHandler!!.truncateDataFromTable(DBHelper.CATEGORIES_TABLE)
//        databaseHandler!!.truncateDataFromTable(DBHelper.CHANNELS_TABLE)
    }

    fun getOperator() {
        db = databaseHandler!!.writableDatabase
        _data.postValue("showLoader")
        if (databaseHandler!!.getRowCounts(db, DBHelper.OPERATOR_TABLE) > 0) {
            getDevices()
        } else {
            loginVM.operator(MyPreferences.getFromPreferences(applicationContext, Constant.USERID))
            loginVM.observeOperator().observe(lifecycleOwner) {
                Log.i("DownloadManager", "observeOperator called")
                if (it != null) {
                    db?.execSQL("DELETE FROM ${DBHelper.OPERATOR_TABLE}")
                    for (item in it) {
                        databaseHandler!!.addOperator(db, item.operatorName, item.maxDevices, item.operador)
                    }
                    loginVM.observeOperator().removeObservers(lifecycleOwner)
                    getDevices()
                }
            }
        }
    }

    private fun getDevices() {
        if (databaseHandler!!.getRowCounts(db, DBHelper.DEVICES_TABLE) > 0) {
            getBanners()
        } else {
            homeVM.userDeviceList(
                MyPreferences.getFromPreferences(applicationContext, Constant.USERID),
                MyPreferences.getFromPreferences(applicationContext, Constant.OPERATORID))
            homeVM.observeDevices().observe(lifecycleOwner) {
                Log.i("DownloadManager", "observeDevices called")
                if (it != null) {
                    db?.execSQL("DELETE FROM ${DBHelper.DEVICES_TABLE}")
                    for (item in it) {
                        databaseHandler!!.addDevices(db, item.operatorId, item.pais_dispositivos, item.nodeId, item.deviceMac, item.deviceTitle, item.deviceIp)
                    }
                    homeVM.observeDevices().removeObservers(lifecycleOwner)
                    getBanners()
                }
            }
        }
    }

    private fun getBanners() {
        if (databaseHandler!!.getRowCounts(db, DBHelper.BANNERS_TABLE) > 0) {
            getCategories()
        } else {
            homeVM.banner(MyPreferences.getFromPreferences(applicationContext, Constant.OPERATORID))
            homeVM.observeBanner().observe(lifecycleOwner) {
                Log.i("DownloadManager", "observeBanner called")
                if (it != null) {
                    db?.execSQL("DELETE FROM ${DBHelper.BANNERS_TABLE}")
                    for (item in it) {
                        databaseHandler!!.addBanners(db, item.fieldPosition, item.status, item.field_active_banner, item.type, item.fieldImage, item.field_automatic_slide)
                    }
                    homeVM.observeBanner().removeObservers(lifecycleOwner)
                    getCategories()
                }
            }
        }
    }

    private fun getCategories() {
        if (databaseHandler!!.getRowCounts(db, DBHelper.CATEGORIES_TABLE) > 0) {
            getChannels()
        } else {
            homeVM.channelCategory(MyPreferences.getFromPreferences(applicationContext, Constant.OPERATORID))
            homeVM.observeChannelCategory().observe(lifecycleOwner) {
                Log.i("DownloadManager", "observeChannelCategory called")
                if (it != null) {
                    db?.execSQL("DELETE FROM ${DBHelper.CATEGORIES_TABLE}")
                    val arrCategoryId = ArrayList<String>()
                    for (item in it) {
                        if (!arrCategoryId.contains(item.idCategory)) {
                            arrCategoryId.add(item.idCategory)
                            databaseHandler!!.addCategories(db, item.category, item.idCategory)
                        }
                    }
                    homeVM.observeChannelCategory().removeObservers(lifecycleOwner)
                    getChannels()
                }
            }
        }
    }

    private fun getChannels() {
        if (databaseHandler!!.getRowCounts(db, DBHelper.CHANNELS_TABLE) > 0) {
            getFavouriteChannels()
        } else {
            homeVM.allChannelList(MyPreferences.getFromPreferences(applicationContext, Constant.OPERATORID))
            homeVM.observeAllChannels().observe(lifecycleOwner) {
                Log.i("DownloadManager", "observeAllChannels called")
                if (it != null) {
                    db?.execSQL("DELETE FROM ${DBHelper.CHANNELS_TABLE}")
                    for (item in it) {
                        databaseHandler!!.addChannels(
                            db,
                            item.fieldLogo,
                            item.fieldChannelCategory,
                            item.fieldUrl,
//                            item.fieldVideoType,
//                            item.abreviacion,
//                            item.fieldGenres,
//                            item.fieldAdPlayBackOnStartOfV,
//                            item.fieldTypeOfCensorship,
                            item.title,
//                            item.fieldId,
//                            item.fieldDefault,
                            item.protectedChannel,
//                            item.fieldBitrate,
                            item.posicion,
//                            item.fieldTags,
//                            item.type,
//                            item.fieldEpgCode,
                            item.nid,
//                            item.status,
                            item.EPG_ID_Channel,
//                            item.fieldAdPlayBackUrl,
//                            item.tipoTransmision
                        )
                    }
                    homeVM.observeAllChannels().removeObservers(lifecycleOwner)
                    getFavouriteChannels()
                }
            }
        }
    }

    private fun getFavouriteChannels() {
        homeVM.getUserFavourite(
            MyPreferences.getFromPreferences(applicationContext, Constant.USERID),
            MyPreferences.getFromPreferences(applicationContext, Constant.OPERATORID))
        homeVM.observeFavourite().observe(lifecycleOwner) {
            Log.i("DownloadManager", "observeFavourite called")
            if (it != null) {
                for (item in it) {
                    databaseHandler!!.updateChannel(db, item.title, 1, item.nid)
                }
                homeVM.observeFavourite().removeObservers(lifecycleOwner)
                db?.close()
                _data.postValue("hideLoader")
            }
        }
    }

    fun getLanguages() {
        db = databaseHandler!!.writableDatabase
        _langData.postValue("showLoader")
        if (databaseHandler!!.getRowCounts(db, DBHelper.LANGUAGES_TABLE) > 0) {
            db?.close()
            _langData.postValue("hideLoader")
        } else {
            loginVM.getLanguageNameList()
            loginVM.observeLanguageName().observe(lifecycleOwner) { it ->
                Log.i("DownloadManager", "observeLanguageName called")
                if (it != null) {
                    db?.execSQL("DELETE FROM ${DBHelper.LANGUAGES_TABLE}")
                    for (item in it) {
                        databaseHandler!!.addLanguage(db, item.key, item.value, item.image)
                    }
                    loginVM.observeLanguageName().removeObservers(lifecycleOwner)
                    db?.close()
                    _langData.postValue("hideLoader")
                }
            }
        }
    }

    fun getTranslations(languageCode: String) {
        db = databaseHandler!!.writableDatabase
        _langKeywordsData.postValue("showLoader")
        if (databaseHandler!!.getRowCounts(db, DBHelper.TRANSLATIONS_TABLE) > 0) {
            db?.close()
            _langKeywordsData.postValue("hideLoader")
        } else {
            loginVM.getLanguageStringList(languageCode)
            loginVM.observeLanguageString().observe(lifecycleOwner) {
                Log.i("DownloadManager", "observeLanguageString called")
                if (it != null) {
                    val languageList = ArrayList<LanguageStringModel>()
                    db?.execSQL("DELETE FROM ${DBHelper.TRANSLATIONS_TABLE}")
                    for (item in it) {
                        databaseHandler!!.addTranslation(db, item.key, item.value)
                        languageList.add(LanguageStringModel(item.key, item.value))
                    }
                    SharedPreferencesHelper.saveArrayList(applicationContext, languageList)
                    db?.close()
                    _langKeywordsData.postValue("hideLoader")
                    loginVM.observeLanguageString().removeObservers(lifecycleOwner)
                }
            }
        }

    }
}