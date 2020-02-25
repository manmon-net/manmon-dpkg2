package net.manmon.pkg.services.yumrepoimport;

import javax.xml.bind.annotation.*;

@XmlRootElement(namespace = "http://linux.duke.edu/metadata/common")
@XmlAccessorType(XmlAccessType.FIELD)
public class Checksum {
	@XmlValue
	private String checksum;
	@XmlAttribute
	private String type;
	@XmlAttribute
	private String pkgid;

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPkgid() {
		return pkgid;
	}

	public void setPkgid(String pkgid) {
		this.pkgid = pkgid;
	}

}
