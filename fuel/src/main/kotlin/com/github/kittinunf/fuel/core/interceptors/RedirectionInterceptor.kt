package com.github.kittinunf.fuel.core.interceptors

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Encoding
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import javax.net.ssl.HttpsURLConnection

class RedirectException() : Exception("Redirection fail, not found URL to redirect to")

fun redirectResponseInterceptor() =
        { next: (Request, Response) -> Response ->
            { request: Request, response: Response ->
                if (response.httpStatusCode == HttpsURLConnection.HTTP_MOVED_PERM ||
                        response.httpStatusCode == HttpsURLConnection.HTTP_MOVED_TEMP) {
                    val redirectedUrl = response.httpResponseHeaders["Location"]
                    if (redirectedUrl != null && !redirectedUrl.isEmpty()) {
                        val encoding = Encoding().apply {
                            httpMethod = request.httpMethod
                            urlString = redirectedUrl[0]
                        }
                        Fuel.request(encoding).response().second
                    } else {
                        //error
                        val error = FuelError().apply {
                            exception = RedirectException()
                            errorData = response.data
                            this.response = response
                        }
                        throw error
                    }
                } else {
                    next(request, response)
                }
            }
        }

 
