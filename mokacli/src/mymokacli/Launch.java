package mymokacli;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;

public class Launch implements ILaunch {

	private String mode;

	public Launch(String mode) {
		this.mode = mode;
	}

	@Override
	public String getLaunchMode() {
		return mode;
	}

	// All methods below are unsupported (because not required to execute Moka)

	@Override
	public boolean canTerminate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isTerminated() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void terminate() throws DebugException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] getChildren() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IDebugTarget getDebugTarget() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IProcess[] getProcesses() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IDebugTarget[] getDebugTargets() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addDebugTarget(IDebugTarget target) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeDebugTarget(IDebugTarget target) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addProcess(IProcess process) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeProcess(IProcess process) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ISourceLocator getSourceLocator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSourceLocator(ISourceLocator sourceLocator) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ILaunchConfiguration getLaunchConfiguration() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttribute(String key, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAttribute(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasChildren() {
		throw new UnsupportedOperationException();
	}
}
