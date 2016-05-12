package fr.eseo.transformations.standalone;

import java.util.HashMap;
import java.util.Map;

public class Result {

	private boolean failed = false;
	private Map<String, IModel> modelsByName = new HashMap<>();

	public Result addModel(String name, IModel model) {
		this.modelsByName.put(name, model);
		return this;
	}

	public IModel get(String name) {
		return this.modelsByName.get(name);
	}

	public Result setFailed(boolean failed) {
		this.failed = failed;
		return this;
	}

	public boolean hasFailed() {
		return this.failed;
	}
}
