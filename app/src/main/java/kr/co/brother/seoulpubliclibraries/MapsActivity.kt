package kr.co.brother.seoulpubliclibraries

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kr.co.brother.seoulpubliclibraries.data.Library

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //서울 도서관 인증키 6557544e4170686a3336705652794a

    private lateinit var mMap: GoogleMap
    //private lateinit var  binding: ActivityMapsBinding 왜 오류가 나는지 모르겠음

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        loadLibraries()

        mMap.setOnMarkerClickListener {
            if(it.tag != null){
                var url=it.tag as String
                if(!url.startsWith("http")){
                    url="http://${url}"
                }
                val intent= Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
            true
        }
    }

        //717쪽
        fun loadLibraries() {
            val retrofit = Retrofit.Builder()
                .baseUrl(SeoulOpenApi.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val seoulOpenService = retrofit.create(SeoulOpenService::class.java)

            seoulOpenService
                .getLibrary(SeoulOpenApi.API_KEY)
                .enqueue(object : Callback<Library> {
                    override fun onResponse(
                        call: Call<Library>,
                        response: Response<Library>
                    ) { //방금 () 이거 잘못해서 ctrl+i가 안되었음
                        showLibraries(response.body() as Library)
                    }

                    override fun onFailure(call: Call<Library>, t: Throwable) {
                        Toast.makeText(
                            baseContext, "서버에서 데이터를 가져올 수 없습니다",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                })
        }

        fun showLibraries(libraries: Library) {
            val latLngBounds=LatLngBounds.builder()
            for(lib in libraries.SeoulPublicLibraryInfo.row){
                val position=LatLng(lib.XCNTS.toDouble(),lib.YDNTS.toDouble())
                val marker=MarkerOptions().position(position).title(lib.LBRRY_NAME)

                var obj=mMap.addMarker(marker)
                obj.tag=lib.HMPG_URL

                latLngBounds.include(marker.position)
            }
            val bounds=latLngBounds.build()
            val padding=0
            val updated=CameraUpdateFactory.newLatLngBounds(bounds,padding)
            mMap.moveCamera(updated)
        }
    }
