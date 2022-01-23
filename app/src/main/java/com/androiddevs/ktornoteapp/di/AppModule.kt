package com.androiddevs.ktornoteapp.di

import android.content.Context
import androidx.room.Room
import com.androiddevs.ktornoteapp.data.local.DAO.NoteDAO
import com.androiddevs.ktornoteapp.data.remote.api.NoteApi
import com.androiddevs.ktornoteapp.data.remote.interceptors.BasicAuthInterceptor
import com.androiddevs.ktornoteapp.other.Constants.DATABASE_NAME
import com.androiddevs.ktornoteapp.preferences.BasicAuthPreferences
import com.androiddevs.ktornoteapp.repository.auth.AuthRepositoryImpl
import com.androiddevs.ktornoteapp.repository.main.MainRepositoryImpl
import com.vmakd1916gmail.com.login_logout_register.DB.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class) // live as long as application
object AppModule {

    @Singleton
    @Provides
    fun provideNotesDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, NoteDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideNoteDao(db: NoteDatabase) = db.noteDAO()

    @Singleton
    @Provides
    fun provideBasicAuthPreferences(@ApplicationContext context: Context) =
        BasicAuthPreferences(context)


    @Singleton
    @Provides
    fun provideBasicAuthInterceptor(basicAuthSharedPreferences: BasicAuthPreferences): BasicAuthInterceptor =
        BasicAuthInterceptor(basicAuthSharedPreferences)

    @Singleton
    @Provides
    fun provideOkHttpClient(basicAuthInterceptor: BasicAuthInterceptor): OkHttpClient {
        val trustAllCertificates: Array<TrustManager> = arrayOf(
            object: X509TrustManager{
                override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                    /*NO-OP*/
                }

                override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                    /*NO-OP*/
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCertificates, SecureRandom())
        return OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCertificates[0] as X509TrustManager)
            .hostnameVerifier(HostnameVerifier { _, _ -> true })
            .addInterceptor(basicAuthInterceptor)
            .build()
    }

    @Provides
    fun providesBaseUrl(): String = "https://10.0.2.2:8002"

    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL: String, client: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    @Singleton
    @Provides
    fun providePostApi(retrofit: Retrofit): NoteApi =
        retrofit.create(NoteApi::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(
        noteApi: NoteApi
    ): AuthRepositoryImpl = AuthRepositoryImpl(noteApi)

    @Provides
    @Singleton
    fun provideMainRepository(
        noteApi: NoteApi,
        noteDAO: NoteDAO
    ): MainRepositoryImpl = MainRepositoryImpl(noteApi, noteDAO)


}