package mymokacli;

import static org.junit.Assert.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.impl.URIMappingRegistryImpl;
import org.eclipse.gmt.tcs.metadata.Language;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.drivers.emf4atl.AtlEMFModelHandler;
import org.eclipse.m2m.atl.engine.vm.AtlModelHandler;
import org.eclipse.papyrus.moka.fuml.standardlibrary.library.io.StandardOutputChannelImpl;
import org.eclipse.papyrus.uml.alf.libraries.AlfLibrariesActivator;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.CallOperationAction;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import fr.eseo.transformations.standalone.Result;
import fr.eseo.transformations.standalone.Transformation;
import fr.eseo.transformations.standalone.impl.Model;
import fr.eseo.transformations.standalone.impl.StandaloneLanguage;
import fr.eseo.transformations.standalone.impl.TransformationFramework;

/**
 * This class can be used to run Moka from command line
 * using its {@link #main(String[])} method.
 * It can also be used to run tests by running it using
 * junit.
 */
@RunWith(Parameterized.class)
public class Main {

	private static final boolean generatePlantUML = true;

	// Configuration: model & main activity
	private static String sourcePath = "../fr.inria.atlanmodels.OCL2UML/";
	private static String inputModel = sourcePath + "input.uml";
	private static String helpersFile = sourcePath + "tests.atl";
	private static String outputModel = sourcePath + "output.uml";
	private static String mainActivityName = "Company_Factory";


	private static final String PAPYRUS_fUML_LIBRARY_PATHMAP = "pathmap://PAPYRUS_fUML_LIBRARY/";
	public static final URI PAPYRUS_fUML_LIBRARY = URI.createURI(PAPYRUS_fUML_LIBRARY_PATHMAP + "fUML_Library.uml");

	private static final String PAPYRUS_ALF_LIBRARY_PATHMAP = "pathmap://PAPYRUS_ALF_LIBRARY/";
	public static final URI PAPYRUS_ALF_LIBRARY = URI.createURI(PAPYRUS_ALF_LIBRARY_PATHMAP + "Alf.library.uml");

	public static final URI UML_PRIMITIVE_TYPES = URI.createURI(UMLResource.LIBRARIES_PATHMAP + "UMLPrimitiveTypes.library.uml");

	public static void main(String[] args) throws Exception {
		globalSetup();
		System.out.println("- executing");
		engine.start(mainActivity);
		System.out.println("\tdone.");
	}

	// The four following fields are used for command-line or test.

	private static ResourceSet rs;
	private static Resource r;

	private static Activity mainActivity;

	private static FUMLExecutionEngineForMokaCLI engine;

	public static void globalSetup() throws Exception {
		umlInit();

		rs = new ResourceSetImpl();

		TransformationFramework tf = transformationsInit();

		long i = new File(inputModel).lastModified();
		long h = new File(helpersFile).lastModified();
		long o = new File(outputModel).lastModified();
		long a = new File(augmentUMLWithATLHelpers.getPath()).lastModified();
		long f = new File(fixObjectFlows.getPath()).lastModified();
		long t = new File(sourcePath + "ATLTypeInference.asm").lastModified();
		if(o > i && o > h && o > a && o > f && o > t) {
			System.out.println("Using existing output model.");
			r = rs.createResource(URI.createFileURI(outputModel));
			r.load(new HashMap<>());
		} else {
			System.out.println("Output model needs to be rebuilt:");
			System.out.println("- loading UML model");
			Result input = tf.injectFrom(sourcePath + "input.uml");
			System.out.println("- loading OCL helpers");
			Result helpers = tf.injectFrom(helpersFile);

			// We need to use the same ResourceSet to load UMLPrimitiveTypes
			// (or any other Resource referenced from the input model)
			// so that it is loaded only once.
			rs = ((EMFModel)((Model)input.get("OUT")).getInternal()).getResource().getResourceSet();

			System.out.println("- augmenting UML model with OCL helpers");
			Result output = input.get("OUT").apply(augmentUMLWithATLHelpers,
					helpers.get("OUT"),
					tf.loadModel(umlLanguage, rs.getResource(PAPYRUS_fUML_LIBRARY, true)),
					tf.loadModel(umlLanguage, rs.getResource(PAPYRUS_ALF_LIBRARY, true)),
					tf.loadModel(umlLanguage, rs.getResource(UML_PRIMITIVE_TYPES, true))
			);
	
			System.out.println("- fixing ObjectFlows");
			output = output.get("IN").apply(fixObjectFlows);

			output.get("IN").extractTo(outputModel,
					"forceXMI", "true"
			);

			if(generatePlantUML) {
				System.out.println("- generating PlantUML for all activities");
				output.get("IN").apply(UML2PlantUML_ActivitiesSM).get("OUT").extractTo("test.plantuml");
			}

	
			r = ((EMFModel)((Model)output.get("IN")).getInternal()).getResource();
		}

		mainActivity = (Activity)find(r, Activity.class, mainActivityName);

		engine = new FUMLExecutionEngineForMokaCLI(rs, r);
	}

// Test-specific fields and methods below

