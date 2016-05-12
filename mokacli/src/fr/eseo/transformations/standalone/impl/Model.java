package fr.eseo.transformations.standalone.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gmt.tcs.metadata.ExtractorForATL;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.IExtractor;
import org.eclipse.m2m.atl.core.emf.EMFExtractor;

import fr.eseo.transformations.standalone.IModel;
import fr.eseo.transformations.standalone.Result;
import fr.eseo.transformations.standalone.Transformation;
import fr.eseo.transformations.standalone.Utils;

public class Model implements IModel {

	private TransformationFramework tf;
	private org.eclipse.m2m.atl.core.IModel internal ;

	public Model(TransformationFramework tf, org.eclipse.m2m.atl.core.IModel internal) {
		this.tf = tf;
		this.internal = internal;
	}

	public org.eclipse.m2m.atl.core.IModel getInternal() {
		return this.internal;
	}

	@Override
	public Result apply(Transformation transformation, IModel... additionalSourceModels) {
		List<IModel> l = new ArrayList<>();
		l.add(this);
		for(IModel additionalSourceModel : additionalSourceModels) {
			l.add(additionalSourceModel);
		}
		return transformation.getTransformationFramework().apply(transformation, l.toArray(additionalSourceModels));
	}

	@Override
	public void extractTo(String targetFile, String...params_) {

		String extension = Utils.getExtension(targetFile);
		Map<String, Object> params = addParams(null, params_);
		if("xmi".equals(extension) || "true".equals(params.get("forceXMI"))) {
			try {
				new EMFExtractor().extract(this.getInternal(), new File(targetFile).toURI().toString(), params);
			} catch (ATLCoreException e) {
				e.printStackTrace();
			}
		} else {
			extractWithTCSTo(targetFile, extension, params_);
		}
	}

	private Map<String, Object> addParams(Map<String, Object> ret, String...params) {
		//Map<String, Object> params = new HashMap<>();
		if(ret == null) {
			ret = new HashMap<>();
		}
		for(int i = 0 ; i < params.length ; i += 2) {
			ret.put(params[i], params[i + 1]);
		}
		return ret;
	}

	private void extractWithTCSTo(String targetFile, String extension, String...params_) {
		IExtractor tcsExtractor = new ExtractorForATL();
		Map<String, Object> params = new HashMap<>(this.tf.getDefaultParams(Utils.getExtension(targetFile)));
		addParams(params, params_);
		params.put(ExtractorForATL.PARAM_LANGUAGE, extension);
//		AbstractLanguage tcs = (AbstractLanguage)LanguageRegistry.getDefault().getLanguage("tcs");
//		params.put("tcsMetamodel", tcs.getMetamodel());
//		params.put("tcsJar", tcs.getJarURL());
		params.put("useTCSLanguage", true);

		if(!params.containsKey("indentString")) {
			params.put("indentString", "\t");
		}

		boolean extractionFailed = false;
		try {
			tcsExtractor.extract(this.getInternal(), new FileOutputStream(targetFile), params);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("\tExtraction failed.");
			extractionFailed = true;
		}
		if(!extractionFailed) {
			System.out.println("\tExtracted.");
		}

//		if(injectionFailed || transformationFailed || extractionFailed) {
//			System.out.println("Result is incorrect.");
//		} else {
//			System.out.println("Done.");
//		}
	}
}
