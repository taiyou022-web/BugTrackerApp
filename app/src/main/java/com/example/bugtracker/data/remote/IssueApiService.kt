package com.example.bugtracker.data.remote

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Body
import retrofit2.http.Path

interface IssueApiService {

    @GET("issues")
    suspend fun getIssues(): List<IssueDto>

    @POST("issues")
    suspend fun createIssue(@Body issue: IssueDto): IssueDto

    @PUT("issues/{id}")
    suspend fun updateIssue(
        @Path("id") id: Int,
        @Body issue: IssueDto
    ): IssueDto

    @DELETE("issues/{id}")
    suspend fun deleteIssue(@Path("id") id: Int)
}