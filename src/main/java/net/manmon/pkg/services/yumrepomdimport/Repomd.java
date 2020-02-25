package net.manmon.pkg.services.yumrepomdimport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(namespace = "http://linux.duke.edu/metadata/repo", name="repomd")
@XmlAccessorType(XmlAccessType.FIELD)
public class Repomd implements Serializable
{
	private static final long serialVersionUID = 1L;

	@XmlElement(namespace = "http://linux.duke.edu/metadata/repo")
	private String revision;
    @XmlElement(name="data", namespace = "http://linux.duke.edu/metadata/repo")
    private Data[] data;

    public String getRevision ()
    {
        return revision;
    }

    public void setRevision (String revision)
    {
        this.revision = revision;
    }

    public Data[] getData ()
    {
        return data;
    }

    public void setData (Data[] data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [revision = "+revision+", data = "+data+"]";
    }
}