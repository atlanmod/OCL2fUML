package fr.eseo.transformations.standalone.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.tcs.metadata.ASMModelFactory;
import org.eclipse.gmt.tcs.metadata.InjectorForATL;
import org.eclipse.gmt.tcs.metadata.Language;
import org.eclipse.gmt.tcs.metadata.LanguageRegistry;
import org.eclipse.gmt.tcs.metadata.RegularVMLauncher;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.IInjector;
import org.eclipse.m2m.atl.core.IReferenceModel;
import org.eclipse.m2m.atl.core.emf.EMFInjector;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.launch.ILauncher;
import org.eclipse.m2m.atl.core.service.LauncherService;
import org.eclipse.m2m.atl.drivers.emf4atl.ASMEMFModel;
import org.eclipse.m2m.atl.drivers.emf4atl.AtlEMFModelHandler;
import org.eclipse.m2m.atl.drivers.emf4atl.EMFModelLoader;
import org.eclipse.m2m.atl.engine.emfvm.launch.EMFVMLauncher;
import org.eclipse.m2m.atl.engine.vm.AtlModelHandler;
import org.eclipse.m2m.atl.engine.vm.nativelib.ASMModel;

import fr.eseo.transformations.standalone.IModel;
import fr.eseo.transformations.standalone.ITransformationFramework;
import fr.eseo.transformations.standalone.Library;
import fr.eseo.transformations.standalone.ModelParameter;
import fr.eseo.transformations.standalone.Result;
import fr.eseo.transformations.standalone.Transformation;
import fr.eseo.transformations.standalone.Utils;

public class TransformationFramework implements ITransformationFramework {

//	public Result extractTo(String path, IModel sourceModel) {
//		
//	}

	private static final String injectionFailedMessage = "\tInjection failed, attempting to go on but result will be incorrect.";

	@Override
	public Result injectFrom(String path) {
		String extension = Utils.getExtension(path);
		if(LanguageRegistry.getDefault().getLanguage(extension) == null) {
			System.out.println("\tLanguage not found for '" + extension + "', attempting XMI loading");
			Language language = languageByModelExt.get(extension);
			Result ret = new Result();

			EMFModelFactory modelFactory = new EMFModelFactory();
			IReferenceModel targetMM = loadMM(path, modelFactory, language);

			Model targetM = new Model(this, modelFactory.newModel(targetMM));
			try {
				new EMFInjector().inject(targetM.getInternal(), path);
			} catch (ATLCoreException e) {
				e.printStackTrace();
				System.out.println(injectionFailedMessage);
				ret.setFailed(true);
			}
			if(!ret.hasFailed()) {
				System.out.println("\tInjected.");
			}
			return ret.addModel("OUT", targetM);
		} else {
			return tcsInjectFrom(path);
		}
	}

	public IModel loadModel(Language language, Resource r) {
		EMFModelFactory modelFactory = new EMFModelFactory();
		org.eclipse.m2m.atl.core.IModel m = modelFactory.newModel(loadMM(null, modelFactory, language));
		new EMFInjector().inject(m, r);
		return new Model(this, m);
	}


	private IReferenceModel loadMM(String path, EMFModelFactory modelFactory, Language language) {
		IReferenceModel mm = modelFactory.newReferenceModel();
		if(language == null) {
			System.out.println("Error: language not found for extension " + Utils.getExtension(path));
			return null;
		}
		new EMFInjector().inject(mm, ((ASMEMFModel)language.getMetamodel(ASMModelFactory.getDefault())).getExtent());
		return mm;
	}

	public Result tcsInjectFrom(String path) {
		Result ret = new Result();

		boolean injectionFailed = false;

		IInjector tcsInjector = new InjectorForATL();
		AtlModelHandler mh = new AtlEMFModelHandler();
		EMFModelLoader ml = (EMFModelLoader)mh.createModelLoader();

		ASMModel pbs = ml.newModel("pbs", "pbs.xmi", ml.getBuiltInMetaModel("Problem.ecore"));
		Map params = new HashMap();
		params.put("problems", pbs);
		params.put("vmLauncher", new RegularVMLauncher());

		EMFModelFactory modelFactory = new EMFModelFactory();

		Language language = LanguageRegistry.getDefault().getLanguage(Utils.getExtension(path));
		IReferenceModel targetMM = loadMM(path, modelFactory, language);
		if(targetMM == null) {
			injectionFailed = true;
		}

		Model targetM = new Model(this, modelFactory.newModel(targetMM));
		ret.addModel("OUT", targetM);
		//TODOret.addModel("pbs", new Model(pbs));

		String injectionFailedMessage = "\tInjection failed, attempting to go on but result will be incorrect.";
		try {
			tcsInjector.inject(targetM.getInternal(), URI.createFileURI(new File(path).getAbsolutePath()).toString(), params);
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(injectionFailedMessage);
			injectionFailed = true;
//			System.exit(1);
		}
		if(Utils.showProblems(pbs) > 0) {
			System.out.println(injectionFailedMessage);
			injectionFailed = true;
//			System.exit(1);
		}
		if(!injectionFailed) {
			System.out.println("\tInjected.");
		}
		ret.setFailed(injectionFailed);
		return ret;
	}

