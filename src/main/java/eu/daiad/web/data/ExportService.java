package eu.daiad.web.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.daiad.web.model.ExportData;
import eu.daiad.web.model.ExportException;
import eu.daiad.web.model.ExtendedSessionData;

@Service
public class ExportService {

	@Value("${tmp.folder}")
	private String temporaryPath;

	@Autowired
	private MeasurementRepository storage;
	
	public String export(ExportData data) throws ExportException {
		try {
			File path = new File(temporaryPath);
	
			path.mkdirs();
			
			if(!path.exists()) {
				throw new ExportException("Unable to create temporary path.");
			};
			
			List<ExtendedSessionData> sessions = this.storage.exportDataAmhiroSession(data);
			
			if(sessions.size() == 0) {
				throw new ExportException("No data found for the selected criteria.");
			}
			
			String token =  UUID.randomUUID().toString();
			
			File csvFile = new File(path, token + ".csv");
			File zipFile = new File(path, token + ".zip");
			
			Set<String> zones = DateTimeZone.getAvailableIDs();
			if(data.getTimezone() == null) {
				data.setTimezone("Europe/Athens");
			}
			if(!zones.contains(data.getTimezone())) {
				throw new ExportException(String.format("Time zone [%s] is not supported.", data.getTimezone()));
			}
		    
			DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(DateTimeZone.forID(data.getTimezone()));
			
			PrintWriter writer = new PrintWriter(csvFile, StandardCharsets.UTF_8.toString());
			for(int s = 0; s < sessions.size(); s++) {
				ExtendedSessionData session = sessions.get(s);
	
				writer.format("%s\t%s\t%s\t%s\t%s\t%s\t",
						  session.getUser().getKey(),
						  session.getUser().getUsername(),
						  session.getUser().getPostalCode(),
						  session.getDevice().getId(),
						  session.getDevice().getKey(),
						  session.getDevice().getName());
	
				writer.format("%s\t%d\t%d\t%.4f\t%.4f\t%.4f\t%.4f",
							  session.getTimestamp().toString(formatter),
							  session.getShowerId(),
							  session.getDuration(),
							  session.getTemperature(),
							  session.getVolume(),
							  session.getFlow(),
							  session.getEnergy());
				
				if(data.getProperties().size() != 0) {
					Iterator<String> iter = data.getProperties().iterator();
				    while (iter.hasNext()) {
				        String key = (String) iter.next();
				        
				        String value = session.getPropertyByKey(key);
				        if(value == null) {
				        	writer.print("\t");
				        } else {
				        	writer.format("\t%s", value);
				        }
				    }
				}
				writer.print("\n");
			}
			writer.flush();
			writer.close();
			
			// Compress file
			byte[] buffer = new byte[4096];
			
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry ze= new ZipEntry("export.csv");
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(csvFile);
	
			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			
			in.close();
			
			zos.flush();
			zos.closeEntry();
	
			zos.close();
	
			csvFile.delete();
			
			return token;
		} catch(ExportException ex) {
			throw ex;
		} catch(Exception ex) {
			throw new ExportException("Unhandled exception has occured.", ex);
		}
	}
}
