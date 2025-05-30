package com.time.app.model

import java.util.UUID


data class HyperFocusPlanProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val plan: HyperFocus = HyperFocus(),
    val createdAt: Long = System.currentTimeMillis())


