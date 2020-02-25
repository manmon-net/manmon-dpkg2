package net.manmon.pkg.services.yumrepomdimport;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(namespace = "http://linux.duke.edu/metadata/common")
@XmlAccessorType(XmlAccessType.FIELD)
public class Checksum implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlValue
	private String content;

	@XmlAttribute
	private String type;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ClassPojo [content = " + content + ", type = " + type + "]";
	}
}
