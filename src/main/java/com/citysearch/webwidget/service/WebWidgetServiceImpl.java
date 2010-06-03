package com.citysearch.webwidget.service;

import javax.jws.WebService;

@WebService(endpointInterface = "com.citysearch.webwidget.service.WebWidgetService")
public class WebWidgetServiceImpl implements WebWidgetService {
	public String getString() {
		return "Hello!!!!!";
	}
}
