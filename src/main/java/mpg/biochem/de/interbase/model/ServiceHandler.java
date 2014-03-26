package mpg.biochem.de.interbase.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import mpg.biochem.de.interbase.util.XMLConverter;

public class ServiceHandler {
	
	private XMLConverter XMLConverter; 
	private ServiceList sList;
	private String path;
	
	public ServiceHandler() {
	}
	
	public void setXMLConverter(XMLConverter XMLConverter) {
		this.XMLConverter = XMLConverter;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void initIt() throws IOException{
		
		File f = new File(path);
		if(f.exists()){
			sList = (ServiceList) XMLConverter.convertFromXMLToObject(path);
		}else{
			sList = new ServiceList();
		}
	}
	
	public synchronized  void  save() throws IOException{
		XMLConverter.convertFromObjectToXML(sList, path);
	}
	
	public synchronized void add(Service service) throws IOException{
		sList.add(service);
		save();
	}
	
	public List<Service> getServiceList(){
		return sList.getServices();
	}
	
	public Service getService(String serviceName){
		return sList.getService(serviceName);
	}
}


