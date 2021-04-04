package kr.co.brother.seoulpubliclibraries

import retrofit2.Call
import kr.co.brother.seoulpubliclibraries.data.Library
import retrofit2.http.GET
import retrofit2.http.Path

class SeoulOpenApi {
    companion object{
        val DOMAIN="http://openapi.seoul.go.kr:8088/"
        val API_KEY="6557544e4170686a3336705652794a"
    }
}

interface SeoulOpenService{
    @GET("{api_key}/json/SeoulPublicLibraryInfo/1/200")
    fun getLibrary(@Path("api_key")key:String): Call<Library>
}