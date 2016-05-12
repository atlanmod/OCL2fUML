package fr.eseo.transformations.standalone;

import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.gmt.tcs.metadata.Language;
import org.eclipse.gmt.tcs.metadata.LanguageRegistry;
import org.eclipse.m2m.atl.engine.vm.nativelib.ASMEnumLiteral;
import org.eclipse.m2m.atl.engine.vm.nativelib.ASMModel;
import org.eclipse.m2m.atl.engine.vm.nativelib.ASMModelElement;
import org.eclipse.m2m.atl.engine.vm.nativelib.ASMString;

public class Utils {
	public static int showProblems(ASMModel pbs) {
		int nbErrors = 0;
		for(ASMModelElement pb : (Iterable<ASMModelElement>)pbs.getElementsByType("Problem")) {
			String severity = aGet(pb, "severity");
			String location = aGet(pb, "location");
			String description = aGet(pb, "description");
			System.out.println(severity + ": " + location + ": " + description);
			if(severity.equals("error")) {
				nbErrors++;
			}
		}
		return nbErrors;
	}

	public static <T> T aGet(ASMModelElement element, String propertyName) {
		Object ret = element.get(null, propertyName);
		if(ret instanceof ASMString) {
			ret = ((ASMString)ret).getSymbol();
		} else if(ret instanceof ASMEnumLiteral) {
				ret = ((ASMEnumLiteral)ret).getName();
		}
		return (T)ret;
	}

	public static String getExtension(String path) {
		return path.replaceAll("^.*\\.([^.]*)$", "$1");
	}

	public static void registerLanguage(Language language) throws Exception {
//		LanguageRegistry languageRegistry = LanguageRegistry.getDefault();
//		findMethod(languageRegistry, "register", Language.class).invoke(languageRegistry, language);
		Map languageByExtension = get(LanguageRegistry.getDefault(), "languageByExtension");
		languageByExtension.put(language.getExtension(), language);
	}

// @begin Reflection utils
//	private static Method findMethod(Object self, String methodName, Class...parameterTypes) throws Exception {
//		Method ret = self.getClass().getDeclaredMethod(methodName, parameterTypes);
//		ret.setAccessible(true);
//		return ret;
//	}

	private static Field findField(Object self, String fieldName) throws Exception {
		Field f = self.getClass().getDeclaredField(fieldName);
		f.setAccessible(true);
		return f;
	}

	private static <T> T get(Object self, String fieldName) throws Exception {
		return (T)findField(self, fieldName).get(self);
	}
// @end Reflection utils
}
