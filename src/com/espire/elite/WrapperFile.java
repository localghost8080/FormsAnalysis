package com.espire.elite;

import java.io.File;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Element;

/**
 * @author rajiv.kumar
 *
 */
public class WrapperFile {

	private File file;
	private String version;
	private String fileName;
	private String filePath;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	// <tagName,tageValues(node/element info)>
	private Map<String, Set<Element>> tagData;
	

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Map<String, Set<Element>> getTagData() {
		return tagData;
	}

	public void setTagData(Map<String, Set<Element>> tagData) {
		this.tagData = tagData;
	}

}
