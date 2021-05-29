package com.passbasedemo.detectme.data.api.repository.datasourceImpl

import com.passbasedemo.detectme.data.api.APIService
import com.passbasedemo.detectme.data.api.repository.datasource.FileRemoteDataSource

class FileRemoteDataSourceImpl(
    private val apiService: APIService
) :FileRemoteDataSource {
    override suspend fun uploadVideo() {
        apiService.upload()
    }
}