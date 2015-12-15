package com.aeo.mylenses.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aeo.mylenses.R;
import com.aeo.mylenses.activity.MainActivity;
import com.aeo.mylenses.db.DB;
import com.aeo.mylenses.util.Utility;
import com.aeo.mylenses.vo.LensVO;
import com.aeo.mylenses.vo.LensesVO;

public class LensDAO {

	private static String tableName = "lens";
	private static String[] columns = { "id", "date_left", "date_right",
			"expiration_left", "expiration_right", "type_left", "type_right",
			"num_days_not_used_left", "num_days_not_used_right", "in_use_left",
			"in_use_right", "count_unit_left", "count_unit_right" };
	private SQLiteDatabase db;
	private static LensDAO instance;
	public static final String LEFT = "LEFT";
	public static final String RIGHT = "RIGHT";

	/** Also cache a reference to the Backup Manager */
	BackupManager mBackupManager;
	private Context context;

	public static LensDAO getInstance(Context context) {
		if (instance == null) {
			instance = new LensDAO(context);
		}
		return instance;
	}

	public LensDAO(Context context) {
		db = new DB(context).getWritableDatabase();
		/** It is handy to keep a BackupManager cached */
		mBackupManager = new BackupManager(context);
		this.context = context;
	}

	public boolean insert(LensVO lensVO) {
		synchronized (MainActivity.sDataLock) {
			mBackupManager.dataChanged();
			return db.insert(tableName, null, getContentValues(lensVO)) > 0;
		}
	}

	public boolean update(LensVO lensVO) {
		synchronized (MainActivity.sDataLock) {
			ContentValues content = new ContentValues();
			content.put("date_left",
					Utility.formatDateToSqlite(lensVO.getDateLeft()));
			content.put("date_right",
					Utility.formatDateToSqlite(lensVO.getDateRight()));
			content.put("expiration_left", lensVO.getExpirationLeft());
			content.put("expiration_right", lensVO.getExpirationRight());
			content.put("type_left", lensVO.getTypeLeft());
			content.put("type_right", lensVO.getTypeRight());
			content.put("in_use_left", lensVO.getInUseLeft());
			content.put("in_use_right", lensVO.getInUseRight());
			content.put("count_unit_left", lensVO.getCountUnitLeft());
			content.put("count_unit_right", lensVO.getCountUnitRight());
			mBackupManager.dataChanged();
			return db.update(tableName, content, "id=?", new String[] { lensVO
					.getId().toString() }) > 0;
		}
	}

	public boolean incrementDaysNotUsed(LensVO lensVO) {
		synchronized (MainActivity.sDataLock) {
			// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			ContentValues content = new ContentValues();
			if (lensVO.getInUseLeft() == 1) {
				content.put("num_days_not_used_left",
						lensVO.getNumDaysNotUsedLeft() + 1);
			}
			if (lensVO.getInUseRight() == 1) {
				content.put("num_days_not_used_right",
						lensVO.getNumDaysNotUsedRight() + 1);
			}
			mBackupManager.dataChanged();
			return db.update(tableName, content, "id=?", new String[] { lensVO
					.getId().toString() }) > 0;
		}
	}

	public boolean updateDaysNotUsed(int days, String side, int idLens) {
		synchronized (MainActivity.sDataLock) {
			ContentValues content = new ContentValues();
			if (LEFT.equals(side)) {
				content.put("num_days_not_used_left", days);
			} else {
				content.put("num_days_not_used_right", days);
			}

			mBackupManager.dataChanged();
			return db.update(tableName, content, "id=?",
					new String[] { String.valueOf(idLens) }) > 0;
		}
	}

	public boolean delete(Integer id) {
		synchronized (MainActivity.sDataLock) {
			mBackupManager.dataChanged();
			return db.delete(tableName, "id=?", new String[] { id.toString() }) > 0;
		}
	}

	private ContentValues getContentValues(LensVO lensVO) {
		ContentValues content = new ContentValues();
		content.put("date_left",
				Utility.formatDateToSqlite(lensVO.getDateLeft()));
		content.put("date_right",
				Utility.formatDateToSqlite(lensVO.getDateRight()));
		content.put("expiration_left", lensVO.getExpirationLeft());
		content.put("expiration_right", lensVO.getExpirationRight());
		content.put("type_left", lensVO.getTypeLeft());
		content.put("type_right", lensVO.getTypeRight());
		content.put("num_days_not_used_left", lensVO.getNumDaysNotUsedLeft());
		content.put("num_days_not_used_right", lensVO.getNumDaysNotUsedRight());
		content.put("in_use_left", lensVO.getInUseLeft());
		content.put("in_use_right", lensVO.getInUseRight());
		content.put("count_unit_left", lensVO.getCountUnitLeft());
		content.put("count_unit_right", lensVO.getCountUnitRight());

		return content;
	}

