package fr.eseo.transformations.standalone;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gmt.tcs.metadata.Language;

public class Transformation {

	private ITransformationFramework tf;
	private String path;
	private List<ModelParameter> sourceModels = new ArrayList<>();
	private List<ModelParameter> targetModels = new ArrayList<>();
	private List<Library> libraries = new ArrayList<>();
	private boolean isRefining = false;
	private boolean allowInterModelReferences = false;

	public Transformation(ITransformationFramework tf, String path) {
		this.tf = tf;
		this.path = path;
	}

	public ITransformationFramework getTransformationFramework() {
		return this.tf;
	}

	public String getPath() {
		return this.path;
	}

	public Transformation addSourceModel(String name, String referenceModelName) {
		this.sourceModels.add(new ModelParameter(name, referenceModelName, false));
		return this;
	}

	public Transformation addRefinedModel(String name, String referenceModelName) {
		this.sourceModels.add(new ModelParameter(name, referenceModelName, true));
		return this;
	}

	public Transformation addTargetModel(String name, String referenceModelName, String languageExtension) {
		this.targetModels.add(new ModelParameter(name, referenceModelName, languageExtension, false));
		return this;
	}

	public Transformation addTargetModel(String name, String referenceModelName, Language language) {
		this.targetModels.add(new ModelParameter(name, referenceModelName, language, false));
		return this;
	}

	public Transformation addLibrary(String name, String path) {
		this.libraries.add(new Library(name, path));
		return this;
	}

	public Transformation setRefining(boolean isRefining) {
		this.isRefining = isRefining;
		return this;
	}

	public Iterable<ModelParameter> getSourceModels() {
		return this.sourceModels;
	}

	public Iterable<ModelParameter> getTargetModels() {
		return this.targetModels;
	}

	public Iterable<Library> getLibraries() {
		return this.libraries;
	}

	public String toString() {
		return path + "(" + sourceModels + ")->(" + targetModels + ")";
	}

	public boolean isRefining() {
		return this.isRefining;
	}

	public boolean getAllowInterModelReferences() {
		return this.allowInterModelReferences;
	}

	public Transformation setAllowInterModelReferences(boolean allowInterModelReferences) {
		this.allowInterModelReferences = allowInterModelReferences;
		return this;
	}
}
