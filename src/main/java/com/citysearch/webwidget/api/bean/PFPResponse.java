package com.citysearch.webwidget.api.bean;

import java.util.ArrayList;
import java.util.List;

public class PFPResponse {
	private List<PFPAd> localPfp;
	private List<PFPAd> backfill;

	public PFPResponse() {
		localPfp = new ArrayList<PFPAd>();
		backfill = new ArrayList<PFPAd>();
	}

	public List<PFPAd> getLocalPfp() {
		return localPfp;
	}

	public void setLocalPfp(List<PFPAd> localPfp) {
		this.localPfp = localPfp;
	}

	public List<PFPAd> getBackfill() {
		return backfill;
	}

	public void setBackfill(List<PFPAd> backfill) {
		this.backfill = backfill;
	}

}