	public LensVO getById(Integer id) {
		Cursor cursor = db.query(tableName, columns, "id=?",
				new String[] { id.toString() }, null, null, null);

		LensVO vo = null;
		if (cursor.moveToFirst()) {
			vo = setLensVO(cursor);
		}
		return vo;
	}

	public List<LensVO> getListLens() {
		Cursor cursor = db.query(tableName, columns, null, null, null, null,
				"id desc");

		List<LensVO> listVO = new ArrayList<LensVO>();

		while (cursor.moveToNext()) {
			listVO.add(setLensVO(cursor));
		}
		return listVO;
	}

	private LensVO setLensVO(Cursor cursor) {
		LensVO vo = new LensVO();
		vo.setId(cursor.getInt(cursor.getColumnIndex("id")));
		vo.setDateLeft(Utility.formatDateDefault(cursor.getString(cursor
				.getColumnIndex("date_left"))));
		vo.setDateRight(Utility.formatDateDefault(cursor.getString(cursor
				.getColumnIndex("date_right"))));
		vo.setExpirationLeft(cursor.getInt(cursor
				.getColumnIndex("expiration_left")));
		vo.setExpirationRight(cursor.getInt(cursor
				.getColumnIndex("expiration_right")));
		vo.setTypeLeft(cursor.getInt(cursor.getColumnIndex("type_left")));
		vo.setTypeRight(cursor.getInt(cursor.getColumnIndex("type_right")));
		vo.setInUseLeft(cursor.getInt(cursor.getColumnIndex("in_use_left")));
		vo.setInUseRight(cursor.getInt(cursor.getColumnIndex("in_use_right")));
		vo.setCountUnitLeft(cursor.getInt(cursor
				.getColumnIndex("count_unit_left")));
		vo.setCountUnitRight(cursor.getInt(cursor
				.getColumnIndex("count_unit_right")));
		vo.setNumDaysNotUsedLeft(cursor.getInt(cursor
				.getColumnIndex("num_days_not_used_left")));
		vo.setNumDaysNotUsedRight(cursor.getInt(cursor
				.getColumnIndex("num_days_not_used_right")));

		return vo;
	}

	public int getLastIdLens() {
		Cursor cursor = db.rawQuery("select max(id) from " + tableName, null);

		if (cursor.moveToFirst()) {
			return cursor.getInt(0);
		}
		return 0;
	}

	public LensVO getLastLens() {
		Cursor cursor = db.rawQuery("select * from " + tableName
				+ " order by id desc limit 1", null);

		if (cursor.moveToFirst()) {
			return setLensVO(cursor);
		}

		return null;
	}

