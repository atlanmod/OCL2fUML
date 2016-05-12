package fr.eseo.transformations.standalone.impl;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gmt.tcs.metadata.AbstractLanguage;
import org.eclipse.gmt.tcs.metadata.LanguageSource;
import org.eclipse.gmt.tcs.metadata.ModelFactory;
import org.eclipse.m2m.atl.core.launch.ILauncher;
import org.eclipse.m2m.atl.dsls.textsource.TextSource;
import org.eclipse.m2m.atl.engine.vm.ASM;
import org.eclipse.m2m.atl.engine.vm.ASMXMLReader;


public class StandaloneLanguage extends AbstractLanguage implements LanguageSource {

	private String resourcePath;

	private Properties properties;
	private String name;
	private String extension;

//	private File km3File;
//	private File annotationFile;
	private File tcsFile;
//	private File antlrFile;
	private File jarFile;
//	private File acgFile;
	private File compilerFile;

	private Object metamodelLocation;

	public StandaloneLanguage(String name, String extension, File tcsFile, URI metamodelLocation, File jarFile) throws IOException {
		this.properties = new Properties();	// dummy
		this.name = name;
		this.extension = extension;
		
		this.tcsFile = tcsFile;

		this.metamodelLocation = metamodelLocation;

		this.jarFile = jarFile;
	}

