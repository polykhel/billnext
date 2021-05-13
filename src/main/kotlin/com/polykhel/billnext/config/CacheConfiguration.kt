package com.polykhel.billnext.config

import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration
import com.polykhel.billnext.domain.*
import com.polykhel.billnext.repository.UserRepository
import org.hibernate.cache.jcache.ConfigSettings
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tech.jhipster.config.JHipsterProperties
import tech.jhipster.config.cache.PrefixedKeyGenerator
import java.util.OptionalLong
import java.util.concurrent.TimeUnit
import javax.cache.CacheManager

@Configuration
@EnableCaching
class CacheConfiguration(
    val gitProperties: GitProperties?,
    val buildProperties: BuildProperties?,
    jHipsterProperties: JHipsterProperties
) {

    private val jcacheConfiguration: javax.cache.configuration.Configuration<Any, Any>

    init {
        val caffeine = jHipsterProperties.cache.caffeine

        val caffeineConfiguration = CaffeineConfiguration<Any, Any>()
        caffeineConfiguration.maximumSize = OptionalLong.of(caffeine.maxEntries)
        caffeineConfiguration.expireAfterWrite = OptionalLong.of(TimeUnit.SECONDS.toNanos(caffeine.timeToLiveSeconds.toLong()))
        caffeineConfiguration.isStatisticsEnabled = true
        jcacheConfiguration = caffeineConfiguration
    }

    @Bean
    fun hibernatePropertiesCustomizer(cacheManager: CacheManager) = HibernatePropertiesCustomizer {
        hibernateProperties ->
        hibernateProperties[ConfigSettings.CACHE_MANAGER] = cacheManager
    }

    @Bean
    fun cacheManagerCustomizer(): JCacheManagerCustomizer {
        return JCacheManagerCustomizer { cm: CacheManager ->
            createCache(cm, UserRepository.USERS_BY_LOGIN_CACHE)
            createCache(cm, UserRepository.USERS_BY_EMAIL_CACHE)
            createCache(cm, User::class.java.name)
            createCache(cm, Authority::class.java.name)
            createCache(cm, User::class.java.name + ".authorities")
            createCache(cm, Wallet::class.java.name)
            createCache(cm, Wallet::class.java.name + ".activities")
            createCache(cm, WalletGroup::class.java.name)
            createCache(cm, WalletGroup::class.java.name + ".wallets")
            createCache(cm, Activity::class.java.name)
            createCache(cm, Category::class.java.name)
            createCache(cm, Category::class.java.name + ".activities")
            createCache(cm, Subcategory::class.java.name)
        }
    }

    private fun createCache(cm: CacheManager, cacheName: String) {
        val cache = cm.getCache<Any, Any>(cacheName)
        cache?.clear() ?: cm.createCache(cacheName, jcacheConfiguration)
    }

    @Bean
    fun keyGenerator() = PrefixedKeyGenerator(gitProperties, buildProperties)
}
