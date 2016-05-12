package fr.eseo.transformations.standalone;

import org.eclipse.gmt.tcs.metadata.Language;

public interface ITransformationFramework {
	public ITransformationFramework registerLanguage(Language language, String...defaultParams);
	public Result injectFrom(String path);
	public void setProperty(String propertyName, Object value);
	public Result apply(Transformation transformation, IModel...sourceModels);
}
