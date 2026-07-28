package org.lelestacia.posle.util

interface RunnableService {
    fun enqueue(id: String, serializedData: String)
}
