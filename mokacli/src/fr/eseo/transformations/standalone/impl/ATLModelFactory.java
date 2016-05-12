package fr.eseo.transformations.standalone.impl;

import java.io.InputStream;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.gmt.tcs.metadata.ModelFactory;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.IModel;
import org.eclipse.m2m.atl.core.IReferenceModel;
import org.eclipse.m2m.atl.core.emf.EMFInjector;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFReferenceModel;

public class ATLModelFactory implements ModelFactory {

	private EMFModelFactory modelFactory;

	public ATLModelFactory(EMFModelFactory modelFactory) {
		this.modelFactory = modelFactory;
	}

	@Override
	public Object newModel(String name, Object metamodel) {
		return this.modelFactory.newModel((IReferenceModel)metamodel);
	}

	@Override
	public Object loadMetamodel(String name, Object location) {
		IReferenceModel ret = this.modelFactory.newReferenceModel();
		if(location instanceof URI) {
			location = location.toString();
		}
		try {
			if(location instanceof String) {
				new EMFInjector().inject(ret, (String)location);
			} else if(location instanceof Resource) {
				new EMFInjector().inject(ret, (Resource)location);
			} else if(location instanceof InputStream) {
				new EMFInjector().inject(ret, (InputStream)location, Collections.<String, Object>emptyMap());
			} else {
				throw new RuntimeException("no support for location " + location);
			}
		} catch(ATLCoreException e) {
			new RuntimeException(e);
		}
		return ret;
	}

	@Override
	public Object loadModel(String name, Object metamodel, Object location) {
		IModel ret = this.modelFactory.newModel((EMFReferenceModel)metamodel, (String)location);
		return ret;
	}

	@Override
	public Object metamodelOf(Object model) {
		return ((IModel)model).getReferenceModel();
	}

}
