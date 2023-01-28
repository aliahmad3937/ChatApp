package com.trimad.ichat.singlton

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class MySingleton private constructor(private var ctx: Context) {
    private var requestQueue: RequestQueue?
    private fun getRequestQueue(): RequestQueue {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext())
        }
        return requestQueue as RequestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>?) {
        getRequestQueue().add(req)
    }

    companion object {
        private var instance: MySingleton? = null
        @Synchronized
        fun getInstance(context: Context): MySingleton? {
            if (instance == null) {
                instance = MySingleton(context)
            }
            return instance
        }
    }

    init {
        requestQueue = getRequestQueue()
    }
}