package com.aeo.mylenses.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aeo.mylenses.util.Utility;

public class DB extends SQLiteOpenHelper {

	public static final String DB_NAME = "mylenses.db";
	public static final String TABLE_NAME_LENS = "lens";
	public static final String TABLE_NAME_REG_LENSES = "reg_lenses";
	public static final String TABLE_NAME_HISTORY = "history";
	private static final String SQL_LENS = "CREATE TABLE [lens] ([id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, [date_left] VARCHAR2(10), [date_right] VARCHAR2(10), [expiration_left] INTEGER, [expiration_right] INTEGER, [type_left] INTEGER, [type_right] INTEGER, [num_days_not_used_left] INTEGER DEFAULT 0, [num_days_not_used_right] INTEGER DEFAULT 0, [in_use_left] INTEGER, [in_use_right] INTEGER, [count_unit_left] INTEGER DEFAULT 1, [count_unit_right] INTEGER DEFAULT 1);";
	private static final String SQL_ALARM = "CREATE TABLE [alarm] ([hour] INTEGER, [minute] INTEGER, [days_before] INTEGER, [remind_every_day] INTEGER);";
	private static final String SQL_REG_LENSES = "CREATE TABLE [reg_lenses] ([id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, [description_left] VARCHAR2(200), [brand_left] VARCHAR2(50), [discard_type_left] INTEGER, [type_left] INTEGER, [power_left] INTEGER, [cylinder_left] INTEGER, [axis_left] INTEGER, [add_left] INTEGER, [buy_site_left] VARCHAR2(100), [date_ini_left] VARCHAR2(10), [number_units_left] INTEGER, [description_right] VARCHAR2(200), [brand_right] VARCHAR2(50), [discard_type_right] INTEGER, [type_right] INTEGER, [power_right] INTEGER, [cylinder_right] INTEGER, [axis_right] INTEGER, [add_right] INTEGER, [buy_site_right] VARCHAR2(100), [date_ini_right] VARCHAR2(10), [number_units_right] INTEGER);";
	private static final String SQL_HISTORY = "CREATE TABLE [history] ([id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, [id_lens] INTEGER, [id_reg_lenses] INTEGER, [date_hist] VARCHAR2(10), [date_left] VARCHAR2(10), [date_right] VARCHAR2(10), [expiration_left] INTEGER, [expiration_right] INTEGER, [type_time_left] INTEGER, [type_time_right] INTEGER, [num_days_not_used_left] INTEGER, [num_days_not_used_right] INTEGER, [in_use_left] INTEGER, [in_use_right] INTEGER, [description_left] VARCHAR2(200), [brand_left] VARCHAR2(50), [discard_type_left] INTEGER, [type_left] INTEGER, [power_left] INTEGER, [cylinder_left] INTEGER, [axis_left] INTEGER, [add_left] INTEGER, [buy_site_left] VARCHAR2(100), [date_ini_left] VARCHAR2(10), [number_units_left] INTEGER, [description_right] VARCHAR2(200), [brand_right] VARCHAR2(50), [discard_type_right] INTEGER, [type_right] INTEGER, [power_right] INTEGER, [cylinder_right] INTEGER, [axis_right] INTEGER, [add_right] INTEGER, [buy_site_right] VARCHAR2(100), [date_ini_right] VARCHAR2(10), [number_units_right] INTEGER);";
	private static final String SQL_DROP_LENS = "DROP TABLE [lens];";
	private static final String SQL_DROP_ALARM = "DROP TABLE [alarm];";
	private static final String SQL_ALTER_TABLE_LENS_1 = "alter table lens add column num_days_not_used_left INTEGER DEFAULT 0;";
	private static final String SQL_ALTER_TABLE_LENS_2 = "alter table lens add column num_days_not_used_right INTEGER DEFAULT 0;";
	private static final String SQL_ALTER_TABLE_LENS_3 = "alter table lens add column in_use_left INTEGER;";
	private static final String SQL_ALTER_TABLE_LENS_4 = "alter table lens add column in_use_right INTEGER;";
	private static final String SQL_ALTER_TABLE_LENS_5 = "alter table lens add column count_unit_left INTEGER DEFAULT 1;";
	private static final String SQL_ALTER_TABLE_LENS_6 = "alter table lens add column count_unit_right INTEGER DEFAULT 1;";
	private static final String SQL_ATER_TABLE_ALARM_1 = "alter table alarm add column remind_every_day INTEGER DEFAULT 1;";
	private static final int VERSION_1 = 1;
	private static final int VERSION_2 = 2;
	private static final int VERSION_3 = 3;
	private static final String SQL_INSERT_ALARM = "INSERT INTO [alarm] (hour, minute, days_before, remind_every_day) values (12, 0, 0, 1);";
	private static final String SQL_UPDATE_ALARM = "UPDATE [alarm] SET remind_every_day = 1;";

