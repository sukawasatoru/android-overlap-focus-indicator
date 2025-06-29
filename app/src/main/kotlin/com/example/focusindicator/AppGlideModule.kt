/*
 * Copyright 2022, 2025 sukawasatoru
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.focusindicator

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.EncodeStrategy
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestOptions
import java.io.InputStream
import okhttp3.OkHttpClient

@GlideModule
@Excludes(OkHttpLibraryGlideModule::class)
class AppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val okHttp = OkHttpClient.Builder().build()
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(okHttp),
        )
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultRequestOptions(
            RequestOptions().diskCacheStrategy(AppDiskCacheStrategy),
        )
    }

    override fun isManifestParsingEnabled(): Boolean = false
}

private object AppDiskCacheStrategy : DiskCacheStrategy() {
    override fun isDataCacheable(dataSource: DataSource): Boolean {
        // Default Fetcher returns the LOCAL or the REMOTE always.
        return when (dataSource) {
            DataSource.LOCAL,
            DataSource.REMOTE -> true

            DataSource.DATA_DISK_CACHE,
            DataSource.RESOURCE_DISK_CACHE,
            DataSource.MEMORY_CACHE ->
                throw IllegalStateException("[isDataCacheable] $dataSource")
        }
    }

    override fun isResourceCacheable(
        isFromAlternateCacheKey: Boolean,
        dataSource: DataSource,
        encodeStrategy: EncodeStrategy,
    ): Boolean {
        if ((dataSource == DataSource.REMOTE || dataSource == DataSource.DATA_DISK_CACHE) &&
            encodeStrategy == EncodeStrategy.TRANSFORMED
        ) {
            return true
        }

        return false
    }

    override fun decodeCachedResource(): Boolean = true
    override fun decodeCachedData(): Boolean = true
}
