package com.wavecell.sample.app.models

data class AuthRequestBody(val userId: String, val accountId: String, val expIn: String? = null)