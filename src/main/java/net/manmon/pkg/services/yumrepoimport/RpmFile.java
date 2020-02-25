package net.manmon.pkg.services.yumrepoimport;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "file", namespace = "http://linux.duke.edu/metadata/common")
@XmlAccessorType(XmlAccessType.FIELD)
public class RpmFile {
	@XmlValue
	private String name;
	@XmlAttribute
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
