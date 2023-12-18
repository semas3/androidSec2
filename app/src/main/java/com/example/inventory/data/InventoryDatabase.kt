package com.example.inventory.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.sqlcipher.database.SupportFactory
import java.security.KeyPairGenerator
import java.security.KeyStore

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Item::class], version = 3, exportSchema = false)
abstract class InventoryDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        private const val ALIAS = "item_key"

        @Volatile
        private var Instance: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            return Instance ?: synchronized(this) {
                Instance ?: buildDatabase(context).also { Instance = it }
            }
        }

        private fun buildDatabase(context: Context): InventoryDatabase {
            val cipherKey = getCipherKey() ?: createCipherKey()
            return Room.databaseBuilder(
                context.applicationContext,
                InventoryDatabase::class.java,
                "item_database"
            )
            .openHelperFactory(SupportFactory(cipherKey))
            .fallbackToDestructiveMigration()
            .build()
        }

        private fun getCipherKey(): ByteArray? {
            val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
            }
            val entry: KeyStore.Entry = ks.getEntry(ALIAS, null) ?: return null
            if (entry !is KeyStore.PrivateKeyEntry) {
                return null
            }

            return entry.privateKey.encoded
        }

        private fun createCipherKey(): ByteArray? {
            val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC,
                "AndroidKeyStore"
            )

            val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
                ALIAS,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            ).run {
                setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                build()
            }

            kpg.initialize(parameterSpec)
            kpg.generateKeyPair()

            val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
            }

            val entry: KeyStore.Entry = ks.getEntry(ALIAS, null)
            if (entry is KeyStore.PrivateKeyEntry) {
                return entry.privateKey.encoded
            }

            return null
        }
    }
}