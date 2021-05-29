package com.passbasedemo.detectme.data.api.repository.datasource

interface FileRemoteDataSource {
    suspend fun uploadVideo()
}