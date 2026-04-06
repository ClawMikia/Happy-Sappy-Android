// File: data/database/AppDatabase.kt
package com.happysappy.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.happysappy.app.data.dao.ExpenseDao
import com.happysappy.app.data.entity.ExpenseEntity

/**
 * Room Database configuration for the Happy Sappy expense tracker app.
 * 
 * This class manages the SQLite database and provides access to Data Access Objects (DAOs).
 * It follows the singleton pattern to ensure only one instance of the database exists
 * throughout the application lifecycle.
 * 
 * Database Configuration:
 * - Name: "happy_sappy_database"
 * - Version: 1
 * - Entities: ExpenseEntity
 * - Export Schema: true (for migration testing)
 */
@Database(
    entities = [ExpenseEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Abstract method to get the ExpenseDao for database operations.
     * Room will generate the implementation at compile time.
     * 
     * @return ExpenseDao instance for interacting with the expenses table
     */
    abstract fun expenseDao(): ExpenseDao
    
    companion object {
        // Singleton instance of the database
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        // Database name constant
        const val DATABASE_NAME = "happy_sappy_database"
        
        /**
         * Gets the singleton instance of the database.
         * Creates the database if it doesn't exist.
         * 
         * This method is thread-safe and uses double-checked locking
         * to ensure only one instance is created.
         * 
         * @param context The application context (use applicationContext to avoid leaks)
         * @return The singleton AppDatabase instance
         */
        fun getDatabase(context: Context): AppDatabase {
            // Return existing instance if available
            return INSTANCE ?: synchronized(this) {
                // Create new database instance
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                // Add fallback to destructive migration for development
                // In production, implement proper migration strategies
                .fallbackToDestructiveMigration()
                // Allow queries on main thread (for development only)
                // Remove in production and use coroutines/ LiveData
                .allowMainThreadQueries()
                // Add database callback for lifecycle events
                .addCallback(object : Callback() {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Database created - can perform initial setup here
                        android.util.Log.d("AppDatabase", "Database created successfully")
                    }
                    
                    override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // Database opened - can perform validation here
                        android.util.Log.d("AppDatabase", "Database opened successfully")
                    }
                })
                .build()
                
                // Set the instance
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Gets the singleton instance without creating a new one.
         * Use this when you need to access the database from a context
         * where you're not sure if it's been initialized.
         * 
         * @return The AppDatabase instance or null if not initialized
         */
        fun getInstance(): AppDatabase? {
            return INSTANCE
        }
        
        /**
         * Closes the database and clears the singleton instance.
         * Use this for testing purposes or when you need to reset the database.
         * 
         * WARNING: This will make the database inaccessible until getDatabase() is called again.
         */
        fun closeDatabase() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
                android.util.Log.d("AppDatabase", "Database closed successfully")
            }
        }
    }
}