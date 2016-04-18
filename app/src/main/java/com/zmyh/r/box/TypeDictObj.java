package com.zmyh.r.box;


public class TypeDictObj {

	public final static String MU_SZ_TYPE = "mu_sz_type";
	public final static String MU_J = "mu_j";
	public final static String MU_ZG = "mu_zg";
	public final static String MU_GF = "mu_gf";
	public final static String MU_TYPE = "mu_type";
	public final static String MU_JZ_TIME = "mu_jz_time";

	private String mu_sz_type;
	private String[] mu_j;
	private String[] mu_zg;
	private String[] mu_gf;
	private String[] mu_type;
	private String[] mu_jz_time;

	// *************************************
	public String getMu_sz_type() {
		if (mu_sz_type == null) {
			return "";
		}
		return mu_sz_type;
	}

	public void setMu_sz_type(String mu_sz_type) {
		this.mu_sz_type = mu_sz_type;
	}

	// *************************************
	public String[] getMu_j() {
		return mu_j;
	}

	public void setMu_j(String mu_j) {
		if (mu_j != null && !mu_j.equals("") && !mu_j.equals("null")) {
			setMu_j(mu_j.split("\\|"));
		}
	}

	public void setMu_j(String[] mu_j) {
		this.mu_j = mu_j;
	}

	// *************************************
	public String[] getMu_zg() {
		return mu_zg;
	}

	public void setMu_zg(String mu_zg) {
		if (mu_zg != null && !mu_zg.equals("") && !mu_zg.equals("null")) {
			setMu_zg(mu_zg.split("\\|"));
		}
	}

	public void setMu_zg(String[] mu_zg) {
		this.mu_zg = mu_zg;
	}

	// *************************************
	public String[] getMu_gf() {
		return mu_gf;
	}

	public void setMu_gf(String mu_gf) {
		if (mu_gf != null && !mu_gf.equals("") && !mu_gf.equals("null")) {
			setMu_gf(mu_gf.split("\\|"));
		}
	}

	public void setMu_gf(String[] mu_gf) {
		this.mu_gf = mu_gf;
	}

	// *************************************
	public String[] getMu_type() {
		return mu_type;
	}

	public void setMu_type(String mu_type) {
		if (mu_type != null && !mu_type.equals("") && !mu_type.equals("null")) {
			setMu_type(mu_type.split("\\|"));
		}
	}

	public void setMu_type(String[] mu_type) {
		this.mu_type = mu_type;
	}

	// *************************************
	public String[] getMu_jz_time() {
		return mu_jz_time;
	}

	public void setMu_jz_time(String mu_jz_time) {
		if (mu_jz_time != null && !mu_jz_time.equals("")
				&& !mu_jz_time.equals("null")) {
			setMu_jz_time(mu_jz_time.split("\\|"));
		}
	}

	public void setMu_jz_time(String[] mu_jz_time) {
		this.mu_jz_time = mu_jz_time;
	}

}
