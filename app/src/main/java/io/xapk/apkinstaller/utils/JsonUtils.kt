package io.xapk.apkinstaller.utils

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.lang.reflect.Type

object JsonUtils {
    val gson by lazy { GsonBuilder().excludeFieldsWithoutExposeAnnotation().create() }

    fun <T> objectFromJson(json: String, classOfT: Class<T>): T? {
        return try {
            gson.fromJson(json, classOfT)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> objectFromJson(json: Reader, classOfT: Class<T>): T? {
        return try {
            gson.fromJson(json, classOfT)
        } catch (e: Exception) {
            null
        }

    }

    fun <T> objectFromJson(json: String, typeOfT: Type): T? {
        return try {
            gson.fromJson<T>(json, typeOfT)
        } catch (e: Exception) {
            null
        }
    }

    fun <T> objectFromJson(json: Reader, typeOfT: Type): T? {
        return try {
            gson.fromJson<T>(json, typeOfT)
        } catch (e: Exception) {
            null
        }

    }

    fun stringListFromJson(json: String): List<String>? {
        return try {
            objectFromJson<List<String>>(json, object : TypeToken<List<String>>() {}.type)
        } catch (e: Exception) {
            null
        }
    }

    fun stringArrayJson(json: String): Array<String>? {
        return try {
            gson.fromJson<Array<String>>(json, object : TypeToken<Array<String>>() {}.type)
        } catch (e: Exception) {
            null
        }
    }

    fun objectToJson(src: Any): String {
        return gson.toJson(src)
    }
}
