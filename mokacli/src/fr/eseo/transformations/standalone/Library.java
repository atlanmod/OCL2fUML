package fr.eseo.transformations.standalone;

public class Library {

	private String name;
	private String path;

	public Library(String name, String path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return this.name;
	}

	public String getPath() {
		return this.path;
	}

}
