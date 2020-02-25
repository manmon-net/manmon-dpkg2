package net.manmon.pkg.services.yumrepoimport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://linux.duke.edu/metadata/common")
@XmlAccessorType(XmlAccessType.FIELD)
public class Format {
	@XmlElement(namespace = "http://linux.duke.edu/metadata/rpm")
	private String license;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/rpm")
	private String vendor;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/rpm")
	private String group;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/rpm")
	private String buildhost;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/rpm")
	private String sourcerpm;
	@XmlElement(name="header-range", namespace = "http://linux.duke.edu/metadata/rpm")
	private HeaderRange headerRange;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/rpm")
	private RpmProvides provides;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/rpm")
	private RpmRequires requires;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/rpm")
	private RpmConflicts conflicts;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/rpm")
	private RpmConflicts obsoletes;
	@XmlElement(name="file", namespace = "http://linux.duke.edu/metadata/common")
	private RpmFile[] files;
	
	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getBuildhost() {
		return buildhost;
	}

	public void setBuildhost(String buildhost) {
		this.buildhost = buildhost;
	}

	public String getSourcerpm() {
		return sourcerpm;
	}

	public void setSourcerpm(String sourcerpm) {
		this.sourcerpm = sourcerpm;
	}

	public RpmProvides getProvides() {
		return provides;
	}

	public void setProvides(RpmProvides provides) {
		this.provides = provides;
	}

	public RpmRequires getRequires() {
		return requires;
	}

	public void setRequires(RpmRequires requires) {
		this.requires = requires;
	}

	public RpmConflicts getConflicts() {
		return conflicts;
	}

	public void setConflicts(RpmConflicts conflicts) {
		this.conflicts = conflicts;
	}

	public RpmFile[] getFiles() {
		return files;
	}

	public void setFiles(RpmFile[] files) {
		this.files = files;
	}

	public RpmConflicts getObsoletes() {
		return obsoletes;
	}

	public void setObsoletes(RpmConflicts obsoletes) {
		this.obsoletes = obsoletes;
	}

	public HeaderRange getHeaderRange() {
		return headerRange;
	}

	public void setHeaderRange(HeaderRange headerRange) {
		this.headerRange = headerRange;
	}

	
}
