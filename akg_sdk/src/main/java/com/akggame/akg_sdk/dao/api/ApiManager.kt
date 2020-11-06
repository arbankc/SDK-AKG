package com.akggame.akg_sdk.dao.api

import android.os.Build
import android.util.Log
import com.akggame.akg_sdk.IConfig
import com.akggame.akg_sdk.dao.api.model.TLSSocketFactory
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class ApiManager {
    companion object {
        private var mApi: Any? = null
        fun initApiService(
            apiDomain: String,
            allowUntrusted: Boolean,
            clazz: Class<IApi>,
            timeout: Int,
            enableLoggingHttp: Boolean
        ): Any {
            val retrofit = Retrofit.Builder()
                .baseUrl(apiDomain)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getHttpClient(true, IConfig.TIMEOUT_LONG_INSECOND, true))
                .build()
            mApi = retrofit.create(clazz)

            return mApi as Any
        }


        fun getHttpClient(allowUntrustedSSL: Boolean, timeout: Int, enableLoggingHttp: Boolean): OkHttpClient {

            val httpClient = OkHttpClient.Builder()

            if (allowUntrustedSSL) {
                allowUntrustedSSL(httpClient)
            }//    implementation 'com.github.acan12:coconut:2.0.13'

            var trustManager = object :X509TrustManager{
                override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {

                }

                override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {

                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    val cArrr = arrayOf<X509Certificate>()
                    return cArrr
                }
            }
            try {
                val sc = SSLContext.getInstance("TLS")
                sc.init(null, null, null)
                var noSSLv3Factory :SSLSocketFactory?= null
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){
                    noSSLv3Factory = TLSSocketFactory(sc.socketFactory)
                } else {
                    noSSLv3Factory = sc.socketFactory
                }
                httpClient.sslSocketFactory(noSSLv3Factory,trustManager)

            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }


            httpClient.connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
            httpClient.readTimeout(timeout.toLong(), TimeUnit.SECONDS)
            httpClient.writeTimeout(timeout.toLong(), TimeUnit.SECONDS)

            if (enableLoggingHttp) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                httpClient.addInterceptor(logging)
            }


            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("User-Agent", "base")
                    .header("Accept", "application/json")
                    .method(original.method(), original.body())
                    .build()

                chain.proceed(request)
            }

            return httpClient.build()
        }

        private fun allowUntrustedSSL(httpClient: OkHttpClient.Builder) {

            Log.w("", "**** Allow untrusted SSL connection ****")
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    val cArrr = arrayOfNulls<X509Certificate>(0)
                    return cArrr
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>,
                    authType: String
                ) {
                }
            })

            var sslContext: SSLContext? = null
            try {
                sslContext = SSLContext.getInstance("SSL")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

            try {
                sslContext!!.init(null, trustAllCerts, java.security.SecureRandom())
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }

            httpClient.sslSocketFactory(sslContext!!.socketFactory)

            val hostnameVerifier = HostnameVerifier { hostname, session ->
                Log.d("", "Trust Host :$hostname")
                true
            }
            httpClient.hostnameVerifier(hostnameVerifier)

        }

    }
}