package com.adwaymalhotra.parkstash.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.adwaymalhotra.parkstash.R
import com.adwaymalhotra.parkstash.model.MapItem
import com.adwaymalhotra.parkstash.presenter.MapPresenter
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.frag_map.*

/**
 * Created by adway on 27/03/18.
 */

public class MapFragment : Fragment(), IMapFragmentView, GoogleApiClient.ConnectionCallbacks {
    lateinit private var googleApiClient: GoogleApiClient
    lateinit private var placesAdapter: PlacesAutocompleteAdapter
    private var presenter = MapPresenter()
    private var place: Place? = null

    companion object {
        private val TAG = "MapFragment"
    }

    override fun onConnected(p0: Bundle?) {
        redrawMap()
        Log.d(TAG, "connected!")
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d(TAG, "connection suspended!")
    }

    override fun update(mapItem: MapItem) = mapView.getMapAsync {
        it.addMarker(MarkerOptions().position(LatLng(mapItem.lat, mapItem.lng)).title(mapItem.title))
    }

    override fun updateResults() = placesAdapter.notifyDataSetChanged()

    override fun getGeoDataApi(): GeoDataClient? {
        return Places.getGeoDataClient(context)
    }

    @SuppressLint("MissingPermission")
    private fun redrawMap() {
        mapView.getMapAsync {
            Log.d(TAG, "onmapready")
            val map = it
            map.uiSettings.isMyLocationButtonEnabled = false
            map.clear()
            presenter.markers
                    .forEach {
                        map.addMarker(MarkerOptions()
                                .position(LatLng(it.lat, it.lng))
                                .title(it.title))
                    }
            val x = presenter.markers[0]
            Log.d(TAG, "onmapready" + presenter.markers.size)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(x.lat, x.lng), 10.0F))
        }
    }

    override fun onStart() {
        super.onStart()
        googleApiClient.connect()
    }

    override fun onStop() {
        super.onStop()
        googleApiClient.disconnect()
    }

    override fun getSharedPreferences(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            layoutInflater.inflate(R.layout.frag_map, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.view = this
        presenter.loadMarkers()

        addButton.setOnClickListener { onSaveClicked() }
        searchBarLayout.setOnClickListener { showPlacesSearch() }
        googleApiClient = GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addApi(Places.GEO_DATA_API)
                .addOnConnectionFailedListener { it: ConnectionResult ->
                    Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show()
                }
                .addApi(LocationServices.API).build()
        mapView.onCreate(savedInstanceState)
        MapsInitializer.initialize(context)
        mapView.onResume()
    }


    @SuppressLint("RestrictedApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                place = PlaceAutocomplete.getPlace(context, data);
                searchBarLayout.setText(place!!.name)
                mapView.getMapAsync {
                    it.addMarker(MarkerOptions().position(place!!.latLng))
                    it.moveCamera(CameraUpdateFactory.newLatLng(place!!.latLng))
                }
            }
        }
    }

    private fun onSaveClicked() {
        if (place != null) {
            presenter.addPin(place!!)
        } else {
            mapView.getMapAsync {
                val x = it.cameraPosition.target
                presenter.addPin(x.latitude, x.longitude)
            }
        }
    }

    private fun showPlacesSearch() {
        var intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                .build(activity)
        startActivityForResult(intent, 0)
    }
}