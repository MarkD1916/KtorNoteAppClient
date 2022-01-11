package com.androiddevs.ktornoteapp.di

import android.content.Context
import androidx.room.Room
import com.androiddevs.ktornoteapp.data.remote.api.NoteApi
import com.androiddevs.ktornoteapp.data.remote.interceptors.BasicAuthInterceptor
import com.androiddevs.ktornoteapp.other.Constants.BASE_URL
import com.androiddevs.ktornoteapp.other.Constants.DATABASE_NAME
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
    fun provideBasicAuthInterceptor(email: String?, password: String?): BasicAuthInterceptor =
        BasicAuthInterceptor(email, password)


    @Provides
    fun provideOkHttpClient(basicAuthInterceptor: BasicAuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(basicAuthInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    @Singleton
    @Provides
    fun providePostApi(retrofit: Retrofit): NoteApi =
        retrofit.create(NoteApi::class.java)

}