	@Override
	public void setProperty(String propertyName, Object value) {
		// TODO Auto-generated method stub
	}

	@Override
	public Result apply(Transformation transformation, IModel... sourceModels) {
		Result ret = new Result();

		ILauncher launcher = new EMFVMLauncher();
		launcher.initialize(Collections.<String,Object>emptyMap());

		Iterator<ModelParameter> params = transformation.getSourceModels().iterator();
		for(IModel sourceModel : sourceModels) {
			ModelParameter param = params.next();
			if(param.isRefined()) {
				launcher.addInOutModel(((Model)sourceModel).getInternal(), param.getName(), param.getMetaName());
				ret.addModel(param.getName(), sourceModel);
			} else {
				launcher.addInModel(((Model)sourceModel).getInternal(), param.getName(), param.getMetaName());
			}
		}
		EMFModelFactory modelFactory = new EMFModelFactory();
		for(ModelParameter param : transformation.getTargetModels()) {
			Language language = param.getLanguage();
			IReferenceModel targetMM = (IReferenceModel)language.getMetamodel(new ATLModelFactory(modelFactory));
			IModel targetModel = new Model(this, modelFactory.newModel(targetMM));
			launcher.addOutModel(((Model)targetModel).getInternal(), param.getName(), param.getMetaName());
			ret.addModel(param.getName(), targetModel);
		}

		boolean transformationFailed = false;
		try {
			for(Library library : transformation.getLibraries()) {
				launcher.addLibrary(library.getName(), new FileInputStream(library.getPath()));
			}

			Map pars = new HashMap();
			pars.put("allowInterModelReferences", transformation.getAllowInterModelReferences());

			// this is handled in LauncherService, which we do not use, we must therefore do the work ourselves
			pars.put("isRefiningTraceMode", transformation.isRefining());
			if(transformation.isRefining()) {
				IReferenceModel refiningTraceMetamodel = modelFactory
						.getBuiltInResource(LauncherService.REFINING_TRACE_METAMODEL + ".ecore"); //$NON-NLS-1$

				Map<String, Object> modelOptions = new HashMap<String, Object>();
				modelOptions.put("path", "temp"); //$NON-NLS-1$ //$NON-NLS-2$
				modelOptions.put("modelName", LauncherService.REFINING_TRACE_MODEL); //$NON-NLS-1$
				modelOptions.put("newModel", true); //$NON-NLS-1$
				org.eclipse.m2m.atl.core.IModel refiningTraceModel = modelFactory.newModel(refiningTraceMetamodel, modelOptions);
				launcher.addOutModel(refiningTraceModel, LauncherService.REFINING_TRACE_MODEL, LauncherService.REFINING_TRACE_METAMODEL);
			}
			launcher.launch(ILauncher.RUN_MODE, new NullProgressMonitor(), pars, new FileInputStream(transformation.getPath()));
		} catch(Exception e) {
			e.printStackTrace();
			transformationFailed = true;
			System.out.println("\tTransformation failed, result is incorrect");
		}
		if(!transformationFailed) {
			System.out.println("\tTransformed.");
		}
		ret.setFailed(transformationFailed);

		return ret;
	}

	private Map<Language, Map<String, Object>> defaultParamsByLanguage = new HashMap<>();
	private Map<String, Map<String, Object>> defaultParamsByExtension = new HashMap<>();
	private Map<String, Language> languageByModelExt = new HashMap<>();

	@Override
	public ITransformationFramework registerLanguage(Language language, String...defaultParams) {
		try {
			Utils.registerLanguage(language);
			Map<String, Object> params = new HashMap<>();
			for(int i = 0 ; i < defaultParams.length ; i += 2) {
				params.put(defaultParams[i], defaultParams[i + 1]);
			}
			defaultParamsByLanguage.put(language, params);
			defaultParamsByExtension.put(language.getExtension(), params);
			if(language.getSource() != null) {
				String ext = language.getSource().getProperty("model.ext");
				if(ext != null) {
					languageByModelExt.put(ext, language);
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public Map<String, Object> getDefaultParams(String extension) {
		Map<String, Object> ret = defaultParamsByExtension.get(extension);
		if(ret == null) {
			ret = Collections.emptyMap();
		}
		return ret;
	}

	public String toString() {
		return defaultParamsByLanguage.keySet().toString();
	}
}
