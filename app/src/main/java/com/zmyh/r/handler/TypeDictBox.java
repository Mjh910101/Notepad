package com.zmyh.r.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zmyh.r.box.TypeDictObj;

public class TypeDictBox {

	private Map<String, TypeDictObj> map = new HashMap<String, TypeDictObj>();
	private List<TypeDictObj> list;

	public TypeDictBox(List<TypeDictObj> items) {
		if (items != null) {
			this.list = items;
		} else {
			this.list = new ArrayList<TypeDictObj>();
		}
		for (TypeDictObj obj : list) {
			map.put(obj.getMu_sz_type(), obj);
		}
	}

	public List<String> getMuZsTypeList() {
		List<String> typeList = new ArrayList<String>();
		for (TypeDictObj obj : list) {
			typeList.add(obj.getMu_sz_type());
		}
		return typeList;
	}

	public List<String> getMuJList(String type) {
		List<String> strList = new ArrayList<String>();
		if (map.containsKey(type)) {
			String[] strs = map.get(type).getMu_j();
			if (strs == null) {
				return null;
			}
			for (int i = 0; i < strs.length; i++) {
				strList.add(strs[i]);
			}
		}
		return strList;
	}

	public List<String> getMuZgList(String type) {
		List<String> strList = new ArrayList<String>();
		if (map.containsKey(type)) {
			String[] strs = map.get(type).getMu_zg();
			if (strs == null) {
				return null;
			}
			for (int i = 0; i < strs.length; i++) {
				strList.add(strs[i]);
			}
		}
		return strList;
	}

	public List<String> getMuGfList(String type) {
		List<String> strList = new ArrayList<String>();
		if (map.containsKey(type)) {
			String[] strs = map.get(type).getMu_gf();
			if (strs == null) {
				return null;
			}
			for (int i = 0; i < strs.length; i++) {
				strList.add(strs[i]);
			}
		}
		return strList;
	}

	public List<String> getMuTypeList(String type) {
		List<String> strList = new ArrayList<String>();
		if (map.containsKey(type)) {
			String[] strs = map.get(type).getMu_type();
			if (strs == null) {
				return null;
			}
			for (int i = 0; i < strs.length; i++) {
				strList.add(strs[i]);
			}
		}
		return strList;
	}

	public List<String> getMuJzTimeList(String type) {
		List<String> strList = new ArrayList<String>();
		if (map.containsKey(type)) {
			String[] strs = map.get(type).getMu_jz_time();
			if (strs == null) {
				return null;
			}
			for (int i = 0; i < strs.length; i++) {
				strList.add(strs[i]);
			}
		}
		return strList;
	}
}