	@SuppressLint("SimpleDateFormat")
	public Long[] getDaysToExpire(int idLenses) {
		long daysExpLeft = 0;
		long daysExpRight = 0;

		Calendar[] calendars = getDateAlarm(idLenses);

		Calendar dateExpLeft = Calendar.getInstance();
		Calendar dateExpRight = Calendar.getInstance();

		dateExpLeft.setTime(calendars[0].getTime());
		dateExpRight.setTime(calendars[1].getTime());

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		Date dateReplaceLeft = null;
		Date dateReplaceRight = null;
		Date dateToday = null;

		try {
			dateReplaceLeft = dateFormat.parse(new StringBuilder()
					.append(dateExpLeft.get(Calendar.DAY_OF_MONTH))
					.append("/")
					.append(String.format("%02d",
							(dateExpLeft.get(Calendar.MONTH) + 1))).append("/")
					.append(dateExpLeft.get(Calendar.YEAR)).toString());

			dateReplaceRight = dateFormat.parse(new StringBuilder()
					.append(dateExpRight.get(Calendar.DAY_OF_MONTH))
					.append("/")
					.append(String.format("%02d",
							(dateExpRight.get(Calendar.MONTH) + 1)))
					.append("/").append(dateExpRight.get(Calendar.YEAR))
					.toString());

			dateToday = dateFormat.parse(dateFormat.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long dayLeft = dateReplaceLeft.getTime();
		long dayRight = dateReplaceRight.getTime();
		long dayToday = dateToday.getTime();
		long index = (24 * 60 * 60 * 1000);

		daysExpLeft = (dayLeft - dayToday) / index;
		daysExpRight = (dayRight - dayToday) / index;

		return new Long[] { daysExpLeft, daysExpRight };
	}

	@SuppressLint("SimpleDateFormat")
	public Calendar[] getDateAlarm(int id) {
		Calendar dateExpLeft = Calendar.getInstance();
		Calendar dateExpRight = Calendar.getInstance();

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		int totalDaysLeft = 0;
		int totalDaysRight = 0;

		LensVO lensVO = getById(id);
		if (lensVO != null) {
			int expirationLeft = lensVO.getExpirationLeft();
			int expirationRight = lensVO.getExpirationRight();
			int dayNotUsedLeft = lensVO.getNumDaysNotUsedLeft();
			int dayNotUsedRight = lensVO.getNumDaysNotUsedRight();

			try {
				if (lensVO.getDateLeft() != null) {
					if (lensVO.getTypeLeft() == 0) {
						totalDaysLeft = expirationLeft;
					} else if (lensVO.getTypeLeft() == 1) {
						totalDaysLeft = expirationLeft * 30;
					} else if (lensVO.getTypeLeft() == 2) {
						totalDaysLeft = expirationLeft * 365;
					}
					dateExpLeft.setTime(dateFormat.parse(lensVO.getDateLeft()));
					int totalLeft = totalDaysLeft + dayNotUsedLeft;
					dateExpLeft.add(Calendar.DATE, totalLeft);
				}
				if (lensVO.getDateRight() != null) {
					if (lensVO.getTypeRight() == 0) {
						totalDaysRight = expirationRight;
					} else if (lensVO.getTypeRight() == 1) {
						totalDaysRight = expirationRight * 30;
					} else if (lensVO.getTypeRight() == 2) {
						totalDaysRight = expirationRight * 365;
					}

					dateExpRight
							.setTime(dateFormat.parse(lensVO.getDateRight()));
					int totalRight = totalDaysRight + dayNotUsedRight;
					dateExpRight.add(Calendar.DATE, totalRight);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return new Calendar[] { dateExpLeft, dateExpRight };
	}

	public int getSaldoLensLeft() {
		int totalUnitsLeft = 0;
		LensesVO lensesVO = LensesDataDAO.getInstance(context).getLastLenses();

		if (lensesVO != null) {
			totalUnitsLeft = Integer.valueOf(context.getResources()
					.getStringArray(R.array.array_num_units)[LensesDataDAO
					.getInstance(context).getLastLenses()
					.getNumber_units_left()]);

			StringBuilder sql = new StringBuilder();
			sql.append("select count(id) ").append("from ").append(tableName)
					.append(" where Datetime(date_left) >= Datetime('")
					.append(HistoryDAO.getInstance(context).getDateIniLeft())
					.append("')")
					.append(" and in_use_left = 1 and count_unit_left = 1");
			Cursor rs = db.rawQuery(sql.toString(), null);

			if (rs.moveToFirst()) {
				return totalUnitsLeft - rs.getInt(0);
			}
		}

		return totalUnitsLeft;
	}

	public int getSaldoLensRight() {
		int totalUnitsRight = 0;
		LensesVO lensesVO = LensesDataDAO.getInstance(context).getLastLenses();

		if (lensesVO != null) {
			totalUnitsRight = Integer.valueOf(context.getResources()
					.getStringArray(R.array.array_num_units)[LensesDataDAO
					.getInstance(context).getLastLenses()
					.getNumber_units_right()]);

			StringBuilder sql = new StringBuilder();
			sql.append("select count(id) ").append("from ").append(tableName)
					.append(" where Datetime(date_left) >= Datetime('")
					.append(HistoryDAO.getInstance(context).getDateIniRight())
					.append("')")
					.append(" and in_use_right = 1 and count_unit_right = 1");
			Cursor rs = db.rawQuery(sql.toString(), null);

			if (rs.moveToFirst()) {
				return totalUnitsRight - rs.getInt(0);
			}
		}

		return totalUnitsRight;
	}

	@SuppressLint("SimpleDateFormat")
	public void save(LensVO lensVO) {
		LensDAO lensDAO = LensDAO.getInstance(context);
		AlarmDAO alarmDAO = AlarmDAO.getInstance(context);
		LensesDataDAO lensesDataDAO = LensesDataDAO.getInstance(context);

		// When units == 0, set currency date to lenses data
		if (lensDAO.getSaldoLensLeft() == 0) {
			lensesDataDAO.updateDate("date_ini_left", lensesDataDAO
					.getLastIdLens(), new SimpleDateFormat("yyyy-MM-dd")
					.format(new Date()));
		}
		if (lensDAO.getSaldoLensRight() == 0) {
			lensesDataDAO.updateDate("date_ini_right", lensesDataDAO
					.getLastIdLens(), new SimpleDateFormat("yyyy-MM-dd")
					.format(new Date()));
		}

		int idLens = lensVO.getId() == null ? 0 : lensVO.getId();
		if (idLens != 0) {
			// lensVO.setId(idLens);
			if (!lensVO.equals(lensDAO.getById(idLens))) {
				if (lensDAO.update(lensVO)) {
					HistoryDAO.getInstance(context).insert();
					alarmDAO.setAlarm(idLens);
				}
			}
		} else {
			if (lensDAO.insert(lensVO)) {
				alarmDAO.setAlarm(idLens);
				HistoryDAO.getInstance(context).insert();
			}
		}
	}
}
