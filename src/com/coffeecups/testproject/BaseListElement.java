package com.coffeecups.testproject;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.BaseAdapter;

public abstract class BaseListElement {
	private Drawable icon;
	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	private String name;
	private String surname;
	private int requestCode;
	private BaseAdapter adapter;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		this.surname = surname;
	}

	public void setAdapter(BaseAdapter adapter) {
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
		this.adapter = adapter;
	}

	public BaseListElement(Drawable icon, String name, String surname,
			int requestCode) {
		this.icon = icon;
		this.name = name;
		this.surname = surname;
		this.requestCode = requestCode;
	}

	protected abstract View.OnClickListener getOnClickListener();
}
