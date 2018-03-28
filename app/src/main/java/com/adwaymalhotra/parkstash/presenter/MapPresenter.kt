package com.adwaymalhotra.parkstash.presenter

import android.util.Log
import com.adwaymalhotra.parkstash.model.MapItem
import com.adwaymalhotra.parkstash.view.IMapFragmentView
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by adway on 27/03/18.
 */

class MapPresenter {
    lateinit var view: IMapFragmentView
    private inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
    var markers = ArrayList<MapItem>()
    val results: ArrayList<String> = ArrayList<String>()

    fun addPin(place: Place) {
        addPin(place.latLng.latitude, place.latLng.longitude, place.name)
    }

    private fun addPin(lat: Double, lng: Double, name: CharSequence?) {
        val x = MapItem(lat, lng)
        x.title = name.toString()
        markers.add(x)
        view.getSharedPreferences().edit().putString("json", Gson().toJson(markers)).apply()
        view.update(x)
    }

    fun addPin(lat: Double, lng: Double) {
        val x = MapItem(lat, lng)
        markers.add(x)
        view.getSharedPreferences().edit().putString("json", Gson().toJson(markers)).apply()
        view.update(x)
    }

    fun loadMarkers() {
        val json = view.getSharedPreferences().getString("json", "")
        if (json.isNotEmpty())
            markers.addAll(Gson().fromJson<List<MapItem>>(json))
        else {
            markers.add(MapItem(40.42, -74.0))
        }
    }

    fun doSearch(query: CharSequence?) {
        results.clear()
        Log.d("Presenter", "searching")
        view.getGeoDataApi()!!.getAutocompletePredictions(
                query.toString(), LatLngBounds.Builder()
                .include(LatLng(40.42, 74.0))
                .include(LatLng(45.0, 74.0))
                .include(LatLng(40.42, 79.0))
                .include(LatLng(45.0, 79.0)).build(), null)
                .addOnCompleteListener {
                    Log.d("Presenter", "complete")
                    print(it.isSuccessful)
                    if (it.isSuccessful) {
                        print(it.result.count())
                        it.result.forEach {
                            results.add(it.getFullText(null).toString())
                        }
                    }
                }
//        r.forEach {
//            results.add(it.featureName)
//        }
        view.updateResults()
    }

}