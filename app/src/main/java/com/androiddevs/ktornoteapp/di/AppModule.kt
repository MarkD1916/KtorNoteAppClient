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
import javax.inject.Singleton

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


    @Provides
    fun provideOkHttpClient(basicAuthInterceptor: BasicAuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(basicAuthInterceptor)

            .build()
    }

    @Provides
    fun providesBaseUrl(): String = "http://10.0.2.2:8001"

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