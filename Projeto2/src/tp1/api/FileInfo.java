package tp1.api;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a File in the system
 */
public class FileInfo {
	/**
	 * userId of the owner
	 */
	private String owner;
	private String filename;
	/**
	 * URL for direct access to a file
	 */
	private String fileURL;
	/**
	 * List of user with whom the file has been shared
	 */
	private Set<String> sharedWith;

	//private Set<URI> servers;
	
	public FileInfo() {
		this.sharedWith = ConcurrentHashMap.newKeySet();
		//this.servers = ConcurrentHashMap.newKeySet();
	}
	
	public FileInfo(String owner, String filename, String fileURL, Set<String> sharedWith) {
		this.owner = owner;
		this.filename = filename;
		this.fileURL = fileURL;
		this.sharedWith = sharedWith;
		//this.servers = ConcurrentHashMap.newKeySet();

	}

	/*
	public void setServer(URI serverURI) {
		this.servers.add(serverURI);
	}

	public Set<URI> getServers() {
		return servers;
	}*/

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFileURL() {
		return fileURL;
	}

	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}

	public Set<String> getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(Set<String> sharedWith) {
		this.sharedWith = sharedWith;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fileURL, filename);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileInfo other = (FileInfo) obj;
		return Objects.equals(fileURL, other.fileURL) && Objects.equals(filename, other.filename);
	}

	@Override
	public String toString() {
		return "FileInfo [owner=" + owner + ", filename=" + filename + ", fileURL=" + fileURL + ", sharedWith="
				+ sharedWith + "]";
	}
	
	/**
	 * 
	 * Extended Operations...
	 * 
	 */
	public boolean hasAccess(String userId) {
		return userId.equals(getOwner()) || getSharedWith().contains(userId);
	}
	
}
