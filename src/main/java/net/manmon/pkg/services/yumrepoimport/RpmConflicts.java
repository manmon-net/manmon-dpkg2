package net.manmon.pkg.services.yumrepoimport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="conflicts", namespace = "http://linux.duke.edu/metadata/rpm")
@XmlAccessorType(XmlAccessType.FIELD)
public class RpmConflicts {
	@XmlElement(name="entry", namespace = "http://linux.duke.edu/metadata/rpm")
	private RpmEntry[] entries;

	public RpmEntry[] getEntries() {
		return entries;
	}

	public void setEntries(RpmEntry[] entries) {
		this.entries = entries;
	}
	
	
}
