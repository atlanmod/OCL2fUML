package mymokacli;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.papyrus.moka.debug.MokaDebugTarget;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngine;
import org.eclipse.papyrus.moka.fuml.FUMLExecutionEngineForMoka;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.DispatchStrategy;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Object_;
import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.Execution;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.OpaqueBehaviorExecution;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;
import org.eclipse.papyrus.moka.fuml.Semantics.Loci.LociL1.Locus;
import org.eclipse.papyrus.moka.fuml.debug.ControlDelegate;
import org.eclipse.papyrus.moka.fuml.registry.service.framework.AbstractService;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.FunctionBehavior;
import org.eclipse.uml2.uml.OpaqueBehavior;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.resource.UMLResource;

public class FUMLExecutionEngineForMokaCLI extends FUMLExecutionEngineForMoka {
		private final ResourceSet rs;
		private final Resource r;

		private final HashMap<String, String> operationNameTranslations = new HashMap<String, String>();

		FUMLExecutionEngineForMokaCLI(ResourceSet rs, Resource r) {
			this.rs = rs;
			this.r = r;
			FUMLExecutionEngine.eInstance = this;
			operationNameTranslations.put(">", "Greater");
			operationNameTranslations.put(">=", "GreaterOrEqual");
			operationNameTranslations.put("<", "Lower");
			operationNameTranslations.put("<=", "LowerOrEqual");
			operationNameTranslations.put("+", "Add");
			operationNameTranslations.put("-", "Minus");
			operationNameTranslations.put("*", "Times");
			operationNameTranslations.put("/", "Div");
		}

		public void start(Activity a) {
			controlDelegate = new ControlDelegate(this) {
				// AnimationUtils renamed into AnimationManager in later versions?
				// => but since we cannot connect our own animation manager, we
				// hijack the animate method of ControlDelegate
				protected void animate(EObject element) {
					// TODO: animation?
//						System.out.println(get(element, "name") + " : " + element.eClass().getName());
				};
			};
			super.start(a);
		}

		@Override
		protected void registerSemanticStrategies(Locus locus) {
			super.registerSemanticStrategies(locus);

			// we only do the following in order to report a better error than
			// a crash in the case a method is not found
			final DispatchStrategy original = (DispatchStrategy)locus.factory.getStrategy("dispatch");
			locus.factory.setStrategy(new DispatchStrategy() {
				@Override
				public Behavior getMethod(Object_ object, Operation operation) {
					if(operation == null) {
						throw new RuntimeException("error: attemping a call to null operation");
					}
					try {
						return original.getMethod(object, operation);
					} catch(Exception e) {
						throw new RuntimeException("error: could not find behavior associated to operation " + operation.getName() + " on object: " + object, e);
					}
				}
			});

/*				// ChoiceStrategy not used to select next runnable action
			final Random random = new Random();
			locus.factory.setStrategy(new ChoiceStrategy() {
				@Override
				public Integer choose(Integer size) {
					return random.nextInt(size) + 1;
				}
			});
/**/
		}

		protected void initializeBuiltInPrimitiveTypes(Locus locus) {
			URI uri = Main.UML_PRIMITIVE_TYPES;
			Resource r = rs.getResource(uri, true);
			Iterator<EObject> i = r.getAllContents();
			while(i.hasNext()) {
				EObject eo = i.next();
				if(eo instanceof PrimitiveType) {
					locus.factory.addBuiltInType((Type)eo);
				}
			}
		}

		protected void registerOpaqueBehaviorExecutions(Locus locus) {
			registerStandardLibraryExecutionsFor(locus, "Integer");
			registerStandardLibraryExecutionsFor(locus, "Real");
			registerStandardLibraryExecutionsFor(locus, "UnlimitedNatural");
			registerStandardLibraryExecutionsFor(locus, "Boolean", "boolean_");
			registerStandardLibraryExecutionsFor(locus, "String");
			registerStandardLibraryExecutionsFor(locus, "List");
		}

		protected void registerStandardLibraryExecutionsFor(Locus locus, String typeName) {
			registerStandardLibraryExecutionsFor(locus, typeName, typeName.toLowerCase());
		}

		protected void registerStandardLibraryExecutionsFor(Locus locus, String typeName, String packageTail) {
			Resource fUMLLib = rs.getResource(Main.PAPYRUS_fUML_LIBRARY, true);
			Package p = Main.find(fUMLLib, Package.class, typeName + "Functions");
			for(PackageableElement pe : p.getPackagedElements()) {
				if(pe instanceof FunctionBehavior) {
					OpaqueBehavior ob = (FunctionBehavior)pe;
					try {
						String className = ob.getName();
						if(operationNameTranslations.containsKey(className)) {
							className = operationNameTranslations.get(className);
						}
						OpaqueBehaviorExecution behaviorExecution = (OpaqueBehaviorExecution)java.lang.Class.forName("org.eclipse.papyrus.moka.fuml.standardlibrary.library." + packageTail + "." + className).newInstance();
						behaviorExecution.types.add(ob);
						locus.factory.primitiveBehaviorPrototypes.add(behaviorExecution);
					} catch (Exception e) {
						throw new RuntimeException("error: could not instantiate execution from fUML library", e);
					}
				}
			}

			FunctionBehavior ae = Main.find(r, FunctionBehavior.class, "assertEquals");
			OpaqueBehaviorExecution behaviorExecution = new AssertEqualsExecution();
			behaviorExecution.types.add(ae);
			locus.factory.primitiveBehaviorPrototypes.add(behaviorExecution);
		}

		protected void registerSystemServices(Locus locus) {
			// TODO: extend support to more than just writeLine
			Resource r = rs.getResource(Main.PAPYRUS_fUML_LIBRARY, true);
			final Class c = Main.find(r, Class.class, "StandardOutputChannel");
			locus.extensionalValues.add(new AbstractService(c) {
				@Override
				public Execution dispatch(Operation operation) {
					if("writeLine".equals(operation.getName())) {
						return new ServiceOperationExecution(operation) {
							@Override
							public Value new_() {
								// Original Moka code returns a new instance of the current class
								// but why would we need a new one? 
								return this;
							}
							@Override
							public void doBody(List<ParameterValue> inputParameters, List<ParameterValue> arg1) {
								System.out.println(inputParameters.get(0).values.get(0));
							}
						};
					} else {
						return super.dispatch(operation);
					}
				}
				
			});
		}

		@Override
		public MokaDebugTarget getDebugTarget() {
			try {
				return new MokaDebugTarget(new Launch("debug"), null);
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
	}