package com.example.spendwise.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        Expense::class,
        Category::class,
        RecurringExpense::class,
        SavingsGoal::class,
        SplitGroup::class,
        SplitMember::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun recurringExpenseDao(): RecurringExpenseDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun splitDao(): SplitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spendwise_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.categoryDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(categoryDao: CategoryDao) {
            categoryDao.insertCategory(Category(name = "Food", iconResId = 0, colorHex = "#FF5722"))
            categoryDao.insertCategory(Category(name = "Transport", iconResId = 0, colorHex = "#03A9F4"))
            categoryDao.insertCategory(Category(name = "Shopping", iconResId = 0, colorHex = "#E91E63"))
            categoryDao.insertCategory(Category(name = "Entertainment", iconResId = 0, colorHex = "#9C27B0"))
            categoryDao.insertCategory(Category(name = "Health", iconResId = 0, colorHex = "#4CAF50"))
            categoryDao.insertCategory(Category(name = "Others", iconResId = 0, colorHex = "#607D8B"))
        }
    }
}
