package de.kevcodez.gotrue

import de.kevcodez.gotrue.http.GoTrueHttpClient
import de.kevcodez.gotrue.json.GoTrueJsonConverter
import de.kevcodez.gotrue.types.*

open class GoTrueClient(
        private val goTrueHttpClient: GoTrueHttpClient,
        private val goTrueJsonConverter: GoTrueJsonConverter
) {

    /**
     * Returns the publicly available settings for this GoTrue instance.
     */
    fun settings(): GoTrueSettings {
        val response = goTrueHttpClient.get(
                path = "/settings"
        )

        return goTrueJsonConverter.deserialize(response, GoTrueSettings::class)
    }

    /**
     * Register a new user with an email and password.
     */
    fun signup(email: String, password: String): GoTrueUserResponse {
        val response = goTrueHttpClient.post(
                path = "/signup",
                data = mapOf("email" to email, "password" to password)
        )!!

        return goTrueJsonConverter.deserialize(response, GoTrueUserResponse::class)
    }

    /**
     * Invites a new user with an email.
     */
    fun invite(email: String): GoTrueUserResponse {
        val response = goTrueHttpClient.post(
                path = "/invite",
                data = mapOf("email" to email)
        )!!

        return goTrueJsonConverter.deserialize(response, GoTrueUserResponse::class)
    }

    /**
     * Verify a registration or a password recovery.
     * Type can be signup or recovery and the token is a token returned from either /signup or /recover.
     */
    fun verify(type: GoTrueVerifyType, token: String, password: String? = null): GoTrueTokenResponse {
        val response = goTrueHttpClient.post(
                path = "/verify",
                data = mapOf("type" to type.name.toLowerCase(), "token" to token, "password" to password),
        )!!

        return goTrueJsonConverter.deserialize(response, GoTrueTokenResponse::class)
    }

    /**
     * Password recovery. Will deliver a password recovery mail to the user based on email address.
     */
    fun recover(email: String) {
        goTrueHttpClient.post(
                path = "/recover",
                data = mapOf("email" to email)
        )
    }

    /**
     * Update a user (Requires authentication).
     * Apart from changing email/password, this method can be used to set custom user data.
     */
    fun updateUser(accessToken: String, email: String? = null, password: String? = null, data: Map<String, Any>? = null): GoTrueUserResponse {
        val response = goTrueHttpClient.put(
                path = "/user",
                headers = mapOf("Authorization" to "Bearer $accessToken"),
                data = mapOf("email" to email, "password" to password, "data" to data)
        )

        return goTrueJsonConverter.deserialize(response, GoTrueUserResponse::class)
    }

    /**
     * Get the JSON object for the logged in user
     */
    fun getUser(accessToken: String): GoTrueUserResponse {
        val response = goTrueHttpClient.get(
                path = "/user",
                headers = mapOf("Authorization" to "Bearer $accessToken")
        )

        return goTrueJsonConverter.deserialize(response, GoTrueUserResponse::class)
    }

    /**
     * This is an OAuth2 endpoint that currently implements the password, refresh_token, and authorization_code grant types
     */
    fun token(grantType: GoTrueGrantType, email: String? = null, password: String? = null, refreshToken: String? = null): GoTrueTokenResponse {
        val response = goTrueHttpClient.post(
                path = "/token?grant_type=${grantType.name}",
                data = mapOf("email" to email, "password" to password, "refresh_token" to refreshToken),
        )!!

        return goTrueJsonConverter.deserialize(response, GoTrueTokenResponse::class)
    }

    /**
     * Logout a user.
     * This will revoke all refresh tokens for the user.
     * Remember that the JWT tokens will still be valid for stateless auth until they expires.
     */
    fun logout(accessToken: String) {
        goTrueHttpClient.post(
                path = "/logout",
                headers = mapOf("Authorization" to "Bearer $accessToken")
        )
    }

}