	private static CallOperationAction testOperationCall;

	/**
	 * Finds all test_ operations on the class owning the main activity. 
	 */
	@Parameters(name="{0}")
	public static Iterable<Object[]> data() throws Exception {
		List<Object[]> ret = new ArrayList<>();

		globalSetup();

		Class c = (Class)mainActivity.getOwner();

		Activity cb = (Activity)c.getOwnedBehavior("__classifierBehavior__");
		testOperationCall = (CallOperationAction)cb.getOwnedNodes().get(2);

		List<Operation> ops = c.getOwnedOperations();
		for(Operation op : ops) {
			if(op.getName().startsWith("test_")) {
				ret.add(new Object[] {op.getName(), op});
			}
		}

		return ret;
	}

	private String operationName;
	private Operation operation;

	public Main(String operationName, Operation operation) {
		this.operationName = operationName;
		this.operation = operation;
	}

	public static int asserts = 0;
	// TODO: make sure an assert operation (e.g., assertEquals)
	// is at least called once (in case bad compilation fails to
	// generate code that calls it)
	@Test
	public void test() {
		testOperationCall.setOperation(operation);
		asserts = 0;
		engine.start(mainActivity);
		assertNotEquals("no assert called", 0, asserts); 
	}

// Init

	private static void umlInit() throws Exception {
		EPackage.Registry.INSTANCE.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put("http://www.eclipse.org/uml2/4.0.0/UML", UMLPackage.eINSTANCE);
		
		ResourceFactoryRegistryImpl.INSTANCE.getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);

		Map<String, String> pluginToURL = new HashMap<>();

