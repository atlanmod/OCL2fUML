package fr.eseo.transformations.standalone.impl;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.m2m.atl.dsls.textsource.TextSource;


public class FileTextSource extends TextSource {

	private File file;

	public FileTextSource(String path) {
		this.file = new File(path);
	}

	public FileTextSource(File file) {
		this.file = file;
	}

	@Override
	public InputStream openStream() throws IOException {
		return new FileInputStream(this.file);
	}

}
