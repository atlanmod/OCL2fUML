package fr.eseo.transformations.standalone;

import org.eclipse.gmt.tcs.metadata.Language;
import org.eclipse.gmt.tcs.metadata.LanguageRegistry;

public class ModelParameter {

	private String name;
	private String referenceModelName;
	private Language language;
	private boolean isRefined;

	public ModelParameter(String name, String referenceModelName, boolean isRefined) {
		this(name, referenceModelName, (Language)null, isRefined);
	}

	public ModelParameter(String name, String referenceModelName, String languageExtension, boolean isRefined) {
		this(name, referenceModelName, LanguageRegistry.getDefault().getLanguage(languageExtension), isRefined);
	}

	public ModelParameter(String name, String referenceModelName, Language language, boolean isRefined) {
		this.name = name;
		this.referenceModelName = referenceModelName;
		this.language = language;
		this.isRefined = isRefined;
	}

	public String getName() {
		return this.name;
	}

	public String getMetaName() {
		return this.referenceModelName;
	}

	public String toString() {
		return this.name + " : " + this.referenceModelName + "(" + this.getLanguage() + ")";
	}

	public Language getLanguage() {
		return this.language;
	}

	public boolean isRefined() {
		return this.isRefined;
	}
}
