package mymokacli;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.eclipse.papyrus.moka.fuml.Semantics.Classes.Kernel.Value;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.OpaqueBehaviorExecution;
import org.eclipse.papyrus.moka.fuml.Semantics.CommonBehaviors.BasicBehaviors.ParameterValue;

public class AssertEqualsExecution extends OpaqueBehaviorExecution {

	@Override
	public Value new_() {
		return new AssertEqualsExecution();
	}

	@Override
	public void doBody(List<ParameterValue> inputParameters, List<ParameterValue> outputParameters) {
		List<Value> first = inputParameters.get(0).values;
		List<Value> second = inputParameters.get(1).values;
		// we cannot use ArrayList.equals because hasCode is not overridden for all classes overriding equals (e.g., BooleanValue)
		boolean eq = true;
		if(first.size() == second.size()) {
			Iterator<Value> i = second.iterator();
			for(Value v1 : first) {
				Value v2 = i.next();
				if(!v1.equals(v2)) {
					eq = false;
					break;
				}
			}
		} else {
			eq = false;
		}
		assertTrue("expected: " + second + ", got: " + first, eq);
		Main.asserts++;
		outputParameters.get(0).values = first;
	}
}