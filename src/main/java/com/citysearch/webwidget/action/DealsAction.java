package com.citysearch.webwidget.action;

import org.apache.log4j.Logger;

import com.citysearch.webwidget.bean.RequestBean;
import com.citysearch.webwidget.exception.CitysearchException;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ModelDriven;

public class DealsAction extends AbstractCitySearchAction implements
		ModelDriven<RequestBean> {
	private Logger log = Logger.getLogger(getClass());

	private RequestBean dealsRequest = new RequestBean();

	public RequestBean getModel() {
		return dealsRequest;
	}

	public RequestBean getDealsRequest() {
		return dealsRequest;
	}

	public void setDealsRequest(RequestBean dealsRequest) {
		this.dealsRequest = dealsRequest;
	}

	public String execute() throws CitysearchException {
		return Action.SUCCESS;
	}

}
