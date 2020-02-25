package net.manmon.pkg.services.yumrepoimport;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(namespace="http://linux.duke.edu/metadata/common")
@XmlAccessorType(XmlAccessType.FIELD)
public class Metadata {
	@XmlElement(name="package", namespace="http://linux.duke.edu/metadata/common")
	private List<RpmPackage> rpmPackages = new ArrayList<>();
	@XmlAttribute
	private Long packages;
	
	public Long getPackages() {
		return packages;
	}

	public void setPackages(Long packages) {
		this.packages = packages;
	}

	public List<RpmPackage> getRpmPackages() {
		return rpmPackages;
	}

	public void setRpmPackages(List<RpmPackage> rpmPackages) {
		this.rpmPackages = rpmPackages;
	}
}
