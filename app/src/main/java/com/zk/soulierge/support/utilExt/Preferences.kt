package com.zk.soulierge.support.utilExt

/**
 * Created by jayeshparkariya on 27/2/18.
 */

import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.preference.PreferenceManager
import com.google.gson.Gson
import java.io.File


const val IS_LOGIN = "is_login"
const val IS_REMEMBER = "is_remember"
const val LOGIN_TOKEN = "login_token"
const val USERID = "user_id"
const val USER_NAME = "user_name"
const val USERDATA = "userdata"
const val LECAMEDIADATA = "lecamediadata"
const val FIRSTNAME = "first_name"
const val QUOTE_ID = "quote_id"
const val LASTNAME = "last_name"
const val PROFILEPIC = "profile_pic"
const val ROLEID = "role_id"
const val COMPANYID = "companyid"
const val FCM_TOKEN = "fcm"
const val PASSWORD = "password"
const val IS_EMAIL = "is_email"
const val COUNTRY_CODE = "country_code"
const val MOBILE_NUM = "mobile_num"
const val SELECTED_LANGUAGE = "selected_language"
const val ADMIN_TOKEN = "admin_token"

//const val TITLE = "title"
//const val USERNAME = "username"

val Context.getPreferences: SharedPreferences
    get() {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

fun SharedPreferences.clear() {
    apply(getEditor().clear())
}

fun SharedPreferences.putBoolean(key: String, value: Boolean) {
    apply(getEditor().putBoolean(key, value))
}

fun SharedPreferences.putFloat(key: String, value: Float) {
    apply(getEditor().putFloat(key, value))
}

fun SharedPreferences.putInt(key: String, value: Int) {
    apply(getEditor().putInt(key, value))
}

fun SharedPreferences.putLong(key: String, value: Long) {
    apply(getEditor().putLong(key, value))
}

fun SharedPreferences.putString(key: String, value: String?) {
    apply(getEditor().putString(key, value))
}

fun SharedPreferences.putStringSet(key: String, values: Set<String>?) {
    apply(getEditor().putStringSet(key, values))
}

fun SharedPreferences.remove(key: String) {
    apply(getEditor().remove(key))
}

fun SharedPreferences.bulk(): SharedPreferences {
    this.bulkEditor = this.edit()
    return this
}

fun SharedPreferences.applyBulk(): SharedPreferences {
    this.bulkEditor?.apply()
    return this
}

fun SharedPreferences.discardBulk(): SharedPreferences {
    this.bulkEditor = null
    return this
}

/*
 * -----------------------------------------------------------------------------
 *  Private fields
 * -----------------------------------------------------------------------------
 */
private var SharedPreferences.bulkEditor: SharedPreferences.Editor?
    get() = this.bulkEditor
    set(editor: SharedPreferences.Editor?) {
        this.bulkEditor = editor
    }

/*
 * -----------------------------------------------------------------------------
 *  Private methods
 * -----------------------------------------------------------------------------
 */
private fun SharedPreferences.getEditor(): SharedPreferences.Editor {
    return this.edit()
}

private fun SharedPreferences.apply(editor: SharedPreferences.Editor) {
    editor.apply()
}

fun Context.setIsLogin(has: Boolean) {
    return getPreferences.putBoolean(IS_LOGIN, has)
}

fun Context.isLogin(): Boolean {
    return getPreferences.getBoolean(IS_LOGIN, false)
}

fun Context.setIsEmail(has: Boolean) {
    return getPreferences.putBoolean(IS_EMAIL, has)
}
fun Context.isEmail(): Boolean {
    return getPreferences.getBoolean(IS_EMAIL, false)
}
fun Context.setCountryCode(has: String?) {
    return getPreferences.putString(COUNTRY_CODE, has)
}

fun Context.getCountryCode(): String? {
    return getPreferences.getString(COUNTRY_CODE,"+966")
}
fun Context.setMobileNo(has: String?) {
    return getPreferences.putString(MOBILE_NUM, has)
}

fun Context.getMobileNo(): String? {
    return getPreferences.getString(MOBILE_NUM, "")
}

fun Context.setIsRemember(has: Boolean) {
    return getPreferences.putBoolean(IS_REMEMBER, has)
}

fun Context.IsRemember(): Boolean {
    return getPreferences.getBoolean(IS_REMEMBER, true)
}

fun Context.setLoginToken(token: String) {
    return getPreferences.putString(LOGIN_TOKEN, token)
}

fun Context.getLoginToken(): String? {
    return getPreferences.getString(LOGIN_TOKEN, "")
}

fun Context.setFCMToken(token: String?) {
    return getPreferences.putString(FCM_TOKEN, token)
}

fun Context.getFCMToken(): String? {
    return getPreferences.getString(FCM_TOKEN, "")
}

fun Context.setUserData(data: String) {
    return getPreferences.putString(USERDATA, data)
}

fun Context.getUserData(): String? {
    return getPreferences.getString(USERDATA, "")
}

inline fun <reified T> Context.getUserData(): T? {
    return Gson().fromJson(getPreferences.getString(USERDATA, ""), T::class.java)
}

fun Context.setLecaMediaData(data: String) {
    return getPreferences.putString(LECAMEDIADATA, data)
}

fun Context.getLecaMediaData(): String? {
    return getPreferences.getString(LECAMEDIADATA, "")
}

fun Context.setUserId(userid: String) {
    return getPreferences.putString(USERID, userid)
}

fun Context.getUserId(): String? {
    return getPreferences.getString(USERID, "")
}

//
fun Context.setUserName(userName: String) {
    return getPreferences.putString(USER_NAME, userName)
}

fun Context.getUserName(): String? {
    return getPreferences.getString(USER_NAME, "")
}

fun Context.setSelectedLanguage(selectedLanguage: String?) {
    return getPreferences.putString(SELECTED_LANGUAGE, selectedLanguage)
}

fun Context.getSelectedLanguage(): String? {
    return getPreferences.getString(SELECTED_LANGUAGE, "en")
}

fun Context.setAdminToken(adminToken: String) {
    return getPreferences.putString(ADMIN_TOKEN, adminToken)
}

fun Context.getAdminToken(): String? {
    return getPreferences.getString(ADMIN_TOKEN, "")
}

fun Context.setPassword(password: String) {
    return getPreferences.putString(PASSWORD, password)
}

fun Context.getPassword(): String? {
    return getPreferences.getString(PASSWORD, "")
}

fun Context.setQuoteID(quoteId: Int) {
    return getPreferences.putInt(QUOTE_ID, quoteId)
}

fun Context.getQuoteID(): Int? {
    return getPreferences.getInt(QUOTE_ID, 0)
}


//
fun Context.setFirstName(firstname: String) {
    return getPreferences.putString(FIRSTNAME, firstname)
}

fun Context.getFirstName(): String? {
    return getPreferences.getString(FIRSTNAME, "")
}

fun Context.setLastName(lastname: String) {
    return getPreferences.putString(LASTNAME, lastname)
}

fun Context.getLastName(): String? {
    return getPreferences.getString(LASTNAME, "")
}

fun Context.setProfilePic(profil_pic: String) {
    return getPreferences.putString(PROFILEPIC, profil_pic)
}

fun Context.getProfilePic(): String? {
    return getPreferences.getString(PROFILEPIC, "")
}

//
fun Context.setRoleId(role_id: Int) {
    return getPreferences.putInt(ROLEID, role_id)
}

fun Context.getRoleId(): Int? {
    return getPreferences.getInt(ROLEID, -1)
}

//
fun Context.setCompanyId(companyid: Int) {
    return getPreferences.putInt(COMPANYID, companyid)
}

fun Context.getCompanyId(): Int? {
    return getPreferences.getInt(COMPANYID, -1)
}
//
//fun Context.setTitle(title: String) {
//    return getPreferences.putString(TITLE, title)
//}
//
//fun Context.getTitle(): String? {
//    return getPreferences.getString(TITLE, "")
//}
//
//fun Context.setUserName(username: String) {
//    return getPreferences.putString(USERNAME, username)
//}
//
//fun Context.getUserName(): String? {
//    return getPreferences.getString(USERNAME, "")
//}


fun Context.clearUserData() {
    getPreferences.remove(IS_LOGIN)
    getPreferences.remove(USERDATA)
    getPreferences.remove(LOGIN_TOKEN)
    getPreferences.remove(USERID)
    getPreferences.remove(FIRSTNAME)
    getPreferences.remove(LASTNAME)
    getPreferences.remove(PROFILEPIC)
    getPreferences.remove(LECAMEDIADATA)
    getPreferences.remove(ROLEID)
    getPreferences.remove(COMPANYID)
//    getPreferences.remove(TITLE)
//    getPreferences.remove(USERNAME)

    if (!getPreferences.getBoolean(IS_REMEMBER, false)) {
        getPreferences.remove(USER_NAME)
        getPreferences.remove(PASSWORD)
        getPreferences.remove(IS_REMEMBER)
    }


    val notificationManager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancelAll()

    var mFile2: File? = File(Environment.getExternalStorageDirectory(), ".lecaMedia")

    mFile2?.deleteRecursively()

    val externalFilesDir =
        applicationContext?.getExternalFilesDir(Environment.getDataDirectory().absolutePath + "/pdfs")
    externalFilesDir?.deleteRecursively()
}
