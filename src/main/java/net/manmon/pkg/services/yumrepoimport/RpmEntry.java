package net.manmon.pkg.services.yumrepoimport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entry", namespace = "http://linux.duke.edu/metadata/rpm")
@XmlAccessorType(XmlAccessType.FIELD)
public class RpmEntry {
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String flags;
	@XmlAttribute
	private String epoch;
	@XmlAttribute
	private String ver;
	@XmlAttribute 
	private String rel;
	@XmlAttribute
	private String pre;
	
	public String getFlags() {
		return flags;
	}

	public void setFlags(String flags) {
		this.flags = flags;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEpoch() {
		return epoch;
	}

	public void setEpoch(String epoch) {
		this.epoch = epoch;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getRel() {
		return rel;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public String getPre() {
		return pre;
	}

	public void setPre(String pre) {
		this.pre = pre;
	}

	
}
