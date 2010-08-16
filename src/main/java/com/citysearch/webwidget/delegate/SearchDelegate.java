package com.citysearch.webwidget.delegate;

import org.apache.log4j.Logger;

public class SearchDelegate {
	private Logger log = Logger.getLogger(getClass());
	
	private String rootPath;
	private Integer displaySize;

	public SearchDelegate(String rootPath, Integer displaySize) {
		this.rootPath = rootPath;
		this.displaySize = displaySize;
	}
}