		URI uri = URI.createURI(addPlugin(pluginToURL, UMLResourcesUtil.class).toString() + '/');
		URIMappingRegistryImpl.INSTANCE.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), uri.appendSegment("libraries").appendSegment(""));
		URIMappingRegistryImpl.INSTANCE.put(URI.createURI(UMLResource.METAMODELS_PATHMAP), uri.appendSegment("metamodels").appendSegment(""));
		URIMappingRegistryImpl.INSTANCE.put(URI.createURI(UMLResource.PROFILES_PATHMAP), uri.appendSegment("profiles").appendSegment(""));

		uri = URI.createURI(addPlugin(pluginToURL, StandardOutputChannelImpl.class).toString() + '/');
		URIMappingRegistryImpl.INSTANCE.put(URI.createURI(PAPYRUS_fUML_LIBRARY_PATHMAP), uri.appendSegment("resources").appendSegment(""));

		uri = URI.createURI(addPlugin(pluginToURL, AlfLibrariesActivator.class).toString() + '/');
		URIMappingRegistryImpl.INSTANCE.put(URI.createURI(PAPYRUS_ALF_LIBRARY_PATHMAP), uri.appendSegment("resources").appendSegment("action-language-libraries").appendSegment(""));

		// Moka
		new DebugPlugin();	// initialize for DebugPlugin.getDefault()
	}

	private static Language umlLanguage;
	private static Transformation augmentUMLWithATLHelpers;
	private static Transformation fixObjectFlows;
	private static Transformation UML2PlantUML_ActivitiesSM;

	private static TransformationFramework transformationsInit() throws Exception {
		AtlModelHandler mh = new AtlEMFModelHandler();
		AtlModelHandler.registerDefaultHandler(AtlModelHandler.AMH_EMF, mh);

		TransformationFramework tf = new TransformationFramework();

		String resourcePath = "resources";
		umlLanguage = new StandaloneLanguage(resourcePath + "/UML");
		tf.registerLanguage(umlLanguage);
		tf.registerLanguage(new StandaloneLanguage(resourcePath + "/ATL"));
		tf.registerLanguage(new StandaloneLanguage(resourcePath + "/PlantUML"),
			"identEsc", "",
			"stringDelim", "\""
		);
		tf.registerLanguage(new StandaloneLanguage(resourcePath + "/TCS"));

		augmentUMLWithATLHelpers = new Transformation(tf, sourcePath + "AugmentUMLModelWithATLHelpers.asm")
				.setRefining(true)
				.setAllowInterModelReferences(true)
				.addLibrary("ATLTypeInference", sourcePath + "ATLTypeInference.asm")
				.addRefinedModel("IN", "UML")
				.addSourceModel("helpers", "ATL")
				.addSourceModel("lib", "FUML")
				.addSourceModel("lib_ALF", "ALF")
				.addSourceModel("UMLPrimitiveTypes", "UML")
				//.addTargetModel("OUT", "UML", "tuml")
		;

		fixObjectFlows = new Transformation(tf, sourcePath + "MovedObjFlowOutsideOfStructureNode.asm")
				.setRefining(true)
				.addRefinedModel("IN", "UML")
		;

		if(generatePlantUML) {
			UML2PlantUML_ActivitiesSM = new Transformation(tf, resourcePath + "/UML/Compiler/UML2PlantUML-ActivitiesSM.asm")
					.addLibrary("UMLHelpers", resourcePath + "/UML/Helpers/UMLHelpers.asm")
					.addLibrary("UMLNavigation", resourcePath + "/UML/WFR/UMLNavigation.asm")
					.addLibrary("UML2PlantUMLHelpers", resourcePath + "/UML/Compiler/UML2PlantUMLHelpers.asm")
					.addSourceModel("IN", "UML")
					.addTargetModel("OUT", "PlantUML", "plantuml")
			;
		}

		return tf;
	}

// Utils

	private static class MyIterable<T> implements Iterable<T> {

		private Iterator<T> iterator;

		public MyIterable(Iterator<T> iterator) {
			this.iterator = iterator;
		}

		@Override
		public Iterator<T> iterator() {
			return this.iterator;
		}
		
	}

	private static <T> T get(EObject eo, String featureName) {
		return (T)eo.eGet(eo.eClass().getEStructuralFeature(featureName));
	}

	public static <T extends EObject> T find(Resource r, java.lang.Class<T> type, String name) {
		for(EObject eo : new MyIterable<EObject>(r.getAllContents())) {
			if(type.isInstance(eo)) {
				if(name.equals(get(eo, "name"))) {
					return (T)eo;
				}
			}
		}
		return null;
	}

	private static String addPlugin(Map<String, String> pluginToURL, java.lang.Class<?> c) {
		String url = c.getResource(c.getSimpleName() + ".class").toString();
		url = url.replaceAll("![^!]*$", "!");
		String plugin = url.replaceAll("^.*/plugins/([^_-]*)[_-].*$", "$1");	// launched form Eclipse
		plugin = plugin.replaceAll("^.*/libs/([^_-]*)[_-].*$", "$1");			// launched from command line
		pluginToURL.put(plugin, url);
		return url;
	}
}
