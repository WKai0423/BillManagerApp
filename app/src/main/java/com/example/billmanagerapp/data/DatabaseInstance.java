package com.example.billmanagerapp.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**

  Room数据库初始化

 * */

public class DatabaseInstance {
    private static AppDatabase instance;

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
           // 用SQL 语句，手动创建新表
            db.execSQL("CREATE TABLE IF NOT EXISTS `material_items` (" + "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + "`materialName` TEXT, " + "`materialPrice` REAL NOT NULL)");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE invoice ADD COLUMN invoiceNumber TEXT");
        }
    };

    public static AppDatabase getDatabase(Context context){

        if (instance == null) {
            // 初始化 Room 数据库，只执行一次
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),  // 用 Application Context 防止内存泄漏
                            AppDatabase.class,
                            "bill_manager_db"                // 数据库名
                    )
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .build();                          // ⚠️ 不再使用 allowMainThreadQueries()
        }
        return instance;

    }
}
