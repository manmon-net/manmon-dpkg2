package net.manmon.pkg.services.yumrepomdimport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(namespace="http://linux.duke.edu/metadata/repo")
@XmlAccessorType(XmlAccessType.FIELD)
public class Repo implements Serializable {
	private static final long serialVersionUID = 1L;
	@XmlElement(name="repomd", namespace="http://linux.duke.edu/metadata/repo")
	private Repomd repomd;

	public Repomd getRepomd() {
		return repomd;
	}

	public void setRepomd(Repomd repomd) {
		this.repomd = repomd;
	}

	@Override
	public String toString() {
		return "ClassPojo [repomd = " + repomd + "]";
	}
}