package com.adwaymalhotra.parkstash.view

import android.content.SharedPreferences
import android.location.Geocoder
import com.adwaymalhotra.parkstash.model.MapItem
import com.google.android.gms.location.places.GeoDataClient

/**
 * Created by adway on 27/03/18.
 */
interface IMapFragmentView {
    fun getSharedPreferences(): SharedPreferences
    fun update(mapItem: MapItem)
    fun updateResults()
    fun getGeoDataApi(): GeoDataClient?
}