	public StandaloneLanguage(String resourcePath) throws IOException {
		this.resourcePath = resourcePath;
		File propertiesFile = new File(resourcePath + "/build.properties");

		if(!propertiesFile.exists()) {
			throw new RuntimeException("Missing property file: " + propertiesFile);
		}

		properties = new Properties();
		properties.load(new FileReader(propertiesFile));

		name = properties.getProperty("dsl.name");
		extension = properties.getProperty("dsl.ext");
		if(extension == null) {
			throw new RuntimeException("Missing extension");
		}

		if(name == null) {
			throw new RuntimeException("Missing name");
		}

//		km3File = new File(resourcePath + "/Metamodel/" + name + ".km3");
//		annotationFile = new File(resourcePath + "Metamodel/" + name + ".ann");
		tcsFile = new File(resourcePath + "/Syntax/" + name + ".tcs");

//		if(!km3File.exists()) {
//			System.out.println(km3File.getAbsolutePath());
//			throw new RuntimeException("Missing KM3 file");
//		}

		String metamodelOverrideURI = properties.getProperty("override.metamodel.uri");
		if(metamodelOverrideURI != null) {
			metamodelLocation = URI.createURI(metamodelOverrideURI);
		} else {
			File ecoreFile = new File(resourcePath + "/Metamodel/" + name + ".ecore");
//			metamodelLocation = new FileInputStream(ecoreFile);
			metamodelLocation = URI.createFileURI(ecoreFile.getAbsolutePath());
		}

//		antlrFile = new File(resourcePath + "Syntax/" + name + "_ANTLR3.g");
		jarFile = new File(resourcePath + "/Syntax/" + name + "-parser.jar");

//		acgFile = new File(resourcePath + "Compiler/" + name + ".acg");
		compilerFile = new File(resourcePath + "/Compiler/" + name + ".asm");
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getExtension() {
		return this.extension;
	}

	@Override
	public Object getOutlineModel(ModelFactory factory) {
		throw new RuntimeException("Standalone languages are not for use by editors.");
	}

	@Override
	public Object getEditorModel(ModelFactory factory) {
		throw new RuntimeException("Standalone languages are not for use by editors.");
	}

	@Override
	public Object getMetamodel(ModelFactory factory) {
		return factory.loadMetamodel(name, metamodelLocation);
	}

	@Override
	public ASM getCompiler() {
		ASM ret = null;
		if(compilerFile != null && compilerFile.exists()) {
			try {
				ret = new ASMXMLReader().read(new BufferedInputStream(new FileInputStream(compilerFile)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	@Override
	public LanguageSource getSource() {
		return this;	// necessary for getProperty used to get model.ext
	}

	private void addLibraries(ILauncher launcher, String propertyName) throws IOException {
		String libNameAndPathsCSV = this.getProperty(propertyName);

		if(libNameAndPathsCSV != null) {
			String libNameAndPaths[] = libNameAndPathsCSV.split(",");
			for(int i = 0 ; i < libNameAndPaths.length ; i++) {
				String parts[] = libNameAndPaths[i].split("=");
				launcher.addLibrary(parts[0], new FileInputStream(this.resourcePath + "/" + parts[1]));
			}
		}
	}

	protected void addWFRLibraries(ILauncher launcher) throws IOException {
		this.addLibraries(launcher, "wfr.libraries");
	}

	protected void addPreExtractLibraries(ILauncher launcher) throws IOException {
		this.addLibraries(launcher, "pre-extract.libraries");
	}

	private File getFile(String path) {
		return new File(this.resourcePath + path);
	}

	private InputStream getASM(String path) throws IOException {
		File wfrFile = getFile(path);
		if(wfrFile.exists()) {
			return new FileInputStream(wfrFile);
		} else {
			return null;
		}
	}

	protected boolean hasWFR() {
		return this.getFile("/WFR/" + name + ".asm").exists();
	}

	protected InputStream getWFR() throws IOException {
		return this.getASM("/WFR/" + name + ".asm");
	}

	protected boolean hasPreExtract() {
		return this.getFile("/WFR/PreExtract.asm").exists();
	}

	protected InputStream getPreExtract() throws IOException {
		return this.getASM("/WFR/PreExtract.asm");
	}

	protected void addExtractOptions(Map params) {
		String opts = getProperty("extract.options");
		if(opts != null) {
			String optsParts[] = opts.split(",");
			for(int i = 0 ; i < optsParts.length ; i++) {
				String mapping[] = optsParts[i].split("=");
				params.put(mapping[0], mapping[1]);
			}
		}
	}

	protected TextSource getTCS() {
		if(!tcsFile.exists()) {
			throw new RuntimeException("Missing TCS file");
		}
		return new FileTextSource(tcsFile);
	}

	protected URL getJarURL() throws MalformedURLException {
		return jarFile.toURL();
	}

	public String getProperty(String propertyName) {
		return this.properties.getProperty(propertyName);
	}

	@Override
	public IFile getTCSSourceFile() {
		throw new RuntimeException("Standalone languages do not support compilation.");
	}

	@Override
	public IFile getKM3SourceFile() {
		throw new RuntimeException("Standalone languages do not support compilation.");
	}

	@Override
	public IFile getAnnotationSourceFile() {
		throw new RuntimeException("Standalone languages do not support compilation.");
	}

	@Override
	public IFile getEditorFile() {
		throw new RuntimeException("Standalone languages do not support compilation.");
	}

	@Override
	public IFile getOutlineFile() {
		throw new RuntimeException("Standalone languages do not support compilation.");
	}

	@Override
	public IFile getMetamodelFile() {
		throw new RuntimeException("Standalone languages do not support compilation.");
	}

	@Override
	public IFile getGrammarFile() {
		throw new RuntimeException("Standalone languages do not support compilation.");
	}

	@Override
	public String getParserGenerator() {
		throw new RuntimeException("Standalone languages do not support compilation.");
	}

	@Override
	public IFile getParserFile() {
		throw new RuntimeException("Standalone languages do not support compilation.");
	}

	@Override
	public IFile getACGSourceFile() {
		throw new RuntimeException("Standalone languages do not support compilation.");
	}

	@Override
	public IFile getCompilerFile() {
		throw new RuntimeException("Standalone languages do not support compilation.");
	}

	public String toString() {
		//return tcsFile + ", " + metamodelLocation + ", " + jarFile + "\n";
		return this.name + ", " + this.extension + ", " + metamodelLocation + "\n";
	}
}
