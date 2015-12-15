package com.aeo.mylenses.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.aeo.mylenses.vo.HistoryVO;
import com.aeo.mylenses.vo.LensVO;
import com.aeo.mylenses.vo.LensesVO;

public class HistoryDAO {

	private static String tableName = "history";
	private static String[] columns = { "id", "id_lens", "id_reg_lenses",
			"date_hist", "date_left", "date_right", "expiration_left",
			"expiration_right", "type_time_left", "type_time_right",
			"description_left", "brand_left", "discard_type_left", "type_left",
			"power_left", "cylinder_left", "axis_left", "add_left",
			"buy_site_left", "date_ini_left", "number_units_left",
			"description_right", "brand_right", "discard_type_right",
			"type_right", "power_right", "cylinder_right", "axis_right",
			"add_right", "buy_site_right", "date_ini_right",
			"number_units_right", "num_days_not_used_left",
			"num_days_not_used_right", "in_use_left", "in_use_right" };
	private SQLiteDatabase db;
	private static HistoryDAO instance;
	private Context context;
	private Integer idLastLens;

	BackupManager mBackupManager;

	public static HistoryDAO getInstance(Context context) {
		if (instance == null) {
			return new HistoryDAO(context);
		}
		return instance;
	}

	public HistoryDAO(Context context) {
		this.context = context;
		db = new DB(context).getWritableDatabase();
		mBackupManager = new BackupManager(context);
	}

	public boolean insert() {
		synchronized (MainActivity.sDataLock) {
			mBackupManager.dataChanged();
			return db.insert(tableName, null, getContentValues()) > 0;
		}
	}

	public boolean update() {
		synchronized (MainActivity.sDataLock) {
			mBackupManager.dataChanged();
			return db.update(tableName, getContentValues(), "id=?",
					new String[] { String.valueOf(getLastIdHistory()) }) > 0;
		}
	}

	public boolean delete(String id) {
		synchronized (MainActivity.sDataLock) {
			mBackupManager.dataChanged();
			return db.delete(tableName, "id=?", new String[] { id }) > 0;
		}
	}

	public boolean delete() {
		synchronized (MainActivity.sDataLock) {
			mBackupManager.dataChanged();
			return db.delete(tableName, null, null) > 0;
		}
	}

