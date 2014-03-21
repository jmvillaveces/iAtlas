package mpg.biochem.de.interbase.model;

public class Service {

	private String name, restURL, version;
	private long count, countAfterClustering;
	private boolean indexed, clustered;
	
	public Service(){
		indexed = false;
		clustered = false;
		countAfterClustering = 0;
	}
	
	public Service(String name, String restURL, String version, long count) {
		this();
		this.name = name;
		this.restURL = restURL;
		this.version = version;
		this.count = count;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getCountAfterClustering() {
		return countAfterClustering;
	}

	public void setCountAfterClustering(long countAfterClustering) {
		this.countAfterClustering = countAfterClustering;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	public boolean isClustered() {
		return clustered;
	}

	public void setClustered(boolean clustered) {
		this.clustered = clustered;
	}

	public String getRestURL() {
		return restURL;
	}
	
	public void setRestURL(String restURL) {
		this.restURL = restURL;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