	private static String[] columns_lens = { "id", "date_left", "date_right",
			"expiration_left", "expiration_right", "type_left", "type_right",
			"num_days_not_used_left", "num_days_not_used_right", "in_use_left",
			"in_use_right" };

	public DB(Context context) {
		super(context, DB_NAME, null, VERSION_3);
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_LENS);
		db.execSQL(SQL_ALARM);
		db.execSQL(SQL_REG_LENSES);
		db.execSQL(SQL_HISTORY);
		db.execSQL(SQL_INSERT_ALARM);

		// Insert lenses data with currency date and 6 units
		ContentValues content = new ContentValues();
		content.put("date_ini_left",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		content.put("date_ini_right",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		content.put("number_units_left", "6");
		content.put("number_units_right", "6");
		db.insert(TABLE_NAME_REG_LENSES, null, content);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == VERSION_1 && newVersion == VERSION_2) {
			db.execSQL(SQL_DROP_LENS);
			db.execSQL(SQL_DROP_ALARM);
			onCreate(db);
		} else if (oldVersion == VERSION_2 && newVersion == VERSION_3) {
			setVersion3(db);
		} else if (oldVersion == VERSION_1 && newVersion == VERSION_3) {
			db.execSQL(SQL_DROP_LENS);
			db.execSQL(SQL_DROP_ALARM);
			onCreate(db);
			setVersion3(db);
		}
	}
	
	private void setVersion3(SQLiteDatabase db) {
		db.execSQL(SQL_REG_LENSES);
		db.execSQL(SQL_HISTORY);
		db.execSQL(SQL_ALTER_TABLE_LENS_1);
		db.execSQL(SQL_ALTER_TABLE_LENS_2);
		db.execSQL(SQL_ALTER_TABLE_LENS_3);
		db.execSQL(SQL_ALTER_TABLE_LENS_4);
		db.execSQL(SQL_ALTER_TABLE_LENS_5);
		db.execSQL(SQL_ALTER_TABLE_LENS_6);
		db.execSQL(SQL_ATER_TABLE_ALARM_1);
		db.execSQL(SQL_UPDATE_ALARM);

		/* Get initial date of lenses */
		Cursor c = db.query(TABLE_NAME_LENS, columns_lens, null, null,
				null, null, null);

		String idLens = null;
		String dateLeft = null;
		String dateRight = null;

		if (c.moveToFirst()) {
			idLens = c.getString(c.getColumnIndex("id"));
			dateLeft = Utility.formatDateToSqlite(c.getString(c
					.getColumnIndex("date_left")));
			dateRight = Utility.formatDateToSqlite(c.getString(c
					.getColumnIndex("date_right")));

			// Insert lenses data with last dates and 6 units
			ContentValues content = new ContentValues();
			content.put("date_ini_left", dateLeft);
			content.put("date_ini_right", dateRight);
			content.put("number_units_left", "6");
			content.put("number_units_right", "6");

			db.insert(TABLE_NAME_REG_LENSES, null, content);

			// Update/adjust lenses time (dates) with Sqlite date convention
			// "yyyy-MM-dd"
			ContentValues content1 = new ContentValues();
			content1.put("date_left", dateLeft);
			content1.put("date_right", dateRight);
			content1.put("in_use_left", "1");
			content1.put("in_use_right", "1");
//			content1.put("count_unit_left", "1");
//			content1.put("count_unit_right", "1");

			db.update(TABLE_NAME_LENS, content1, null, null);

			// Insert history
			ContentValues content2 = new ContentValues();
			content2.put("id_lens", idLens);
			content2.put("id_reg_lenses", "1");
			content2.put("date_hist", dateLeft);
			content2.put("date_left", dateLeft);
			content2.put("date_right", dateRight);
			content2.put("in_use_left", "1");
			content2.put("in_use_right", "1");
			content2.put("expiration_left",
					c.getString(c.getColumnIndex("expiration_left")));
			content2.put("expiration_right",
					c.getString(c.getColumnIndex("expiration_right")));
			content2.put("type_time_left",
					c.getString(c.getColumnIndex("type_left")));
			content2.put("type_time_right",
					c.getString(c.getColumnIndex("type_right")));
			content2.put("num_days_not_used_left", "0");
			content2.put("num_days_not_used_right", "0");
			content2.put("date_ini_left", dateLeft);
			content2.put("date_ini_right", dateRight);
			content2.put("number_units_left", "6");
			content2.put("number_units_right", "6");

			content2.put("type_left", "");
			content2.put("power_left", "");
			content2.put("cylinder_left", "");
			content2.put("axis_left", "");
			content2.put("add_left", "");
			content2.put("type_right", "");
			content2.put("power_right", "");
			content2.put("cylinder_right", "");
			content2.put("axis_right", "");
			content2.put("add_right", "");
			
			db.insert(TABLE_NAME_HISTORY, null, content2);
		}
	}
}