	@SuppressLint("SimpleDateFormat")
	public ContentValues getContentValues() {
		ContentValues content = new ContentValues();

		LensDAO lensDao = LensDAO.getInstance(context);
		LensVO lensVO = new LensVO();

		idLastLens = lensDao.getLastIdLens();
		lensVO = lensDao.getById(idLastLens);

		content.put("id_lens", lensVO.getId());

		LensesDataDAO lensesDataDao = LensesDataDAO.getInstance(context);
		LensesVO lensesVO = new LensesVO();
		lensesVO = lensesDataDao.getById(lensesDataDao.getLastIdLens());

		content.put("id_reg_lenses", lensesDataDao.getLastIdLens());
		content.put("date_hist",
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		if (lensVO != null) {
			content.put("date_left", Utility.formatDateToSqlite(lensVO.getDateLeft()));
			content.put("date_right", Utility.formatDateToSqlite(lensVO.getDateRight()));
			content.put("expiration_left", lensVO.getExpirationLeft());
			content.put("expiration_right", lensVO.getExpirationRight());
			content.put("type_time_left", lensVO.getTypeLeft());
			content.put("type_time_right", lensVO.getTypeRight());
			content.put("num_days_not_used_left",
					lensVO.getNumDaysNotUsedLeft());
			content.put("num_days_not_used_right",
					lensVO.getNumDaysNotUsedRight());
			content.put("in_use_left", lensVO.getInUseLeft());
			content.put("in_use_right", lensVO.getInUseRight());
		}
		if (lensesVO != null) {
			content.put("description_left", lensesVO.getDescription_left());
			content.put("brand_left", lensesVO.getBrand_left());
			content.put("discard_type_left", lensesVO.getDiscard_type_left());
			content.put("type_left", lensesVO.getType_left());
			content.put("power_left", lensesVO.getPower_left());
			content.put("cylinder_left", lensesVO.getCylinder_left());
			content.put("axis_left", lensesVO.getAxis_left());
			content.put("add_left", lensesVO.getAdd_left());
			content.put("buy_site_left", lensesVO.getBuy_site_left());
			content.put("date_ini_left",
					Utility.formatDateToSqlite(lensesVO.getDate_ini_left()));
			content.put("number_units_left", lensesVO.getNumber_units_left());
			content.put("description_right", lensesVO.getDescription_right());
			content.put("brand_right", lensesVO.getBrand_right());
			content.put("discard_type_right", lensesVO.getDiscard_type_right());
			content.put("type_right", lensesVO.getType_right());
			content.put("power_right", lensesVO.getPower_right());
			content.put("cylinder_right", lensesVO.getCylinder_right());
			content.put("axis_right", lensesVO.getAxis_right());
			content.put("add_right", lensesVO.getAdd_right());
			content.put("buy_site_right", lensesVO.getBuy_site_right());
			content.put("date_ini_right",
					Utility.formatDateToSqlite(lensesVO.getDate_ini_right()));
			content.put("number_units_right", lensesVO.getNumber_units_right());
		}
		return content;
	}

	public HistoryVO getById(Integer id) {
		Cursor c = db.query(tableName, columns, "id=?",
				new String[] { id.toString() }, null, null, null);

		HistoryVO vo = null;
		if (c.moveToFirst()) {
			vo = setHistoryVO(vo, c);
		}
		return vo;
	}

	public List<HistoryVO> getListHistory() {
		Cursor c = db.query(tableName, columns, null, null, null, null,
				"id desc");

		List<HistoryVO> listVO = new ArrayList<HistoryVO>();
		HistoryVO vo = null;

		while (c.moveToNext()) {
			listVO.add(setHistoryVO(vo, c));
		}
		return listVO;
	}

	public List<String> getListHistoryDate() {
		Cursor c = db.query(tableName, columns, null, null, null, null,
				"id desc");

		List<String> listVO = new ArrayList<String>();

		while (c.moveToNext()) {
			listVO.add(R.string.str_date_history
					+ c.getString(c.getColumnIndex("date_hist")));
		}
		return listVO;

	}

	@SuppressLint("SimpleDateFormat")
	private HistoryVO setHistoryVO(HistoryVO vo, Cursor c) {
		vo = new HistoryVO();

		vo.setId(c.getInt(c.getColumnIndex("id")));
		vo.setId_lens(c.getInt(c.getColumnIndex("id_lens")));
		vo.setId_reg_lenses(c.getInt(c.getColumnIndex("id_reg_lenses")));
		vo.setDate_hist(Utility.formatDateDefault(c.getString(c.getColumnIndex("date_hist"))));
		vo.setDate_left(Utility.formatDateDefault(c.getString(c.getColumnIndex("date_left"))));
		vo.setDate_right(Utility.formatDateDefault(c.getString(c.getColumnIndex("date_right"))));
		vo.setExpiration_left(c.getInt(c.getColumnIndex("expiration_left")));
		vo.setExpiration_right(c.getInt(c.getColumnIndex("expiration_right")));
		vo.setType_time_left(c.getInt(c.getColumnIndex("type_time_left")));
		vo.setType_time_right(c.getInt(c.getColumnIndex("type_time_right")));
		vo.setDescription_left(c.getString(c.getColumnIndex("description_left")));
		vo.setBrand_left(c.getString(c.getColumnIndex("brand_left")));
		vo.setDiscard_type_left(c.getString(c
				.getColumnIndex("discard_type_left")));
		vo.setType_left(c.getString(c.getColumnIndex("type_left")));
		vo.setPower_left(c.getString(c.getColumnIndex("power_left")));
		vo.setCylinder_left(c.getString(c.getColumnIndex("cylinder_left")));
		vo.setAxis_left(c.getString(c.getColumnIndex("axis_left")));
		vo.setAdd_left(c.getString(c.getColumnIndex("add_left")));
		vo.setBuy_site_left(c.getString(c.getColumnIndex("buy_site_left")));
		vo.setDate_ini_left(Utility.formatDateDefault(c.getString(c
				.getColumnIndex("date_ini_left"))));
		vo.setNumber_units_left(c.getInt(c.getColumnIndex("number_units_left")));
		vo.setDescription_right(c.getString(c
				.getColumnIndex("description_right")));
		vo.setBrand_right(c.getString(c.getColumnIndex("brand_right")));
		vo.setDiscard_type_right(c.getString(c
				.getColumnIndex("discard_type_right")));
		vo.setType_right(c.getString(c.getColumnIndex("type_right")));
		vo.setPower_right(c.getString(c.getColumnIndex("power_right")));
		vo.setCylinder_right(c.getString(c.getColumnIndex("cylinder_right")));
		vo.setAxis_right(c.getString(c.getColumnIndex("axis_right")));
		vo.setAdd_right(c.getString(c.getColumnIndex("add_right")));
		vo.setBuy_site_right(c.getString(c.getColumnIndex("buy_site_right")));
		vo.setDate_ini_right(Utility.formatDateDefault(c.getString(c
				.getColumnIndex("date_ini_right"))));
		vo.setNumber_units_right(c.getInt(c
				.getColumnIndex("number_units_right")));
		vo.setNum_days_not_used_left(c.getInt(c
				.getColumnIndex("num_days_not_used_left")));
		vo.setNum_days_not_used_right(c.getInt(c
				.getColumnIndex("num_days_not_used_right")));
		vo.setIn_use_left(c.getInt(c.getColumnIndex("in_use_left")));
		vo.setIn_use_right(c.getInt(c.getColumnIndex("in_use_right")));
		return vo;
	}

	public int getLastIdHistory() {
		Cursor rs = db.rawQuery("select max(id) from " + tableName, null);

		if (rs.moveToFirst()) {
			return rs.getInt(0);
		}
		return 0;
	}

	public String getDateIniLeft() {
		Cursor c = db.rawQuery("select date_ini_left from " + tableName
				+ " order by id desc limit 1", null);

		if (c.moveToFirst()) {
			return c.getString(0);
		}
		return null;
	}

	public String getDateIniRight() {
		Cursor c = db.rawQuery("select date_ini_right from " + tableName
				+ " order by id desc limit 1", null);

		if (c.moveToFirst()) {
			return c.getString(0);
		}
		return null;
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
			sql.append("select count(distinct(id_lens)) ").append("from ")
					.append(tableName)
					.append(" where Datetime(date_left) >= Datetime('")
					.append(getDateIniLeft()).append("')")
					.append(" and id_reg_lenses = ")
					.append(LensesDataDAO.getInstance(context).getLastIdLens());
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
			sql.append("select count(distinct(id_lens)) from ")
					.append(tableName)
					.append(" where Datetime(date_right) >= Datetime('")
					.append(getDateIniRight()).append("')")
					.append(" and id_reg_lenses = ")
					.append(LensesDataDAO.getInstance(context).getLastIdLens());
			Cursor rs = db.rawQuery(sql.toString(), null);

			if (rs.moveToFirst()) {
				return totalUnitsRight - rs.getInt(0);
			}
		}

		return totalUnitsRight;
	}

}
