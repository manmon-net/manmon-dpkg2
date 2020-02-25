package net.manmon.pkg.services.yumrepoimport;

import javax.xml.bind.annotation.*;

@XmlRootElement(namespace = "http://linux.duke.edu/metadata/common", name="package")
@XmlAccessorType(XmlAccessType.FIELD)
public class RpmPackage {
	@XmlElement(namespace = "http://linux.duke.edu/metadata/common")
	private String name;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/common")
	private String arch;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/common")
	private Version version;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/common")
	private Checksum checksum;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/common")
	private String summary;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/common")
	private String description;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/common")
	private String packager;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/common")
	private String url;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/common")
	private Time time;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/common")
	private Size size;
	@XmlElement(namespace = "http://linux.duke.edu/metadata/common")
	private Location location;
	@XmlElement(name = "format", namespace = "http://linux.duke.edu/metadata/common")
	private Format format;
	@XmlAttribute
	private String type;
	
	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPackager() {
		return packager;
	}

	public void setPackager(String packager) {
		this.packager = packager;
	}

	public String getArch() {
		return arch;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public Checksum getChecksum() {
		return checksum;
	}

	public void setChecksum(Checksum checksum) {
		this.checksum = checksum;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	
}
