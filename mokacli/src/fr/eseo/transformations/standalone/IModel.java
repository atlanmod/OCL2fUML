package fr.eseo.transformations.standalone;

public interface IModel {
	public Result apply(Transformation transformation, IModel...additionalSourceModels);
	public void extractTo(String targetFile, String...params);
}
